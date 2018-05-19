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
package com.doctoror.particlesdrawable.util

import com.doctoror.particlesdrawable.util.ParticleColorResolver.resolveParticleColorWithSceneAlpha

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ParticleColorResolverTest {

    @Test
    fun resolvesColorWithOpaqueAlpha() {
        val particleColor = 0xaa000000.toInt()
        assertEquals(particleColor,
                resolveParticleColorWithSceneAlpha(particleColor, 255))
    }

    @Test
    fun resolvesColorWithTransparentAlpha() {
        val particleColor = 0xaa000000.toInt()
        assertEquals(0,
                resolveParticleColorWithSceneAlpha(particleColor, 0))
    }
}
