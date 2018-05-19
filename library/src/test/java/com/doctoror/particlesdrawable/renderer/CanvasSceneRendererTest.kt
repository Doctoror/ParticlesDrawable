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
package com.doctoror.particlesdrawable.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class CanvasSceneRendererTest {

    private val underTest = CanvasSceneRenderer()

    @Test
    fun paintHasAntialias() {
        assertTrue(underTest.paint.isAntiAlias)
    }

    @Test
    fun appliesColorFilter() {
        // Given
        val colorFilter = PorterDuffColorFilter(0, PorterDuff.Mode.SRC_ATOP)

        // When
        underTest.setColorFilter(colorFilter)

        // Then
        assertTrue(colorFilter === underTest.paint.colorFilter)
    }

    @Test
    fun drawsLine() {
        // Given
        val canvas: Canvas = mock()

        val x1 = 1f
        val y1 = 2f

        val x2 = 3f
        val y2 = 4f

        val strokeWidth = 4f
        val color = Color.DKGRAY

        // When
        underTest.setCanvas(canvas)
        underTest.drawLine(x1, y1, x2, y2, strokeWidth, color)
        underTest.setCanvas(null)

        // Then
        assertEquals(strokeWidth, underTest.paint.strokeWidth)
        assertEquals(color, underTest.paint.color)
        verify(canvas).drawLine(x1, y1, x2, y2, underTest.paint)
    }

    @Test
    fun fillsCircle() {
        // Given
        val canvas: Canvas = mock()

        val x = 1f
        val y = 2f
        val radius = 3f

        val color = Color.CYAN

        // When
        underTest.setCanvas(canvas)
        underTest.fillCircle(x, y, radius, color)
        underTest.setCanvas(null)

        // Then
        assertEquals(color, underTest.paint.color)
        verify(canvas).drawCircle(x, y, radius, underTest.paint)
    }
}
