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
            float minDotRadius = Defaults.PARTICLE_RADIUS_MIN;
            float maxDotRadius = Defaults.PARTICLE_RADIUS_MAX;
            for (int i = 0; i < count; i++) {
                final int attr = a.getIndex(i);
                if (attr == R.styleable.ParticlesView_minDotRadius) {
                    minDotRadius = a.getDimension(attr, Defaults.PARTICLE_RADIUS_MIN);

                } else if (attr == R.styleable.ParticlesView_maxDotRadius) {
                    maxDotRadius = a.getDimension(attr, Defaults.PARTICLE_RADIUS_MAX);

                } else if (attr == R.styleable.ParticlesView_lineThickness) {
                    scene.setLineThickness(a.getDimension(attr, Defaults.LINE_THICKNESS));

                } else if (attr == R.styleable.ParticlesView_lineDistance) {
                    scene.setLineDistance(a.getDimension(attr, Defaults.LINE_LENGTH));

                } else if (attr == R.styleable.ParticlesView_density) {
                    scene.setDensity(a.getInteger(attr, Defaults.DENSITY));

                } else if (attr == R.styleable.ParticlesView_dotColor) {
                    scene.setDotColor(a.getColor(attr, Defaults.PARTICLE_COLOR));

                } else if (attr == R.styleable.ParticlesView_lineColor) {
                    scene.setLineColor(a.getColor(attr, Defaults.LINE_COLOR));

                } else if (attr == R.styleable.ParticlesView_frameDelayMillis) {
                    scene.setFrameDelay(a.getInteger(attr, Defaults.FRAME_DELAY));

                } else if (attr == R.styleable.ParticlesView_stepMultiplier) {
                    scene.setStepMultiplier(a.getFloat(attr, Defaults.SPEED_FACTOR));
                }
            }
            scene.setDotRadiusRange(minDotRadius, maxDotRadius);
        } finally {
            a.recycle();
        }
    }
}
