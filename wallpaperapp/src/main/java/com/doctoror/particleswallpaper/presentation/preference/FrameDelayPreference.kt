package com.doctoror.particleswallpaper.presentation.preference

import android.content.Context
import android.util.AttributeSet
import com.doctoror.particleswallpaper.data.prefs.PrefsConfigPrefs
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryFactory
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository

/**
 * Created by Yaroslav Mytkalyk on 30.05.17.
 */
class FrameDelayPreference @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : SeekBarPreference(context, attrs), SettingsPreference, MapperSeekbarPreference<Int> {

    val settings: SettingsRepository = SettingsRepositoryFactory.provideSettingsRepository(context)

    val frameDelaySeekbarMin = 10

    init {
        max = 80
        isPersistent = false
        setDefaultValue(transformToProgress(settings.getFrameDelay().blockingFirst()))
        setOnPreferenceChangeListener({ _, v ->
            if (v is Int) {
                val value = transformToRealValue(v)
                settings.setFrameDelay(value)
            }
            true
        })
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        progress = transformToProgress(settings.getFrameDelay().blockingFirst())
    }

    override fun setDefaultValue() {
        progress = transformToProgress(PrefsConfigPrefs.frameDelay)
    }


    /**
     * The seek bar represents frame rate as percentage.
     * Converts the seek bar value between 0 and 30 to percent and then the percentage to a
     * frame delay, where
     * 10 ms = 100%
     * 40 ms = 0%
     */
    override fun transformToRealValue(progress: Int) = (frameDelaySeekbarMin.toFloat()
            + max.toFloat() * (1f - progress.toFloat() / max.toFloat())).toInt()

    /**
     * Converts frame delay to seek bar frame rate.
     * @see transformToRealValue
     */
    override fun transformToProgress(value: Int): Int {
        val percent = (value.toFloat() - frameDelaySeekbarMin.toFloat()) / max.toFloat()
        return ((1f - percent) * max.toFloat()).toInt()
    }
}