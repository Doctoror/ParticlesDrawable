package com.doctoror.particleswallpaper.presentation.config

import android.os.Bundle
import com.doctoror.particleswallpaper.R

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 */
open class ConfigFragmentKitKat: ConfigFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val p = findPreference(getString(R.string.pref_key_background_image))
        if (p != null) {
            p.isEnabled = true
            p.summary = null
        }
    }

}