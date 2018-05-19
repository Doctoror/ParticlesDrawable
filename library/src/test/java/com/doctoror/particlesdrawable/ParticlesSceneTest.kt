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

import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*

/**
 * [ParticlesScene] test
 */
class ParticlesSceneTest {

    @Test
    fun testPointsEmptyByDefault() {
        val scene = ParticlesScene()
        assertTrue(scene.getMutablePoints().isEmpty())
    }

    @Test
    fun testAddTwoPoints() {
        val scene = ParticlesScene()

        val p1 = Particle()
        val p2 = Particle()

        scene.addParticleData(p1)
        scene.addParticleData(p2)

        assertFalse(scene.getMutablePoints().isEmpty())

        assertTrue(scene.getMutablePoints().contains(p1))
        assertTrue(scene.getMutablePoints().contains(p2))
    }

    @Test
    fun testRemoveFirstPointWhenEmpty() {
        ParticlesScene().removeFirstParticle()
    }

    @Test
    fun testRemoveFirstPointWithTwoPoints() {
        val scene = ParticlesScene()

        val p1 = Particle()
        val p2 = Particle()

        scene.addParticleData(p1)
        scene.addParticleData(p2)

        scene.removeFirstParticle()

        assertFalse(scene.getMutablePoints().contains(p1))
        assertTrue(scene.getMutablePoints().contains(p2))

        scene.removeFirstParticle()

        assertFalse(scene.getMutablePoints().contains(p2))
    }

    @Test
    fun testClearPointsWithTwoPoints() {
        val scene = ParticlesScene()

        val p1 = Particle()
        val p2 = Particle()

        scene.addParticleData(p1)
        scene.addParticleData(p2)

        scene.clearParticles()
        assertTrue(scene.getMutablePoints().isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetFrameDelayNegative() {
        ParticlesScene().frameDelay = -1
    }

    @Test
    fun testSetFrameDelay() {
        val s = ParticlesScene()
        s.frameDelay = 1
        assertEquals(1, s.frameDelay.toLong())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStepMultiplierNegative() {
        ParticlesScene().stepMultiplier = -0.01f
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStepMultiplierNAN() {
        ParticlesScene().stepMultiplier = java.lang.Float.NaN
    }

    @Test
    fun testSetStepMultiplier() {
        val s = ParticlesScene()
        s.stepMultiplier = 0f
        Assert.assertEquals(0, s.stepMultiplier, Config.ASSERT_DELTA)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetDotRadiusRangeInvalidFirstArgument() {
        ParticlesScene().setDotRadiusRange(0.49f, 1f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetDotRadiusRangeInvalidSecondArgument() {
        ParticlesScene().setDotRadiusRange(2f, 0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetDotRadiusRangeInvalidBothArguments() {
        ParticlesScene().setDotRadiusRange(0.1f, -2f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetDotRadiusRangeFirstArgumentNAN() {
        ParticlesScene().setDotRadiusRange(java.lang.Float.NaN, 1f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetDotRadiusRangeSecondArgumentNAN() {
        ParticlesScene().setDotRadiusRange(1f, java.lang.Float.NaN)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetDotRadiusRangeBothArgumentsNAN() {
        ParticlesScene().setDotRadiusRange(java.lang.Float.NaN, java.lang.Float.NaN)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetDotRadiusRangeMaxLessThanMin() {
        ParticlesScene().setDotRadiusRange(0.7f, 0.6f)
    }

    @Test
    fun testSetDotRadiusRange() {
        val s = ParticlesScene()
        s.setDotRadiusRange(0.5f, 0.6f)
        Assert.assertEquals(0.5, s.minDotRadius, Config.ASSERT_DELTA)
        Assert.assertEquals(0.6, s.maxDotRadius, Config.ASSERT_DELTA)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetLineThicknessInvalidArgument() {
        ParticlesScene().lineThickness = 0.99f
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetLineThicknessNAN() {
        ParticlesScene().lineThickness = java.lang.Float.NaN
    }

    @Test
    fun testSetLineThickness() {
        val s = ParticlesScene()
        s.lineThickness = 1f
        Assert.assertEquals(1, s.lineThickness, Config.ASSERT_DELTA)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetLineDistanceInvalidArgument() {
        ParticlesScene().lineDistance = java.lang.Float.NEGATIVE_INFINITY
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetLineDistanceNAN() {
        ParticlesScene().lineDistance = java.lang.Float.NaN
    }

    @Test
    fun testSetLineDistance() {
        val s = ParticlesScene()
        s.lineDistance = 0f
        Assert.assertEquals(0, s.lineDistance, Config.ASSERT_DELTA)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetNumDotsInvalidArgument() {
        ParticlesScene().numDots = -1
    }

    @Test
    fun testSetNumDots() {
        val s = ParticlesScene()
        s.numDots = 0
        assertEquals(0, s.numDots.toLong())
    }

    @Test
    fun testSetLineColor() {
        val s = ParticlesScene()
        s.lineColor = 2
        assertEquals(2, s.lineColor.toLong())
    }
}
