package uk.co.appsbystudio.geoshare.authentication

import androidx.fragment.app.Fragment

interface AuthView {

    fun setFragment(fragment: Fragment)

    fun onBack()

    fun onSuccess()

}
