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
package com.doctoror.particlesdrawable.renderer;

import android.graphics.Color;
import android.opengl.GLES11;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.doctoror.particlesdrawable.ParticlesScene;
import com.doctoror.particlesdrawable.util.DistanceResolver;
import com.doctoror.particlesdrawable.util.LineColorResolver;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL11;

final class GlSceneRendererLines {

    private static final int BYTES_PER_SHORT = 2;
    private static final int COORDINATES_PER_VERTEX = 2;
    private static final int COLOR_BYTES_PER_VERTEX = 4;
    private static final int VERTICES_PER_LINE = 2;

    private ByteBuffer lineColorBuffer;
    private ShortBuffer lineCoordinatesBuffer;

    private int lineCount;

    private void initBuffers(final int vertexCount) {
        final int segmentsCount = segmentsCount(vertexCount);
        initLineCoordinates(segmentsCount);
        initLineColorBuffer(segmentsCount);
        lineCount = 0;
    }

    private int segmentsCount(final int vertices) {
        return (vertices * (vertices - 1)) / 2;
    }

    private void initLineCoordinates(final int segmentsCount) {
        final int shortcapacity = segmentsCount * VERTICES_PER_LINE * COORDINATES_PER_VERTEX;
        if (lineCoordinatesBuffer == null || lineCoordinatesBuffer.capacity() != shortcapacity) {
            final ByteBuffer coordinatesByteBuffer = ByteBuffer.allocateDirect(
                    shortcapacity * BYTES_PER_SHORT);
            coordinatesByteBuffer.order(ByteOrder.nativeOrder());
            lineCoordinatesBuffer = coordinatesByteBuffer.asShortBuffer();
        }
    }

    private void initLineColorBuffer(final int lineCount) {
        final int targetCapacity = lineCount * VERTICES_PER_LINE * COLOR_BYTES_PER_VERTEX;
        if (lineColorBuffer == null || lineColorBuffer.capacity() != targetCapacity) {
            lineColorBuffer = ByteBuffer.allocateDirect(targetCapacity);
            lineColorBuffer.order(ByteOrder.nativeOrder());
        }
    }

    void drawScene(@NonNull final ParticlesScene scene) {
        GLES11.glLineWidth(scene.getLineThickness());

        initBuffers(scene.getNumDots());
        resolveLines(scene);
        drawLines();
    }

    private void resolveLines(@NonNull final ParticlesScene scene) {
        lineColorBuffer.clear();
        lineCoordinatesBuffer.clear();

        final int particlesCount = scene.getNumDots();
        if (particlesCount != 0) {
            for (int i = 0; i < particlesCount; i++) {

                final float x1 = scene.getParticleX(i);
                final float y1 = scene.getParticleY(i);

                // Draw connection lines for eligible points
                for (int j = i + 1; j < particlesCount; j++) {

                    final float x2 = scene.getParticleX(j);
                    final float y2 = scene.getParticleY(j);

                    final float distance = DistanceResolver.distance(x1, y1, x2, y2);
                    if (distance < scene.getLineDistance()) {
                        final int lineColor = LineColorResolver.resolveLineColorWithAlpha(
                                scene.getAlpha(),
                                scene.getLineColor(),
                                scene.getLineDistance(),
                                distance);

                        resolveLine(
                                x1,
                                y1,
                                x2,
                                y2,
                                lineColor);
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
            @ColorInt final int color) {
        lineCoordinatesBuffer.put((short) startX);
        lineCoordinatesBuffer.put((short) startY);
        lineCoordinatesBuffer.put((short) stopX);
        lineCoordinatesBuffer.put((short) stopY);

        lineColorBuffer.put((byte) Color.red(color));
        lineColorBuffer.put((byte) Color.green(color));
        lineColorBuffer.put((byte) Color.blue(color));
        lineColorBuffer.put((byte) Color.alpha(color));

        lineColorBuffer.put((byte) Color.red(color));
        lineColorBuffer.put((byte) Color.green(color));
        lineColorBuffer.put((byte) Color.blue(color));
        lineColorBuffer.put((byte) Color.alpha(color));

        lineCount++;
    }

    private void drawLines() {
        lineCoordinatesBuffer.position(0);
        lineColorBuffer.position(0);

        GLES11.glEnableClientState(GL11.GL_COLOR_ARRAY);

        GLES11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 0, lineColorBuffer);
        GLES11.glVertexPointer(2, GL11.GL_SHORT, 0, lineCoordinatesBuffer);
        GLES11.glDrawArrays(GL11.GL_LINES, 0, lineCount * 2);

        GLES11.glDisableClientState(GL11.GL_COLOR_ARRAY);
    }
}
