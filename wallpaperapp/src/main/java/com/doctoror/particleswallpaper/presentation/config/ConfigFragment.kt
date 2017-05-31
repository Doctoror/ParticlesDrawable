/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doctoror.particleswallpaper.presentation.config

import android.app.Fragment
import android.content.Intent
import android.os.Build
import android.preference.PreferenceFragment
import android.text.TextUtils
import com.doctoror.particlesdrawable.ParticlesDrawable
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.data.config.DrawableConfiguratorFactory
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryFactory
import com.doctoror.particleswallpaper.domain.config.DrawableConfigurator
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.base.OnActivityResultCallbackHost
import com.doctoror.particleswallpaper.presentation.base.OnActivityResultCallbackHostImpl
import com.doctoror.particleswallpaper.presentation.compat.ViewCompat
import com.doctoror.particleswallpaper.presentation.preference.BackgroundImagePreference
import io.reactivex.disposables.Disposable

/**
 * Created by Yaroslav Mytkalyk on 28.05.17.
 */
class ConfigFragment(val ch: OnActivityResultCallbackHostImpl = OnActivityResultCallbackHostImpl())
    : PreferenceFragment(), OnActivityResultCallbackHost by ch {

    private val particlesDrawable = ParticlesDrawable()
    private var bgDisposable: Disposable? = null;

    private val settings: SettingsRepository by lazy {
        SettingsRepositoryFactory.provideSettingsRepository(activity)
    }

    private val configurator: DrawableConfigurator by lazy {
        DrawableConfiguratorFactory.provideDrawableConfigurator()
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.prefs)
        handlePreKitKat()
        setBackgroundImagePreferenceHost(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        setBackgroundImagePreferenceHost(null)
    }

    private fun handlePreKitKat() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            val p = findPreference(getString(R.string.pref_key_background_image))
            if (p != null) {
                p.isEnabled = false
                p.summary = getText(R.string.Supported_on_KitKat_and_later)
            }
        }
    }

    private fun setBackgroundImagePreferenceHost(host : Fragment?) {
        val p = findPreference(getString(R.string.pref_key_background_image))
        if (p is BackgroundImagePreference) {
            p.host = host
        }
    }

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        ViewCompat.setBackground(view, particlesDrawable)
        return view;
    }

    override fun onStart() {
        super.onStart()
        configurator.subscribe(particlesDrawable, settings)
        particlesDrawable.start()
    }

    override fun onStop() {
        super.onStop()
        particlesDrawable.stop()
        configurator.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ch.callbacks.forEach { it.onActivityResult(requestCode, resultCode, data) }
    }
}