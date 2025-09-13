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
package com.doctoror.particlesdrawable.util

import org.junit.Assert.assertTrue
import org.junit.Test

class DistanceResolverTest {

    @Test
    fun distanceIsZeroForZeroCoordinates() {
        val result = DistanceResolver.distance(0f, 0f, 0f, 0f)
        assertTrue(result.compareTo(0f) == 0)
    }

    @Test
    fun distanceIsZeroForEqualCoordinates() {
        val result = DistanceResolver.distance(13f, 666f, 13f, 666f)
        assertTrue(result.compareTo(0f) == 0)
    }

    @Test
    fun calculatesDistance() {
        val result = DistanceResolver.distance(32f, 64f, 128f, 256f)
        assertTrue(result.compareTo(214.66252f) == 0)
    }
}
