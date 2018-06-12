package uk.co.appsbystudio.geoshare

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class Application : android.app.Application() {
    init {
        instance = this
    }

    companion object {

        private var instance: Application? = null

        val context: Context?
            get() = instance
    }

    override fun onCreate() {
        super.onCreate()
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }
    }
}
