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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public final class ParticleColorResolverTest {

    @Test
    public void resolvesColorWithOpaqueAlpha() {
        final int particleColor = 0xaa000000;
        assertEquals(particleColor,
                ParticleColorResolver.resolveParticleColorWithSceneAlpha(particleColor, 255));
    }

    @Test
    public void resolvesColorWithTransparentAlpha() {
        final int particleColor = 0xaa000000;
        assertEquals(0,
                ParticleColorResolver.resolveParticleColorWithSceneAlpha(particleColor, 0));
    }
}
