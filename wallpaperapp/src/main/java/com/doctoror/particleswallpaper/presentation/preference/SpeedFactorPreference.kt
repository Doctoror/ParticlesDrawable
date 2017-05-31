package com.doctoror.particleswallpaper.presentation.preference

import android.content.Context
import android.util.AttributeSet
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryFactory
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository

/**
 * Created by Yaroslav Mytkalyk on 30.05.17.
 */
class SpeedFactorPreference @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : SeekBarPreference(context, attrs), MapperSeekbarPreference<Float> {

    val settings: SettingsRepository = SettingsRepositoryFactory.provideSettingsRepository(context)

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
    override fun transformToProgress(value : Float) = ((value - 0.1f) * 10f).toInt()
}