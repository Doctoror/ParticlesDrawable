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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.doctoror.particlesdrawable.ParticlesScene;
import com.doctoror.particlesdrawable.opengl.util.GLErrorChecker;
import com.doctoror.particlesdrawable.opengl.util.PotCalculator;
import com.doctoror.particlesdrawable.opengl.util.ShaderLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

final class GlSceneRendererParticles {

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

    private static final int BYTES_PER_FLOAT = 4;
    private static final int COORDINATES_PER_VERTEX = 2;
    private static final int VERTICES_PER_PARTICLE = 6;
    private static final int TEXTURE_COORDINATES_PER_VERTEX = 6;

    private FloatBuffer particlesTrianglesCoordinates;
    private ByteBuffer particlesTexturesCoordinates;

    private volatile boolean textureDirty;

    private int program;

    private int textureId;

    void init(final int textureId) {
        this.textureId = textureId;

        final int vertexShader = ShaderLoader.loadShader(
                GLES20.GL_VERTEX_SHADER,
                VERTEX_SHADER_CODE);

        final int fragmentShader = ShaderLoader.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                FRAGMENT_SHADER_CODE);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        GLErrorChecker.checkGlError();
    }

    void markTextureDirty() {
        textureDirty = true;
    }

    private void initBuffers(final int vertexCount) {
        initParticleTrianglesBuffer(vertexCount);
        initParticleTexturesBuffer(vertexCount);
    }

    private void initParticleTrianglesBuffer(final int vertexCount) {
        final int capacity = vertexCount * COORDINATES_PER_VERTEX * VERTICES_PER_PARTICLE;
        if (particlesTrianglesCoordinates == null
                || particlesTrianglesCoordinates.capacity() != capacity) {
            final ByteBuffer triangleShit = ByteBuffer.allocateDirect(capacity * BYTES_PER_FLOAT);
            triangleShit.order(ByteOrder.nativeOrder());
            particlesTrianglesCoordinates = triangleShit.asFloatBuffer();
        }
    }

    private void initParticleTexturesBuffer(final int vertexCount) {
        final int capacity = vertexCount
                * COORDINATES_PER_VERTEX
                * TEXTURE_COORDINATES_PER_VERTEX
                * VERTICES_PER_PARTICLE;

        if (particlesTexturesCoordinates == null
                || particlesTexturesCoordinates.capacity() != capacity) {
            particlesTexturesCoordinates = ByteBuffer.allocateDirect(capacity);
            particlesTexturesCoordinates.order(ByteOrder.nativeOrder());

            final int step = VERTICES_PER_PARTICLE * TEXTURE_COORDINATES_PER_VERTEX;
            for (int i = 0; i < capacity; i += step) {
                particlesTexturesCoordinates.put((byte) 0);
                particlesTexturesCoordinates.put((byte) 1);
                particlesTexturesCoordinates.put((byte) 1);
                particlesTexturesCoordinates.put((byte) 1);
                particlesTexturesCoordinates.put((byte) 0);
                particlesTexturesCoordinates.put((byte) 0);

                particlesTexturesCoordinates.put((byte) 1);
                particlesTexturesCoordinates.put((byte) 1);
                particlesTexturesCoordinates.put((byte) 0);
                particlesTexturesCoordinates.put((byte) 0);
                particlesTexturesCoordinates.put((byte) 1);
                particlesTexturesCoordinates.put((byte) 0);
            }
        }
    }

    private void reloadTextureIfDirty(
            @ColorInt final int color,
            final float maxParticleRadius) {
        if (textureDirty) {
            generateAndLoadTexture(color, maxParticleRadius);
        }
    }

    private void generateAndLoadTexture(
            @ColorInt final int color,
            final float maxParticleRadius) {
        final Bitmap texture = generateParticleTexture(color, maxParticleRadius);
        loadTexture(texture);
        texture.recycle();
    }

    @NonNull
    private Bitmap generateParticleTexture(
            @ColorInt final int color,
            final float maxPointRadius) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);

        final int size = (int) (maxPointRadius * 2f);
        final int sizePowerOfTwo = PotCalculator.findNextOrReturnIfPowerOfTwo(size);
        final int halfSizePowerOfTwo = sizePowerOfTwo / 2;
        final Bitmap bitmap = Bitmap.createBitmap(sizePowerOfTwo, sizePowerOfTwo, Bitmap.Config.ARGB_4444);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(halfSizePowerOfTwo, halfSizePowerOfTwo, halfSizePowerOfTwo, paint);

        return bitmap;
    }

    private void loadTexture(@NonNull final Bitmap texture) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        textureDirty = false;
    }

    public void drawScene(
            @NonNull final ParticlesScene scene,
            @NonNull final float[] matrix) {
        initBuffers(scene.getNumDots());
        reloadTextureIfDirty(scene.getDotColor(), scene.getMaxDotRadius());
        resolveParticleTriangles(scene);
        drawParticles(scene.getNumDots(), matrix);
    }

    private void resolveParticleTriangles(@NonNull final ParticlesScene scene) {
        final FloatBuffer coordinates = scene.getCoordinates();
        coordinates.position(0);

        final FloatBuffer radiuses = scene.getRadiuses();
        radiuses.position(0);

        particlesTrianglesCoordinates.clear();

        final int count = scene.getNumDots();
        for (int i = 0; i < count; i++) {
            final float particleRadius = radiuses.get();

            final float coordX = coordinates.get() - particleRadius;
            final float coordY = coordinates.get() - particleRadius;

            final float particleSize = particleRadius * 2f;

            particlesTrianglesCoordinates.put(coordX);
            particlesTrianglesCoordinates.put(coordY);
            particlesTrianglesCoordinates.put(coordX + particleSize);
            particlesTrianglesCoordinates.put(coordY);
            particlesTrianglesCoordinates.put(coordX);
            particlesTrianglesCoordinates.put(coordY + particleSize);

            particlesTrianglesCoordinates.put(coordX + particleSize);
            particlesTrianglesCoordinates.put(coordY);
            particlesTrianglesCoordinates.put(coordX);
            particlesTrianglesCoordinates.put(coordY + particleSize);
            particlesTrianglesCoordinates.put(coordX + particleSize);
            particlesTrianglesCoordinates.put(coordY + particleSize);
        }
    }

    private void drawParticles(final int count, @NonNull final float[] matrix) {
        particlesTexturesCoordinates.position(0);
        particlesTrianglesCoordinates.position(0);

        GLES20.glUseProgram(program);

        final int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(
                positionHandle,
                COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                0,
                particlesTrianglesCoordinates);

        final int texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord");
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        GLES20.glVertexAttribPointer(
                texCoordHandle,
                COORDINATES_PER_VERTEX,
                GLES20.GL_BYTE,
                false,
                0,
                particlesTexturesCoordinates);

        final int samplerLoc = GLES20.glGetUniformLocation(program, "sTexture");
        GLES20.glUniform1i(samplerLoc, 0);

        final int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, matrix, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, count * VERTICES_PER_PARTICLE);
    }
}
