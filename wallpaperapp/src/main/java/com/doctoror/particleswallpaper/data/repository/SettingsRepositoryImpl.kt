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
import android.content.res.Resources
import android.util.TypedValue
import com.doctoror.particleswallpaper.data.prefs.Prefs
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by Yaroslav Mytkalyk on 28.05.17.
 */
class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    val colorSubject = BehaviorSubject.create<Int>()!!
    val numDotsSubject = BehaviorSubject.create<Int>()!!
    val frameDelaySubject = BehaviorSubject.create<Int>()!!
    val stepMultiplierSubject = BehaviorSubject.create<Float>()!!
    val dotScaleSubject = BehaviorSubject.create<Float>()!!
    val lineScaleSubject = BehaviorSubject.create<Float>()!!
    val lineDistanceSubject = BehaviorSubject.create<Float>()!!

    val prefs = Prefs.with(context)!!

    init {
        colorSubject.onNext(prefs.color)
        numDotsSubject.onNext(prefs.numDots)
        frameDelaySubject.onNext(prefs.frameDelay)
        stepMultiplierSubject.onNext(prefs.stepMultiplier)

        val scale = prefs.dotScale
        dotScaleSubject.onNext(if (scale != 0f) scale
        else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, Resources.getSystem().displayMetrics))

        val lineScale = prefs.lineScale
        lineScaleSubject.onNext(if (lineScale != 0f) lineScale
        else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, Resources.getSystem().displayMetrics))

        val lineDistance = prefs.lineDistance
        lineDistanceSubject.onNext(if (lineDistance != 0f) lineDistance
        else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 86f, Resources.getSystem().displayMetrics))
    }

    override fun getNumDots() = numDotsSubject

    override fun setNumDots(numDots: Int) {
        prefs.numDots = numDots
        numDotsSubject.onNext(numDots)
    }

    override fun getFrameDelay() = frameDelaySubject

    override fun setFrameDelay(frameDelay: Int) {
        prefs.frameDelay = frameDelay;
        frameDelaySubject.onNext(frameDelay)
    }

    override fun getStepMultiplier() = stepMultiplierSubject

    override fun setStepMultiplier(stepMultiplier: Float) {
        prefs.stepMultiplier = stepMultiplier;
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

    override fun getColor() = colorSubject

    override fun setColor(color: Int) {
        prefs.color = color
        colorSubject.onNext(color)
    }
}