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
package com.doctoror.particleswallpaper.data.repository

import android.content.res.Resources
import android.graphics.Color
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import io.reactivex.Observable

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 */
class SettingsRepositoryDefault(val res: Resources) : SettingsRepository {

    override fun getNumDots() = Observable.just(res.getInteger(R.integer.default_density))!!

    override fun getFrameDelay() = Observable.just(10)!!

    override fun getStepMultiplier() = Observable.just(1f)!!

    override fun getDotScale() = Observable.just(res.getDimension(R.dimen.default_dot_scale))!!

    override fun getLineScale() = Observable.just(res.getDimension(R.dimen.default_line_scale))!!

    override fun getLineDistance() = Observable.just(res.getDimension(R.dimen.default_line_distance))!!

    override fun getParticlesColor() = Observable.just(Color.WHITE)!!

    override fun getBackgroundUri() = Observable.just("")!!

    override fun getBackgroundColor() = Observable.just(Color.BLACK)!!
}