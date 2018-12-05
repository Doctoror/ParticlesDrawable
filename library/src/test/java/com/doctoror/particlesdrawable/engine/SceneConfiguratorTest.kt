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

import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import com.doctoror.particlesdrawable.Defaults
import com.doctoror.particlesdrawable.R
import com.doctoror.particlesdrawable.model.Scene
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class SceneConfiguratorTest {

    private val attrs: AttributeSet = mock()
    private val typedArray: TypedArray = mock()

    private val resources: Resources = mock {
        on(it.obtainAttributes(attrs, R.styleable.ParticlesView)).thenReturn(typedArray)
    }

    private val scene: Scene = mock()

    private val underTest = SceneConfigurator()

    @Test
    fun setsDensityFromAttributes() {
        val density = 120

        whenever(typedArray.indexCount).thenReturn(1)
        whenever(typedArray.getIndex(0)).thenReturn(R.styleable.ParticlesView_density)

        whenever(
            typedArray.getInteger(
                R.styleable.ParticlesView_density,
                Defaults.DENSITY
            )
        ).thenReturn(density)

        underTest.configureSceneFromAttributes(scene, resources, attrs)

        verify(scene).density = density
    }

    @Test
    fun setsFrameDelayFromAttributes() {
        val frameDelay = 16

        whenever(typedArray.indexCount).thenReturn(1)
        whenever(typedArray.getIndex(0)).thenReturn(R.styleable.ParticlesView_frameDelayMillis)

        whenever(
            typedArray.getInteger(
                R.styleable.ParticlesView_frameDelayMillis,
                Defaults.FRAME_DELAY
            )
        ).thenReturn(frameDelay)

        underTest.configureSceneFromAttributes(scene, resources, attrs)

        verify(scene).frameDelay = frameDelay
    }

    @Test
    fun setsLineColorFromAttributes() {
        val color = Color.MAGENTA

        whenever(typedArray.indexCount).thenReturn(1)
        whenever(typedArray.getIndex(0)).thenReturn(R.styleable.ParticlesView_lineColor)

        whenever(
            typedArray.getColor(
                R.styleable.ParticlesView_lineColor,
                Defaults.LINE_COLOR
            )
        ).thenReturn(color)

        underTest.configureSceneFromAttributes(scene, resources, attrs)

        verify(scene).lineColor = color
    }

    @Test
    fun setsLineLengthFromAttributes() {
        val lineLength = 64f

        whenever(typedArray.indexCount).thenReturn(1)
        whenever(typedArray.getIndex(0)).thenReturn(R.styleable.ParticlesView_lineLength)

        whenever(
            typedArray.getDimension(
                R.styleable.ParticlesView_lineLength,
                Defaults.LINE_LENGTH
            )
        ).thenReturn(lineLength)

        underTest.configureSceneFromAttributes(scene, resources, attrs)

        verify(scene).lineLength = lineLength
    }

    @Test
    fun setsLineThicknessFromAttributes() {
        val lineThickness = 64f

        whenever(typedArray.indexCount).thenReturn(1)
        whenever(typedArray.getIndex(0)).thenReturn(R.styleable.ParticlesView_lineThickness)

        whenever(
            typedArray.getDimension(
                R.styleable.ParticlesView_lineThickness,
                Defaults.LINE_THICKNESS
            )
        ).thenReturn(lineThickness)

        underTest.configureSceneFromAttributes(scene, resources, attrs)

        verify(scene).lineThickness = lineThickness
    }

    @Test
    fun setsParticleColorFromAttributes() {
        val color = Color.CYAN

        whenever(typedArray.indexCount).thenReturn(1)
        whenever(typedArray.getIndex(0)).thenReturn(R.styleable.ParticlesView_particleColor)

        whenever(
            typedArray.getColor(
                R.styleable.ParticlesView_particleColor,
                Defaults.PARTICLE_COLOR
            )
        ).thenReturn(color)

        underTest.configureSceneFromAttributes(scene, resources, attrs)

        verify(scene).particleColor = color
    }

    @Test
    fun setsParticleRadiusRangeFromAttributes() {
        val particleRadiusMax = 16f
        val particleRadiusMin = 8f

        whenever(typedArray.indexCount).thenReturn(2)

        whenever(typedArray.getIndex(0)).thenReturn(R.styleable.ParticlesView_particleRadiusMax)
        whenever(typedArray.getIndex(1)).thenReturn(R.styleable.ParticlesView_particleRadiusMin)

        whenever(
            typedArray.getDimension(
                R.styleable.ParticlesView_particleRadiusMax,
                Defaults.PARTICLE_RADIUS_MAX
            )
        ).thenReturn(particleRadiusMax)

        whenever(
            typedArray.getDimension(
                R.styleable.ParticlesView_particleRadiusMin,
                Defaults.PARTICLE_RADIUS_MIN
            )
        ).thenReturn(particleRadiusMin)

        underTest.configureSceneFromAttributes(scene, resources, attrs)

        verify(scene).setParticleRadiusRange(particleRadiusMin, particleRadiusMax)
    }

    @Test
    fun setsSpeedFactorFromAttributes() {
        val speedFactor = 64f

        whenever(typedArray.indexCount).thenReturn(1)
        whenever(typedArray.getIndex(0)).thenReturn(R.styleable.ParticlesView_speedFactor)

        whenever(
            typedArray.getFloat(
                R.styleable.ParticlesView_speedFactor,
                Defaults.SPEED_FACTOR
            )
        ).thenReturn(speedFactor)

        underTest.configureSceneFromAttributes(scene, resources, attrs)

        verify(scene).speedFactor = speedFactor
    }
}
