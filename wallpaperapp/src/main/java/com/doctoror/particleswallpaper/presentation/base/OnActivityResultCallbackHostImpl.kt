package com.doctoror.particleswallpaper.presentation.base

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 */
class OnActivityResultCallbackHostImpl : OnActivityResultCallbackHost {

    val callbacks = mutableListOf<OnActivityResultCallback>()

    override fun registerCallback(callback: OnActivityResultCallback) {
        callbacks.add(callback)
    }

    override fun unregsiterCallback(callback: OnActivityResultCallback) {
        callbacks.remove(callback)
    }
}