package com.doctoror.particleswallpaper.domain.interactor

import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import io.reactivex.Observable

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 */
class ResetToDefaultsUseCase(val settings: MutableSettingsRepository,
                             val defaults: SettingsRepository) : UseCase<Unit> {

    override fun useCase() = Observable.fromCallable({ -> reset()})!!

    private fun reset() {
        settings.setParticlesColor(defaults.getParticlesColor().blockingFirst())
        settings.setBackgroundColor(defaults.getBackgroundColor().blockingFirst())
        settings.setBackgroundUri(defaults.getBackgroundUri().blockingFirst())
        settings.setNumDots(defaults.getNumDots().blockingFirst())

        settings.setDotScale(defaults.getDotScale().blockingFirst())
        settings.setLineScale(defaults.getLineScale().blockingFirst())
        settings.setLineDistance(defaults.getLineDistance().blockingFirst())

        settings.setStepMultiplier(defaults.getStepMultiplier().blockingFirst())
        settings.setFrameDelay(defaults.getFrameDelay().blockingFirst())
    }
}