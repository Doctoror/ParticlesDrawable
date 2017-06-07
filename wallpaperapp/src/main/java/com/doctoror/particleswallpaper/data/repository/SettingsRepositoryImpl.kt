/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doctoror.particleswallpaper.data.repository

import android.content.Context
import android.support.annotation.ColorInt
import com.doctoror.particleswallpaper.data.prefs.Prefs
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by Yaroslav Mytkalyk on 28.05.17.
 */
class SettingsRepositoryImpl(context: Context) : MutableSettingsRepository {

    val defaults = SettingsRepositoryDefault(context.resources)

    val colorSubject = BehaviorSubject.create<Int>()!!
    val backgroundColorSubject = BehaviorSubject.create<Int>()!!
    val backgroundUriSubject = BehaviorSubject.create<String>()!!
    val numDotsSubject = BehaviorSubject.create<Int>()!!
    val frameDelaySubject = BehaviorSubject.create<Int>()!!
    val stepMultiplierSubject = BehaviorSubject.create<Float>()!!
    val dotScaleSubject = BehaviorSubject.create<Float>()!!
    val lineScaleSubject = BehaviorSubject.create<Float>()!!
    val lineDistanceSubject = BehaviorSubject.create<Float>()!!

    val prefs = Prefs.with(context)!!

    init {
        colorSubject.onNext(prefs.particlesColor)
        backgroundColorSubject.onNext(prefs.backgroundColor)
        backgroundUriSubject.onNext(prefs.backgroundUri)

        val numDots = prefs.numDots
        numDotsSubject.onNext(if (numDots != -1) numDots
        else defaults.getNumDots().blockingFirst())

        val frameDelay = prefs.frameDelay
        frameDelaySubject.onNext(if (frameDelay != -1) frameDelay
        else defaults.getFrameDelay().blockingFirst())

        val stepMultiplier = prefs.stepMultiplier
        stepMultiplierSubject.onNext(if (stepMultiplier != -1f) stepMultiplier
        else defaults.getStepMultiplier().blockingFirst())

        val scale = prefs.dotScale
        dotScaleSubject.onNext(if (scale != -1f) scale
        else defaults.getDotScale().blockingFirst())

        val lineScale = prefs.lineScale
        lineScaleSubject.onNext(if (lineScale != -1f) lineScale
        else defaults.getLineScale().blockingFirst())

        val lineDistance = prefs.lineDistance
        lineDistanceSubject.onNext(if (lineDistance != -1f) lineDistance
        else defaults.getLineDistance().blockingFirst())
    }

    override fun getNumDots() = numDotsSubject

    override fun setNumDots(numDots: Int) {
        prefs.numDots = numDots
        numDotsSubject.onNext(numDots)
    }

    override fun getFrameDelay() = frameDelaySubject

    override fun setFrameDelay(frameDelay: Int) {
        prefs.frameDelay = frameDelay
        frameDelaySubject.onNext(frameDelay)
    }

    override fun getStepMultiplier() = stepMultiplierSubject

    override fun setStepMultiplier(stepMultiplier: Float) {
        prefs.stepMultiplier = stepMultiplier
        stepMultiplierSubject.onNext(stepMultiplier)
    }

    override fun getDotScale() = dotScaleSubject

    override fun setDotScale(dotScale: Float) {
        prefs.dotScale = dotScale
        dotScaleSubject.onNext(dotScale)
    }

    override fun getLineScale() = lineScaleSubject

    override fun setLineScale(lineScale: Float) {
        prefs.lineScale = lineScale
        lineScaleSubject.onNext(lineScale)
    }

    override fun getLineDistance() = lineDistanceSubject

    override fun setLineDistance(lineDistance: Float) {
        prefs.lineDistance = lineDistance
        lineDistanceSubject.onNext(lineDistance)
    }

    override fun getParticlesColor() = colorSubject

    override fun setParticlesColor(color: Int) {
        prefs.particlesColor = color
        colorSubject.onNext(color)
    }

    override fun setBackgroundColor(@ColorInt color: Int) {
        prefs.backgroundColor = color
        backgroundColorSubject.onNext(color)
    }

    override fun getBackgroundColor() = backgroundColorSubject

    override fun getBackgroundUri() = backgroundUriSubject

    override fun setBackgroundUri(uri: String) {
        prefs.backgroundUri = uri
        backgroundUriSubject.onNext(uri)
    }
}