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
package com.doctoror.particleswallpaper.presentation.config

/**
 * Created by Yaroslav Mytkalyk on 29.05.17.
 */
class ConfigMapper {

    companion object {

        @JvmStatic val frameDelaySeekbarMin = 10f
        @JvmStatic val frameDelaySeekbarMax = 80f

        @JvmStatic fun toNumDots(seekbarNumDots : Int) = seekbarNumDots + 1
        @JvmStatic fun toSeekbarNumDots(numDots : Int) = numDots - 1

        @JvmStatic fun toStepMultiplier(seekbarValue : Int) = seekbarValue / 10f + 0.1f
        @JvmStatic fun toSeekbarStepMultiplier(value : Float) = ((value - 0.1f) * 10f).toInt()

        @JvmStatic fun toDotScale(seekbarValue : Int) = seekbarValue / 5f + 0.5f
        @JvmStatic fun toSeekbarDotScale(value : Float) = ((value - 0.5f) * 5f).toInt()

        @JvmStatic fun toLineScale(seekbarValue : Int) = seekbarValue / 5f + 1f
        @JvmStatic fun toSeekbarLineScale(value : Float) = ((value - 1f) * 5f).toInt()

        @JvmStatic fun toLineDistance(seekbarValue : Int) = seekbarValue * 3f
        @JvmStatic fun toSeekbarLineDistance(value : Float) = (value / 3f).toInt()

        /**
         * The seek bar represents frame rate as percentage.
         * Converts the seek bar value between 0 and 30 to percent and then the percentage to a
         * frame delay, where
         * 10 ms = 100%
         * 40 ms = 0%
         */
        @JvmStatic fun toFrameDelay(seekbarValue : Int) = (frameDelaySeekbarMin
                + frameDelaySeekbarMax * (1f - seekbarValue / frameDelaySeekbarMax)).toInt()

        /**
         * Converts frame delay to seek bar frame rate.
         * @see toFrameDelay
         */
        @JvmStatic fun toSeekbarFrameRate(value : Int) : Int {
            val percent = (value.toFloat() - frameDelaySeekbarMin) / frameDelaySeekbarMax
            return ((1 - percent) * frameDelaySeekbarMax).toInt()
        }

    }
}