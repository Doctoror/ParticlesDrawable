package com.doctoror.particlesdrawable.util;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

public final class TextureUtils {

    private TextureUtils() {

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

    @NonNull
    public static Bitmap scaleToSmallerPot(@NonNull Bitmap texture) {
        final int sourceWidth = texture.getWidth();
        final int sourceHeight = texture.getHeight();
        if (sourceWidth <= 0 && sourceHeight <= 0) {
            return texture;
        }

        final int targetWidth = findPreviousOrReturnIfPowerOfTwo(sourceWidth);
        final int targetHeight = findPreviousOrReturnIfPowerOfTwo(sourceHeight);

        if (targetWidth != sourceWidth || targetHeight != sourceHeight) {
            texture = Bitmap.createScaledBitmap(texture, targetWidth, targetHeight, true);
        }
        return texture;
    }
}
