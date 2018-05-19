/*
 * Copyright (C) 2018 Yaroslav Mytkalyk
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
package com.doctoror.particlesdrawable.util

import android.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class LineColorResolverTest {

    @Test
    fun lineColorIs0ForTransparentAlpha() {
        val color = 0xff666666.toInt()
        val expectedColor = 0x00666666

        val result = LineColorResolver.resolveLineColorWithAlpha(
                0,
                color,
                10f,
                0f)

        assertEquals(expectedColor, result)
    }

    @Test
    fun lineColorIsUnchangedForMaxDistanceDeltaAndOpaqueAlpha() {
        val lineColor = Color.DKGRAY

        val result = LineColorResolver.resolveLineColorWithAlpha(
                255,
                lineColor,
                10f,
                0f)

        assertEquals(lineColor, result)
    }

    @Test
    fun lineColorIsTransparentForZeroDistanceDeltaAndOpaqueAlpha() {
        val color = 0xff112233.toInt()
        val expectedColor = 0x00112233

        val result = LineColorResolver.resolveLineColorWithAlpha(
                255,
                color,
                10f,
                10f)

        assertEquals(expectedColor, result)
    }

    @Test
    fun resolvesLineColorForArbitraryDeltaAndAlpha() {
        val color = 0xff112233.toInt()
        val expectedColor = 0x2d112233

        val result = LineColorResolver.resolveLineColorWithAlpha(
                184,
                color,
                128f,
                96f)

        assertEquals(expectedColor, result)
    }
}
