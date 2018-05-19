package com.doctoror.particlesdrawable.renderer;

import android.support.annotation.NonNull;

import com.doctoror.particlesdrawable.ParticlesScene;
import com.doctoror.particlesdrawable.contract.LowLevelRenderer;
import com.doctoror.particlesdrawable.contract.SceneRenderer;
import com.doctoror.particlesdrawable.util.DistanceResolver;
import com.doctoror.particlesdrawable.util.LineColorResolver;
import com.doctoror.particlesdrawable.util.ParticleColorResolver;

import java.nio.FloatBuffer;

public final class DefaultSceneRenderer implements SceneRenderer {

    private final LowLevelRenderer renderer;

    public DefaultSceneRenderer(@NonNull final LowLevelRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void drawScene(@NonNull final ParticlesScene scene) {
        if (scene.getNumDots() > 0) {

            final int particleColor = ParticleColorResolver.resolveParticleColorWithSceneAlpha(
                    scene.getDotColor(),
                    scene.getAlpha()
            );

            final FloatBuffer radiuses = scene.getRadiuses();
            final int particlesCount = scene.getParticlesCount();
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

                        renderer.drawLine(
                                x1,
                                y1,
                                x2,
                                y2,
                                scene.getLineThickness(),
                                lineColor);
                    }
                }

                final float radius = radiuses.get(i);
                renderer.fillCircle(x1, y1, radius, particleColor);
            }
        }
    }
}
