package uk.co.appsbystudio.geoshare.authentication

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import uk.co.appsbystudio.geoshare.base.MainActivity
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.authentication.login.LoginFragment
import uk.co.appsbystudio.geoshare.setup.InitialSetupActivity

class AuthActivity : AppCompatActivity(), AuthView {

    private var firebaseAuth: FirebaseAuth? = null

    private var presenter: AuthPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        if (savedInstanceState == null) supportFragmentManager.beginTransaction().replace(R.id.frame_auth_fragments_auth, LoginFragment()).commit()

        presenter = AuthPresenterImpl(this)

        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth?.currentUser != null) presenter?.updateUI()
    }

    override fun setFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.frame_auth_fragments_auth, fragment)
                .addToBackStack(null).commit()
    }

    override fun onSuccess() {
        if (PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("first_run", true)) {
            startActivity(Intent(this, InitialSetupActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }

    override fun onBack() {
        super.onBackPressed()
    }

}
