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

import android.graphics.Color
import com.doctoror.particlesdrawable.ParticlesScene
import com.doctoror.particlesdrawable.contract.LowLevelRenderer
import com.doctoror.particlesdrawable.util.DistanceResolver
import com.doctoror.particlesdrawable.util.LineColorResolver
import com.doctoror.particlesdrawable.util.ParticleColorResolver
import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.nio.FloatBuffer

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class DefaultSceneRendererTest {

    private val lowLevelRenderer: LowLevelRenderer = mock()
    private val underTest = DefaultSceneRenderer(lowLevelRenderer)

    @Test
    fun doesNotRenderSceneWithZeroParticles() {
        // When
        underTest.drawScene(mock())

        // Then
        verify(lowLevelRenderer, never()).drawLine(
                any(), any(), any(), any(), any(), any())

        verify(lowLevelRenderer, never()).fillCircle(
                any(), any(), any(), any())
    }

    @Test
    fun rendersSceneWithOneParticle() {
        val radius = 1.5f
        val radiuses = FloatBuffer.allocate(1)
        radiuses.put(radius)

        val color = Color.CYAN

        val x = 1f
        val y = 2f

        val scene: ParticlesScene = mock {
            on { it.numDots }.doReturn(1)
            on { it.radiuses }.doReturn(radiuses)
            on { it.dotColor }.doReturn(color)
            on { it.getParticleX(0) }.doReturn(x)
            on { it.getParticleY(0) }.doReturn(y)
        }

        // When
        underTest.drawScene(scene)

        // Then
        verify(lowLevelRenderer, never()).drawLine(
                any(), any(), any(), any(), any(), any())

        val expectedColor = ParticleColorResolver.resolveParticleColorWithSceneAlpha(
                scene.dotColor,
                scene.alpha
        )

        verify(lowLevelRenderer).fillCircle(
                x, y, radius, expectedColor)
    }

    @Test
    fun rendersSceneWithTwoParticlesNoLines() {
        val radius1 = 1.5f
        val radius2 = 2f

        val radiuses = FloatBuffer.allocate(2)
        radiuses.put(radius1)
        radiuses.put(radius2)

        val color = Color.DKGRAY

        val x1 = 1f
        val y1 = 2f

        val x2 = 96f
        val y2 = 93f

        val scene: ParticlesScene = mock {
            on { it.numDots }.doReturn(2)

            on { it.radiuses }.doReturn(radiuses)
            on { it.dotColor }.doReturn(color)

            on { it.getParticleX(0) }.doReturn(x1)
            on { it.getParticleY(0) }.doReturn(y1)
            on { it.getParticleX(1) }.doReturn(x2)
            on { it.getParticleY(1) }.doReturn(y2)
        }

        // When
        underTest.drawScene(scene)

        // Then
        verify(lowLevelRenderer, never()).drawLine(
                any(), any(), any(), any(), any(), any())

        val expectedColor = ParticleColorResolver.resolveParticleColorWithSceneAlpha(
                scene.dotColor,
                scene.alpha
        )

        verify(lowLevelRenderer).fillCircle(
                x1, y1, radius1, expectedColor)

        verify(lowLevelRenderer).fillCircle(
                x2, y2, radius2, expectedColor)
    }

    @Test
    fun rendersSceneWithTwoParticlesWithLines() {
        val radius1 = 1.1f
        val radius2 = 2.1f

        val radiuses = FloatBuffer.allocate(2)
        radiuses.put(radius1)
        radiuses.put(radius2)

        val particleColor = Color.DKGRAY
        val lineColor = 0xff334455.toInt()

        val lineWidth = 2f

        val x1 = -3.1f
        val y1 = 5.8f

        val x2 = 68.1f
        val y2 = 98.9f

        val scene: ParticlesScene = mock {
            on { it.numDots }.doReturn(2)

            on { it.radiuses }.doReturn(radiuses)
            on { it.dotColor }.doReturn(particleColor)

            on { it.lineDistance }.doReturn(666f)
            on { it.lineThickness }.doReturn(lineWidth)

            on { it.lineColor }.doReturn(lineColor)
            on { it.alpha }.doReturn(255)

            on { it.getParticleX(0) }.doReturn(x1)
            on { it.getParticleY(0) }.doReturn(y1)
            on { it.getParticleX(1) }.doReturn(x2)
            on { it.getParticleY(1) }.doReturn(y2)
        }

        // When
        underTest.drawScene(scene)

        // Then
        val expectedLineColor = LineColorResolver.resolveLineColorWithAlpha(
                scene.alpha,
                scene.lineColor,
                scene.lineDistance,
                DistanceResolver.distance(x1, y1, x2, y2))

        verify(lowLevelRenderer).drawLine(
                x1, y1, x2, y2, lineWidth, expectedLineColor)

        val expectedColor = ParticleColorResolver.resolveParticleColorWithSceneAlpha(
                scene.dotColor,
                scene.alpha
        )

        verify(lowLevelRenderer).fillCircle(
                x1, y1, radius1, expectedColor)

        verify(lowLevelRenderer).fillCircle(
                x2, y2, radius2, expectedColor)
    }

    @Test
    fun rendersSceneWithThreeParticlesWithLines() {
        val radius1 = 1.1f
        val radius2 = 2.1f
        val radius3 = 8.1f

        val radiuses = FloatBuffer.allocate(3)
        radiuses.put(radius1)
        radiuses.put(radius2)
        radiuses.put(radius3)

        val particleColor = Color.DKGRAY
        val lineColor = Color.GRAY

        val lineWidth = 2f

        val x1 = -3.1f
        val y1 = 5.8f

        val x2 = 68.1f
        val y2 = 98.9f

        val x3 = 12.1f
        val y3 = 24.9f

        val scene: ParticlesScene = mock {
            on { it.numDots }.doReturn(3)

            on { it.radiuses }.doReturn(radiuses)
            on { it.dotColor }.doReturn(particleColor)

            on { it.lineDistance }.doReturn(666f)
            on { it.lineThickness }.doReturn(lineWidth)

            on { it.lineColor }.doReturn(lineColor)
            on { it.alpha }.doReturn(255)

            on { it.getParticleX(0) }.doReturn(x1)
            on { it.getParticleY(0) }.doReturn(y1)
            on { it.getParticleX(1) }.doReturn(x2)
            on { it.getParticleY(1) }.doReturn(y2)
            on { it.getParticleX(2) }.doReturn(x3)
            on { it.getParticleY(2) }.doReturn(y3)
        }

        // When
        underTest.drawScene(scene)

        // Then
        val expectedLineColor1 = LineColorResolver.resolveLineColorWithAlpha(
                scene.alpha,
                scene.lineColor,
                scene.lineDistance,
                DistanceResolver.distance(x1, y1, x2, y2))

        verify(lowLevelRenderer).drawLine(
                x1, y1, x2, y2, lineWidth, expectedLineColor1)

        val expectedLineColor2 = LineColorResolver.resolveLineColorWithAlpha(
                scene.alpha,
                scene.lineColor,
                scene.lineDistance,
                DistanceResolver.distance(x1, y1, x3, y3))

        verify(lowLevelRenderer).drawLine(
                x1, y1, x3, y3, lineWidth, expectedLineColor2)

        val expectedLineColor3 = LineColorResolver.resolveLineColorWithAlpha(
                scene.alpha,
                scene.lineColor,
                scene.lineDistance,
                DistanceResolver.distance(x2, y2, x3, y3))

        verify(lowLevelRenderer).drawLine(
                x2, y2, x3, y3, lineWidth, expectedLineColor3)

        val expectedColor = ParticleColorResolver.resolveParticleColorWithSceneAlpha(
                scene.dotColor,
                scene.alpha
        )

        verify(lowLevelRenderer).fillCircle(
                x1, y1, radius1, expectedColor)

        verify(lowLevelRenderer).fillCircle(
                x2, y2, radius2, expectedColor)

        verify(lowLevelRenderer).fillCircle(
                x3, y3, radius3, expectedColor)
    }
}
