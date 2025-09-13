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

import android.content.Context
import android.graphics.Canvas
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.doctoror.particlesdrawable.engine.Engine
import com.doctoror.particlesdrawable.engine.SceneConfigurator
import com.doctoror.particlesdrawable.model.Scene
import com.doctoror.particlesdrawable.renderer.CanvasSceneRenderer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ParticlesViewTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val canvasRenderer: CanvasSceneRenderer = mock()
    private val scene: Scene = mock()
    private val sceneConfigurator: SceneConfigurator = mock()
    private val engine: Engine = mock()

    private val underTest = spy(ParticlesView(context).apply {
        overridePrivateMember(this, "canvasSceneRenderer", canvasRenderer)
        overridePrivateMember(this, "scene", scene)
        overridePrivateMember(this, "sceneConfigurator", sceneConfigurator)
        overridePrivateMember(this, "engine", engine)
        setEmulateOnAttachToWindow(true)
    })

    @Test
    fun setsDimensionsOnSizeChange() {
        val width = 240
        val height = 320

        underTest.onSizeChanged(width, height, 0, 0)

        verify(engine).setDimensions(width, height)
    }

    @Test
    fun drawsAndRuns() {
        val canvas: Canvas = mock()

        underTest.onDraw(canvas)

        val inorder = inOrder(canvasRenderer, engine)
        inorder.verify(canvasRenderer).setCanvas(canvas)
        inorder.verify(engine).draw()
        inorder.verify(canvasRenderer).setCanvas(null)
        inorder.verify(engine).run()
    }

    @Test
    fun invalidatesOnRequestRender() {
        underTest.requestRender()
        verify(underTest).invalidate()
    }

    @Test
    fun reqeustsRenderOnScheduleNextFrameWithZeroDelay() {
        underTest.scheduleNextFrame(0)
        verify(underTest).requestRender()
    }

    @Test
    fun postInvalidatesDelayedScheduleNextFrameWithNonZeroDelay() {
        val delay = 36000L

        underTest.scheduleNextFrame(delay)

        verify(underTest).postInvalidateDelayed(delay)
    }

    @Test
    fun doesNotCrashOnOnUnscheduleNextFrame() {
        underTest.unscheduleNextFrame()
    }

    @Test
    fun doesNotStartWhenVisibleButNotAttachedToWindow() {
        underTest.visibility = View.VISIBLE
        verify(engine, never()).start()
    }

    @Test
    fun doesNotStartAttachedToWindowButNotVisible() {
        underTest.visibility = View.VISIBLE
        verify(engine, never()).start()
    }

    @Test
    fun startsEngineOnAttachToWindowWhenVisible() {
        underTest.visibility = View.VISIBLE
        underTest.onAttachedToWindow()

        verify(engine).start()
    }

    @Test
    fun startsEngineOnVisibiltyChangeWhenAttachedToWindow() {
        underTest.onAttachedToWindow()
        underTest.visibility = View.VISIBLE

        verify(engine).start()
    }

    @Test
    fun startDoesNotStartEngineWhenNotAttachedToWindow() {
        underTest.visibility = View.VISIBLE

        underTest.start()

        verify(engine, never()).start()
    }

    @Test
    fun startDoesNotStartEngineWhenNotVisible() {
        underTest.visibility = View.INVISIBLE
        underTest.onAttachedToWindow()

        underTest.start()

        verify(engine, never()).start()
    }

    @Test
    fun doesNotStartEngineOnAttachToWindowWhenVisibleIfExplicitlyStopped() {
        underTest.stop()

        underTest.visibility = View.VISIBLE
        underTest.onAttachedToWindow()

        verify(engine, never()).start()
    }

    @Test
    fun startsEngineOnStartIfConditionsAllowAndWasExplicitlyStopped() {
        underTest.stop()

        underTest.visibility = View.VISIBLE
        underTest.onAttachedToWindow()

        underTest.start()

        verify(engine).start()
    }

    @Test
    fun stopsEngineOnStop() {
        underTest.stop()
        verify(engine).stop()
    }

    @Test
    @Ignore // TODO Broken as part of Robolectric 4.10.3 -> 4.16 update
    fun stopsEngineWhenNotVisible() {
        underTest.visibility = View.INVISIBLE
        verify(engine).stop()
    }

    @Test
    @Ignore // TODO Broken as part of Robolectric 4.10.3 -> 4.16 update
    fun stopsEngineOnDetachFromWindow() {
        underTest.visibility = View.INVISIBLE
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
