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

import android.opengl.GLSurfaceView
import com.nhaarman.mockito_kotlin.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.*
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay

private fun mockEglThatReturnsConfigForDisplayAndSpec(
    expectedDisplay: EGLDisplay,
    expectedSpec: IntArray?,
    expectedConfig: EGLConfig
): EGL10 = mock {
    on { it.eglChooseConfig(eq(expectedDisplay), eq(expectedSpec), eq(null), eq(0), any()) }
        .thenAnswer { invocation ->
            (invocation.arguments[4] as IntArray)[0] = 1
            true
        }

    on { it.eglChooseConfig(eq(expectedDisplay), eq(expectedSpec), any(), eq(1), any()) }
        .thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            (invocation.arguments[2] as Array<EGLConfig?>)[0] = expectedConfig
            true
        }
}

fun choosesConfigWithExpectedSpecAndNotifiesCallback(
    underTest: GLSurfaceView.EGLConfigChooser,
    expectedSpec: IntArray?,
    callback: EGLConfigChooserCallback
) {
    val expectedConfig: EGLConfig = mock()

    val display: EGLDisplay = mock()

    val egl: EGL10 = mockEglThatReturnsConfigForDisplayAndSpec(
        display,
        expectedSpec,
        expectedConfig
    )

    val result = underTest.chooseConfig(egl, display)
    Assertions.assertEquals(expectedConfig, result)
    verify(callback).onConfigChosen(expectedSpec)

    val specCaptor = argumentCaptor<IntArray>()
    verify(egl).eglChooseConfig(eq(display), specCaptor.capture(), eq(null), eq(0), any())

    if (expectedSpec == null) {
        assertNull(specCaptor.firstValue)
    } else {
        assertTrue(Arrays.equals(expectedSpec, specCaptor.firstValue))
    }
}
