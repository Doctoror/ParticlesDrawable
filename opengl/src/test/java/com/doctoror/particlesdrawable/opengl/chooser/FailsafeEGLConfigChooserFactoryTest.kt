package com.doctoror.particlesdrawable.opengl.chooser

import org.junit.Assert.assertTrue
import org.junit.Test

class FailsafeEGLConfigChooserFactoryTest {

    @Test
    fun returnsFailsafeRGB888ConfigChooserForZeroSamples() {
        val result = FailsafeEGLConfigChooserFactory.newFailsafeEGLConfigChooser(0, null)
        assertTrue(result is FailsafeRGB888ConfigChooser)
    }

    @Test
    fun returnsFailsafeMultisamplingConfigChooserForPositiveNumberOfsamples() {
        val result = FailsafeEGLConfigChooserFactory.newFailsafeEGLConfigChooser(2, null)
        assertTrue(result is FailsafeMultisamplingConfigChooser)
    }
}
