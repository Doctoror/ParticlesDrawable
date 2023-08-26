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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

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
}
