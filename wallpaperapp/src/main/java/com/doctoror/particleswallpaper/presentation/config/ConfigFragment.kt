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

import com.doctoror.particlesdrawable.ParticlesDrawable
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.data.config.DrawableConfiguratorFactory
import com.doctoror.particleswallpaper.data.mapper.DotRadiusMapper
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryFactory
import com.doctoror.particleswallpaper.domain.config.DrawableConfigurator
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.preference.SeekBarPreference

/**
 * Created by Yaroslav Mytkalyk on 28.05.17.
 */
class ConfigFragment : android.preference.PreferenceFragment() {

    private val particlesDrawable = ParticlesDrawable()

    private val prefNumDots: SeekBarPreference by lazy {
        findPreference(getString(R.string.pref_key_num_dots)) as SeekBarPreference
    }

    private val prefStepMultiplier: SeekBarPreference by lazy {
        findPreference(getString(R.string.pref_key_step_multiplier)) as SeekBarPreference
    }

    private val prefFrameDelay: SeekBarPreference by lazy {
        findPreference(getString(R.string.pref_key_frame_delay)) as SeekBarPreference
    }

    private val prefDotScale: SeekBarPreference by lazy {
        findPreference(getString(R.string.pref_key_dot_scale)) as SeekBarPreference
    }

    private val prefLineScale: SeekBarPreference by lazy {
        findPreference(getString(R.string.pref_key_line_scale)) as SeekBarPreference
    }

    private val prefLineDistance: SeekBarPreference by lazy {
        findPreference(getString(R.string.pref_key_line_distance)) as SeekBarPreference
    }

    private val settings: SettingsRepository by lazy {
        SettingsRepositoryFactory.provideSettingsRepository(activity)
    }

    private val configurator: DrawableConfigurator by lazy {
        DrawableConfiguratorFactory.provideDrawableConfigurator()
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(com.doctoror.particleswallpaper.R.xml.prefs)
        bindPrefs()
        initPrefs()
    }

    fun bindPrefs() {
        prefNumDots.progress = ConfigMapper.toSeekbarNumDots(settings.getNumDots())
        prefDotScale.progress = ConfigMapper.toSeekbarDotScale(settings.getDotScale())
        prefLineScale.progress = ConfigMapper.toSeekbarLineScale(settings.getLineScale())
        prefLineDistance.progress = ConfigMapper.toSeekbarLineDistance(settings.getLineDistance())
        prefStepMultiplier.progress = ConfigMapper.toSeekbarStepMultiplier(settings.getStepMultiplier())
        prefFrameDelay.progress = ConfigMapper.toSeekbarFrameRate(settings.getFrameDelay())
    }

    fun initPrefs() {
        prefNumDots.setOnPreferenceChangeListener({ p, v ->
            if (v is Int) {
                val value = ConfigMapper.toNumDots(v)
                particlesDrawable.setNumDots(value)
                particlesDrawable.makeBrandNewFrame()
                settings.setNumDots(value)
            }
            true
        })

        prefDotScale.setOnPreferenceChangeListener({ p, v ->
            if (v is Int) {
                val value = ConfigMapper.toDotScale(v)
                val radiusRange = DotRadiusMapper.transform(value)
                particlesDrawable.setDotRadiusRange(radiusRange.first, radiusRange.second)
                particlesDrawable.makeBrandNewFrame()
                settings.setDotScale(value)
            }
            true
        })

        prefLineScale.setOnPreferenceChangeListener({ p, v ->
            if (v is Int) {
                val value = ConfigMapper.toLineScale(v)
                particlesDrawable.setLineThickness(value)
                particlesDrawable.makeBrandNewFrame()
                settings.setLineScale(value)
            }
            true
        })

        prefLineDistance.setOnPreferenceChangeListener({ p, v ->
            if (v is Int) {
                val value = ConfigMapper.toLineDistance(v)
                particlesDrawable.setLineDistance(value)
                settings.setLineDistance(value)
            }
            true
        })

        prefStepMultiplier.setOnPreferenceChangeListener({ p, v ->
            if (v is Int) {
                val value = ConfigMapper.toStepMultiplier(v)
                particlesDrawable.setStepMultiplier(value)
                settings.setStepMultiplier(value)
            }
            true
        })

        prefFrameDelay.setOnPreferenceChangeListener({ p, v ->
            if (v is Int) {
                val value = ConfigMapper.toFrameDelay(v)
                particlesDrawable.setFrameDelay(value)
                settings.setFrameDelay(value)
            }
            true
        })
    }

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view.setBackgroundDrawable(particlesDrawable)
        return view;
    }

    override fun onStart() {
        super.onStart()
        bindPrefs()
        configurator.configure(particlesDrawable, settings)
        particlesDrawable.start()
    }

    override fun onStop() {
        super.onStop()
        particlesDrawable.stop()
    }


}