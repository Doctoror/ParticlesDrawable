/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
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
package com.doctoror.particlesdrawable

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ParticlesSceneTest {

    private val underTest = ParticlesScene()

    @Test
    fun addTwoParticles() {
        // When
        underTest.setParticleData(
                0,
                1f,
                2f,
                3f,
                4f,
                5f,
                6f
        )

        underTest.setParticleData(
                1,
                7f,
                8f,
                9f,
                10f,
                11f,
                12f
        )

        // Then
        assertEquals(1f, underTest.getParticleX(0))
        assertEquals(2f, underTest.getParticleY(0))
        assertEquals(3f, underTest.getParticleDirectionCos(0))
        assertEquals(4f, underTest.getParticleDirectionSin(0))
        assertEquals(5f, underTest.radiuses.get(0))
        assertEquals(6f, underTest.getParticleStepMultiplier(0))

        assertEquals(7f, underTest.getParticleX(1))
        assertEquals(8f, underTest.getParticleY(1))
        assertEquals(9f, underTest.getParticleDirectionCos(1))
        assertEquals(10f, underTest.getParticleDirectionSin(1))
        assertEquals(11f, underTest.radiuses.get(1))
        assertEquals(12f, underTest.getParticleStepMultiplier(1))
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetFrameDelayToNegative() {
        underTest.frameDelay = -1
    }

    @Test
    fun setsFrameDelay() {
        underTest.frameDelay = 1
        assertEquals(1, underTest.frameDelay.toLong())
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetStepMultiplierToNegative() {
        underTest.stepMultiplier = -0.01f
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetMultiplierNAN() {
        underTest.stepMultiplier = java.lang.Float.NaN
    }

    @Test
    fun setsStepMultiplier() {
        underTest.stepMultiplier = 1f
        assertEquals(1f, underTest.stepMultiplier, ASSERT_DELTA)
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetDotRadiusRangeInvalidFirstArgument() {
        underTest.setDotRadiusRange(0.49f, 1f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetDotRadiusRangeInvalidSecondArgument() {
        underTest.setDotRadiusRange(2f, 0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetDotRadiusRangeInvalidBothArguments() {
        underTest.setDotRadiusRange(0.1f, -2f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetDotRadiusRangeFirstArgumentNAN() {
        underTest.setDotRadiusRange(java.lang.Float.NaN, 1f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetDotRadiusRangeSecondArgumentNAN() {
        underTest.setDotRadiusRange(1f, java.lang.Float.NaN)
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetDotRadiusRangeBothArgumentsNAN() {
        underTest.setDotRadiusRange(java.lang.Float.NaN, java.lang.Float.NaN)
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetDotRadiusRangeMaxLessThanMin() {
        underTest.setDotRadiusRange(0.7f, 0.6f)
    }

    @Test
    fun setsDotRadiusRange() {
        underTest.setDotRadiusRange(0.5f, 0.6f)
        assertEquals(0.5f, underTest.minDotRadius, ASSERT_DELTA)
        assertEquals(0.6f, underTest.maxDotRadius, ASSERT_DELTA)
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetLineThicknessInvalidArgument() {
        underTest.lineThickness = 0.99f
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetLineThicknessNAN() {
        underTest.lineThickness = java.lang.Float.NaN
    }

    @Test
    fun setsLineThickness() {
        underTest.lineThickness = 1f
        assertEquals(1f, underTest.lineThickness, ASSERT_DELTA)
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetLineDistanceInvalidArgument() {
        underTest.lineDistance = java.lang.Float.NEGATIVE_INFINITY
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetLineDistanceNAN() {
        underTest.lineDistance = java.lang.Float.NaN
    }

    @Test
    fun setsLineDistance() {
        underTest.lineDistance = 0f
        assertEquals(0f, underTest.lineDistance, ASSERT_DELTA)
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenSetNumDotsInvalidArgument() {
        underTest.numDots = -1
    }

    @Test
    fun setsNumDots() {
        underTest.numDots = 2
        assertEquals(2, underTest.numDots)
    }

    @Test
    fun setsLineColor() {
        underTest.lineColor = 2
        assertEquals(2, underTest.lineColor)
    }
}
