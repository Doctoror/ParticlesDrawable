package com.doctoror.particleswallpaper.presentation.preference

import android.app.AlertDialog
import android.content.Context
import android.preference.Preference
import android.util.AttributeSet
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryFactory
import com.doctoror.particleswallpaper.domain.interactor.ResetToDefaultsUseCase

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 */
class ResetToDefaultPreference @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : Preference(context, attrs) {

    init {
        isPersistent = false
    }

    override fun onClick() {
        super.onClick()
        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(R.string.Are_you_sure_you_want_to_reset_all_settings_to_default_values)
                .setPositiveButton(R.string.Reset, { _, _ -> onResetClick() })
                .setNegativeButton(R.string.Cancel, null)
                .show()
    }

    private fun onResetClick() {
        ResetToDefaultsUseCase(SettingsRepositoryFactory.provideMutable(context),
                SettingsRepositoryFactory.provideDefault())
                .useCase().subscribe()
    }
}