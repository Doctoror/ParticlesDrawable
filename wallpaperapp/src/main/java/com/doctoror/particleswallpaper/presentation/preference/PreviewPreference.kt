package com.doctoror.particleswallpaper.presentation.preference

import android.app.Activity
import android.content.Context
import android.preference.Preference
import android.util.AttributeSet
import android.widget.Toast
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.domain.interactor.SetWallpaperUseCase

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 */
class PreviewPreference @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : Preference(context, attrs) {

    private val requestCodeSetWallpaper = 1;

    init {
        isPersistent = false
    }

    override fun onClick() {
        super.onClick()
        val context = context
        if (context is Activity) {
            SetWallpaperUseCase(context, requestCodeSetWallpaper).useCase().subscribe({
                v ->
                if (!v) Toast.makeText(context, R.string.Failed_to_start_preview, Toast.LENGTH_LONG).show()
            })
        }
    }
}