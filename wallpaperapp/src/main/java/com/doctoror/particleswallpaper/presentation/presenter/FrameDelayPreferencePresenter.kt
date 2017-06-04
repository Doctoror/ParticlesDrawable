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
package com.doctoror.particleswallpaper.presentation.presenter

import android.support.annotation.VisibleForTesting
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.presentation.view.SeekBarPreferenceView
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * Created by Yaroslav Mytkalyk on 03.06.17.
 */
class FrameDelayPreferencePresenter @Inject constructor(val settings: MutableSettingsRepository)
    : Presenter<SeekBarPreferenceView>, MapperSeekBarPresenter<Int> {

    lateinit var view: SeekBarPreferenceView

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val frameDelaySeekbarMin = 10
    private val frameDelaySeekbarMax = 80

    private var disposable: Disposable? = null

    private val changeAction = Consumer<Int> { t ->
        if (t != null) {
            view.setProgressInt(transformToProgress(t))
        }
    }

    override fun onTakeView(view: SeekBarPreferenceView) {
        view.setMaxInt(frameDelaySeekbarMax)
        this.view = view
    }

    fun onPreferenceChange(v: Int?) {
        if (v != null) {
            val value = transformToRealValue(v)
            settings.setFrameDelay(value)
        }
    }

    override fun onStart() {
        disposable = settings.getFrameDelay().subscribe(changeAction)
    }

    override fun onStop() {
        disposable?.dispose()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun getSeekbarMax() = frameDelaySeekbarMax

    /**
     * The seek bar represents frame rate as percentage.
     * Converts the seek bar value between 0 and 30 to percent and then the percentage to a
     * frame delay, where
     * 10 ms = 100%
     * 40 ms = 0%
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun transformToRealValue(progress: Int) = (frameDelaySeekbarMin.toFloat()
            + view.getMaxInt().toFloat() * (1f - progress.toFloat() / view.getMaxInt().toFloat())).toInt()

    /**
     * Converts frame delay to seek bar frame rate.
     * @see transformToRealValue
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun transformToProgress(value: Int): Int {
        val percent = (value.toFloat() - frameDelaySeekbarMin.toFloat()) / view.getMaxInt().toFloat()
        return ((1f - percent) * view.getMaxInt().toFloat()).toInt()
    }
}