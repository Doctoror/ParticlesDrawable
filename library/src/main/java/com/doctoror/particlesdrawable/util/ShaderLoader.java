package com.doctoror.particlesdrawable.util;

import android.opengl.GLES20;
import android.support.annotation.NonNull;

public final class ShaderLoader {

    private ShaderLoader() {}

    public static int loadShader(final int type, @NonNull final String shaderCode) {
        final int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
