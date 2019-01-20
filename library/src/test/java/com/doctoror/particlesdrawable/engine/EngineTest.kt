package com.doctoror.particlesdrawable.engine

import com.doctoror.particlesdrawable.model.Scene
import com.doctoror.particlesdrawable.contract.SceneRenderer
import com.doctoror.particlesdrawable.contract.SceneScheduler
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EngineTest {

    private val frameDelay = 10

    private val frameAdvancer: FrameAdvancer = mock()
    private val particleGenerator: ParticleGenerator = mock()
    private val scene: Scene = mock {
        on(it.frameDelay).thenReturn(frameDelay)
    }
    private val scheduler: SceneScheduler = mock()
    private val renderer: SceneRenderer = mock()
    private val timeProvider: TimeProvider = mock()

    private val underTest = Engine(
        frameAdvancer,
        particleGenerator,
        scene,
        scheduler,
        renderer,
        timeProvider
    )

    @Test
    fun isRunningByDefault() {
        assertFalse(underTest.isRunning)
    }

    @Test
    fun isRunningIsTrueWhenStarted() {
        underTest.start()
        assertTrue(underTest.isRunning)
    }

    @Test
    fun isRunningIsFalseWhenStopped() {
        underTest.start()
        underTest.stop()
        assertFalse(underTest.isRunning)
    }

    @Test
    fun forwardsAlphaToScene() {
        val alpha = 128
        underTest.alpha = alpha
        verify(scene).alpha = alpha
    }

    @Test
    fun returnsAlphaFromScene() {
        val alpha = 128
        whenever(scene.alpha).thenReturn(alpha)
        assertEquals(alpha, underTest.alpha)
    }

    @Test
    fun advancesFrameAndSchedulesNextFrameOnStart() {
        underTest.start()

        verifyAdvancesFrameAndSchedulesNextFrame()
    }

    @Test
    fun advancesFrameAndSchedulesNextFrameOnStartOnlyOnce() {
        underTest.start()
        underTest.start()

        verifyAdvancesFrameAndSchedulesNextFrame()
    }

    @Test
    fun onStopDoesNothingIfNotStarted() {
        underTest.stop()
        verifyZeroInteractions(scheduler)
    }

    @Test
    fun unschedulesNextFrameOnStop() {
        underTest.start()
        underTest.stop()

        verify(scheduler).unscheduleNextFrame()
    }

    @Test
    fun canBeRestartedAfterStop() {
        underTest.start()

        verifyAdvancesFrameAndSchedulesNextFrame()

        underTest.stop()
        underTest.start()

        verifyAdvancesFrameAndSchedulesNextFrame()
    }

    @Test
    fun frameTimeAffectsStep() {
        val frameTime1 = 128L
        whenever(timeProvider.uptimeMillis()).thenReturn(frameTime1)
        underTest.nextFrame()
        verify(frameAdvancer).advanceToNextFrame(scene, 1f)

        val timeWhenFrame2Starts = 512L
        whenever(timeProvider.uptimeMillis()).thenReturn(timeWhenFrame2Starts)
        underTest.nextFrame()

        val stepPerFrame = 0.05f
        verify(frameAdvancer)
            .advanceToNextFrame(scene, (timeWhenFrame2Starts - frameTime1) * stepPerFrame)
    }

    @Test
    fun frameTimeResetOnMakeFreshFrame() {
        verifyFrameTimeResetsWhenFunctionInvoked { underTest.makeFreshFrame() }
    }

    @Test
    fun frameTimeResetOnMakeFreshFrameWithParticlesOffScreen() {
        verifyFrameTimeResetsWhenFunctionInvoked {
            underTest.makeFreshFrameWithParticlesOffscreen()
        }
    }

    @Test
    fun frameTimeResetOnStart() {
        verifyFrameTimeResetsWhenFunctionInvoked(1) { underTest.start() }
    }

    @Test
    fun frameTimeResetOnStop() {
        verifyFrameTimeResetsWhenFunctionInvoked { underTest.stop() }
    }

    @Test
    fun frameTimeResetOnRunWhenNotAnimating() {
        verifyFrameTimeResetsWhenFunctionInvoked(1) { underTest.run() }
    }

    private fun verifyFrameTimeResetsWhenFunctionInvoked(
        effectsOnAdvanceToNextFrame: Int = 0,
        func: () -> Unit
    ) {
        val frameTime1 = 128L
        whenever(timeProvider.uptimeMillis()).thenReturn(frameTime1)
        underTest.nextFrame()
        verify(frameAdvancer).advanceToNextFrame(scene, 1f)

        val timeWhenFrame2Starts = 512L
        whenever(timeProvider.uptimeMillis()).thenReturn(timeWhenFrame2Starts)

        func()

        underTest.nextFrame()
        verify(frameAdvancer, times(1 + effectsOnAdvanceToNextFrame))
            .advanceToNextFrame(scene, 1f)
    }

    @Test
    fun drawsSceneOnDraw() {
        underTest.draw()
        verify(renderer).drawScene(scene)
    }

    @Test
    fun drawDurationAffectsScheduling() {
        val givenFrameDelay = 40
        whenever(scene.frameDelay).thenReturn(givenFrameDelay)

        underTest.start()

        val givenDrawDuration = 10L
        givenDifferenceBetweenUptime(givenDrawDuration)

        underTest.draw()
        underTest.run()

        verify(scheduler).scheduleNextFrame(givenFrameDelay - givenDrawDuration)
    }

    @Test
    fun willNotScheduleWithNegativeDelay() {
        underTest.start()

        val givenDrawDuration = 20L
        givenDifferenceBetweenUptime(givenDrawDuration)

        underTest.draw()
        underTest.run()

        verify(scheduler).scheduleNextFrame(0)
    }

    @Test
    fun doesNotMakeeFreshFrameWhenDimensionsNotSet() {
        underTest.makeFreshFrame()
        verifyZeroInteractions(particleGenerator)
    }

    @Test
    fun doesNotMakeeFreshFrameWhenParticlesCountIsZero() {
        givenSceneDimensions(1, 1)

        underTest.makeFreshFrame()

        verifyZeroInteractions(particleGenerator)
    }

    @Test
    fun makesFreshFrame() {
        givenSceneDimensions(1, 1)

        val particlesCount = 7
        whenever(scene.density).thenReturn(particlesCount)

        underTest.makeFreshFrame()

        verifyMakesFreshFrame(particlesCount)
    }

    @Test
    fun doesNotMakeeFreshFrameWithParticlesOffScreenWhenDimensionsNotSet() {
        underTest.makeFreshFrameWithParticlesOffscreen()
        verifyZeroInteractions(particleGenerator)
    }

    @Test
    fun doesNotMakeeFreshFrameWithParticlesOffScreenWhenParticlesCountIsZero() {
        givenSceneDimensions(1, 1)

        underTest.makeFreshFrameWithParticlesOffscreen()

        verifyZeroInteractions(particleGenerator)
    }

    @Test
    fun makesFreshFrameWithParticlesOffScreen() {
        givenSceneDimensions(1, 1)

        val particlesCount = 4
        whenever(scene.density).thenReturn(particlesCount)

        underTest.makeFreshFrameWithParticlesOffscreen()

        val inorder = inOrder(particleGenerator)
        inorder.verify(particleGenerator).applyFreshParticleOffScreen(scene, 0)
        inorder.verify(particleGenerator).applyFreshParticleOffScreen(scene, 1)
        inorder.verify(particleGenerator).applyFreshParticleOffScreen(scene, 2)
        inorder.verify(particleGenerator).applyFreshParticleOffScreen(scene, 3)
    }

    @Test
    fun forwardsDimensionsToScene() {
        val width = 1
        val height = 2

        whenever(scene.width).thenReturn(width)
        whenever(scene.height).thenReturn(height)

        underTest.setDimensions(width, height)

        verify(scene).width = width
        verify(scene).height = height
    }

    @Test
    fun makesFreshFrameOnDimensionsChange() {
        val width = 1
        val height = 2
        givenSceneDimensions(width, height)

        val particlesCount = 3
        whenever(scene.density).thenReturn(particlesCount)

        underTest.setDimensions(width, height)

        verifyMakesFreshFrame(particlesCount)
        verifyNoMoreInteractions(particleGenerator)
    }

    @Test
    fun makesFreshFrameOnDimensionsChangeOnlyOnce() {
        val width = 1
        val height = 2
        givenSceneDimensions(width, height)

        val particlesCount = 3
        whenever(scene.density).thenReturn(particlesCount)

        underTest.setDimensions(width, height)
        underTest.setDimensions(width, height)
        underTest.setDimensions(width, height)

        verifyMakesFreshFrame(particlesCount)
        verifyNoMoreInteractions(particleGenerator)
    }

    @Test
    fun makesFreshFrameOnDimensionsChangeWhenReset() {
        val width = 1
        val height = 2
        givenSceneDimensions(width, height)

        val particlesCount = 3
        whenever(scene.density).thenReturn(particlesCount)

        underTest.setDimensions(width, height)
        underTest.setDimensions(0, 0)
        underTest.setDimensions(width, height)

        verifyMakesFreshFrame(particlesCount, 2)
        verifyNoMoreInteractions(particleGenerator)
    }

    private fun verifyMakesFreshFrame(particlesCount: Int, times: Int = 1) {
        val inorder = inOrder(particleGenerator)
        repeat(times) {
            repeat(particlesCount) { position ->
                if (position % 2 == 0) {
                    inorder.verify(particleGenerator).applyFreshParticleOnScreen(scene, position)
                } else {
                    inorder.verify(particleGenerator).applyFreshParticleOffScreen(scene, position)
                }
            }
        }
    }

    private fun givenDifferenceBetweenUptime(delta: Long) {
        var firstTimeCall = true
        whenever(timeProvider.uptimeMillis()).thenAnswer {
            if (firstTimeCall) {
                firstTimeCall = false
                666L
            } else {
                666L + delta
            }
        }
    }

    private fun givenSceneDimensions(width: Int, height: Int) {
        whenever(scene.width).thenReturn(width)
        whenever(scene.height).thenReturn(height)
    }

    private fun verifyAdvancesFrameAndSchedulesNextFrame() {
        val inorder = inOrder(frameAdvancer, scheduler)
        inorder.verify(frameAdvancer).advanceToNextFrame(scene, 1f)
        inorder.verify(scheduler).scheduleNextFrame(frameDelay.toLong())
    }
}
