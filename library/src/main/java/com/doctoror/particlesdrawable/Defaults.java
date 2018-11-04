/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
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
package com.doctoror.particlesdrawable;

import android.content.res.Resources;
import android.graphics.Color;
import androidx.annotation.ColorInt;
import android.util.TypedValue;

/**
 * Default values are here.
 */
public final class Defaults {

    private Defaults() {
        throw new UnsupportedOperationException();
    }

    public static final int DENSITY = 60;

    public static final int FRAME_DELAY = 10;

    @ColorInt
    public static final int LINE_COLOR = Color.WHITE;

    public static final float LINE_LENGTH = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 86, Resources.getSystem().getDisplayMetrics());

    public static final float LINE_THICKNESS = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 1, Resources.getSystem().getDisplayMetrics());

    @ColorInt
    public static final int PARTICLE_COLOR = Color.WHITE;

    public static final float PARTICLE_RADIUS_MAX = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 3f, Resources.getSystem().getDisplayMetrics());

    public static final float PARTICLE_RADIUS_MIN = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 1f, Resources.getSystem().getDisplayMetrics());

    public static final float SPEED_FACTOR = 1f;
}
