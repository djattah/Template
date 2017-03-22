package com.l24o.vyatich

import android.app.Application
import android.content.ContextWrapper
import com.pixplicity.easyprefs.library.Prefs
import io.realm.Realm

class VyatichApplication : Application() {


    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(packageName)
                .setUseDefaultSharedPreference(true)
                .build()
    }
}