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
package com.doctoror.particlesdrawable.opengl.util

import android.util.Pair
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class PotCalculatorTest {

    @Test
    fun isPowerOfTwo() {
        assertTrue(PotCalculator.isPowerOfTwo(1))
        assertTrue(PotCalculator.isPowerOfTwo(2))
        assertTrue(PotCalculator.isPowerOfTwo(4))
        assertTrue(PotCalculator.isPowerOfTwo(8))
        assertTrue(PotCalculator.isPowerOfTwo(16))
        assertTrue(PotCalculator.isPowerOfTwo(32))
        assertTrue(PotCalculator.isPowerOfTwo(64))
        assertTrue(PotCalculator.isPowerOfTwo(128))
        assertTrue(PotCalculator.isPowerOfTwo(256))
        assertTrue(PotCalculator.isPowerOfTwo(512))
        assertTrue(PotCalculator.isPowerOfTwo(1024))
        assertTrue(PotCalculator.isPowerOfTwo(2048))
        assertTrue(PotCalculator.isPowerOfTwo(4096))
        assertTrue(PotCalculator.isPowerOfTwo(8192))
        assertTrue(PotCalculator.isPowerOfTwo(16384))
        assertTrue(PotCalculator.isPowerOfTwo(32768))
        assertTrue(PotCalculator.isPowerOfTwo(65536))
        assertTrue(PotCalculator.isPowerOfTwo(131072))

        assertFalse(PotCalculator.isPowerOfTwo(0))
        assertFalse(PotCalculator.isPowerOfTwo(3))
        assertFalse(PotCalculator.isPowerOfTwo(5))
        assertFalse(PotCalculator.isPowerOfTwo(6))
        assertFalse(PotCalculator.isPowerOfTwo(7))
        assertFalse(PotCalculator.isPowerOfTwo(10))
        assertFalse(PotCalculator.isPowerOfTwo(12))
        assertFalse(PotCalculator.isPowerOfTwo(24))
        assertFalse(PotCalculator.isPowerOfTwo(48))
        assertFalse(PotCalculator.isPowerOfTwo(255))
        assertFalse(PotCalculator.isPowerOfTwo(1000))
        assertFalse(PotCalculator.isPowerOfTwo(2049))
        assertFalse(PotCalculator.isPowerOfTwo(4097))
        assertFalse(PotCalculator.isPowerOfTwo(8190))
        assertFalse(PotCalculator.isPowerOfTwo(16385))
        assertFalse(PotCalculator.isPowerOfTwo(32769))
        assertFalse(PotCalculator.isPowerOfTwo(65537))
        assertFalse(PotCalculator.isPowerOfTwo(131073))
    }

    @Test
    fun findPreviousOrReturnIfPowerOfTwo() {
        assertEquals(1, PotCalculator.findPreviousOrReturnIfPowerOfTwo(1))
        assertEquals(2, PotCalculator.findPreviousOrReturnIfPowerOfTwo(2))
        assertEquals(2, PotCalculator.findPreviousOrReturnIfPowerOfTwo(3))
        assertEquals(4, PotCalculator.findPreviousOrReturnIfPowerOfTwo(4))
        assertEquals(4, PotCalculator.findPreviousOrReturnIfPowerOfTwo(5))
        assertEquals(4, PotCalculator.findPreviousOrReturnIfPowerOfTwo(6))
        assertEquals(4, PotCalculator.findPreviousOrReturnIfPowerOfTwo(7))
        assertEquals(8, PotCalculator.findPreviousOrReturnIfPowerOfTwo(8))
        assertEquals(8, PotCalculator.findPreviousOrReturnIfPowerOfTwo(9))
        assertEquals(8, PotCalculator.findPreviousOrReturnIfPowerOfTwo(10))
        assertEquals(8, PotCalculator.findPreviousOrReturnIfPowerOfTwo(15))
        assertEquals(16, PotCalculator.findPreviousOrReturnIfPowerOfTwo(16))
        assertEquals(16, PotCalculator.findPreviousOrReturnIfPowerOfTwo(31))
        assertEquals(32, PotCalculator.findPreviousOrReturnIfPowerOfTwo(32))
        assertEquals(32, PotCalculator.findPreviousOrReturnIfPowerOfTwo(63))
        assertEquals(32768, PotCalculator.findPreviousOrReturnIfPowerOfTwo(33000))
    }

    @Test
    fun findNextOrReturnIfPowerOfTwo() {
        assertEquals(1, PotCalculator.findNextOrReturnIfPowerOfTwo(0))
        assertEquals(1, PotCalculator.findNextOrReturnIfPowerOfTwo(1))
        assertEquals(2, PotCalculator.findNextOrReturnIfPowerOfTwo(2))
        assertEquals(4, PotCalculator.findNextOrReturnIfPowerOfTwo(3))
        assertEquals(4, PotCalculator.findNextOrReturnIfPowerOfTwo(4))
        assertEquals(8, PotCalculator.findNextOrReturnIfPowerOfTwo(5))
        assertEquals(8, PotCalculator.findNextOrReturnIfPowerOfTwo(6))
        assertEquals(8, PotCalculator.findNextOrReturnIfPowerOfTwo(7))
        assertEquals(8, PotCalculator.findNextOrReturnIfPowerOfTwo(8))
        assertEquals(16, PotCalculator.findNextOrReturnIfPowerOfTwo(9))
        assertEquals(16, PotCalculator.findNextOrReturnIfPowerOfTwo(10))
        assertEquals(16, PotCalculator.findNextOrReturnIfPowerOfTwo(15))
        assertEquals(16, PotCalculator.findNextOrReturnIfPowerOfTwo(16))
        assertEquals(32, PotCalculator.findNextOrReturnIfPowerOfTwo(17))
        assertEquals(64, PotCalculator.findNextOrReturnIfPowerOfTwo(33))
        assertEquals(128, PotCalculator.findNextOrReturnIfPowerOfTwo(65))
        assertEquals(32768, PotCalculator.findNextOrReturnIfPowerOfTwo(32767))
    }

    @Test
    fun returnsSameDimensionsWhenWidthIs0() {
        val result = PotCalculator.toLargerPotDimensions(0, 32)
        assertEquals(Pair(0, 32), result)
    }

    @Test
    fun returnsSameDimensionsWhenHeightIs0() {
        val result = PotCalculator.toLargerPotDimensions(0, 32)
        assertEquals(Pair(0, 32), result)
    }

    @Test
    fun returnsSameDimensionsWhenBothDimensionsAre0() {
        val result = PotCalculator.toLargerPotDimensions(0, 0)
        assertEquals(Pair(0, 0), result)
    }

    @Test
    fun returnsSameDimensionsWhenBothDimensionsAreNegative() {
        val result = PotCalculator.toLargerPotDimensions(-1, -2)
        assertEquals(Pair(-1, -2), result)
    }

    @Test
    fun largerPotReturnsTheSameDimensionsWhenAlreadyPot() {
        val result = PotCalculator.toLargerPotDimensions(32, 32)
        assertEquals(Pair(32, 32), result)
    }

    @Test
    fun scalesToLargerPotWhenOneDimensionNpot() {
        val result = PotCalculator.toLargerPotDimensions(32, 50)
        assertEquals(Pair(32, 64), result)
    }

    @Test
    fun scalesToLargerPotWhenTwoDimensionsNpot() {
        val result = PotCalculator.toLargerPotDimensions(62, 96)
        assertEquals(Pair(64, 128), result)
    }
}
