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

import android.graphics.Color;
import android.opengl.GLES20;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.doctoror.particlesdrawable.model.Scene;
import com.doctoror.particlesdrawable.opengl.util.GLErrorChecker;
import com.doctoror.particlesdrawable.opengl.util.ShaderLoader;
import com.doctoror.particlesdrawable.util.DistanceResolver;
import com.doctoror.particlesdrawable.util.LineColorResolver;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

final class GlSceneRendererLines {

    private static final String VERTEX_SHADER_CODE =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 aColor;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  vColor.r = aColor.r * aColor.a;" +
                    "  vColor.g = aColor.g * aColor.a;" +
                    "  vColor.b = aColor.b * aColor.a;" +
                    "  vColor.a = aColor.a;" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private static final int BYTES_PER_FLOAT = 4;
    private static final int COORDINATES_PER_VERTEX = 2;
    private static final int COLOR_BYTES_PER_VERTEX = 4;
    private static final int VERTICES_PER_THIN_LINE = 2;
    private static final int VERTICES_PER_THICK_LINE = 6;

    private ByteBuffer lineColorBuffer;
    private FloatBuffer lineCoordinatesBuffer;

    private boolean lineAsTriangles = false;
    private int lineVerticesCount;

    private int program;

    void init() {
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

    private void initBuffers(final int vertexCount) {
        final int segmentsCount = segmentsCount(vertexCount);

        initCoordinates(segmentsCount, lineAsTriangles
                ? VERTICES_PER_THICK_LINE : VERTICES_PER_THIN_LINE);

        initLineColorBuffer(segmentsCount, lineAsTriangles
                ? VERTICES_PER_THICK_LINE : VERTICES_PER_THIN_LINE);

        lineVerticesCount = 0;
    }

    private int segmentsCount(final int vertices) {
        return (vertices * (vertices - 1)) / 2;
    }

    private void initCoordinates(final int segmentsCount, final int verticesPerLine) {
        final int shortcapacity = segmentsCount * verticesPerLine * COORDINATES_PER_VERTEX;
        if (lineCoordinatesBuffer == null || lineCoordinatesBuffer.capacity() != shortcapacity) {
            final ByteBuffer coordinatesByteBuffer = ByteBuffer.allocateDirect(
                    shortcapacity * BYTES_PER_FLOAT);
            coordinatesByteBuffer.order(ByteOrder.nativeOrder());
            lineCoordinatesBuffer = coordinatesByteBuffer.asFloatBuffer();
        }
    }

    private void initLineColorBuffer(final int lineCount, final int verticesPerLine) {
        final int targetCapacity = lineCount * verticesPerLine * COLOR_BYTES_PER_VERTEX;
        if (lineColorBuffer == null || lineColorBuffer.capacity() != targetCapacity) {
            lineColorBuffer = ByteBuffer.allocateDirect(targetCapacity);
            lineColorBuffer.order(ByteOrder.nativeOrder());
        }
    }

    void drawScene(
            @NonNull final Scene scene,
            @NonNull final float[] matrix) {
        lineAsTriangles = scene.getLineThickness() >= 2f;
        initBuffers(scene.getDensity());
        resolveLines(scene);
        drawLines(matrix);
    }

    private void resolveLines(@NonNull final Scene scene) {
        lineColorBuffer.clear();
        lineCoordinatesBuffer.clear();

        final int particlesCount = scene.getDensity();
        if (particlesCount != 0) {
            for (int i = 0; i < particlesCount; i++) {

                final float x1 = scene.getParticleX(i);
                final float y1 = scene.getParticleY(i);

                // Draw connection lines for eligible particles
                for (int j = i + 1; j < particlesCount; j++) {

                    final float x2 = scene.getParticleX(j);
                    final float y2 = scene.getParticleY(j);

                    final float distance = DistanceResolver.distance(x1, y1, x2, y2);
                    if (distance < scene.getLineLength()) {
                        final int lineColor = LineColorResolver.resolveLineColorWithAlpha(
                                scene.getAlpha(),
                                scene.getLineColor(),
                                scene.getLineLength(),
                                distance);

                        resolveLine(
                                x1,
                                y1,
                                x2,
                                y2,
                                lineColor,
                                distance,
                                scene.getLineThickness());
                    }
                }
            }
        }
    }

    private void resolveLine(
            final float startX,
            final float startY,
            final float stopX,
            final float stopY,
            @ColorInt final int color,
            final float lineLength,
            final float lineThickness) {
        if (lineAsTriangles) {
            resolveThickLine(startX, startY, stopX, stopY, color, lineLength, lineThickness);
        } else {
            resolveThinLine(startX, startY, stopX, stopY, color);
        }
    }

    private void resolveThinLine(
            final float startX,
            final float startY,
            final float stopX,
            final float stopY,
            @ColorInt final int color) {
        lineCoordinatesBuffer.put(startX);
        lineCoordinatesBuffer.put(startY);
        lineCoordinatesBuffer.put(stopX);
        lineCoordinatesBuffer.put(stopY);

        lineColorBuffer.put((byte) Color.red(color));
        lineColorBuffer.put((byte) Color.green(color));
        lineColorBuffer.put((byte) Color.blue(color));
        lineColorBuffer.put((byte) Color.alpha(color));

        lineColorBuffer.put((byte) Color.red(color));
        lineColorBuffer.put((byte) Color.green(color));
        lineColorBuffer.put((byte) Color.blue(color));
        lineColorBuffer.put((byte) Color.alpha(color));

        lineVerticesCount += VERTICES_PER_THIN_LINE;
    }

    private void resolveThickLine(
            final float startX,
            final float startY,
            final float stopX,
            final float stopY,
            @ColorInt final int color,
            final float lineLength,
            final float lineThickness) {
        // Based on https://stackoverflow.com/a/1937202/1366471
        final float dx = (stopX - startX) / lineLength; //delta x
        final float dy = (stopY - startY) / lineLength; //delta y

        //Ok, (dx, dy) is now a unit vector pointing in the direction of the line
        //A perpendicular vector is given by (-dy, dx)
        final float px = 0.5f * lineThickness * (-dy); //perpendicular vector with lenght thickness * 0.5
        final float py = 0.5f * lineThickness * dx;

        final float x1 = startX + px;
        final float y1 = startY + py;

        final float x2 = stopX + px;
        final float y2 = stopY + py;

        final float x3 = stopX - px;
        final float y3 = stopY - py;

        final float x4 = startX - px;
        final float y4 = startY - py;

        putLineTrianglesBasedOnQuad(
                x1, y1,
                x2, y2,
                x3, y3,
                x4, y4);

        lineColorBuffer.put((byte) Color.red(color));
        lineColorBuffer.put((byte) Color.green(color));
        lineColorBuffer.put((byte) Color.blue(color));
        lineColorBuffer.put((byte) Color.alpha(color));

        lineColorBuffer.put((byte) Color.red(color));
        lineColorBuffer.put((byte) Color.green(color));
        lineColorBuffer.put((byte) Color.blue(color));
        lineColorBuffer.put((byte) Color.alpha(color));

        lineColorBuffer.put((byte) Color.red(color));
        lineColorBuffer.put((byte) Color.green(color));
        lineColorBuffer.put((byte) Color.blue(color));
        lineColorBuffer.put((byte) Color.alpha(color));

        lineColorBuffer.put((byte) Color.red(color));
        lineColorBuffer.put((byte) Color.green(color));
        lineColorBuffer.put((byte) Color.blue(color));
        lineColorBuffer.put((byte) Color.alpha(color));

        lineColorBuffer.put((byte) Color.red(color));
        lineColorBuffer.put((byte) Color.green(color));
        lineColorBuffer.put((byte) Color.blue(color));
        lineColorBuffer.put((byte) Color.alpha(color));

        lineColorBuffer.put((byte) Color.red(color));
        lineColorBuffer.put((byte) Color.green(color));
        lineColorBuffer.put((byte) Color.blue(color));
        lineColorBuffer.put((byte) Color.alpha(color));

        lineVerticesCount += VERTICES_PER_THICK_LINE;
    }

    private void putLineTrianglesBasedOnQuad(
            final float x1,
            final float y1,
            final float x2,
            final float y2,
            final float x3,
            final float y3,
            final float x4,
            final float y4
    ) {
        lineCoordinatesBuffer.put(x1);
        lineCoordinatesBuffer.put(y1);

        lineCoordinatesBuffer.put(x2);
        lineCoordinatesBuffer.put(y2);

        lineCoordinatesBuffer.put(x4);
        lineCoordinatesBuffer.put(y4);

        lineCoordinatesBuffer.put(x2);
        lineCoordinatesBuffer.put(y2);

        lineCoordinatesBuffer.put(x3);
        lineCoordinatesBuffer.put(y3);

        lineCoordinatesBuffer.put(x4);
        lineCoordinatesBuffer.put(y4);
    }

    private void drawLines(@NonNull final float[] matrix) {
        lineCoordinatesBuffer.position(0);
        lineColorBuffer.position(0);

        GLES20.glUseProgram(program);

        final int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(
                positionHandle,
                COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                0,
                lineCoordinatesBuffer);

        final int colorHandle = GLES20.glGetAttribLocation(program, "aColor");
        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glVertexAttribPointer(
                colorHandle,
                COLOR_BYTES_PER_VERTEX,
                GLES20.GL_UNSIGNED_BYTE,
                true,
                0,
                lineColorBuffer);

        final int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, matrix, 0);

        GLES20.glDrawArrays(lineAsTriangles ? GLES20.GL_TRIANGLES : GLES20.GL_LINES, 0, lineVerticesCount);
    }
}
