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
package com.doctoror.particlesdrawable.engine

import com.doctoror.particlesdrawable.model.Scene
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

// TODO test everything
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ParticleGeneratorTest {

    private val scene: Scene = mock {
        on(it.width).thenReturn(320)
        on(it.height).thenReturn(240)

        on(it.lineLength).thenReturn(24f)

        on(it.particleRadiusMin).thenReturn(4f)
        on(it.particleRadiusMax).thenReturn(8f)
    }

    private val random: Random = mock()
    private val underTest = ParticleGenerator(random)

    @Test(expected = IllegalStateException::class)
    fun applyFreshParticleOnScreenThrowsWhenWidthIs0() {
        whenever(scene.width).thenReturn(0)
        underTest.applyFreshParticleOnScreen(scene, 0)
    }

    @Test(expected = IllegalStateException::class)
    fun applyFreshParticleOnScreenThrowsWhenHeightIs0() {
        whenever(scene.height).thenReturn(0)
        underTest.applyFreshParticleOnScreen(scene, 0)
    }

    @Test
    fun generatesParticleOffScreenFromLeft() {
        // Given
        val rawX = 2
        val expectedY = 4

        whenever(random.nextInt(scene.width)).thenReturn(rawX)
        whenever(random.nextInt(scene.height)).thenReturn(expectedY)

        val expectedSide = 0
        whenever(random.nextInt(4)).thenReturn(expectedSide)

        val expectedStartAngle = 16.927513f
        val expectedAngleAdjustment = 2
        whenever(random.nextInt(61)).thenReturn(expectedAngleAdjustment)

        val expectedAngle = expectedStartAngle + expectedAngleAdjustment

        val expectedSpeedFactor = 0.9f
        whenever(random.nextInt(11)).thenReturn(4)

        val expectedRadiusOffset = 1
        whenever(random.nextInt(400)).thenReturn(expectedRadiusOffset * 100)

        val expectedRadius = scene.particleRadiusMin + expectedRadiusOffset

        // When
        underTest.applyFreshParticleOffScreen(scene, 0)

        // When
        val offset = (scene.particleRadiusMin + scene.lineLength).toShort()
        val expectedX = -offset

        val expectedDirection = Math.toRadians(expectedAngle.toDouble())

        val expectedCos = Math.cos(expectedDirection).toFloat()
        val expectedSin = Math.sin(expectedDirection).toFloat()

        verify(scene).setParticleData(
            0,
            expectedX.toFloat(),
            expectedY.toFloat(),
            expectedCos,
            expectedSin,
            expectedRadius,
            expectedSpeedFactor
        )
    }
}
