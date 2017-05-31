package com.doctoror.particleswallpaper.presentation.preference

import android.content.Context
import android.util.AttributeSet
import com.doctoror.particleswallpaper.data.prefs.PrefsConfigPrefs
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryFactory
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository

/**
 * Created by Yaroslav Mytkalyk on 30.05.17.
 */
class ColorPreferenceImpl @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ColorPreferenceNoPreview(context, attrs), SettingsPreference {

    val settings: SettingsRepository = SettingsRepositoryFactory.provideSettingsRepository(context)

    init {
        isPersistent = false
        setDefaultValue(settings.getColor().blockingFirst())
        setOnPreferenceChangeListener({ _, v ->
            val color = v as? Int ?: PrefsConfigPrefs.color
            settings.setColor(color)
            true
        })
    }

    override fun getPersistedInt(defaultReturnValue: Int): Int {
        return settings.getColor().blockingFirst()
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        color = settings.getColor().blockingFirst()
    }

    override fun setDefaultValue() {
        color = PrefsConfigPrefs.color
    }
}