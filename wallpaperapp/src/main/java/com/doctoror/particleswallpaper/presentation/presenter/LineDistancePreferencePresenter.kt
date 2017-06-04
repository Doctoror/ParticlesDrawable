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
class LineDistancePreferencePresenter @Inject constructor(val settings: MutableSettingsRepository)
    : Presenter<SeekBarPreferenceView>, MapperSeekBarPresenter<Float> {

    private lateinit var view: SeekBarPreferenceView

    private val seekBarMaxValue = 100
    private var disposable: Disposable? = null

    private val changeAction = Consumer<Float> { t ->
        if (t != null) {
            view.setProgressInt(transformToProgress(t))
        }
    }

    override fun onTakeView(view: SeekBarPreferenceView) {
        view.setMaxInt(seekBarMaxValue)
        this.view = view
    }

    override fun onStart() {
        disposable = settings.getLineDistance().subscribe(changeAction)
    }

    override fun onStop() {
        disposable?.dispose()
    }

    fun onPreferenceChange(v: Int?) {
        if (v != null) {
            val value = transformToRealValue(v)
            settings.setLineDistance(value)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun getSeekbarMax() = seekBarMaxValue

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun transformToRealValue(progress: Int) = progress.toFloat() * 3f

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun transformToProgress(value: Float) = (value / 3f).toInt()
}