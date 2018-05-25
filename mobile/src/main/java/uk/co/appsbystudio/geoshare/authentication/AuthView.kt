package uk.co.appsbystudio.geoshare.authentication

import android.support.v4.app.Fragment

interface AuthView {

    fun setFragment(fragment: Fragment)

    fun onBack()

    fun onSuccess()

}
