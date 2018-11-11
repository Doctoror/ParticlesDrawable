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
package com.doctoror.particlesdrawable.opengl.renderer;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.doctoror.particlesdrawable.opengl.util.GLErrorChecker;
import com.doctoror.particlesdrawable.opengl.util.ShaderLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

final class GlSceneRendererBackground {

    private static final String VERTEX_SHADER_CODE =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 aTexCoord;" +
                    "varying vec2 vTexCoord;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  vTexCoord = aTexCoord;" +
                    "}";

    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "varying vec2 vTexCoord;" +
                    "uniform sampler2D sTexture;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(sTexture, vTexCoord);" +
                    "}";

    private static final int BYTES_PER_SHORT = 2;
    private static final int COORDINATES_PER_VERTEX = 2;

    private volatile boolean backgroundCoordinatesDirty;

    private ShortBuffer backgroundCoordinates;
    private ByteBuffer backgroundTextureCoordinates;

    private short width;
    private short height;

    private int program;

    private int textureId;
    private boolean hasTexture;

    void init(final int textureId) {
        this.textureId = textureId;

        final int vertexShader = ShaderLoader.loadShader(
                GLES20.GL_VERTEX_SHADER,
                VERTEX_SHADER_CODE);
        GLErrorChecker.checkGlError("background glCompileShader vertex");

        final int fragmentShader = ShaderLoader.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                FRAGMENT_SHADER_CODE);
        GLErrorChecker.checkGlError("background glCompileShader fragment");

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLErrorChecker.checkGlError("background glAttachShader vertex");

        GLES20.glAttachShader(program, fragmentShader);
        GLErrorChecker.checkGlError("background glAttachShader fragment");

        GLES20.glLinkProgram(program);
        GLErrorChecker.checkGlError("background glLinkProgram");
    }

    void setDimensions(final int width, final int height) {
        this.width = (short) width;
        this.height = (short) height;

        backgroundCoordinatesDirty = true;
    }

    void setTexture(@Nullable final Bitmap texture) {
        if (texture != null) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);
            GLErrorChecker.checkGlError("background texImage2D");

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLErrorChecker.checkGlError("background glTexParameterf");
        }

        hasTexture = texture != null;
    }

    private void ensureBackgroundCooridnates() {
        if (backgroundCoordinates == null) {
            final ByteBuffer coordinatesByteBuffer = ByteBuffer.allocateDirect(12 * BYTES_PER_SHORT);
            coordinatesByteBuffer.order(ByteOrder.nativeOrder());
            backgroundCoordinates = coordinatesByteBuffer.asShortBuffer();
            backgroundCoordinatesDirty = true;
        }
        if (backgroundCoordinatesDirty) {
            backgroundCoordinates.clear();

            backgroundCoordinates.put((short) 0);
            backgroundCoordinates.put((short) 0);

            backgroundCoordinates.put(width);
            backgroundCoordinates.put((short) 0);

            backgroundCoordinates.put((short) 0);
            backgroundCoordinates.put(height);

            backgroundCoordinates.put(width);
            backgroundCoordinates.put((short) 0);

            backgroundCoordinates.put((short) 0);
            backgroundCoordinates.put(height);

            backgroundCoordinates.put(width);
            backgroundCoordinates.put(height);

            backgroundCoordinatesDirty = false;
        }
    }

    private void ensureBackgroundTextureCoordiantes() {
        if (backgroundTextureCoordinates == null) {
            backgroundTextureCoordinates = ByteBuffer.allocateDirect(14);
            backgroundTextureCoordinates.order(ByteOrder.nativeOrder());

            backgroundTextureCoordinates.put((byte) 0);
            backgroundTextureCoordinates.put((byte) 1);

            backgroundTextureCoordinates.put((byte) 1);
            backgroundTextureCoordinates.put((byte) 1);

            backgroundTextureCoordinates.put((byte) 0);
            backgroundTextureCoordinates.put((byte) 0);

            backgroundTextureCoordinates.put((byte) 1);
            backgroundTextureCoordinates.put((byte) 1);

            backgroundTextureCoordinates.put((byte) 0);
            backgroundTextureCoordinates.put((byte) 0);

            backgroundTextureCoordinates.put((byte) 1);
            backgroundTextureCoordinates.put((byte) 0);
        }
    }

    void drawScene(@NonNull final float[] matrix) {
        if (hasTexture && width != 0 && height != 0) {
            ensureBackgroundTextureCoordiantes();
            ensureBackgroundCooridnates();

            backgroundTextureCoordinates.position(0);
            backgroundCoordinates.position(0);

            GLES20.glUseProgram(program);

            final int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
            GLES20.glEnableVertexAttribArray(positionHandle);

            GLES20.glVertexAttribPointer(
                    positionHandle,
                    COORDINATES_PER_VERTEX,
                    GLES20.GL_SHORT,
                    false,
                    0,
                    backgroundCoordinates);

            final int texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord");
            GLES20.glEnableVertexAttribArray(texCoordHandle);

            GLES20.glVertexAttribPointer(
                    texCoordHandle,
                    COORDINATES_PER_VERTEX,
                    GLES20.GL_BYTE,
                    false,
                    0,
                    backgroundTextureCoordinates);

            final int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, matrix, 0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        }
    }
}
