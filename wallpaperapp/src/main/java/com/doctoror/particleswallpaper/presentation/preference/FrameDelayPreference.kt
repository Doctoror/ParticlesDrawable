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
package com.doctoror.particleswallpaper.presentation.preference

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.util.AttributeSet
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.presentation.di.Injector
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * Created by Yaroslav Mytkalyk on 30.05.17.
 */
class FrameDelayPreference @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : SeekBarPreference(context, attrs, defStyle),
        MapperSeekbarPreference<Int>,
        LifecycleObserver {

    @Inject lateinit var settings: MutableSettingsRepository

    val frameDelaySeekbarMin = 10

    var disposable: Disposable? = null

    private val changeAction = Consumer<Int> { t ->
        if (t != null) {
            progress = transformToProgress(t)
        }
    }

    init {
        Injector.configComponent.inject(this)
        max = 80
        isPersistent = false
        setOnPreferenceChangeListener({ _, v ->
            if (v is Int) {
                val value = transformToRealValue(v)
                settings.setFrameDelay(value)
            }
            true
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun subscribe() {
        disposable = settings.getFrameDelay().subscribe(changeAction)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unsubscribe() {
        disposable?.dispose()
    }

    /**
     * The seek bar represents frame rate as percentage.
     * Converts the seek bar value between 0 and 30 to percent and then the percentage to a
     * frame delay, where
     * 10 ms = 100%
     * 40 ms = 0%
     */
    override fun transformToRealValue(progress: Int) = (frameDelaySeekbarMin.toFloat()
            + max.toFloat() * (1f - progress.toFloat() / max.toFloat())).toInt()

    /**
     * Converts frame delay to seek bar frame rate.
     * @see transformToRealValue
     */
    override fun transformToProgress(value: Int): Int {
        val percent = (value.toFloat() - frameDelaySeekbarMin.toFloat()) / max.toFloat()
        return ((1f - percent) * max.toFloat()).toInt()
    }
}