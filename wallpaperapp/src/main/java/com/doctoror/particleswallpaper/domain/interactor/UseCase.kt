package com.doctoror.particleswallpaper.domain.interactor

import io.reactivex.Observable

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 */
interface UseCase<T> {

    fun useCase(): Observable<T>
}