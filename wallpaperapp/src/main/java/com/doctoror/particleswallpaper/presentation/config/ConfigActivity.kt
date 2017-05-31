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
package com.doctoror.particleswallpaper.presentation.config

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryFactory
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.squareup.picasso.Picasso
import io.reactivex.disposables.Disposable

class ConfigActivity : Activity() {

    private val settings: SettingsRepository by lazy {
        SettingsRepositoryFactory.provide(this)
    }

    private var bgDisposable: Disposable? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
    }

    override fun onStart() {
        super.onStart()
        bgDisposable = settings.getBackgroundUri().subscribe({v -> applyBackground(v)})
    }

    override fun onStop() {
        super.onStop()
        bgDisposable?.dispose()
    }

    private fun applyBackground(uri: String) {
        val bg = findViewById(R.id.bg) as ImageView
        if (uri == "") {
            bg.setImageDrawable(null)
        } else {
            Picasso.with(this)
                    .load(uri)
                    .into(bg)
        }
    }
}
