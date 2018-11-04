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
package com.doctoror.particlesdrawable.engine;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.doctoror.particlesdrawable.Defaults;
import com.doctoror.particlesdrawable.R;
import com.doctoror.particlesdrawable.model.Scene;

import androidx.annotation.NonNull;

public final class SceneConfigurator {

    public void configureSceneFromAttributes(
            @NonNull final Scene scene,
            @NonNull final Resources r,
            @NonNull final AttributeSet attrs) {
        final TypedArray a = r.obtainAttributes(attrs, R.styleable.ParticlesView);
        try {
            final int count = a.getIndexCount();
            float particleRadiusMax = Defaults.PARTICLE_RADIUS_MAX;
            float particleRadiusMin = Defaults.PARTICLE_RADIUS_MIN;
            for (int i = 0; i < count; i++) {
                final int attr = a.getIndex(i);
                if (attr == R.styleable.ParticlesView_particleRadiusMin) {
                    particleRadiusMin = a.getDimension(attr, Defaults.PARTICLE_RADIUS_MIN);

                } else if (attr == R.styleable.ParticlesView_particleRadiusMax) {
                    particleRadiusMax = a.getDimension(attr, Defaults.PARTICLE_RADIUS_MAX);

                } else if (attr == R.styleable.ParticlesView_lineThickness) {
                    scene.setLineThickness(a.getDimension(attr, Defaults.LINE_THICKNESS));

                } else if (attr == R.styleable.ParticlesView_lineLength) {
                    scene.setLineLength(a.getDimension(attr, Defaults.LINE_LENGTH));

                } else if (attr == R.styleable.ParticlesView_density) {
                    scene.setDensity(a.getInteger(attr, Defaults.DENSITY));

                } else if (attr == R.styleable.ParticlesView_particleColor) {
                    scene.setParticleColor(a.getColor(attr, Defaults.PARTICLE_COLOR));

                } else if (attr == R.styleable.ParticlesView_lineColor) {
                    scene.setLineColor(a.getColor(attr, Defaults.LINE_COLOR));

                } else if (attr == R.styleable.ParticlesView_frameDelayMillis) {
                    scene.setFrameDelay(a.getInteger(attr, Defaults.FRAME_DELAY));

                } else if (attr == R.styleable.ParticlesView_speedFactor) {
                    scene.setSpeedFactor(a.getFloat(attr, Defaults.SPEED_FACTOR));
                }
            }
            scene.setParticleRadiusRange(particleRadiusMin, particleRadiusMax);
        } finally {
            a.recycle();
        }
    }
}
