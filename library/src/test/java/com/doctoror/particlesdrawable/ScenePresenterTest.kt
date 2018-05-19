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

import com.doctoror.particlesdrawable.contract.SceneRenderer
import com.doctoror.particlesdrawable.contract.SceneScheduler
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ScenePresenterTest {

    private fun newScenePresenter(): ScenePresenter {
        return ScenePresenter(mock(SceneRenderer::class.java), mock(SceneScheduler::class.java))
    }

    @Test
    fun testIsRunningByDefault() {
        assertFalse(newScenePresenter().isRunning)
    }

    @Test
    fun testIsRunningWhenStarted() {
        val c = newScenePresenter()
        c.setBounds(0, 0, 10, 10)
        c.start()
        try {
            assertTrue(c.isRunning)
        } finally {
            c.stop()
        }
    }

    @Test
    fun testIsStopedWhenStopped() {
        val c = newScenePresenter()
        c.setBounds(0, 0, 10, 10)
        c.start()
        c.stop()
        assertFalse(c.isRunning)
    }

    @Test
    fun testSetBoundsWhenRunning() {
        val c = newScenePresenter()
        c.start()
        c.setBounds(0, 0, 10, 10)
        c.stop()
    }

    @Test
    fun testWithZeroBounds() {
        val c = newScenePresenter()
        c.setBounds(0, 0, 0, 0)
        c.start()
        assertTrue(c.isRunning)
        c.stop()
        assertFalse(c.isRunning)
    }

    @Test
    fun testMakeBrandNewFrameWithZeroBounds() {
        val c = newScenePresenter()
        c.makeBrandNewFrame()
    }

    @Test
    fun testMakeBrandNewFrame() {
        val c = newScenePresenter()
        c.setBounds(0, 0, 10, 10)
        c.makeBrandNewFrame()
    }

    @Test
    fun testMakeBrandNewFrameWhenRunning() {
        val c = newScenePresenter()
        c.setBounds(0, 0, 10, 10)
        c.start()
        c.makeBrandNewFrame()
        c.stop()
    }

    @Test
    fun testDotRadiusRangeThatRoundsUpToTheSameInt() {
        val c = newScenePresenter()
        c.setBounds(0, 0, 10, 10)
        c.setDotRadiusRange(0.5f, 0.6f)
        c.makeBrandNewFrame()
    }

    @Test
    fun testSetFrameDelay() {
        val s = newScenePresenter()
        s.frameDelay = 1
        assertEquals(1, s.frameDelay.toLong())
    }

    @Test
    fun testSetStepMultiplier() {
        val s = newScenePresenter()
        s.stepMultiplier = 0f
        assertEquals(0.0f, s.stepMultiplier, ASSERT_DELTA)
    }

    @Test
    fun testSetDotRadiusRange() {
        val s = newScenePresenter()
        s.setDotRadiusRange(0.5f, 0.6f)
        assertEquals(0.5f, s.minDotRadius, ASSERT_DELTA)
        assertEquals(0.6f, s.maxDotRadius, ASSERT_DELTA)
    }

    @Test
    fun testSetLineThickness() {
        val s = newScenePresenter()
        s.lineThickness = 1f
        assertEquals(1.0f, s.lineThickness, ASSERT_DELTA)
    }

    @Test
    fun testSetLineDistance() {
        val s = newScenePresenter()
        s.lineDistance = 0f
        assertEquals(0.0f, s.lineDistance, ASSERT_DELTA)
    }

    @Test
    fun testSetNumDots() {
        val s = newScenePresenter()
        s.numDots = 0
        assertEquals(0, s.numDots)
    }

    @Test
    fun testSetLineColor() {
        val s = newScenePresenter()
        s.lineColor = 2
        assertEquals(2, s.lineColor)
    }

    @Test
    fun testSetDotColor() {
        val s = newScenePresenter()
        s.dotColor = 3
        assertEquals(3, s.dotColor)
    }
}
