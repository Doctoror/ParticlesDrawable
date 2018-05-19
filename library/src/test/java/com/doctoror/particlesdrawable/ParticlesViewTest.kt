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

import android.view.View
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.NONE

@Config(manifest = NONE)
@RunWith(RobolectricTestRunner::class)
class ParticlesViewTest {

    private fun newParticlesView(): ParticlesView {
        val v = ParticlesView(RuntimeEnvironment.application)
        v.setEmulateOnAttachToWindow(true)
        return v
    }

    private fun newAttachedAndVisibleParticlesView(): ParticlesView {
        val v = newParticlesView()
        v.visibility = View.VISIBLE
        v.onAttachedToWindow()
        return v
    }

    @Test
    fun testIsRunningByDefault() {
        assertFalse(newParticlesView().isRunning)
    }

    @Test
    fun testIsRunningWhenVisibleAndAttached() {
        val v = newAttachedAndVisibleParticlesView()
        try {
            assertTrue(v.isRunning)
        } finally {
            v.stopInternal()
        }
    }

    @Test
    fun testIsStopedWhenStopped() {
        val v = newAttachedAndVisibleParticlesView()
        v.stopInternal()
        assertFalse(v.isRunning)
    }

    @Test
    fun testIsStopedWhenDetachedFromWindow() {
        val v = newAttachedAndVisibleParticlesView()
        v.onDetachedFromWindow()
        assertFalse(v.isRunning)
    }

    @Test
    fun testNotRunningWhenInvisible() {
        val v = newAttachedAndVisibleParticlesView()
        v.visibility = View.INVISIBLE
        v.onVisibilityChanged(v, View.INVISIBLE)
        assertFalse(v.isRunning)
    }

    @Test
    fun testNotRunningWhenGone() {
        val v = newAttachedAndVisibleParticlesView()
        v.visibility = View.GONE
        v.onVisibilityChanged(v, View.GONE)
        assertFalse(v.isRunning)
    }

    @Test
    fun testNotRunningWhenNotVisibleBeforeStart() {
        val v = newParticlesView()
        v.visibility = View.INVISIBLE
        v.onVisibilityChanged(v, View.INVISIBLE)
        v.startInternal()
        assertFalse(v.isRunning)
    }

    @Test
    fun testNotRunningWhenNotVisibleBeforeAttachedToWindow() {
        val v = newParticlesView()
        v.visibility = View.INVISIBLE
        v.onVisibilityChanged(v, View.INVISIBLE)
        v.onAttachedToWindow()
        assertFalse(v.isRunning)
    }

    @Test
    fun testNotRunningWhenExplicitlyStoppedAndAttachedToWindow() {
        val v = newAttachedAndVisibleParticlesView()
        v.stop()
        v.onAttachedToWindow()
        assertFalse(v.isRunning)
    }

    @Test
    fun testNotRunningWhenExplicitlyStoppedAndVisible() {
        val v = newAttachedAndVisibleParticlesView()
        v.stop()
        v.visibility = View.VISIBLE
        v.onVisibilityChanged(v, View.VISIBLE)
        assertFalse(v.isRunning)
    }

    @Test
    fun testNotRunningWhenExplicitlyStoppedButStartedInternally() {
        val v = newAttachedAndVisibleParticlesView()
        v.stop()
        v.startInternal()
        assertFalse(v.isRunning)
    }

    @Test
    fun testRunningWhenExplicitlyStartedAfterExplicitStop() {
        val v = newAttachedAndVisibleParticlesView()
        v.stop()
        v.start()
        try {
            assertTrue(v.isRunning)
        } finally {
            v.stopInternal()
        }
    }

    @Test
    fun testMakeBrandNewFrameWhenRunning() {
        val v = newAttachedAndVisibleParticlesView()
        v.makeBrandNewFrame()
    }

    @Test
    fun testMakeBrandNewFrameWhenStopped() {
        val v = newAttachedAndVisibleParticlesView()
        v.startInternal()
        v.makeBrandNewFrame()
    }

    @Test
    fun testSetFrameDelay() {
        val s = newParticlesView()
        s.frameDelay = 1
        assertEquals(1, s.frameDelay)
    }

    @Test
    fun testSetStepMultiplier() {
        val s = newParticlesView()
        s.stepMultiplier = 0f
        assertEquals(0f, s.stepMultiplier, ASSERT_DELTA)
    }

    @Test
    fun testSetDotRadiusRange() {
        val s = newParticlesView()
        s.setDotRadiusRange(0.5f, 0.6f)
        assertEquals(0.5f, s.minDotRadius, ASSERT_DELTA)
        assertEquals(0.6f, s.maxDotRadius, ASSERT_DELTA)
    }

    @Test
    fun testSetLineThickness() {
        val s = newParticlesView()
        s.lineThickness = 1f
        assertEquals(1f, s.lineThickness, ASSERT_DELTA)
    }

    @Test
    fun testSetLineDistance() {
        val s = newParticlesView()
        s.lineDistance = 0f
        assertEquals(0f, s.lineDistance, ASSERT_DELTA)
    }

    @Test
    fun testSetNumDots() {
        val s = newParticlesView()
        s.numDots = 0
        assertEquals(0, s.numDots)
    }

    @Test
    fun testSetLineColor() {
        val s = newParticlesView()
        s.lineColor = 2
        assertEquals(2, s.lineColor)
    }

    @Test
    fun testSetDotColor() {
        val s = newParticlesView()
        s.dotColor = 3
        assertEquals(3, s.dotColor)
    }
}
