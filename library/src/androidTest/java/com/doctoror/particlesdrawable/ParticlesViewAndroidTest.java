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

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.util.TypedValue;
import android.view.LayoutInflater;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * {@link ParticlesView} test
 */
public final class ParticlesViewAndroidTest {

    @SuppressLint("InflateParams")
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
