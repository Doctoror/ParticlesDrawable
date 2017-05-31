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
package com.doctoror.particleswallpaper.presentation.preference

import android.content.Context
import android.util.AttributeSet
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryFactory
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository

/**
 * Created by Yaroslav Mytkalyk on 30.05.17.
 */
class SpeedFactorPreference @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : SeekBarPreference(context, attrs, defStyle), MapperSeekbarPreference<Float> {

    val settings: MutableSettingsRepository = SettingsRepositoryFactory.provideMutable(context)

    init {
        isPersistent = false
        max = 40
        setDefaultValue(transformToProgress(settings.getStepMultiplier().blockingFirst()))
        setOnPreferenceChangeListener({ _, v ->
            if (v is Int) {
                val value = transformToRealValue(v)
                settings.setStepMultiplier(value)
            }
            true
        })
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        progress = transformToProgress(settings.getStepMultiplier().blockingFirst())
    }

    override fun transformToRealValue(progress: Int) = progress.toFloat() / 10f + 0.1f
    override fun transformToProgress(value: Float) = ((value - 0.1f) * 10f).toInt()
}