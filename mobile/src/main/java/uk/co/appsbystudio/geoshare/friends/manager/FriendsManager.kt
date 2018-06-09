package uk.co.appsbystudio.geoshare.friends.manager

import android.content.Intent
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle

import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_friends_manager.*

import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.manager.pages.FriendSearchActivity
import uk.co.appsbystudio.geoshare.friends.manager.pages.FriendsFragment
import uk.co.appsbystudio.geoshare.friends.manager.pages.FriendsPendingFragment

class FriendsManager : AppCompatActivity(), FriendsManagerView {

    private var firebaseAuth: FirebaseAuth? = null
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private var presenter: FriendsManagerPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_manager)

        presenter = FriendsManagerPresenterImpl(this)

        toolbar_manager.setTitle(R.string.title_activity_friends_manager)
        setSupportActionBar(toolbar_manager)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        firebaseAuth = FirebaseAuth.getInstance()

        val adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment? {
                return when (position) {
                    0 -> FriendsFragment()
                    1 -> FriendsPendingFragment()
                    else -> null
                }
            }

            override fun getCount(): Int {
                return 2
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> "Current"
                    1 -> "Pending"
                    else -> null
                }
            }
        }

        view_pager_manager.adapter = adapter
        if (intent.extras != null) presenter?.viewpagerItem(intent.extras.getInt("tab"))

        tabs_manager.setupWithViewPager(view_pager_manager)

        fab_add_manager.setOnClickListener {
            presenter?.search()
        }

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                presenter?.invalidSession()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth?.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth?.removeAuthStateListener(authStateListener)
    }

    override fun setViewpagerItem(item: Int) {
        view_pager_manager.currentItem = item
    }

    override fun searchIntent() {
        startActivity(Intent(this@FriendsManager, FriendSearchActivity::class.java))
    }
}
