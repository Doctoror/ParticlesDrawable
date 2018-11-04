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
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
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
    fun setsDotColorFromAttributes() {
        val color = Color.CYAN

        whenever(typedArray.indexCount).thenReturn(1)
        whenever(typedArray.getIndex(0)).thenReturn(R.styleable.ParticlesView_dotColor)

        whenever(
            typedArray.getColor(
                R.styleable.ParticlesView_dotColor,
                Defaults.PARTICLE_COLOR
            )
        ).thenReturn(color)

        underTest.configureSceneFromAttributes(scene, resources, attrs)

        verify(scene).dotColor = color
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
    fun setsLineDistanceFromAttributes() {
        val lineDistance = 64f

        whenever(typedArray.indexCount).thenReturn(1)
        whenever(typedArray.getIndex(0)).thenReturn(R.styleable.ParticlesView_lineDistance)

        whenever(
            typedArray.getDimension(
                R.styleable.ParticlesView_lineDistance,
                Defaults.LINE_LENGTH
            )
        ).thenReturn(lineDistance)

        underTest.configureSceneFromAttributes(scene, resources, attrs)

        verify(scene).lineDistance = lineDistance
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
    fun setsStepMultiplierFromAttributes() {
        val stepMultiplier = 64f

        whenever(typedArray.indexCount).thenReturn(1)
        whenever(typedArray.getIndex(0)).thenReturn(R.styleable.ParticlesView_stepMultiplier)

        whenever(
            typedArray.getFloat(
                R.styleable.ParticlesView_stepMultiplier,
                Defaults.SPEED_FACTOR
            )
        ).thenReturn(stepMultiplier)

        underTest.configureSceneFromAttributes(scene, resources, attrs)

        verify(scene).stepMultiplier = stepMultiplier
    }

    @Test
    fun setsDotRadiusRangeFromAttributes() {
        val minDotRadius = 8f
        val maxDotRadius = 16f

        whenever(typedArray.indexCount).thenReturn(2)

        whenever(typedArray.getIndex(0)).thenReturn(R.styleable.ParticlesView_minDotRadius)
        whenever(typedArray.getIndex(1)).thenReturn(R.styleable.ParticlesView_maxDotRadius)

        whenever(
            typedArray.getDimension(
                R.styleable.ParticlesView_minDotRadius,
                Defaults.PARTICLE_RADIUS_MIN
            )
        ).thenReturn(minDotRadius)

        whenever(
            typedArray.getDimension(
                R.styleable.ParticlesView_maxDotRadius,
                Defaults.PARTICLE_RADIUS_MAX
            )
        ).thenReturn(maxDotRadius)

        underTest.configureSceneFromAttributes(scene, resources, attrs)

        verify(scene).setDotRadiusRange(minDotRadius, maxDotRadius)
    }
}
