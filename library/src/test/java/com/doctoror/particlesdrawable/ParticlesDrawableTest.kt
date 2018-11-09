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
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.SystemClock
import android.util.AttributeSet
import com.doctoror.particlesdrawable.engine.Engine
import com.doctoror.particlesdrawable.engine.SceneConfigurator
import com.doctoror.particlesdrawable.model.Scene
import com.doctoror.particlesdrawable.renderer.CanvasSceneRenderer
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.xmlpull.v1.XmlPullParser
import java.lang.reflect.Modifier

@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.LOLLIPOP])
@RunWith(RobolectricTestRunner::class)
class ParticlesDrawableTest {

    private val canvasRenderer: CanvasSceneRenderer = mock()
    private val scene: Scene = mock()
    private val sceneConfigurator: SceneConfigurator = mock()
    private val engine: Engine = mock()

    private val underTest = spy(ParticlesDrawable().apply {
        overrideMember(this, "canvasRenderer", canvasRenderer)
        overrideMember(this, "scene", scene)
        overrideMember(this, "sceneConfigurator", sceneConfigurator)
        overrideMember(this, "engine", engine)
    })

    private fun overrideMember(target: Any, name: String, value: Any) {
        val field = target::class.java.getDeclaredField(name)
        field.isAccessible = true

        val modifiers = field.javaClass.getDeclaredField("modifiers")
        modifiers.isAccessible = true
        modifiers.setInt(field, field.modifiers and Modifier.FINAL.inv())

        field.set(target, value)
    }

    @Test
    fun inflates() {
        val attrs: AttributeSet = mock()
        val typedArray: TypedArray = mock()
        val resources: Resources = mock {
            on(it.obtainAttributes(eq(attrs), any())).thenReturn(typedArray)
        }

        val xmlPullParser: XmlPullParser = mock()

        underTest.inflate(resources, xmlPullParser, attrs, null)

        verify(sceneConfigurator).configureSceneFromAttributes(scene, resources, attrs)
    }

    @Test
    fun setsDimensionsOnBoundsChange() {
        val width = 240
        val height = 320

        underTest.setBounds(0, 0, width, height)

        verify(engine).setDimensions(width, height)
    }

    @Test
    fun drawsAndRuns() {
        val canvas: Canvas = mock()

        underTest.draw(canvas)

        val inorder = inOrder(canvasRenderer, engine)
        inorder.verify(canvasRenderer).setCanvas(canvas)
        inorder.verify(engine).draw()
        inorder.verify(canvasRenderer).setCanvas(null)
        inorder.verify(engine).run()
    }

    @Test
    fun invalidatesSelfOnRequestRender() {
        underTest.requestRender()
        verify(underTest).invalidateSelf()
    }

    @Test
    fun reqeustsRenderOnScheduleNextFrameWithZeroDelay() {
        underTest.scheduleNextFrame(0)
        verify(underTest).requestRender()
    }

    @Test
    fun schedulesSelfOnScheduleNextFrameWithNonZeroDelay() {
        val delay = 36000L

        underTest.scheduleNextFrame(delay)

        val timeCaptor = argumentCaptor<Long>()
        verify(underTest).scheduleSelf(any(), timeCaptor.capture())

        assertEquals(SystemClock.uptimeMillis() + delay, timeCaptor.firstValue)
    }

    @Test
    fun unschedulesSelfOnUnscheduleNextFrame() {
        underTest.unscheduleNextFrame()
        verify(underTest).unscheduleSelf(any())
    }

    @Test
    fun setsAlphaToEngine() {
        val alpha = 64
        underTest.alpha = alpha
        verify(engine).alpha = alpha
    }

    @Test
    fun returnsAlphaFromEngine() {
        val alpha = 128
        whenever(engine.alpha).thenReturn(alpha)
        assertEquals(alpha, underTest.alpha)
    }

    @Test
    fun setsColorFilterToRenderer() {
        val colorFilter: ColorFilter = mock()
        underTest.colorFilter = colorFilter
        verify(canvasRenderer).setColorFilter(colorFilter)
    }

    @Test
    fun opacityIsTranslucent() {
        assertEquals(PixelFormat.TRANSLUCENT, underTest.opacity)
    }

    @Test
    fun startsEngineOnStart() {
        underTest.start()
        verify(engine).start()
    }

    @Test
    fun stopsEngineOnStop() {
        underTest.stop()
        verify(engine).stop()
    }

    @Test
    fun returnsRunningStateFromEngine() {
        whenever(engine.isRunning).thenReturn(true)
        assertTrue(underTest.isRunning)
    }

    @Test
    fun forwardsNextFrameToEngine() {
        underTest.nextFrame()
        verify(engine).nextFrame()
    }

    @Test
    fun forwardsMakeFreshFrameToEngine() {
        underTest.makeFreshFrame()
        verify(engine).makeFreshFrame()
    }

    @Test
    fun forwardsMakeFreshFrameWithParticlesOffScreenToEngine() {
        underTest.makeFreshFrameWithParticlesOffscreen()
        verify(engine).makeFreshFrameWithParticlesOffscreen()
    }

    @Test
    fun forwardsSetDensityToScene() {
        val value = 1
        underTest.density = value
        verify(scene).density = value
    }

    @Test
    fun returnsDensityFromScene() {
        val value = 2
        whenever(scene.density).thenReturn(value)
        assertEquals(value, underTest.density)
    }

    @Test
    fun forwardsSetFrameDelayToScene() {
        val value = 3
        underTest.frameDelay = value
        verify(scene).frameDelay = value
    }

    @Test
    fun returnsFrameDelayFromScene() {
        val value = 4
        whenever(scene.frameDelay).thenReturn(value)
        assertEquals(value, underTest.frameDelay)
    }

    @Test
    fun forwardsSetParticleColorToScene() {
        val value = 5
        underTest.particleColor = value
        verify(scene).particleColor = value
    }

    @Test
    fun returnsParticleColorFromScene() {
        val value = 6
        whenever(scene.particleColor).thenReturn(value)
        assertEquals(value, underTest.particleColor)
    }

    @Test
    fun forwardsSetParticleRadiusRangeToScene() {
        val minRadius = 7f
        val maxRadius = 8f

        underTest.setParticleRadiusRange(minRadius, maxRadius)

        verify(scene).setParticleRadiusRange(minRadius, maxRadius)
    }

    @Test
    fun returnsParticleRadiusMaxFromScene() {
        val value = 9f
        whenever(scene.particleRadiusMax).thenReturn(value)
        assertEquals(value, underTest.particleRadiusMax)
    }

    @Test
    fun returnsParticleRadiusMinFromScene() {
        val value = 10f
        whenever(scene.particleRadiusMin).thenReturn(value)
        assertEquals(value, underTest.particleRadiusMin)
    }

    @Test
    fun forwardsSetLineColorToScene() {
        val value = 11
        underTest.lineColor = value
        verify(scene).lineColor = value
    }

    @Test
    fun returnsLineColorFromScene() {
        val value = 12
        whenever(scene.lineColor).thenReturn(value)
        assertEquals(value, underTest.lineColor)
    }

    @Test
    fun forwardsSetLineLengthToScene() {
        val value = 13f
        underTest.lineLength = value
        verify(scene).lineLength = value
    }

    @Test
    fun returnsLineLengthFromScene() {
        val value = 14f
        whenever(scene.lineLength).thenReturn(value)
        assertEquals(value, underTest.lineLength)
    }

    @Test
    fun forwardsSetLineThicknessToScene() {
        val value = 15f
        underTest.lineThickness = value
        verify(scene).lineThickness = value
    }

    @Test
    fun returnsLineThicknessFromScene() {
        val value = 16f
        whenever(scene.lineThickness).thenReturn(value)
        assertEquals(value, underTest.lineThickness)
    }

    @Test
    fun forwardsSetSpeedFactorToScene() {
        val value = 17f
        underTest.speedFactor = value
        verify(scene).speedFactor = value
    }

    @Test
    fun returnsSpeedFactorFromScene() {
        val value = 18f
        whenever(scene.speedFactor).thenReturn(value)
        assertEquals(value, underTest.speedFactor)
    }
}
