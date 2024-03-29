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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

class FrameAdvancerTest {

    private val particleGenerator: ParticleGenerator = mock()

    private val scene: Scene = mock {
        on(it.width).thenReturn(320)
        on(it.height).thenReturn(240)
        on(it.density).thenReturn(1)
        on(it.speedFactor).thenReturn(1f)
        on(it.getParticleSpeedFactor(any())).thenReturn(1f)
    }

    private val underTest = FrameAdvancer(particleGenerator)

    @Test
    fun doesNothingWhenParticleCountIs0() {
        whenever(scene.density).thenReturn(0)

        underTest.advanceToNextFrame(scene, 1f)

        verify(scene).density
        verifyNoMoreInteractions(scene)
        verifyNoInteractions(particleGenerator)
    }

    @Test
    fun particlesNotOutOfBounds() {
        whenever(scene.lineLength).thenReturn(3f)
        whenever(scene.particleRadiusMin).thenReturn(4f)

        assertFalse(
            underTest.particleOutOfBounds(
                scene,
                -scene.lineLength - scene.particleRadiusMin,
                -scene.lineLength - scene.particleRadiusMin
            )
        )

        assertFalse(
            underTest.particleOutOfBounds(
                scene,
                scene.width + scene.lineLength + scene.particleRadiusMin,
                scene.height + scene.lineLength + scene.particleRadiusMin
            )
        )
    }

    @Test
    fun particlesOutOfBounds() {
        whenever(scene.lineLength).thenReturn(5f)
        whenever(scene.particleRadiusMin).thenReturn(3f)

        assertTrue(
            underTest.particleOutOfBounds(
                scene,
                -scene.lineLength - scene.particleRadiusMin - 0.1f,
                -scene.lineLength - scene.particleRadiusMin - 0.1f
            )
        )

        assertTrue(
            underTest.particleOutOfBounds(
                scene,
                scene.width + scene.lineLength + scene.particleRadiusMin + 0.1f,
                scene.height + scene.lineLength + scene.particleRadiusMin + 0.1f
            )
        )
    }

    @Test
    fun advancesToNextFrameInBound() {
        val dCos = 0.59750384f
        val dSin = 0.80186605f
        val particleX = 4f
        val particleY = 2f

        givenParticleCoordinates(
            dCos = dCos,
            dSin = dSin,
            x = particleX,
            y = particleY
        )

        val step = 2f

        val expectedX = particleX +
                step * scene.speedFactor * scene.getParticleSpeedFactor(0) * dCos

        val expectedY = particleY +
                step * scene.speedFactor * scene.getParticleSpeedFactor(0) * dSin

        underTest.advanceToNextFrame(scene, step)

        verify(scene).setParticleX(0, expectedX)
        verify(scene).setParticleY(0, expectedY)
    }

    @Test
    fun generatesNewOffscreenParticleWhenAdvancedOutOfBoundsNorth() {
        givenParticleCoordinates(
            dCos = 0f,
            dSin = -1f,
            x = 64f,
            y = 1f
        )

        underTest.advanceToNextFrame(scene, 2f)

        verify(particleGenerator).applyFreshParticleOffScreen(scene, 0)
    }

    @Test
    fun generatesNewOffscreenParticleWhenAdvancedOutOfBoundsSouth() {
        givenParticleCoordinates(
            dCos = 0f,
            dSin = 1f,
            x = 64f,
            y = scene.height - 1f
        )

        underTest.advanceToNextFrame(scene, 2f)

        verify(particleGenerator).applyFreshParticleOffScreen(scene, 0)
    }

    @Test
    fun generatesNewOffscreenParticleWhenAdvancedOutOfBoundsWest() {
        givenParticleCoordinates(
            dCos = -1f,
            dSin = -0f,
            x = 1f,
            y = 12f
        )

        underTest.advanceToNextFrame(scene, 2f)

        verify(particleGenerator).applyFreshParticleOffScreen(scene, 0)
    }

    @Test
    fun generatesNewOffscreenParticleWhenAdvancedOutOfBoundsEast() {
        givenParticleCoordinates(
            dCos = 1f,
            dSin = 0f,
            x = scene.width - 1f,
            y = 10f
        )

        underTest.advanceToNextFrame(scene, 2f)

        verify(particleGenerator).applyFreshParticleOffScreen(scene, 0)
    }

    private fun givenParticleCoordinates(
        dCos: Float,
        dSin: Float,
        x: Float,
        y: Float
    ) {
        whenever(scene.getParticleX(0)).thenReturn(x)
        whenever(scene.getParticleY(0)).thenReturn(y)
        whenever(scene.getParticleDirectionCos(0)).thenReturn(dCos)
        whenever(scene.getParticleDirectionSin(0)).thenReturn(dSin)
    }
}
