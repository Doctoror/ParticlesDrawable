package com.doctoror.particleswallpaper.presentation.compat

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 */
class ViewCompat private constructor() {

    companion object {

        @JvmStatic fun setBackground(view: View, drawable: Drawable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.background = drawable
            } else {
                @Suppress("DEPRECATION")
                view.setBackgroundDrawable(drawable)
            }
        }
    }

}