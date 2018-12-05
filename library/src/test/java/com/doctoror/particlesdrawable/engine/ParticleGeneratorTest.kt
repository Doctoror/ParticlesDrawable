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
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

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

    @Test(expected = IllegalStateException::class)
    fun applyFreshParticleOffScreenThrowsWhenWidthIs0() {
        whenever(scene.width).thenReturn(0)
        underTest.applyFreshParticleOnScreen(scene, 0)
    }

    @Test(expected = IllegalStateException::class)
    fun applyFreshParticleOffScreenThrowsWhenHeightIs0() {
        whenever(scene.height).thenReturn(0)
        underTest.applyFreshParticleOnScreen(scene, 0)
    }

    @Test
    fun generatesParticleOnScreen() {
        // Given
        val expectedX = 32
        val expectedY = 64

        whenever(random.nextInt(scene.width)).thenReturn(expectedX)
        whenever(random.nextInt(scene.height)).thenReturn(expectedY)

        val expectedSpeedFactor = 0.9f
        whenever(random.nextInt(11)).thenReturn(4)

        val expectedRadiusOffset = 2
        whenever(random.nextInt(400)).thenReturn(expectedRadiusOffset * 100)

        val expectedRadius = scene.particleRadiusMin + expectedRadiusOffset

        val expectedDirectionDegrees = 128
        val expectedDirectionRadians = Math.toRadians(expectedDirectionDegrees.toDouble())
        whenever(random.nextInt(360)).thenReturn(expectedDirectionDegrees)

        val expectedCos = Math.cos(expectedDirectionRadians).toFloat()
        val expectedSin = Math.sin(expectedDirectionRadians).toFloat()

        // When
        underTest.applyFreshParticleOnScreen(scene, 0)

        // Then
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

    @Test
    fun generatesParticleOffScreenFromLeft() {
        val offset = scene.particleRadiusMin + scene.lineLength
        testGeneratesParticleOffScreen(
            expectedAngleAdjustmentBound = 61,
            expectedStartAngle = 16.927513f,
            expectedX = -offset,
            expectedY = 4f,
            givenAngleAdjustment = 2,
            givenSide = 0,
            givenX = 2,
            givenY = 4
        )
    }

    @Test
    fun generatesParticleOffScreenFromTop() {
        val offset = scene.particleRadiusMin + scene.lineLength
        testGeneratesParticleOffScreen(
            expectedAngleAdjustmentBound = 70,
            expectedStartAngle = 8.922411f,
            expectedX = 9f,
            expectedY = -offset,
            givenAngleAdjustment = 32,
            givenSide = 1,
            givenX = 9,
            givenY = 18
        )
    }

    @Test
    fun generatesParticleOffScreenFromRight() {
        val offset = scene.particleRadiusMin + scene.lineLength
        testGeneratesParticleOffScreen(
            expectedAngleAdjustmentBound = 42,
            expectedStartAngle = 216.46924f,
            expectedX = scene.width + offset,
            expectedY = 256f,
            givenAngleAdjustment = 32,
            givenSide = 2,
            givenX = 128,
            givenY = 256
        )
    }

    @Test
    fun generatesParticleOffScreenFromBottom() {
        val offset = scene.particleRadiusMin + scene.lineLength
        testGeneratesParticleOffScreen(
            expectedAngleAdjustmentBound = 124,
            expectedStartAngle = 225f,
            expectedX = 64f,
            expectedY = scene.height + offset,
            givenAngleAdjustment = 32,
            givenSide = 3,
            givenX = 64,
            givenY = 56
        )
    }

    private fun testGeneratesParticleOffScreen(
        expectedAngleAdjustmentBound: Int,
        expectedStartAngle: Float,
        expectedX: Float,
        expectedY: Float,
        givenAngleAdjustment: Int,
        givenSide: Int,
        givenX: Int,
        givenY: Int
    ) {
        // Given
        whenever(random.nextInt(scene.width)).thenReturn(givenX)
        whenever(random.nextInt(scene.height)).thenReturn(givenY)

        whenever(random.nextInt(4)).thenReturn(givenSide)
        whenever(random.nextInt(expectedAngleAdjustmentBound)).thenReturn(givenAngleAdjustment)

        val givenSpeedFactor = 4
        whenever(random.nextInt(11)).thenReturn(givenSpeedFactor)

        val expectedRadiusOffset = 1
        whenever(random.nextInt(400)).thenReturn(expectedRadiusOffset * 100)

        // When
        underTest.applyFreshParticleOffScreen(scene, 0)

        // When

        val expectedAngle = expectedStartAngle + givenAngleAdjustment
        val expectedDirection = Math.toRadians(expectedAngle.toDouble())
        val expectedRadius = scene.particleRadiusMin + expectedRadiusOffset
        val expectedSpeedFactor = 0.9f

        val expectedCos = Math.cos(expectedDirection).toFloat()
        val expectedSin = Math.sin(expectedDirection).toFloat()

        verify(scene).setParticleData(
            0,
            expectedX,
            expectedY,
            expectedCos,
            expectedSin,
            expectedRadius,
            expectedSpeedFactor
        )
    }
}
