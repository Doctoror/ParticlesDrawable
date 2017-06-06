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
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.annotation.ColorInt
import android.widget.ImageView
import com.doctoror.particlesdrawable.ParticlesDrawable
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.domain.ads.AdsProvider
import com.doctoror.particleswallpaper.domain.config.DrawableConfigurator
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.compat.ViewCompat
import com.doctoror.particleswallpaper.presentation.di.Injector
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

open class ConfigActivity : Activity() {

    private val particlesDrawable = ParticlesDrawable()

    @Inject lateinit var configurator: DrawableConfigurator
    @Inject lateinit var settings: SettingsRepository
    @Inject lateinit var adProvider: AdsProvider

    private var bgDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Injector.configComponent.inject(this)
        adProvider.initialize()

        setContentView(R.layout.activity_config)
        setBackground()
        initAdView()
    }

    private fun setBackground() {
        ViewCompat.setBackground(findViewById(R.id.drawableContainer), particlesDrawable)
    }

    private fun initAdView() {
        val adView: AdView = findViewById(R.id.adView) as AdView
        adView.loadAd(AdRequest.Builder()
                .addTestDevice("1644CF0C8CE728912DC93B6C340AB453")
                .build())
    }

    override fun onStart() {
        super.onStart()
        bgDisposable = Observable.combineLatest(
                settings.getBackgroundUri(),
                settings.getBackgroundColor(),
                BiFunction<String, Int, Pair<String, Int>> { t1, t2 -> Pair(t1!!, t2!!) })
                .subscribe({ result: Pair<String, Int> -> applyBackground(result) })
        configurator.subscribe(particlesDrawable, settings)
        particlesDrawable.start()
    }

    override fun onStop() {
        super.onStop()
        bgDisposable?.dispose()
        particlesDrawable.stop()
        configurator.dispose()
    }

    private fun applyBackground(result: Pair<String, Int>) {
        applyBackground(result.first, result.second)
    }

    private fun applyBackground(uri: String, @ColorInt color: Int) {
        val bg: ImageView = findViewById(R.id.bg) as ImageView
        if (uri == "") {
            onNoBackgroundImage(bg, color)
        } else {
            Picasso.with(this)
                    .load(uri)
                    .into(bg, object : Callback {
                        override fun onSuccess() {
                            ViewCompat.setBackground(bg, null)
                        }

                        override fun onError() {
                            onNoBackgroundImage(bg, color)
                        }
                    })
        }
    }

    private fun onNoBackgroundImage(bg: ImageView, @ColorInt color: Int) {
        bg.setImageDrawable(null)
        ViewCompat.setBackground(bg,
                (if (color == Color.BLACK) null else ColorDrawable(color)))
    }
}
