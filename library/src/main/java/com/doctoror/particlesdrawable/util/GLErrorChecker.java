package com.doctoror.particlesdrawable.util;

import android.opengl.GLES20;
import android.util.Log;

public final class GLErrorChecker {

    private static boolean shouldThrowOnGlError = false;
    private static boolean shouldCheckGlError = false;

    public static void setShouldCheckGlError(final boolean shouldCheckGlError) {
        GLErrorChecker.shouldCheckGlError = shouldCheckGlError;
    }

    public static void setShouldThrowOnGlError(final boolean shouldThrowOnGlError) {
        GLErrorChecker.shouldThrowOnGlError = shouldThrowOnGlError;
    }

    public static void checkGlError() {
        if (shouldCheckGlError) {
            final int error = GLES20.glGetError();
            if (error != GLES20.GL_NO_ERROR) {
                if (shouldThrowOnGlError) {
                    throw new RuntimeException("GLError: " + error);
                } else {
                    Log.e("GLError", Integer.toString(error));
                }
            }
        }
    }
}
