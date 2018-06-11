package uk.co.appsbystudio.geoshare

import android.content.Context

class Application : android.app.Application() {
    init {
        instance = this
    }

    companion object {

        private var instance: Application? = null

        val context: Context?
            get() = instance
    }
}
