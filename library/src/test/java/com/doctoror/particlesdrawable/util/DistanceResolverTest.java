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
package com.doctoror.particlesdrawable.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public final class DistanceResolverTest {

    @Test
    public void distanceIsZeroForZeroCoordinates() {
        final float result = DistanceResolver.distance(0, 0, 0, 0);
        assertTrue(Float.compare(result, 0) == 0);
    }

    @Test
    public void distanceIsZeroForEqualCoordinates() {
        final float result = DistanceResolver.distance(13, 666, 13, 666);
        assertTrue(Float.compare(result, 0) == 0);
    }

    @Test
    public void calculatesDistance() {
        final float result = DistanceResolver.distance(32, 64, 128, 256);
        assertTrue(Float.compare(result, 214.66252f) == 0);
    }
}
