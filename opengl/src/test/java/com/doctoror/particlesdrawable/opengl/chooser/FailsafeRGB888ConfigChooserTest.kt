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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FailsafeRGB888ConfigChooserTest {

    @Test
    fun hasValidChooserList() {
        val underTest = FailsafeRGB888ConfigChooser(null)
        assertEquals(2, underTest.choosers.size)
        assertTrue(underTest.choosers[0] is RGB888ConfigChooser)
        assertTrue(underTest.choosers[1] is AnyConfigChooser)
    }
}
