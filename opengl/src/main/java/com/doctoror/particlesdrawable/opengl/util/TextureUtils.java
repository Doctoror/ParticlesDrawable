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

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Pair;

public final class TextureUtils {

    private TextureUtils() {

    }

    @NonNull
    public static Bitmap scaleToLargerPot(@NonNull Bitmap texture) {
        final int sourceWidth = texture.getWidth();
        final int sourceHeight = texture.getHeight();

        final Pair<Integer, Integer> nextPot = PotCalculator
                .toLargerPotDomensions(sourceWidth, sourceHeight);

        if (nextPot.first != sourceWidth || nextPot.second != sourceHeight) {
            texture = Bitmap.createScaledBitmap(texture, nextPot.first, nextPot.second, true);
        }
        return texture;
    }
}
