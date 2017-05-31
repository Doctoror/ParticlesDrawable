package com.doctoror.particleswallpaper.presentation.preference

import android.support.annotation.VisibleForTesting

/**
 * Created by Yaroslav Mytkalyk on 30.05.17.
 */
interface MapperSeekbarPreference<T> {

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun transformToRealValue(progress: Int): T

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun transformToProgress(value : T) : Int
}