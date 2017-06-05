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

import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import io.reactivex.Observable

class AndroidTestStubMutableSettingsRepository : MutableSettingsRepository {
    
    override fun setNumDots(numDots: Int) {
        // Stub
    }

    override fun setFrameDelay(frameDelay: Int) {
        // Stub
    }

    override fun setStepMultiplier(stepMultiplier: Float) {
        // Stub
    }

    override fun setDotScale(dotScale: Float) {
        // Stub
    }

    override fun setLineScale(lineScale: Float) {
        // Stub
    }

    override fun setLineDistance(lineDistance: Float) {
        // Stub
    }

    override fun setParticlesColor(color: Int) {
        // Stub
    }

    override fun setBackgroundUri(uri: String) {
        // Stub
    }

    override fun setBackgroundColor(color: Int) {
        // Stub
    }

    override fun getNumDots(): Observable<Int> {
        return Observable.just(1)
    }

    override fun getFrameDelay(): Observable<Int> {
        return Observable.just(1)
    }

    override fun getStepMultiplier(): Observable<Float> {
        return Observable.just(1f)
    }

    override fun getDotScale(): Observable<Float> {
        return Observable.just(1f)
    }

    override fun getLineScale(): Observable<Float> {
        return Observable.just(1f)
    }

    override fun getLineDistance(): Observable<Float> {
        return Observable.just(1f)
    }

    override fun getParticlesColor(): Observable<Int> {
        return Observable.just(1)
    }

    override fun getBackgroundUri(): Observable<String> {
        return Observable.just("")
    }

    override fun getBackgroundColor(): Observable<Int> {
        return Observable.just(1)
    }
} 
