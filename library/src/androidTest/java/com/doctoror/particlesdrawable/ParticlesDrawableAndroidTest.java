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
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.test.InstrumentationRegistry;
import android.util.TypedValue;

import static org.junit.Assert.*;

/**
 * {@link ParticlesDrawable} test
 */
public final class ParticlesDrawableAndroidTest {

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

    private float dp(final float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }
}
