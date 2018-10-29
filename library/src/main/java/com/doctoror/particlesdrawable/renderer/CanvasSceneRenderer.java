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

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.doctoror.particlesdrawable.KeepAsApi;
import com.doctoror.particlesdrawable.contract.LowLevelRenderer;
import com.doctoror.particlesdrawable.contract.SceneRenderer;

/**
 * {@link SceneRenderer} that draws on {@link Canvas}
 */
@KeepAsApi
public final class CanvasSceneRenderer implements LowLevelRenderer {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Nullable
    private Canvas canvas;

    private float translationX;

    public void setCanvas(@Nullable final Canvas canvas) {
        if (canvas != null) {
            canvas.translate(translationX, 0);
        }
        this.canvas = canvas;
    }

    @NonNull
    public Paint getPaint() {
        return paint;
    }

    public void setColorFilter(@Nullable final ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public void drawLine(final float startX, final float startY, final float stopX,
            final float stopY, final float strokeWidth, @ColorInt final int color) {
        if (canvas == null) {
            throw new IllegalStateException("Called in wrong state");
        }
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    @Override
    public void fillCircle(final float cx, final float cy, final float radius,
            @ColorInt final int color) {
        if (canvas == null) {
            throw new IllegalStateException("Called in wrong state");
        }
        paint.setColor(color);
        canvas.drawCircle(cx, cy, radius, paint);
    }

    @Override
    public void setTranslationX(final float translationX) {
        this.translationX = translationX;
    }
}
