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

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Yaroslav Mytkalyk on 12.05.17.
 */
public final class ParticlesViewTest {

    @NonNull
    private ParticlesView newParticlesView() {
        final ParticlesView v = new ParticlesView(InstrumentationRegistry.getContext());
        v.setEmulateOnAttachToWindow(true);
        return v;
    }

    @NonNull
    private ParticlesView newAttachedAndVisibleParticlesView() {
        final ParticlesView v = newParticlesView();
        v.setVisibility(View.VISIBLE);
        v.onAttachedToWindow();
        return v;
    }

    @Test
    public void testIsRunningByDefault() {
        assertFalse(newParticlesView().isRunning());
    }

    @Test
    public void testIsRunningWhenVisibleAndAttached() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        try {
            assertTrue(v.isRunning());
        } finally {
            v.stopInternal();
        }
    }

    @Test
    public void testIsStopedWhenStopped() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.stopInternal();
        assertFalse(v.isRunning());
    }

    @Test
    public void testIsStopedWhenDetachedFromWindow() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.onDetachedFromWindow();
        assertFalse(v.isRunning());
    }

    @Test
    public void testNotRunningWhenInvisible() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.setVisibility(View.INVISIBLE);
        v.onVisibilityChanged(v, View.INVISIBLE);
        assertFalse(v.isRunning());
    }

    @Test
    public void testNotRunningWhenGone() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.setVisibility(View.GONE);
        v.onVisibilityChanged(v, View.GONE);
        assertFalse(v.isRunning());
    }

    @Test
    public void testNotRunningWhenNotVisibleBeforeStart() {
        final ParticlesView v = newParticlesView();
        v.setVisibility(View.INVISIBLE);
        v.onVisibilityChanged(v, View.INVISIBLE);
        v.startInternal();
        assertFalse(v.isRunning());
    }

    @Test
    public void testNotRunningWhenNotVisibleBeforeAttachedToWindow() {
        final ParticlesView v = newParticlesView();
        v.setVisibility(View.INVISIBLE);
        v.onVisibilityChanged(v, View.INVISIBLE);
        v.onAttachedToWindow();
        assertFalse(v.isRunning());
    }

    @Test
    public void testNotRunningWhenExplicitlyStoppedAndAttachedToWindow() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.stop();
        v.onAttachedToWindow();
        assertFalse(v.isRunning());
    }

    @Test
    public void testNotRunningWhenExplicitlyStoppedAndVisible() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.stop();
        v.setVisibility(View.VISIBLE);
        v.onVisibilityChanged(v, View.VISIBLE);
        assertFalse(v.isRunning());
    }

    @Test
    public void testNotRunningWhenExplicitlyStoppedButStartedInternally() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.stop();
        v.startInternal();
        assertFalse(v.isRunning());
    }

    @Test
    public void testRunningWhenExplicitlyStartedAfterExplicitStop() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.stop();
        v.start();
        try {
            assertTrue(v.isRunning());
        } finally {
            v.stopInternal();
        }
    }

    @Test
    public void testMakeBrandNewFrameWhenRunning() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.makeBrandNewFrame();
    }

    @Test
    public void testMakeBrandNewFrameWhenStopped() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.startInternal();
        v.makeBrandNewFrame();
    }

    @Test
    public void testSetFrameDelay() {
        final ParticlesView s = newParticlesView();
        s.setFrameDelay(1);
        assertEquals(1, s.getFrameDelay());
    }

    @Test
    public void testSetStepMultiplier() {
        final ParticlesView s = newParticlesView();
        s.setStepMultiplier(0f);
        assertEquals(0, s.getStepMultiplier(), Config.ASSERT_DELTA);
    }

    @Test
    public void testSetDotRadiusRange() {
        final ParticlesView s = newParticlesView();
        s.setDotRadiusRange(0.5f, 0.6f);
        assertEquals(0.5, s.getMinDotRadius(), Config.ASSERT_DELTA);
        assertEquals(0.6, s.getMaxDotRadius(), Config.ASSERT_DELTA);
    }

    @Test
    public void testSetLineThickness() {
        final ParticlesView s = newParticlesView();
        s.setLineThickness(1f);
        assertEquals(1, s.getLineThickness(), Config.ASSERT_DELTA);
    }

    @Test
    public void testSetLineDistance() {
        final ParticlesView s = newParticlesView();
        s.setLineDistance(0f);
        assertEquals(0, s.getLineDistance(), Config.ASSERT_DELTA);
    }

    @Test
    public void testSetNumDots() {
        final ParticlesView s = newParticlesView();
        s.setNumDots(0);
        assertEquals(0, s.getNumDots());
    }

    @Test
    public void testSetLineColor() {
        final ParticlesView s = newParticlesView();
        s.setLineColor(2);
        assertEquals(2, s.getLineColor());
    }

    @Test
    public void testSetDotColor() {
        final ParticlesView s = newParticlesView();
        s.setDotColor(3);
        assertEquals(3, s.getDotColor());
    }

    @NonNull
    private ParticlesView inflateCustomizedView() {
        return (ParticlesView) LayoutInflater.from(InstrumentationRegistry.getContext()).inflate(
                com.doctoror.particlesdrawable.test.R.layout.view_customized, null);
    }

    private float dp(final float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }

    @Test
    public void testDotRadiusRangeFromAttributes() {
        final ParticlesView v = inflateCustomizedView();
        assertEquals(dp(2), v.getMinDotRadius(), Config.ASSERT_DELTA);
        assertEquals(dp(3), v.getMaxDotRadius(), Config.ASSERT_DELTA);
    }

    @Test
    public void testLineThicknessFromAttributes() {
        final ParticlesView v = inflateCustomizedView();
        assertEquals(dp(4), v.getLineThickness(), Config.ASSERT_DELTA);
    }

    @Test
    public void testLineDistanceFromAttributes() {
        final ParticlesView v = inflateCustomizedView();
        assertEquals(dp(5), v.getLineDistance(), Config.ASSERT_DELTA);
    }

    @Test
    public void testNumDotsFromAttributes() {
        final ParticlesView v = inflateCustomizedView();
        assertEquals(6, v.getNumDots());
    }

    @Test
    public void testDotColorFromAttributes() {
        final ParticlesView v = inflateCustomizedView();
        assertEquals(0xfffffff0, v.getDotColor());
    }

    @Test
    public void testLineColorFromAttributes() {
        final ParticlesView v = inflateCustomizedView();
        assertEquals(0xfffffff1, v.getLineColor());
    }

    @Test
    public void testFrameDelayFromAttributes() {
        final ParticlesView v = inflateCustomizedView();
        assertEquals(7, v.getFrameDelay());
    }

    @Test
    public void testStepMultiplierFromAttributes() {
        final ParticlesView v = inflateCustomizedView();
        assertEquals(1.1, v.getStepMultiplier(), Config.ASSERT_DELTA);
    }
}
