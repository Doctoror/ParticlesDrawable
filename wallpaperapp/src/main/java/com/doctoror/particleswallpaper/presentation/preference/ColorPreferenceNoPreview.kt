package com.doctoror.particleswallpaper.presentation.preference

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.rarepebble.colorpicker.ColorPreference

/**
 * Created by Yaroslav Mytkalyk on 29.05.17.
 */
class ColorPreferenceNoPreview @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ColorPreference(context, attrs) {

    override fun onBindView(view: View?) {
        super.onBindView(view)
        if (view != null) {
            val widgetFrameView = view.findViewById(android.R.id.widget_frame) as ViewGroup
            widgetFrameView.visibility = View.GONE
            widgetFrameView.removeAllViews()
        }
    }
}