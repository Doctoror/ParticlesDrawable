package com.doctoror.particleswallpaper.presentation.preference

import android.content.Context
import android.util.AttributeSet
import com.doctoror.particleswallpaper.data.prefs.PrefsConfigPrefs
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryFactory
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository

/**
 * Created by Yaroslav Mytkalyk on 30.05.17.
 */
class DotScalePreference @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : SeekBarPreference(context, attrs), SettingsPreference, MapperSeekbarPreference<Float> {

    val settings: SettingsRepository = SettingsRepositoryFactory.provideSettingsRepository(context)

    init {
        max = 70
        isPersistent = false
        setDefaultValue(transformToProgress(settings.getDotScale().blockingFirst()))
        setOnPreferenceChangeListener({ _, v ->
            if (v is Int) {
                val value = transformToRealValue(v)
                settings.setDotScale(value)
            }
            true
        })
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        progress = transformToProgress(settings.getDotScale().blockingFirst())
    }

    override fun setDefaultValue() {
        progress = transformToProgress(PrefsConfigPrefs.dotScale)
    }

    override fun transformToRealValue(progress: Int) = progress.toFloat() / 5f + 0.5f
    override fun transformToProgress(value: Float) = ((value - 0.5f) * 5f).toInt()
}