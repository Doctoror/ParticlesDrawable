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

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.test.InstrumentationRegistry;
import android.util.TypedValue;

import static org.junit.Assert.*;

/**
 * {@link ParticlesDrawable} test
 */
public final class ParticlesDrawableTest {

    @Test
    public void testIsRunningByDefault() {
        assertFalse(new ParticlesDrawable().isRunning());
    }

    @Test
    public void testIsRunningWhenStarted() {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.setBounds(0, 0, 10, 10);
        d.start();
        try {
            assertTrue(d.isRunning());
        } finally {
            d.stop();
        }
    }

    @Test
    public void testIsStopedWhenStopped() {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.setBounds(0, 0, 10, 10);
        d.start();
        d.stop();
        assertFalse(d.isRunning());
    }

    @Test
    public void testSetBoundsWhenRunning() {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.start();
        d.setBounds(0, 0, 10, 10);
        d.stop();
    }

    @Test
    public void testWithZeroBounds() {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.setBounds(0, 0, 0, 0);
        d.start();
        assertTrue(d.isRunning());
        d.stop();
        assertFalse(d.isRunning());
    }

    @Test
    public void testMakeBrandNewFrameWithZeroBounds() {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.makeBrandNewFrame();
    }

    @Test
    public void testMakeBrandNewFrame() {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.setBounds(0, 0, 10, 10);
        d.makeBrandNewFrame();
    }

    @Test
    public void testMakeBrandNewFrameWhenRunning() {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.setBounds(0, 0, 10, 10);
        d.start();
        d.makeBrandNewFrame();
        d.stop();
    }

    @Test
    public void testDotRadiusRangeThatRoundsUpToTheSameInt() throws Throwable {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.setBounds(0, 0, 10, 10);
        d.setDotRadiusRange(0.5f, 0.6f);
        d.makeBrandNewFrame();
    }

    @NonNull
    @RequiresApi(Build.VERSION_CODES.N)
    private ParticlesDrawable inflateCustomizedDrawable() {
        final ParticlesDrawable result = (ParticlesDrawable) InstrumentationRegistry.getContext()
                .getDrawable(com.doctoror.particlesdrawable.test.R.drawable.drawable_customized);
        if (result == null) {
            throw new NullPointerException("getDrawable() returned null");
        }
        return result;
    }

    private float dp(final float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }

    @Test
    @TargetApi(Build.VERSION_CODES.N)
    public void testDotRadiusRangeFromAttributes() {
        final ParticlesDrawable v = inflateCustomizedDrawable();
        assertEquals(dp(2), v.getMinDotRadius(), Config.ASSERT_DELTA);
        assertEquals(dp(3), v.getMaxDotRadius(), Config.ASSERT_DELTA);
    }

    @Test
    @TargetApi(Build.VERSION_CODES.N)
    public void testLineThicknessFromAttributes() {
        final ParticlesDrawable v = inflateCustomizedDrawable();
        assertEquals(dp(4), v.getLineThickness(), Config.ASSERT_DELTA);
    }

    @Test
    @TargetApi(Build.VERSION_CODES.N)
    public void testLineDistanceFromAttributes() {
        final ParticlesDrawable v = inflateCustomizedDrawable();
        assertEquals(dp(5), v.getLineDistance(), Config.ASSERT_DELTA);
    }

    @Test
    @TargetApi(Build.VERSION_CODES.N)
    public void testNumDotsFromAttributes() {
        final ParticlesDrawable v = inflateCustomizedDrawable();
        assertEquals(6, v.getNumDots());
    }

    @Test
    @TargetApi(Build.VERSION_CODES.N)
    public void testDotColorFromAttributes() {
        final ParticlesDrawable v = inflateCustomizedDrawable();
        assertEquals(0xfffffff0, v.getDotColor());
    }

    @Test
    @TargetApi(Build.VERSION_CODES.N)
    public void testLineColorFromAttributes() {
        final ParticlesDrawable v = inflateCustomizedDrawable();
        assertEquals(0xfffffff1, v.getLineColor());
    }

    @Test
    @TargetApi(Build.VERSION_CODES.N)
    public void testFrameDelayFromAttributes() {
        final ParticlesDrawable v = inflateCustomizedDrawable();
        assertEquals(7, v.getFrameDelay());
    }

    @Test
    @TargetApi(Build.VERSION_CODES.N)
    public void testStepMultiplierFromAttributes() {
        final ParticlesDrawable v = inflateCustomizedDrawable();
        assertEquals(1.1, v.getStepMultiplier(), Config.ASSERT_DELTA);
    }

    @Test
    public void testSetFrameDelay() {
        final ParticlesDrawable s = new ParticlesDrawable();
        s.setFrameDelay(1);
        assertEquals(1, s.getFrameDelay());
    }

    @Test
    public void testSetStepMultiplier() {
        final ParticlesDrawable s = new ParticlesDrawable();
        s.setStepMultiplier(0f);
        assertEquals(0, s.getStepMultiplier(), Config.ASSERT_DELTA);
    }

    @Test
    public void testSetDotRadiusRange() {
        final ParticlesDrawable s = new ParticlesDrawable();
        s.setDotRadiusRange(0.5f, 0.6f);
        assertEquals(0.5, s.getMinDotRadius(), Config.ASSERT_DELTA);
        assertEquals(0.6, s.getMaxDotRadius(), Config.ASSERT_DELTA);
    }

    @Test
    public void testSetLineThickness() {
        final ParticlesDrawable s = new ParticlesDrawable();
        s.setLineThickness(1f);
        assertEquals(1, s.getLineThickness(), Config.ASSERT_DELTA);
    }

    @Test
    public void testSetLineDistance() {
        final ParticlesDrawable s = new ParticlesDrawable();
        s.setLineDistance(0f);
        assertEquals(0, s.getLineDistance(), Config.ASSERT_DELTA);
    }

    @Test
    public void testSetNumDots() {
        final ParticlesDrawable s = new ParticlesDrawable();
        s.setNumDots(0);
        assertEquals(0, s.getNumDots());
    }

    @Test
    public void testSetLineColor() {
        final ParticlesDrawable s = new ParticlesDrawable();
        s.setLineColor(2);
        assertEquals(2, s.getLineColor());
    }

    @Test
    public void testSetDotColor() {
        final ParticlesDrawable s = new ParticlesDrawable();
        s.setDotColor(3);
        assertEquals(3, s.getDotColor());
    }
}
