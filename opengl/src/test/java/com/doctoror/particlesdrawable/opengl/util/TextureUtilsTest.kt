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
package com.doctoror.particlesdrawable.opengl.util

import android.graphics.Bitmap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class TextureUtilsTest {

    @Test
    fun doesNotScaleWhenAlreadyPot() {
        val input = Bitmap.createBitmap(32, 32, Bitmap.Config.RGB_565)

        val result = TextureUtils.scaleToSmallerPot(input)

        assertTrue(result === input)
    }

    @Test
    fun scalesToSmallerPotWhenOneSideNpot() {
        val input = Bitmap.createBitmap(32, 50, Bitmap.Config.RGB_565)

        val result = TextureUtils.scaleToSmallerPot(input)

        assertEquals(32, result.width)
        assertEquals(32, result.height)
    }

    @Test
    fun scalesToSmallerPotWhenTwoSidesNpot() {
        val input = Bitmap.createBitmap(144, 144, Bitmap.Config.RGB_565)

        val result = TextureUtils.scaleToSmallerPot(input)

        assertEquals(128, result.width)
        assertEquals(128, result.height)
    }
}
