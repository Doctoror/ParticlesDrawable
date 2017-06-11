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

import org.junit.Test;

import android.annotation.SuppressLint;

import static org.junit.Assert.*;

/**
 * {@link ParticlesScene} test
 */
public final class ParticlesSceneTest {

    @Test
    public void testResolveDotColorWithDrawableAlphaOpaque() {
        final int dotColor = 0xaa000000;
        assertEquals(dotColor,
                ParticlesScene.resolveDotColorWithDrawableAlpha(dotColor, 255));
    }

    @Test
    public void testResolveDotColorWithDrawableAlphaTransparent() {
        final int dotColor = 0xaa000000;
        assertEquals(0,
                ParticlesScene.resolveDotColorWithDrawableAlpha(dotColor, 0));
    }

    @Test
    public void testDotColorResolvedAlpha() {
        final int dotColor = 0xaa000000;

        final ParticlesScene scene = new ParticlesScene();
        scene.setDotColor(dotColor);

        assertEquals(dotColor, scene.getDotColorResolvedAlpha());
    }

    @Test
    public void testDotColorResolvedAlphaAfterAlphaChange() {
        final int dotColor = 0xaa000000;
        final int drawableAlpha = 20;

        final ParticlesScene scene = new ParticlesScene();
        scene.setDotColor(dotColor);
        scene.setAlpha(drawableAlpha);

        assertEquals(ParticlesScene.resolveDotColorWithDrawableAlpha(dotColor, drawableAlpha),
                scene.getDotColorResolvedAlpha());
    }

    @Test
    public void testDotColorResolvedAlphaWhenChangedAfterAlphaChange() {
        final int dotColor = 0xaa000000;
        final int drawableAlpha = 20;

        final ParticlesScene scene = new ParticlesScene();
        scene.setAlpha(drawableAlpha);
        scene.setDotColor(dotColor);

        assertEquals(ParticlesScene.resolveDotColorWithDrawableAlpha(dotColor, drawableAlpha),
                scene.getDotColorResolvedAlpha());
    }

    @Test
    public void testPointsEmptyByDefault() {
        final ParticlesScene scene = new ParticlesScene();
        assertTrue(scene.getMutablePoints().isEmpty());
    }

    @Test
    public void testAddTwoPoints() {
        final ParticlesScene scene = new ParticlesScene();

        final Particle p1 = new Particle();
        final Particle p2 = new Particle();

        scene.addPoint(p1);
        scene.addPoint(p2);

        assertFalse(scene.getMutablePoints().isEmpty());

        assertTrue(scene.getMutablePoints().contains(p1));
        assertTrue(scene.getMutablePoints().contains(p2));
    }

    @Test
    public void testRemoveFirstPointWhenEmpty() {
        new ParticlesScene().removeFirstPoint();
    }

    @Test
    public void testRemoveFirstPointWithTwoPoints() {
        final ParticlesScene scene = new ParticlesScene();

        final Particle p1 = new Particle();
        final Particle p2 = new Particle();

        scene.addPoint(p1);
        scene.addPoint(p2);

        scene.removeFirstPoint();

        assertFalse(scene.getMutablePoints().contains(p1));
        assertTrue(scene.getMutablePoints().contains(p2));

        scene.removeFirstPoint();

        assertFalse(scene.getMutablePoints().contains(p2));
    }

    @Test
    public void testClearPointsWithTwoPoints() {
        final ParticlesScene scene = new ParticlesScene();

        final Particle p1 = new Particle();
        final Particle p2 = new Particle();

        scene.addPoint(p1);
        scene.addPoint(p2);

        scene.clearPoints();
        assertTrue(scene.getMutablePoints().isEmpty());
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetFrameDelayNegative() {
        new ParticlesScene().setFrameDelay(-1);
    }

    @Test
    public void testSetFrameDelay() {
        final ParticlesScene s = new ParticlesScene();
        s.setFrameDelay(1);
        assertEquals(1, s.getFrameDelay());
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testStepMultiplierNegative() {
        new ParticlesScene().setStepMultiplier(-0.01f);
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testStepMultiplierNAN() {
        new ParticlesScene().setStepMultiplier(Float.NaN);
    }

    @Test
    public void testSetStepMultiplier() {
        final ParticlesScene s = new ParticlesScene();
        s.setStepMultiplier(0f);
        assertEquals(0, s.getStepMultiplier(), Config.ASSERT_DELTA);
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetDotRadiusRangeInvalidFirstArgument() {
        new ParticlesScene().setDotRadiusRange(0.49f, 1f);
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetDotRadiusRangeInvalidSecondArgument() {
        new ParticlesScene().setDotRadiusRange(2f, 0f);
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetDotRadiusRangeInvalidBothArguments() {
        new ParticlesScene().setDotRadiusRange(0.1f, -2f);
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetDotRadiusRangeFirstArgumentNAN() {
        new ParticlesScene().setDotRadiusRange(Float.NaN, 1f);
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetDotRadiusRangeSecondArgumentNAN() {
        new ParticlesScene().setDotRadiusRange(1f, Float.NaN);
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetDotRadiusRangeBothArgumentsNAN() {
        new ParticlesScene().setDotRadiusRange(Float.NaN, Float.NaN);
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetDotRadiusRangeMaxLessThanMin() {
        new ParticlesScene().setDotRadiusRange(0.7f, 0.6f);
    }

    @Test
    public void testSetDotRadiusRange() {
        final ParticlesScene s = new ParticlesScene();
        s.setDotRadiusRange(0.5f, 0.6f);
        assertEquals(0.5, s.getMinDotRadius(), Config.ASSERT_DELTA);
        assertEquals(0.6, s.getMaxDotRadius(), Config.ASSERT_DELTA);
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetLineThicknessInvalidArgument() {
        new ParticlesScene().setLineThickness(0.99f);
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetLineThicknessNAN() {
        new ParticlesScene().setLineThickness(Float.NaN);
    }

    @Test
    public void testSetLineThickness() {
        final ParticlesScene s = new ParticlesScene();
        s.setLineThickness(1f);
        assertEquals(1, s.getLineThickness(), Config.ASSERT_DELTA);
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetLineDistanceInvalidArgument() {
        new ParticlesScene().setLineDistance(Float.NEGATIVE_INFINITY);
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetLineDistanceNAN() {
        new ParticlesScene().setLineDistance(Float.NaN);
    }

    @Test
    public void testSetLineDistance() {
        final ParticlesScene s = new ParticlesScene();
        s.setLineDistance(0f);
        assertEquals(0, s.getLineDistance(), Config.ASSERT_DELTA);
    }

    @SuppressLint("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetNumDotsInvalidArgument() {
        new ParticlesScene().setNumDots(-1);
    }

    @Test
    public void testSetNumDots() {
        final ParticlesScene s = new ParticlesScene();
        s.setNumDots(0);
        assertEquals(0, s.getNumDots());
    }

    @Test
    public void testSetLineColor() {
        final ParticlesScene s = new ParticlesScene();
        s.setLineColor(2);
        assertEquals(2, s.getLineColor());
    }
}
