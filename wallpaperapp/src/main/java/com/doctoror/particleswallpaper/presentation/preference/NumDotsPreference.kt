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

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryFactory
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * Created by Yaroslav Mytkalyk on 30.05.17.
 */
class NumDotsPreference @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : SeekBarPreference(context, attrs, defStyle), MapperSeekbarPreference<Int> {

    val settings: MutableSettingsRepository = SettingsRepositoryFactory.provideMutable(context)
    var disposable: Disposable? = null

    private val changeAction = Consumer<Int> { t ->
        if (t != null) {
            progress = transformToProgress(t)
        }
    }

    init {
        isPersistent = false
        max = 119
        setOnPreferenceChangeListener({ _, v ->
            if (v is Int) {
                val value = transformToRealValue(v)
                settings.setNumDots(value)
            }
            true
        })
        subscribe()
    }

    private fun subscribe() {
        disposable = settings.getNumDots().subscribe(changeAction)
    }

    private fun unsubscribe() {
        disposable?.dispose()
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        subscribe()
    }

    override fun onSaveInstanceState(): Parcelable {
        unsubscribe()
        return super.onSaveInstanceState()
    }

    override fun transformToRealValue(progress: Int) = progress + 1
    override fun transformToProgress(value: Int) = value - 1
}