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
package com.doctoror.particlesdrawable

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ParticlesDrawableTest {

    @Test
    fun testIsRunningByDefault() {
        assertFalse(ParticlesDrawable().isRunning)
    }

    @Test
    fun testIsRunningWhenStarted() {
        val d = ParticlesDrawable()
        d.setBounds(0, 0, 10, 10)
        d.start()
        try {
            assertTrue(d.isRunning)
        } finally {
            d.stop()
        }
    }

    @Test
    fun testIsStopedWhenStopped() {
        val d = ParticlesDrawable()
        d.setBounds(0, 0, 10, 10)
        d.start()
        d.stop()
        assertFalse(d.isRunning)
    }

    @Test
    fun testSetBoundsWhenRunning() {
        val d = ParticlesDrawable()
        d.start()
        d.setBounds(0, 0, 10, 10)
        d.stop()
    }

    @Test
    fun testWithZeroBounds() {
        val d = ParticlesDrawable()
        d.setBounds(0, 0, 0, 0)
        d.start()
        assertTrue(d.isRunning)
        d.stop()
        assertFalse(d.isRunning)
    }

    @Test
    fun testMakeBrandNewFrameWithZeroBounds() {
        val d = ParticlesDrawable()
        d.makeBrandNewFrame()
    }

    @Test
    fun testMakeBrandNewFrame() {
        val d = ParticlesDrawable()
        d.setBounds(0, 0, 10, 10)
        d.makeBrandNewFrame()
    }

    @Test
    fun testMakeBrandNewFrameWhenRunning() {
        val d = ParticlesDrawable()
        d.setBounds(0, 0, 10, 10)
        d.start()
        d.makeBrandNewFrame()
        d.stop()
    }

    @Test
    @Throws(Throwable::class)
    fun testDotRadiusRangeThatRoundsUpToTheSameInt() {
        val d = ParticlesDrawable()
        d.setBounds(0, 0, 10, 10)
        d.setDotRadiusRange(0.5f, 0.6f)
        d.makeBrandNewFrame()
    }

    @Test
    fun testSetFrameDelay() {
        val s = ParticlesDrawable()
        s.frameDelay = 1
        assertEquals(1, s.frameDelay)
    }

    @Test
    fun testSetStepMultiplier() {
        val s = ParticlesDrawable()
        s.stepMultiplier = 0f
        assertEquals(0f, s.stepMultiplier, ASSERT_DELTA)
    }

    @Test
    fun testSetDotRadiusRange() {
        val s = ParticlesDrawable()
        s.setDotRadiusRange(0.5f, 0.6f)
        assertEquals(0.5f, s.minDotRadius, ASSERT_DELTA)
        assertEquals(0.6f, s.maxDotRadius, ASSERT_DELTA)
    }

    @Test
    fun testSetLineThickness() {
        val s = ParticlesDrawable()
        s.lineThickness = 1f
        assertEquals(1f, s.lineThickness, ASSERT_DELTA)
    }

    @Test
    fun testSetLineDistance() {
        val s = ParticlesDrawable()
        s.lineDistance = 0f
        assertEquals(0f, s.lineDistance, ASSERT_DELTA)
    }

    @Test
    fun testSetNumDots() {
        val s = ParticlesDrawable()
        s.numDots = 1
        assertEquals(1, s.numDots)
    }

    @Test
    fun setNumDotsWhenHasSize() {
        val s = ParticlesDrawable()
        s.setBounds(0, 0, 1, 1)
        s.numDots = 1
        assertEquals(1, s.numDots)
    }

    @Test
    fun setNumDotsWhenSizeIsBackTo0() {
        val s = ParticlesDrawable()
        s.setBounds(0, 0, 1, 1)
        s.numDots = 1
        s.setBounds(0, 0, 0, 0)
        s.numDots = 2
        assertEquals(2, s.numDots)
    }

    @Test
    fun testSetLineColor() {
        val s = ParticlesDrawable()
        s.lineColor = 2
        assertEquals(2, s.lineColor)
    }

    @Test
    fun testSetDotColor() {
        val s = ParticlesDrawable()
        s.dotColor = 3
        assertEquals(3, s.dotColor)
    }
}
