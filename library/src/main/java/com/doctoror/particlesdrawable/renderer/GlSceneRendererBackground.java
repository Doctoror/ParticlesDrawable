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

import android.graphics.Bitmap;
import android.opengl.GLES11;
import android.opengl.GLUtils;
import android.support.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL11;

final class GlSceneRendererBackground {

    private static final int BYTES_PER_SHORT = 2;

    private final int[] textureHandle = new int[1];

    private volatile boolean backgroundCoordinatesDirty;

    private ShortBuffer backgroundCoordinates;
    private ByteBuffer backgroundTextureCoordinates;

    private short width;
    private short height;

    void setDimensions(final int width, final int height) {
        this.width = (short) width;
        this.height = (short) height;

        backgroundCoordinatesDirty = true;
    }

    void drawBackground() {
        if (textureHandle[0] != 0 && width != 0 && height != 0) {
            ensureBackgroundTextureCoordiantes();
            ensureBackgroundCooridnates();

            backgroundTextureCoordinates.position(0);
            backgroundCoordinates.position(0);

            GLES11.glEnable(GL11.GL_TEXTURE_2D);
            GLES11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

            GLES11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle[0]);

            GLES11.glTexCoordPointer(2, GL11.GL_BYTE, 0, backgroundTextureCoordinates);
            GLES11.glVertexPointer(2, GL11.GL_SHORT, 0, backgroundCoordinates);
            GLES11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

            GLES11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GLES11.glDisable(GL11.GL_TEXTURE_2D);
        }
    }

    void setTexture(@Nullable final Bitmap texture) {
        GLES11.glDeleteTextures(1, textureHandle, 0);
        textureHandle[0] = 0;

        if (texture != null) {
            GLES11.glGenTextures(1, textureHandle, 0);
            GLES11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle[0]);

            GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, texture, 0);

            GLES11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GLES11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            GLES11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP_TO_EDGE);
            GLES11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP_TO_EDGE);

            GLES11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);
        }
    }

    void recycle() {
        setTexture(null);
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
}
