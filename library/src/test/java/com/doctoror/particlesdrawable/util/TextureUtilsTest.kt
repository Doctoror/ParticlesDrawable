package com.doctoror.particlesdrawable.util

import android.graphics.Bitmap
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class TextureUtilsTest {

    @Test
    fun isPowerOfTwo() {
        assertTrue(TextureUtils.isPowerOfTwo(1))
        assertTrue(TextureUtils.isPowerOfTwo(2))
        assertTrue(TextureUtils.isPowerOfTwo(4))
        assertTrue(TextureUtils.isPowerOfTwo(8))
        assertTrue(TextureUtils.isPowerOfTwo(16))
        assertTrue(TextureUtils.isPowerOfTwo(32))
        assertTrue(TextureUtils.isPowerOfTwo(64))
        assertTrue(TextureUtils.isPowerOfTwo(128))
        assertTrue(TextureUtils.isPowerOfTwo(256))
        assertTrue(TextureUtils.isPowerOfTwo(512))
        assertTrue(TextureUtils.isPowerOfTwo(1024))
        assertTrue(TextureUtils.isPowerOfTwo(2048))
        assertTrue(TextureUtils.isPowerOfTwo(4096))
        assertTrue(TextureUtils.isPowerOfTwo(8192))
        assertTrue(TextureUtils.isPowerOfTwo(16384))
        assertTrue(TextureUtils.isPowerOfTwo(32768))
        assertTrue(TextureUtils.isPowerOfTwo(65536))
        assertTrue(TextureUtils.isPowerOfTwo(131072))

        assertFalse(TextureUtils.isPowerOfTwo(0))
        assertFalse(TextureUtils.isPowerOfTwo(3))
        assertFalse(TextureUtils.isPowerOfTwo(5))
        assertFalse(TextureUtils.isPowerOfTwo(6))
        assertFalse(TextureUtils.isPowerOfTwo(7))
        assertFalse(TextureUtils.isPowerOfTwo(10))
        assertFalse(TextureUtils.isPowerOfTwo(12))
        assertFalse(TextureUtils.isPowerOfTwo(24))
        assertFalse(TextureUtils.isPowerOfTwo(48))
        assertFalse(TextureUtils.isPowerOfTwo(255))
        assertFalse(TextureUtils.isPowerOfTwo(1000))
        assertFalse(TextureUtils.isPowerOfTwo(2049))
        assertFalse(TextureUtils.isPowerOfTwo(4097))
        assertFalse(TextureUtils.isPowerOfTwo(8190))
        assertFalse(TextureUtils.isPowerOfTwo(16385))
        assertFalse(TextureUtils.isPowerOfTwo(32769))
        assertFalse(TextureUtils.isPowerOfTwo(65537))
        assertFalse(TextureUtils.isPowerOfTwo(131073))
    }

    @Test
    fun findPreviousOrReturnIfPowerOfTwo() {
        assertEquals(1, TextureUtils.findPreviousOrReturnIfPowerOfTwo(1))
        assertEquals(2, TextureUtils.findPreviousOrReturnIfPowerOfTwo(2))
        assertEquals(2, TextureUtils.findPreviousOrReturnIfPowerOfTwo(3))
        assertEquals(4, TextureUtils.findPreviousOrReturnIfPowerOfTwo(4))
        assertEquals(4, TextureUtils.findPreviousOrReturnIfPowerOfTwo(5))
        assertEquals(4, TextureUtils.findPreviousOrReturnIfPowerOfTwo(6))
        assertEquals(4, TextureUtils.findPreviousOrReturnIfPowerOfTwo(7))
        assertEquals(8, TextureUtils.findPreviousOrReturnIfPowerOfTwo(8))
        assertEquals(8, TextureUtils.findPreviousOrReturnIfPowerOfTwo(9))
        assertEquals(8, TextureUtils.findPreviousOrReturnIfPowerOfTwo(10))
        assertEquals(8, TextureUtils.findPreviousOrReturnIfPowerOfTwo(15))
        assertEquals(16, TextureUtils.findPreviousOrReturnIfPowerOfTwo(16))
        assertEquals(16, TextureUtils.findPreviousOrReturnIfPowerOfTwo(31))
        assertEquals(32, TextureUtils.findPreviousOrReturnIfPowerOfTwo(32))
        assertEquals(32, TextureUtils.findPreviousOrReturnIfPowerOfTwo(63))
        assertEquals(32768, TextureUtils.findPreviousOrReturnIfPowerOfTwo(33000))
    }

    @Test
    fun findNextOrReturnIfPowerOfTwo() {
        assertEquals(1, TextureUtils.findNextOrReturnIfPowerOfTwo(0))
        assertEquals(1, TextureUtils.findNextOrReturnIfPowerOfTwo(1))
        assertEquals(2, TextureUtils.findNextOrReturnIfPowerOfTwo(2))
        assertEquals(4, TextureUtils.findNextOrReturnIfPowerOfTwo(3))
        assertEquals(4, TextureUtils.findNextOrReturnIfPowerOfTwo(4))
        assertEquals(8, TextureUtils.findNextOrReturnIfPowerOfTwo(5))
        assertEquals(8, TextureUtils.findNextOrReturnIfPowerOfTwo(6))
        assertEquals(8, TextureUtils.findNextOrReturnIfPowerOfTwo(7))
        assertEquals(8, TextureUtils.findNextOrReturnIfPowerOfTwo(8))
        assertEquals(16, TextureUtils.findNextOrReturnIfPowerOfTwo(9))
        assertEquals(16, TextureUtils.findNextOrReturnIfPowerOfTwo(10))
        assertEquals(16, TextureUtils.findNextOrReturnIfPowerOfTwo(15))
        assertEquals(16, TextureUtils.findNextOrReturnIfPowerOfTwo(16))
        assertEquals(32, TextureUtils.findNextOrReturnIfPowerOfTwo(17))
        assertEquals(64, TextureUtils.findNextOrReturnIfPowerOfTwo(33))
        assertEquals(128, TextureUtils.findNextOrReturnIfPowerOfTwo(65))
        assertEquals(32768, TextureUtils.findNextOrReturnIfPowerOfTwo(32767))
    }

    @Test
    fun doesNotScaleWhenAlreadyPot() {
        val input = Bitmap.createBitmap(32, 32, Bitmap.Config.RGB_565)

        val result = TextureUtils.scaleToSmallerPot(input)

        assertTrue(result === input)
    }

    @Test
    fun scalesToSmallerPotWhenOneSideNpot() {
        val input = Bitmap.createBitmap(32, 50, Bitmap.Config.RGB_565)

        val result = TextureUtils.scaleToSmallerPot(input)

        assertEquals(32, result.width)
        assertEquals(32, result.height)
    }

    @Test
    fun scalesToSmallerPotWhenTwoSidesNpot() {
        val input = Bitmap.createBitmap(144, 144, Bitmap.Config.RGB_565)

        val result = TextureUtils.scaleToSmallerPot(input)

        assertEquals(128, result.width)
        assertEquals(128, result.height)
    }
}
