package uk.co.appsbystudio.geoshare.utils.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast

import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.dialog_delete_user.view.*

import uk.co.appsbystudio.geoshare.R

class DeleteUser : DialogFragment() {

    private lateinit var mView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mView = View.inflate(activity, R.layout.dialog_delete_user, null)

        val optionsMenu = AlertDialog.Builder(activity, R.style.DialogTheme)

        optionsMenu.setTitle("Are you sure you want to delete this account?")

        optionsMenu.setView(mView)
                .setPositiveButton("Ok") { dialogInterface, i ->
                    //validate();
                }
                .setNegativeButton("Cancel", null)

        val dialog = optionsMenu.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { validate() }

        return dialog
    }

    private fun validate() {
        val user = FirebaseAuth.getInstance().currentUser
        val passwordText = mView.edit_password_delete.text.toString()

        if (passwordText.isBlank()) {
            mView.edit_password_delete.error = getString(R.string.error_field_required)
            return
        }

        if (user?.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, passwordText)

            user.reauthenticate(credential)?.addOnSuccessListener {
                user.delete()
                dismiss()
            }?.addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
