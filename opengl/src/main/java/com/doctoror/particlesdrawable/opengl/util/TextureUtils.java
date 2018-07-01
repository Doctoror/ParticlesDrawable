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

public final class TextureUtils {

    private TextureUtils() {

    }

    @NonNull
    public static Bitmap scaleToSmallerPot(@NonNull Bitmap texture) {
        final int sourceWidth = texture.getWidth();
        final int sourceHeight = texture.getHeight();
        if (sourceWidth <= 0 && sourceHeight <= 0) {
            return texture;
        }

        final int targetWidth = PotCalculator.findPreviousOrReturnIfPowerOfTwo(sourceWidth);
        final int targetHeight = PotCalculator.findPreviousOrReturnIfPowerOfTwo(sourceHeight);

        if (targetWidth != sourceWidth || targetHeight != sourceHeight) {
            texture = Bitmap.createScaledBitmap(texture, targetWidth, targetHeight, true);
        }
        return texture;
    }
}
