package com.doctoror.particleswallpaper.presentation.config

import android.os.Bundle
import com.doctoror.particleswallpaper.R

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 */
class ConfigFragmentLollipop: ConfigFragmentKitKat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val p = findPreference(getString(R.string.pref_key_preview))
        if (p != null) {
            preferenceScreen?.removePreference(p)
        }
    }

}