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

import org.junit.Test
import org.junit.Assert.*

class ConfigMapperTest {

    @Test
    fun testToFrameDelay() {
        testToFrameDelay(80, 10)
        testToFrameDelay(0, 90)
    }

    fun testToFrameDelay(seekBarValue : Int, expectedDelay : Int) {
        val actualDelay = ConfigMapper.toFrameDelay(seekBarValue)
        assertEquals(expectedDelay, actualDelay)
    }

    @Test
    fun testToSeekbarFrameRate() {
        testToSeekbarFrameRate(10, 80)
        testToSeekbarFrameRate(90, 0)
    }

    fun testToSeekbarFrameRate(expectedSeekBarValue : Int, delay : Int) {
        val seekbarValue = ConfigMapper.toSeekbarFrameRate(delay)
        assertEquals(expectedSeekBarValue, seekbarValue)
    }

}
