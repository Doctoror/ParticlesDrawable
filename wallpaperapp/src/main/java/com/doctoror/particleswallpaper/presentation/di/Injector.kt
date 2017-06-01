package com.doctoror.particleswallpaper.presentation.di

import com.doctoror.particleswallpaper.presentation.di.components.ConfigComponent

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 */
class Injector private constructor() {

    init {
        throw UnsupportedOperationException()
    }

    companion object {
        lateinit var configComponent: ConfigComponent
    }

}