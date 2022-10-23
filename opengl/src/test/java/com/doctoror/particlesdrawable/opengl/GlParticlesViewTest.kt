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
package com.doctoror.particlesdrawable.opengl

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import androidx.annotation.StyleableRes
import androidx.test.core.app.ApplicationProvider
import com.doctoror.particlesdrawable.engine.Engine
import com.doctoror.particlesdrawable.engine.SceneConfigurator
import com.doctoror.particlesdrawable.model.Scene
import com.doctoror.particlesdrawable.opengl.renderer.GlSceneRenderer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowResources
import java.util.*
import javax.microedition.khronos.opengles.GL10

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class GlParticlesViewTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val engine: Engine = mock()
    private val scene: Scene = mock()
    private val sceneConfigurator: SceneConfigurator = mock()
    private val renderer: GlSceneRenderer = mock()

    private val underTest = newInstanceWithAttrs(null)

    private fun newInstanceWithAttrs(attributeSet: AttributeSet?) = spy(
        GlParticlesView(
            context,
            attributeSet,
            engine,
            scene,
            sceneConfigurator,
            renderer,
            4,
            null
        )
    ).apply {
        doAnswer { (it.arguments[0] as Runnable).run() }.`when`(this).queueEvent(any())
    }

    @Test
    fun configuresSceneOnInit() {
        val attributeSet: AttributeSet = mock()

        newInstanceWithAttrs(attributeSet)

        verify(sceneConfigurator).configureSceneFromAttributes(
            scene, context.resources, attributeSet
        )
    }

    @Config(
        manifest = Config.NONE,
        shadows = [WindowBackgroundShadowTheme::class]
    )
    @Test
    fun backgroundIsSetFromWindowBackground() {
        underTest.onSurfaceCreated(mock(), mock())
        underTest.onDrawFrame(mock())
        verify(renderer).setClearColor(SHADOW_WINDOW_BACKROUND)
    }

    @Test
    fun marksParticleTextureDirtyOnInit() {
        verify(renderer).markParticleTextureDirty()
    }

    @Test
    fun setBackgroundColorIsDeliveredOnDraw() {
        val color = Color.MAGENTA
        underTest.setBackgroundColor(color)

        underTest.onDrawFrame(mock())

        verify(renderer).setClearColor(color)
    }

    @Test
    fun setBackgroundColorIsDeliveredOnDrawOnlyOnce() {
        val color = Color.MAGENTA
        underTest.setBackgroundColor(color)

        val gL10: GL10 = mock()
        underTest.onDrawFrame(gL10)
        underTest.onDrawFrame(gL10)

        verify(renderer).setClearColor(color)
    }

    @Test
    fun colorDrawableIsConvertedToBackgroundColor() {
        val color = Color.BLUE
        underTest.background = ColorDrawable(color)

        underTest.onDrawFrame(mock())

        verify(renderer).setClearColor(color)
    }

    @Test
    fun colorDrawableIsConvertedToBackgroundColorAndDeliveredOnlyOnce() {
        val color = Color.BLUE
        underTest.background = ColorDrawable(color)

        val gL10: GL10 = mock()
        underTest.onDrawFrame(gL10)
        underTest.onDrawFrame(gL10)

        verify(renderer).setClearColor(color)
    }

    @Test
    fun bitmapBackgroundIsDeliveredAsTexture() {
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        underTest.background = BitmapDrawable(context.resources, bitmap)

        underTest.onDrawFrame(mock())

        verify(renderer).setBackgroundTexture(bitmap)
    }

    @Test
    fun bitmapBackgroundIsDeliveredAsTextureOnlyOnce() {
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        underTest.background = BitmapDrawable(context.resources, bitmap)

        val gL10: GL10 = mock()
        underTest.onDrawFrame(gL10)
        underTest.onDrawFrame(gL10)

        verify(renderer).setBackgroundTexture(bitmap)
    }

    @Test
    fun bitmapBackgroundDrawableIsDeliveredAsTextureOnlyOnce() {
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        @Suppress("DEPRECATION")
        underTest.setBackgroundDrawable(BitmapDrawable(context.resources, bitmap))

        val gL10: GL10 = mock()
        underTest.onDrawFrame(gL10)
        underTest.onDrawFrame(gL10)

        verify(renderer).setBackgroundTexture(bitmap)
    }

    @Test
    fun backgroundIsResetWhenSetToNull() {
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        underTest.background = BitmapDrawable(context.resources, bitmap)

        val gL10: GL10 = mock()
        underTest.onDrawFrame(gL10)
        verify(renderer).setBackgroundTexture(bitmap)

        underTest.background = null

        underTest.onDrawFrame(gL10)
        verify(renderer).setBackgroundTexture(null)
    }

    @Test
    fun setsDimensionsOnSurfaceChange() {
        val width = 240
        val height = 320

        underTest.onSurfaceChanged(mock(), width, height)

        verify(engine).setDimensions(width, height)
        verify(renderer).setDimensions(width, height)
    }

    @Test
    fun drawsAndRuns() {
        underTest.onDrawFrame(mock())

        val inorder = inOrder(engine)
        inorder.verify(engine).draw()
        inorder.verify(engine).run()
    }

    @Test
    fun reqeustsRenderOnScheduleNextFrameWithZeroDelay() {
        underTest.scheduleNextFrame(0)
        verify(underTest).requestRender()
    }

    @Test
    fun postsRunnableOnScheduleNextFrameWithNonZeroDelay() {
        val delay = 36000L
        underTest.scheduleNextFrame(delay)
        verify(underTest).postDelayed(any(), eq(delay))
    }

    @Test
    fun doesNotCrashOnUnscheduleNextFrame() {
        underTest.unscheduleNextFrame()
    }

    @Test
    fun resumesAndStartsEngineOnStart() {
        underTest.start()
        verify(underTest).onResume()
        verify(engine).start()
    }

    @Test
    fun stopsEngineAndPausesOnStop() {
        underTest.stop()
        verify(engine).stop()
        verify(underTest).onPause()
    }

    @Test
    fun recyclesRendererOnStop() {
        underTest.stop()
        verify(renderer).recycle()
    }

    @Test
    fun recyclesAndSetsUpRendererOnSurfaceCreated() {
        underTest.onSurfaceCreated(mock(), mock())

        val inorder = inOrder(renderer)
        inorder.verify(renderer).recycle()
        inorder.verify(renderer).setupGl()
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
        verify(renderer, atLeastOnce()).markParticleTextureDirty()
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

private const val SHADOW_WINDOW_BACKROUND = Color.CYAN

@Implements(value = Resources.Theme::class)
class WindowBackgroundShadowTheme : ShadowResources.ShadowTheme() {

    @Suppress("unused")
    @Implementation
    fun obtainStyledAttributes(@StyleableRes attrs: IntArray): TypedArray {
        val typedArray: TypedArray = mock()
        if (Arrays.equals(attrs, intArrayOf(android.R.attr.windowBackground))) {

            whenever(typedArray.getColor(0, Color.DKGRAY))
                .thenReturn(SHADOW_WINDOW_BACKROUND)

        }
        return typedArray
    }
}
