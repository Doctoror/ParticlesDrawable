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

import android.content.res.Resources
import android.content.res.TypedArray
import android.util.TypedValue
import com.doctoror.particlesdrawable.engine.ScenePresenter
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ScenePresenterTestOld {

    private val scene: ParticlesScene = mock { stubbing ->
        var width = 0
        var height = 0

        on(stubbing.setWidth(any())).thenAnswer { invocation ->
            width = invocation.arguments[0] as Int
            Unit
        }

        on(stubbing.setHeight(any())).thenAnswer { invocation ->
            height = invocation.arguments[0] as Int
            Unit
        }

        on(stubbing.width).thenAnswer { width }
        on(stubbing.height).thenAnswer { height }
    }

    private val underTest = ScenePresenter(
        scene,
        mock(),
        mock()
    )

    @Test
    fun isRunningWhenStarted() {
        underTest.setDimensions(10, 10)
        underTest.start()
        try {
            assertTrue(underTest.isRunning)
        } finally {
            underTest.stop()
        }
    }

    @Test
    fun isStopedWhenStopped() {
        underTest.setDimensions(10, 10)
        underTest.start()
        underTest.stop()
        assertFalse(underTest.isRunning)
    }

    @Test
    fun setBoundsWhenRunning() {
        underTest.start()
        underTest.setDimensions(10, 10)
        underTest.stop()
    }

    @Test
    fun withZeroBounds() {
        underTest.setDimensions(0, 0)
        underTest.start()
        assertTrue(underTest.isRunning)
        underTest.stop()
        assertFalse(underTest.isRunning)
    }

    @Test
    fun makeBrandNewFrameWithZeroBounds() {
        underTest.makeBrandNewFrame()
    }

    @Test
    fun makeBrandNewFrame() {
        underTest.setDimensions(10, 10)
        underTest.makeBrandNewFrame()
    }

    @Test
    fun makeBrandNewFrameWhenRunning() {
        underTest.setDimensions(10, 10)
        underTest.start()
        underTest.makeBrandNewFrame()
        underTest.stop()
    }

    @Test
    fun dotRadiusRangeThatRoundsUpToTheSameInt() {
        underTest.setDimensions(10, 10)
        scene.setDotRadiusRange(0.5f, 0.6f)
        underTest.makeBrandNewFrame()
    }

    @Test
    fun setsDotRadiusRangeFromAttributes() {
        val minDotRadius = 8f
        val maxDotRadius = 16f

        val typedArray: TypedArray = mock {
            on(it.indexCount).thenReturn(2)

            on(it.getIndex(0)).thenReturn(R.styleable.ParticlesView_minDotRadius)
            on(it.getIndex(1)).thenReturn(R.styleable.ParticlesView_maxDotRadius)

            on(
                it.getDimension(
                    R.styleable.ParticlesView_minDotRadius,
                    Defaults.PARTICLE_RADIUS_MIN
                )
            ).thenReturn(minDotRadius)

            on(
                it.getDimension(
                    R.styleable.ParticlesView_maxDotRadius,
                    Defaults.PARTICLE_RADIUS_MAX
                )
            ).thenReturn(maxDotRadius)
        }

        underTest.handleAttrs(typedArray)

        verify(scene).setDotRadiusRange(minDotRadius, maxDotRadius)
    }

//    @Test
//    fun testLineThicknessFromAttributes() {
//        val v = inflateCustomizedDrawable()
//        assertEquals(
//            dp(4f).toDouble(),
//            v.getLineThickness().toDouble(),
//            com.doctoror.particlesdrawable.Config.ASSERT_DELTA
//        )
//    }
//
//    @Test
//    fun testLineDistanceFromAttributes() {
//        val v = inflateCustomizedDrawable()
//        assertEquals(
//            dp(5f).toDouble(),
//            v.getLineDistance().toDouble(),
//            com.doctoror.particlesdrawable.Config.ASSERT_DELTA
//        )
//    }
//
//    @Test
//    fun testNumDotsFromAttributes() {
//        val v = inflateCustomizedDrawable()
//        assertEquals(6, v.getNumDots().toLong())
//    }
//
//    @Test
//    fun testDotColorFromAttributes() {
//        val v = inflateCustomizedDrawable()
//        assertEquals(-0x10, v.getDotColor().toLong())
//    }
//
//    @Test
//    fun testLineColorFromAttributes() {
//        val v = inflateCustomizedDrawable()
//        assertEquals(-0xf, v.getLineColor().toLong())
//    }
//
//    @Test
//    fun testFrameDelayFromAttributes() {
//        val v = inflateCustomizedDrawable()
//        assertEquals(7, v.getFrameDelay().toLong())
//    }
//
//    @Test
//    fun testStepMultiplierFromAttributes() {
//        val v = inflateCustomizedDrawable()
//        assertEquals(
//            1.1,
//            v.getStepMultiplier().toDouble(),
//            com.doctoror.particlesdrawable.Config.ASSERT_DELTA
//        )
//    }

    private fun dp(value: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().displayMetrics
        )
    }
}
