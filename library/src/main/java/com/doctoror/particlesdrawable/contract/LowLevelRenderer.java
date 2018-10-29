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
package com.doctoror.particlesdrawable.contract;

import androidx.annotation.ColorInt;

public interface LowLevelRenderer {

    void drawLine(
            float startX,
            float startY,
            float stopX,
            float stopY,
            float strokeWidth,
            @ColorInt int color);

    void fillCircle(
            float cx,
            float cy,
            float radius,
            @ColorInt int color);

    void setTranslationX(float translationX);
}
