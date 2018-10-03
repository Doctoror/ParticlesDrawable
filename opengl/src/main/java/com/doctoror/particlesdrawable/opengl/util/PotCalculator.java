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
package com.doctoror.particlesdrawable.opengl.util;

import android.util.Pair;

import com.doctoror.particlesdrawable.ConfigurableKeep;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

@ConfigurableKeep
public final class PotCalculator {

    private PotCalculator() {

    }

    @VisibleForTesting
    static boolean isPowerOfTwo(final int number) {
        return number > 0 && ((number & (number - 1)) == 0);
    }

    public static int findPreviousOrReturnIfPowerOfTwo(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be >= 1");
        }
        if (isPowerOfTwo(n)) {
            return n;
        }
        n = n | (n >> 1);
        n = n | (n >> 2);
        n = n | (n >> 4);
        n = n | (n >> 8);
        n = n | (n >> 16);
        return n - (n >> 1);
    }

    public static int findNextOrReturnIfPowerOfTwo(int n) {
        if (n <= 0) {
            return 1;
        }
        if (isPowerOfTwo(n)) {
            return n;
        }
        n--;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        n++;
        return n;
    }

    /**
     * If both dimensions are already POT, returns them.
     * If the dimensions are not POT, returns next largest POT for both dimensions.
     *
     * @param width  the width to get next POT for
     * @param height the height to get next POT for
     * @return next larger POT dimensions or input dimensions if already POT
     */
    @NonNull
    public static Pair<Integer, Integer> toLargerPotDimensions(final int width, final int height) {
        if (width <= 0 || height <= 0) {
            return new Pair<Integer, Integer>(width, height);
        }

        final int targetWidth = findNextOrReturnIfPowerOfTwo(width);
        final int targetHeight = findNextOrReturnIfPowerOfTwo(height);

        return new Pair<Integer, Integer>(targetWidth, targetHeight);
    }
}
