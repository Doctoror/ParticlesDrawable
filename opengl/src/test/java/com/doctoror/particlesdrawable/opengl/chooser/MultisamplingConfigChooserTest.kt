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
package com.doctoror.particlesdrawable.opengl.chooser

import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import javax.microedition.khronos.egl.EGL10

class MultisamplingConfigChooserTest {

    @Test
    fun throwsWhenNegativeValuePassed() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MultisamplingConfigChooser(-1, null)
        }
    }

    @Test
    fun throwsWhenZeroPassed() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MultisamplingConfigChooser(0, null)
        }
    }

    @Test
    fun throwsWhenOnePassed() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MultisamplingConfigChooser(1, null)
        }
    }

    @Test
    fun throwsWhenNonPowerOfTwoPassed() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MultisamplingConfigChooser(3, null)
        }

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            MultisamplingConfigChooser(6, null)
        }
    }

    @Test
    fun choosesConfigWithExpectedSpecAndNotifiesCallback() {
        val callback: EGLConfigChooserCallback = mock()
        val underTest = MultisamplingConfigChooser(4, callback)
        val expectedSpec = intArrayOf(
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_DEPTH_SIZE, 0,
            EGL10.EGL_SAMPLE_BUFFERS, 1,
            EGL10.EGL_SAMPLES, 4,
            EGL10.EGL_RENDERABLE_TYPE, 4,
            EGL10.EGL_NONE
        )

        choosesConfigWithExpectedSpecAndNotifiesCallback(underTest, expectedSpec, callback)
    }
}
