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

import android.opengl.GLSurfaceView.EGLConfigChooser
import com.doctoror.particlesdrawable.opengl.chooser.CompositeConfigChooser.log
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay

class CompositeConfigChooserTest {

    private val egl: EGL10 = mock()
    private val display: EGLDisplay = mock()

    @Test
    fun throwsWhenEmptyArgumentsPassed() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            CompositeConfigChooser()
        }
    }

    @Test
    fun throwsWhenChoosersFailedToChooseDueToNullValue() {
        Assertions.assertThrows(NoMatchingConfigsException::class.java) {
            val underTest = CompositeConfigChooser(mock()).apply { log = false }
            underTest.chooseConfig(egl, display)
        }
    }

    @Test
    fun throwsWhenChoosersFailedToChooseDueToException() {
        Assertions.assertThrows(NoMatchingConfigsException::class.java) {
            val underTest = CompositeConfigChooser(mock {
                on { it.chooseConfig(egl, display) }.thenThrow(IllegalArgumentException())
            }).apply { log = false }
            underTest.chooseConfig(egl, display)
        }
    }

    @Test
    fun iteratesOverChoosersUntilChooserSucceeds() {
        val config: EGLConfig = mock()

        val choosers = arrayOf<EGLConfigChooser>(
            mock {
                on { it.chooseConfig(egl, display) }.thenThrow(IllegalArgumentException())
            },
            mock {
                on { it.chooseConfig(egl, display) }.thenThrow(IllegalArgumentException())
            },
            mock {
                on { it.chooseConfig(egl, display) }.thenReturn(config)
            },
            mock {
                on { it.chooseConfig(egl, display) }.thenThrow(IllegalArgumentException())
            }
        )

        val underTest = CompositeConfigChooser(*choosers).apply { log = false }
        val result = underTest.chooseConfig(egl, display)

        assertTrue(result === config)
    }
}
