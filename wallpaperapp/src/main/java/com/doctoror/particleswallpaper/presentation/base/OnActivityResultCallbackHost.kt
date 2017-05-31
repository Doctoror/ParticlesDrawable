package com.doctoror.particleswallpaper.presentation.base

import android.content.Intent

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 */
interface OnActivityResultCallbackHost {

    fun registerCallback(callback : OnActivityResultCallback)
    fun unregsiterCallback(callback: OnActivityResultCallback)
}