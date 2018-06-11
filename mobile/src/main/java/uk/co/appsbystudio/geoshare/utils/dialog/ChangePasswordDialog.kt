package uk.co.appsbystudio.geoshare.utils.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_change_password.view.*

import uk.co.appsbystudio.geoshare.R

class ChangePasswordDialog : DialogFragment() {

    private lateinit var mView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mView = LayoutInflater.from(activity).inflate(R.layout.dialog_change_password, null)

        val optionsMenu = AlertDialog.Builder(activity, R.style.DialogTheme)

        optionsMenu.setTitle("Change password")

        optionsMenu.setView(mView)
                .setPositiveButton("Ok") {_, _ -> validate() }
                .setNegativeButton("Cancel", null)

        val dialog = optionsMenu.create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            validate()
        }

        return dialog
    }

    private fun validate() {
        val user = FirebaseAuth.getInstance().currentUser

        val oldPasswordText = mView.edit_old_password_change.text.toString()
        val newPasswordText = mView.edit_new_password_change.text.toString()
        val confirmPasswordText = mView.edit_confirm_change.text.toString()

        println(oldPasswordText)

        when {
            oldPasswordText.isBlank() -> mView.edit_old_password_change.error = getString(R.string.error_field_required)
            newPasswordText.isBlank() -> mView.edit_new_password_change.error = getString(R.string.error_field_required)
            newPasswordText.isBlank() -> mView.edit_confirm_change.error = getString(R.string.error_field_required)
            newPasswordText != confirmPasswordText -> mView.edit_confirm_change.error = getString(R.string.match)
            else -> {
                if (user?.email != null) {
                    val credential = EmailAuthProvider.getCredential(user.email!!, oldPasswordText)

                    user.reauthenticate(credential).addOnSuccessListener {
                        user.updatePassword(newPasswordText).addOnSuccessListener {
                            dismiss()
                        }.addOnFailureListener {
                            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
