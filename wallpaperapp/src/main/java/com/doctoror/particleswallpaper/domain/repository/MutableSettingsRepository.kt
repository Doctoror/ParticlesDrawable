package com.doctoror.particleswallpaper.domain.repository

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 */
interface MutableSettingsRepository: SettingsRepository {

    fun setNumDots(numDots: Int)
    fun setFrameDelay(frameDelay: Int)
    fun setStepMultiplier(stepMultiplier: Float)
    fun setDotScale(dotScale: Float)
    fun setLineScale(lineScale: Float)
    fun setLineDistance(lineDistance: Float)
    fun setColor(color: Int)
    fun setBackgroundUri(uri: String)
}