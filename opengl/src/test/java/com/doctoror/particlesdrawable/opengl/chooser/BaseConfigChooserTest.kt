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

import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.*
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay

class BaseConfigChooserTest {

    private val egl: EGL10 = mock()
    private val display: EGLDisplay = mock()

    private val spec = intArrayOf(0)
    private val callback: EGLConfigChooserCallback = mock()
    private val underTest = BaseConfigChooser("tag", spec, callback)

    @Test
    fun throwsWhenChooseConfigFirstPassReturnsFalse() {
        assertThrows(IllegalArgumentException::class.java) {
            underTest.chooseConfig(egl, display)
        }

        verify(egl).eglChooseConfig(eq(display), eq(spec), eq(null), eq(0), any())
        verifyNoInteractions(callback)
    }

    @Test
    fun throwsWhenChooseConfigFirstPassSetsNegativeValue() {
        whenever(egl.eglChooseConfig(eq(display), eq(spec), eq(null), eq(0), any()))
            .thenAnswer { invocation ->
                (invocation.arguments[4] as IntArray)[0] = -1
                true
            }

        assertThrows(IllegalArgumentException::class.java) {
            underTest.chooseConfig(egl, display)
        }

        verifyNoInteractions(callback)
    }

    @Test
    fun throwsWhenChooseConfigFirstPassSets0() {
        whenever(egl.eglChooseConfig(eq(display), eq(spec), eq(null), eq(0), any()))
            .thenAnswer { invocation ->
                (invocation.arguments[4] as IntArray)[0] = 0
                true
            }

        assertThrows(IllegalArgumentException::class.java) {
            underTest.chooseConfig(egl, display)
        }

        verifyNoInteractions(callback)
    }

    @Test
    fun throwsWhenChooseConfigSecondPassReturnsFalse() {
        givenFirstPassReturnsOneConfigAvailable()

        assertThrows(IllegalArgumentException::class.java) {
            underTest.chooseConfig(egl, display)
        }

        verifyNoInteractions(callback)
    }

    @Test
    fun throwsWhenChooseConfigSecondPassSetsNoConfig() {
        givenFirstPassReturnsOneConfigAvailable()

        // Second pass
        whenever(egl.eglChooseConfig(eq(display), eq(spec), any(), eq(0), any()))
            .thenReturn(true)

        assertThrows(IllegalArgumentException::class.java) {
            underTest.chooseConfig(egl, display)
        }

        verifyNoInteractions(callback)
    }

    @Test
    fun throwsWhenChooseConfigSecondPassSetsNullValue() {
        givenFirstPassReturnsOneConfigAvailable()

        // Second pass
        whenever(egl.eglChooseConfig(eq(display), eq(spec), any(), eq(1), any()))
            .thenAnswer { invocation ->
                @Suppress("UNCHECKED_CAST")
                (invocation.arguments[2] as Array<EGLConfig?>)[0] = null
                true
            }

        assertThrows(IllegalArgumentException::class.java) {
            underTest.chooseConfig(egl, display)
        }

        verifyNoInteractions(callback)
    }

    @Test
    fun returnsConfigWhenResolved() {
        givenFirstPassReturnsOneConfigAvailable()

        val config: EGLConfig = mock()

        // Second pass
        whenever(egl.eglChooseConfig(eq(display), eq(spec), any(), eq(1), any()))
            .thenAnswer { invocation ->
                @Suppress("UNCHECKED_CAST")
                (invocation.arguments[2] as Array<EGLConfig?>)[0] = config
                true
            }

        val result = underTest.chooseConfig(egl, display)

        assertTrue(result === config)

        verify(callback).onConfigChosen(spec)
    }

    private fun givenFirstPassReturnsOneConfigAvailable() {
        whenever(egl.eglChooseConfig(eq(display), eq(spec), eq(null), eq(0), any()))
            .thenAnswer { invocation ->
                (invocation.arguments[4] as IntArray)[0] = 1
                true
            }
    }
}
