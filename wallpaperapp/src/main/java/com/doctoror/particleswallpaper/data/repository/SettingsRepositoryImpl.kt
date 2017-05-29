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

/**
 * Created by Yaroslav Mytkalyk on 28.05.17.
 */
class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    val prefs = Prefs.with(context)!!

    override fun getNumDots() = prefs.numDots

    override fun setNumDots(numDots: Int) {
        prefs.numDots = numDots
    }

    override fun getFrameDelay() = prefs.frameDelay;

    override fun setFrameDelay(frameDelay: Int) {
        prefs.frameDelay = frameDelay;
    }

    override fun getStepMultiplier() = prefs.stepMultiplier;

    override fun setStepMultiplier(stepMultiplier: Float) {
        prefs.stepMultiplier = stepMultiplier;
    }

    override fun getDotScale(): Float {
        val scale = prefs.dotScale
        return if (scale != 0f) scale
        else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, Resources.getSystem().displayMetrics)
    }

    override fun setDotScale(dotScale: Float) {
        prefs.dotScale = dotScale
    }

    override fun getLineScale(): Float {
        val scale = prefs.lineScale
        return if (scale != 0f) scale
        else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, Resources.getSystem().displayMetrics)
    }

    override fun setLineScale(lineScale: Float) {
        prefs.lineScale = lineScale
    }

    override fun getLineDistance(): Float {
        val value = prefs.lineDistance
        return if (value != 0f) value
        else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 86f, Resources.getSystem().displayMetrics)
    }

    override fun setLineDistance(lineDistance: Float) {
        prefs.lineDistance = lineDistance
    }
}