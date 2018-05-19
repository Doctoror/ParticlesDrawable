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
package com.doctoror.particlesdrawable.util;

import android.graphics.Color;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public final class LineColorResolverTest {

    @Test
    public void lineColorIs0ForTransparentAlpha() {
        final int result = LineColorResolver.resolveLineColorWithAlpha(
                0,
                Color.WHITE,
                10,
                10);

        assertEquals(0, result);
    }

    @Test
    public void lineColorIsUnchangedForMaxDistanceDeltaAndOpaqueAlpha() {
        final int lineColor = Color.DKGRAY;

        final int result = LineColorResolver.resolveLineColorWithAlpha(
                255,
                lineColor,
                10,
                0);

        assertEquals(lineColor, result);
    }

    @Test
    public void lineColorIsTransparentForZeroDistanceDeltaAndOpaqueAlpha() {
        final int color = 0xff112233;
        final int expectedColor = 0x00112233;

        final int result = LineColorResolver.resolveLineColorWithAlpha(
                255,
                color,
                10,
                10);

        assertEquals(expectedColor, result);
    }

    @Test
    public void resolvesLineColorForArbitraryDeltaAndAlpha() {
        final int color = 0xff112233;
        final int expectedColor = 0x2d112233;

        final int result = LineColorResolver.resolveLineColorWithAlpha(
                184,
                color,
                128,
                96);

        assertEquals(expectedColor, result);
    }
}
