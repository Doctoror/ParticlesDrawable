package com.doctoror.particleswallpaper.presentation.preference

import android.content.Context
import android.util.AttributeSet
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryFactory
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository

/**
 * Created by Yaroslav Mytkalyk on 30.05.17.
 */
class LineDistancePreference @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : SeekBarPreference(context, attrs), MapperSeekbarPreference<Float> {

    val settings: SettingsRepository = SettingsRepositoryFactory.provideSettingsRepository(context)

    init {
        max = 100
        isPersistent = false
        setDefaultValue(transformToProgress(settings.getLineDistance().blockingFirst()))
        setOnPreferenceChangeListener({ _, v ->
            if (v is Int) {
                val value = transformToRealValue(v)
                settings.setLineDistance(value)
            }
            true
        })
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        progress = transformToProgress(settings.getLineDistance().blockingFirst())
    }

    override fun transformToRealValue(progress: Int) = progress.toFloat() * 3f
    override fun transformToProgress(value : Float) = (value / 3f).toInt()
}