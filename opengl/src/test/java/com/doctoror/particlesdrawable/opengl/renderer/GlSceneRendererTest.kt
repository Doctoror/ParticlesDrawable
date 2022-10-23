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
package com.doctoror.particlesdrawable.opengl.renderer

import android.graphics.Bitmap
import com.doctoror.particlesdrawable.model.Scene
import com.doctoror.particlesdrawable.opengl.util.overridePrivateFinalMember
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class GlSceneRendererTest {

    private val background: GlSceneRendererBackground = mock()
    private val particles: GlSceneRendererParticles = mock()
    private val lines: GlSceneRendererLines = mock()

    private val underTest = GlSceneRenderer().apply {
        overridePrivateFinalMember(
            this,
            "background",
            background
        )
        overridePrivateFinalMember(
            this,
            "particles",
            particles
        )
        overridePrivateFinalMember(this, "lines", lines)
    }

    @Test
    fun forwardsMarkParticleTextureDirty() {
        underTest.markParticleTextureDirty()
        verify(particles).markTextureDirty()
    }

    @Test
    fun setsBackgroundTexture() {
        val texture: Bitmap = mock()
        underTest.setBackgroundTexture(texture)
        verify(background).setTexture(texture)
    }

    @Test
    fun overridesBackgroundDimensions() {
        val width = 48
        val height = 96

        underTest.overrideBackgroundDimensions(width, height)

        verify(background).setDimensions(width, height)
    }

    @Test
    fun doesCorrectSetupSequence() {
        underTest.setupGl()

        val inOrder = inOrder(particles, background, lines)
        inOrder.verify(particles).markTextureDirty()
        inOrder.verify(background).init(any())
        inOrder.verify(lines).init()
        inOrder.verify(particles).init(any())
    }

    @Test
    fun setsDimensions() {
        val width = 56
        val height = 64

        underTest.setDimensions(width, height)

        verify(background).setDimensions(width, height)
    }

    @Test
    fun drawsScene() {
        val scene: Scene = mock()

        underTest.drawScene(scene)

        val inOrder = inOrder(background, lines, particles)
        inOrder.verify(background).drawScene(any())
        inOrder.verify(lines).drawScene(eq(scene), any())
        inOrder.verify(particles).drawScene(eq(scene), any())
    }
}
