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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ScenePresenterTest {

    private val scene = ParticlesScene()

    private val underTest = ScenePresenter(
            scene,
            mock(SceneRenderer::class.java),
            mock(SceneScheduler::class.java))

    @Test
    fun testIsRunningByDefault() {
        assertFalse(underTest.isRunning)
    }

    @Test
    fun testIsRunningWhenStarted() {
        underTest.setBounds(0, 0, 10, 10)
        underTest.start()
        try {
            assertTrue(underTest.isRunning)
        } finally {
            underTest.stop()
        }
    }

    @Test
    fun testIsStopedWhenStopped() {
        underTest.setBounds(0, 0, 10, 10)
        underTest.start()
        underTest.stop()
        assertFalse(underTest.isRunning)
    }

    @Test
    fun testSetBoundsWhenRunning() {
        underTest.start()
        underTest.setBounds(0, 0, 10, 10)
        underTest.stop()
    }

    @Test
    fun testWithZeroBounds() {
        underTest.setBounds(0, 0, 0, 0)
        underTest.start()
        assertTrue(underTest.isRunning)
        underTest.stop()
        assertFalse(underTest.isRunning)
    }

    @Test
    fun testMakeBrandNewFrameWithZeroBounds() {
        underTest.makeBrandNewFrame()
    }

    @Test
    fun testMakeBrandNewFrame() {
        underTest.setBounds(0, 0, 10, 10)
        underTest.makeBrandNewFrame()
    }

    @Test
    fun testMakeBrandNewFrameWhenRunning() {
        underTest.setBounds(0, 0, 10, 10)
        underTest.start()
        underTest.makeBrandNewFrame()
        underTest.stop()
    }

    @Test
    fun testDotRadiusRangeThatRoundsUpToTheSameInt() {
        underTest.setBounds(0, 0, 10, 10)
        scene.setDotRadiusRange(0.5f, 0.6f)
        underTest.makeBrandNewFrame()
    }
}
