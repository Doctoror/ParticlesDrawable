package com.doctoror.particleswallpaper.presentation.preference

import android.app.Activity
import android.content.Context
import android.preference.Preference
import android.util.AttributeSet
import com.doctoror.particleswallpaper.presentation.presenter.PreviewPreferencePresenter

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 */
class PreviewPreference @JvmOverloads constructor
(contextParam: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : Preference(contextParam, attrs) {

    private val presenter = PreviewPreferencePresenter(contextParam as Activity)

    init {
        isPersistent = false
    }

    override fun onClick() {
        presenter.onClick()
    }
}