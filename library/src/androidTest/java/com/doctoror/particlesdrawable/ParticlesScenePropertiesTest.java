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

import static org.junit.Assert.*;

/**
 * {@link ParticlesSceneProperties} test
 */
public final class ParticlesScenePropertiesTest {

    @Test
    public void testResolveDotColorWithDrawableAlphaOpaque() {
        final int dotColor = 0xaa000000;
        assertEquals(dotColor,
                ParticlesSceneProperties.resolveDotColorWithDrawableAlpha(dotColor, 255));
    }

    @Test
    public void testResolveDotColorWithDrawableAlphaTransparent() {
        final int dotColor = 0xaa000000;
        assertEquals(0,
                ParticlesSceneProperties.resolveDotColorWithDrawableAlpha(dotColor, 0));
    }

    @Test
    public void testDotColorResolvedAlpha() {
        final int dotColor = 0xaa000000;

        final ParticlesSceneProperties scene = new ParticlesSceneProperties();
        scene.setDotColor(dotColor);

        assertEquals(dotColor, scene.getDotColorResolvedAlpha());
    }

    @Test
    public void testDotColorResolvedAlphaAfterAlphaChange() {
        final int dotColor = 0xaa000000;
        final int drawableAlpha = 20;

        final ParticlesSceneProperties scene = new ParticlesSceneProperties();
        scene.setDotColor(dotColor);
        scene.setAlpha(drawableAlpha);

        assertEquals(
                ParticlesSceneProperties.resolveDotColorWithDrawableAlpha(dotColor, drawableAlpha),
                scene.getDotColorResolvedAlpha());
    }

    @Test
    public void testDotColorResolvedAlphaWhenChangedAfterAlphaChange() {
        final int dotColor = 0xaa000000;
        final int drawableAlpha = 20;

        final ParticlesSceneProperties scene = new ParticlesSceneProperties();
        scene.setAlpha(drawableAlpha);
        scene.setDotColor(dotColor);

        assertEquals(
                ParticlesSceneProperties.resolveDotColorWithDrawableAlpha(dotColor, drawableAlpha),
                scene.getDotColorResolvedAlpha());
    }

    @Test
    public void testPointsEmptyByDefault() {
        final ParticlesSceneProperties scene = new ParticlesSceneProperties();
        assertTrue(scene.getMutablePoints().isEmpty());
    }

    @Test
    public void testAddTwoPoints() {
        final ParticlesSceneProperties scene = new ParticlesSceneProperties();

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
        new ParticlesSceneProperties().removeFirstPoint();
    }

    @Test
    public void testRemoveFirstPointWithTwoPoints() {
        final ParticlesSceneProperties scene = new ParticlesSceneProperties();

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
        final ParticlesSceneProperties scene = new ParticlesSceneProperties();

        final Particle p1 = new Particle();
        final Particle p2 = new Particle();

        scene.addPoint(p1);
        scene.addPoint(p2);

        scene.clearPoints();
        assertTrue(scene.getMutablePoints().isEmpty());
    }

    @SuppressWarnings("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetFrameDelayNegative() {
        new ParticlesSceneProperties().setFrameDelay(-1);
    }

    @Test
    public void testSetFrameDelay() {
        final ParticlesSceneProperties s = new ParticlesSceneProperties();
        s.setFrameDelay(1);
        assertEquals(1, s.getFrameDelay());
    }

    @SuppressWarnings("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testStepMultiplierNegative() {
        new ParticlesSceneProperties().setStepMultiplier(-0.01f);
    }

    @SuppressWarnings("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testStepMultiplierNAN() {
        new ParticlesSceneProperties().setStepMultiplier(Float.NaN);
    }

    @Test
    public void testSetStepMultiplier() {
        final ParticlesSceneProperties s = new ParticlesSceneProperties();
        s.setStepMultiplier(0f);
        assertEquals(0, s.getStepMultiplier(), Config.ASSERT_DELTA);
    }

    @SuppressWarnings("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetDotRadiusRangeInvalidFirstArgument() {
        new ParticlesSceneProperties().setDotRadiusRange(0.49f, 1f);
    }

    @SuppressWarnings("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetDotRadiusRangeInvalidSecondArgument() {
        new ParticlesSceneProperties().setDotRadiusRange(2f, 0f);
    }

    @SuppressWarnings("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetDotRadiusRangeInvalidBothArguments() {
        new ParticlesSceneProperties().setDotRadiusRange(0.1f, -2f);
    }

    @SuppressWarnings("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetDotRadiusRangeFirstArgumentNAN() {
        new ParticlesSceneProperties().setDotRadiusRange(Float.NaN, 1f);
    }

    @SuppressWarnings("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetDotRadiusRangeSecondArgumentNAN() {
        new ParticlesSceneProperties().setDotRadiusRange(1f, Float.NaN);
    }

    @SuppressWarnings("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetDotRadiusRangeBothArgumentsNAN() {
        new ParticlesSceneProperties().setDotRadiusRange(Float.NaN, Float.NaN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDotRadiusRangeMaxLessThanMin() {
        new ParticlesSceneProperties().setDotRadiusRange(0.7f, 0.6f);
    }

    @Test
    public void testSetDotRadiusRange() {
        final ParticlesSceneProperties s = new ParticlesSceneProperties();
        s.setDotRadiusRange(0.5f, 0.6f);
        assertEquals(0.5, s.getMinDotRadius(), Config.ASSERT_DELTA);
        assertEquals(0.6, s.getMaxDotRadius(), Config.ASSERT_DELTA);
    }

    @SuppressWarnings("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetLineThicknessInvalidArgument() {
        new ParticlesSceneProperties().setLineThickness(0.99f);
    }

    @SuppressWarnings("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetLineThicknessNAN() {
        new ParticlesSceneProperties().setLineThickness(Float.NaN);
    }

    @Test
    public void testSetLineThickness() {
        final ParticlesSceneProperties s = new ParticlesSceneProperties();
        s.setLineThickness(1f);
        assertEquals(1, s.getLineThickness(), Config.ASSERT_DELTA);
    }

    @SuppressWarnings("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetLineDistanceInvalidArgument() {
        new ParticlesSceneProperties().setLineDistance(Float.NEGATIVE_INFINITY);
    }

    @SuppressWarnings("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetLineDistanceNAN() {
        new ParticlesSceneProperties().setLineDistance(Float.NaN);
    }

    @Test
    public void testSetLineDistance() {
        final ParticlesSceneProperties s = new ParticlesSceneProperties();
        s.setLineDistance(0f);
        assertEquals(0, s.getLineDistance(), Config.ASSERT_DELTA);
    }

    @SuppressWarnings("Range")
    @Test(expected = IllegalArgumentException.class)
    public void testSetNumDotsInvalidArgument() {
        new ParticlesSceneProperties().setNumDots(-1);
    }

    @Test
    public void testSetNumDots() {
        final ParticlesSceneProperties s = new ParticlesSceneProperties();
        s.setNumDots(0);
        assertEquals(0, s.getNumDots());
    }

    @Test
    public void testSetLineColor() {
        final ParticlesSceneProperties s = new ParticlesSceneProperties();
        s.setLineColor(2);
        assertEquals(2, s.getLineColor());
    }
}
