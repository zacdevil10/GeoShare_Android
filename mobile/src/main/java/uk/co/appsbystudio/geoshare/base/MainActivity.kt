package uk.co.appsbystudio.geoshare.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

import java.io.File
import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.authentication.AuthActivity
import uk.co.appsbystudio.geoshare.friends.FriendsManager
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsNavAdapter
import uk.co.appsbystudio.geoshare.maps.MapsFragment
import uk.co.appsbystudio.geoshare.utils.Connectivity
import uk.co.appsbystudio.geoshare.utils.ProfileSelectionResult
import uk.co.appsbystudio.geoshare.utils.ProfileUtils
import uk.co.appsbystudio.geoshare.utils.dialog.ProfilePictureOptions
import uk.co.appsbystudio.geoshare.utils.dialog.ShareOptions
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.firebase.TrackingInfo
import uk.co.appsbystudio.geoshare.utils.firebase.UserInformation
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.UpdatedProfilePicturesListener
import uk.co.appsbystudio.geoshare.utils.services.StartTrackingService
import uk.co.appsbystudio.geoshare.utils.services.TrackingService
import uk.co.appsbystudio.geoshare.utils.ui.SettingsActivity

class MainActivity : AppCompatActivity(), MainView, SharedPreferences.OnSharedPreferenceChangeListener, FriendsNavAdapter.Callback, ProfileSelectionResult.Callback.Main {

    private var mainPresenter: MainPresenter? = null

    //FIREBASE
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private var databaseReference: DatabaseReference? = null

    private var userId: String? = null

    private var drawerLayout: DrawerLayout? = null
    private var header: View? = null

    private val mapsFragment = MapsFragment()

    private var rightDrawer: DrawerLayout? = null
    private var friendsNavAdapter: FriendsNavAdapter? = null

    private val uidList = ArrayList<String?>()
    private val hasTracking = HashMap<String?, Boolean?>()

    private var settingsSharedPreferences: SharedPreferences? = null
    private var trackingPreferences: SharedPreferences? = null
    private var showOnMapPreferences: SharedPreferences? = null
    private var profileImageView: CircleImageView? = null

    private var visibility: Boolean = false

    companion object {
        val friendsId = HashMap<String?, Boolean>()
        //TODO: Move to a different class
        val pendingId = HashMap<String?, Boolean>()
        val friendNames = HashMap<String?, String?>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainPresenter = MainPresenterImpl(this, MainInteractorImpl())

        //SharedPreferences
        settingsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        settingsSharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
        trackingPreferences = getSharedPreferences("tracking", Context.MODE_PRIVATE)
        showOnMapPreferences = getSharedPreferences("showOnMap", Context.MODE_PRIVATE)

        setTracking()

        //Firebase initialisation
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.currentUser

        userId = if (firebaseUser != null) firebaseUser!!.uid else null

        val database = FirebaseDatabase.getInstance()
        databaseReference = database.reference

        /* HANDLES FOR VARIOUS VIEWS */
        drawerLayout = findViewById(R.id.drawer_layout)

        val navigationView = findViewById<NavigationView>(R.id.left_nav_view)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.content_frame_map, mapsFragment).commit()
        } else {
            supportFragmentManager.beginTransaction().show(mapsFragment).commit()
        }
        setupDrawerContent(navigationView)

        rightDrawer = findViewById(R.id.right_nav_drawer)

        val rightNavigationView = findViewById<RecyclerView>(R.id.right_friends_drawer)
        rightNavigationView?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        if (rightNavigationView != null) rightNavigationView.layoutManager = layoutManager

        rightDrawer!!.setScrimColor(resources.getColor(android.R.color.transparent))

        //Get friends and populate right nav drawer
        mainPresenter!!.getFriends()
        mainPresenter!!.getFriendsTrackingState()

        friendsNavAdapter = FriendsNavAdapter(this, rightNavigationView, uidList, hasTracking, this)
        if (rightNavigationView != null) rightNavigationView.adapter = friendsNavAdapter

        header = navigationView.getHeaderView(0)

        profileImageView = header!!.findViewById(R.id.profile_image)

        /* POPULATE LEFT NAV DRAWER HEADER FIELDS */
        header!!.findViewById<View>(R.id.profile_image).setOnClickListener { profilePictureSettings() }

        setDisplayName()
        ProfileUtils.setProfilePicture(userId, header!!.findViewById<View>(R.id.profile_image) as CircleImageView, this.cacheDir.toString())
        databaseReference!!.child("picture").addChildEventListener(UpdatedProfilePicturesListener(friendsNavAdapter, this.cacheDir.toString()))

        (findViewById<View>(R.id.show_hide_markers) as Switch).isChecked = showOnMapPreferences!!.getBoolean("all", true)

        (findViewById<View>(R.id.show_hide_markers) as Switch).setOnCheckedChangeListener(ToggleAllMarkersVisibility())

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                ProfileUtils.resetDeviceSettings(settingsSharedPreferences!!, trackingPreferences!!, showOnMapPreferences!!)
                mainPresenter!!.auth()
            }
        }



        show_hide_filter.setOnClickListener {
            if (visibility) {
                findViewById<View>(R.id.filter_content).visibility = View.GONE
            } else {
                findViewById<View>(R.id.filter_content).visibility = View.VISIBLE
            }
            visibility = !visibility
        }
    }

    private fun setTracking() {
        val mobileNetwork = settingsSharedPreferences!!.getBoolean("mobile_network", true)

        //Tracking
        if (mobileNetwork || Connectivity.isConnectedWifi(this)) {
            val startTrackingService = StartTrackingService()
            if (!TrackingService.isRunning) {
                startTrackingService.start()
            }
        }
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.menu.getItem(0).isChecked = true

        navigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            item.isChecked = true
            drawerLayout?.closeDrawers()

            when (item.itemId) {
                R.id.maps -> {
                    mainPresenter?.showFragment(mapsFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.friends -> {
                    item.isChecked = false
                    mainPresenter?.friends()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.settings -> {
                    item.isChecked = false
                    mainPresenter?.settings()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.logout -> {
                    item.isChecked = false
                    mainPresenter?.logout()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.feedback -> {
                    item.isChecked = false
                    mainPresenter!!.feedback()
                    return@OnNavigationItemSelectedListener true
                }
            }
            true
        })
    }

    private fun setDisplayName() {
        if (firebaseUser != null) {
            val welcome = String.format(resources.getString(R.string.welcome_user_header), firebaseUser!!.displayName)
            (header!!.findViewById<View>(R.id.username) as TextView).text = welcome
            settingsSharedPreferences!!.edit().putString("display_name", firebaseUser!!.displayName).apply()
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 213) {
                mapsFragment.setup()
            } else {
                ProfileSelectionResult(this).profilePictureResult(this, requestCode, resultCode, data, userId)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth!!.addAuthStateListener(authStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (authStateListener != null) {
            firebaseAuth!!.removeAuthStateListener(authStateListener!!)
        }
    }

    override fun updateFriendsList(uid: String?, name: String?) {
        uidList.add(uid)
        if (!friendsId.containsKey(uid)) friendsId[uid] = true
        if (!friendNames.containsKey(uid)) friendNames[uid] = name
        friendsNavAdapter!!.notifyDataSetChanged()
        findViewById<View>(R.id.add_friends).visibility = View.GONE
    }

    override fun removeFromFriendList(uid: String?) {
        uidList.remove(uid)
        if (friendsId.containsKey(uid)) friendsId.remove(uid)
        if (friendNames.containsKey(uid)) friendNames.remove(uid)
        friendsNavAdapter!!.notifyDataSetChanged()
        if (friendsId.isEmpty()) findViewById<View>(R.id.add_friends).visibility = View.VISIBLE
    }

    override fun updateTrackingState(uid: String?, trackingState: Boolean?) {
        hasTracking[uid] = trackingState
        friendsNavAdapter!!.notifyDataSetChanged()
    }

    override fun removeTrackingState(uid: String?) {
        hasTracking.remove(uid)
        friendsNavAdapter!!.notifyDataSetChanged()
    }

    override fun swapFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().show(fragment).commit()
    }

    override fun friendsIntent() {
        startActivity(Intent(this@MainActivity, FriendsManager::class.java))
    }

    override fun settingsIntent() {
        startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
    }

    override fun logoutIntent() {
        startActivity(Intent(this@MainActivity, AuthActivity::class.java))
        finish()
    }

    override fun feedbackIntent() {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.type = "text/plain"
        emailIntent.data = Uri.parse("mailto:support@appsbystudio.co.uk")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "GeoShare Feedback")
        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(emailIntent, "Send email via"))
        } else {
            Toast.makeText(this@MainActivity, "No email applications found on this device!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun openNavDrawer() {
        drawerLayout!!.openDrawer(GravityCompat.START)
    }

    override fun openFriendsNavDrawer() {
        rightDrawer!!.openDrawer(GravityCompat.END)
    }

    override fun closeNavDrawer() {
        drawerLayout!!.closeDrawer(GravityCompat.START)
    }

    override fun closeFriendsNavDrawer() {
        rightDrawer!!.closeDrawer(GravityCompat.END)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showErrorSnackbar(message: String) {
        Snackbar.make(findViewById(R.id.coordinator), message, Snackbar.LENGTH_SHORT)
                .setAction("RETRY?") { mainPresenter!!.logout() }
    }

    /* CLICK FUNCTIONALITY FOR PROFILE PIC */
    private fun profilePictureSettings() {
        val fragmentManager = fragmentManager
        val profileDialog = ProfilePictureOptions()
        profileDialog.show(fragmentManager, "profile_dialog")
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        if (s == "mobile_network") {
            val mobileNetwork = sharedPreferences.getBoolean("mobile_network", true)
            val trackingService = Intent(this, TrackingService::class.java)
            if (mobileNetwork) {
                startService(trackingService)
            } else if (Connectivity.isConnectedMobile(this)) {
                stopService(trackingService)
            }
        } else if (s == "display_name") {
            val name = sharedPreferences.getString(s, "DEFAULT")
            databaseReference!!.child("users").child(userId!!).child("name").setValue(name)
            databaseReference!!.child("users").child(userId!!).child("caseFoldedName").setValue(name!!.toLowerCase())

            val profileChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(name).build()

            firebaseUser!!.updateProfile(profileChangeRequest).addOnSuccessListener { setDisplayName() }
        }
    }

    override fun setMarkerHidden(friendId: String, visible: Boolean) {
        mapsFragment.setMarkerVisibility(friendId, visible)
        showOnMapPreferences!!.edit().putBoolean(friendId, visible).apply()
    }

    override fun findOnMapClicked(friendId: String) {
        mapsFragment.findFriendOnMap(friendId)
    }

    override fun sendLocationDialog(name: String, friendId: String) {
        val arguments = Bundle()
        arguments.putString("name", name)
        arguments.putString("friendId", friendId)
        arguments.putString("uid", userId)

        val fragmentManager = fragmentManager
        val friendDialog = ShareOptions()
        friendDialog.arguments = arguments
        friendDialog.show(fragmentManager, "location_dialog")
    }

    override fun stopSharing(user: FirebaseUser, friendId: String) {
        databaseReference!!.child(FirebaseHelper.TRACKING).child(friendId).child("tracking").child(user.uid).removeValue()
                .addOnSuccessListener {
                    trackingPreferences!!.edit().putBoolean(friendId, false).apply()
                    friendsNavAdapter!!.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    //TODO: Show a message (with "try again?" ?)
                }
    }

    override fun updateProfilePicture() {
        ProfileUtils.setProfilePicture(userId, profileImageView, this.cacheDir.toString())
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else if (rightDrawer!!.isDrawerOpen(GravityCompat.END)) {
            rightDrawer!!.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        settingsSharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
    }

    private inner class ToggleAllMarkersVisibility : CompoundButton.OnCheckedChangeListener {

        override fun onCheckedChanged(compoundButton: CompoundButton, b: Boolean) {
            mapsFragment.setAllMarkersVisibility(b)
        }
    }
}