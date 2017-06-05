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
package com.doctoror.particleswallpaper.data.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.doctoror.particlesdrawable.ParticlesDrawable
import com.doctoror.particleswallpaper.domain.config.DrawableConfigurator
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.di.Injector
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.disposables.Disposable
import javax.inject.Inject

/**
 * Created by Yaroslav Mytkalyk on 18.04.17.
 */
class WallpaperServiceImpl : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return EngineImpl()
    }

    inner class EngineImpl internal constructor() : Engine() {

        @Inject lateinit var mConfigurator: DrawableConfigurator
        @Inject lateinit var mSettings: SettingsRepository

        private var mFrameDelayDisposable: Disposable? = null
        private var mBackgroundDisposable: Disposable? = null
        private var mBackgroundColorDisposable: Disposable? = null

        private val DEFAULT_DELAY = 10L
        private val MIN_DELAY = 5L

        private val mPaint = Paint()

        private val mHandler = Handler(Looper.getMainLooper())
        private val mDrawable = ParticlesDrawable()

        private var mVisible = false

        private var mWidth = 0f
        private var mHeight = 0f

        private var mBackground: Bitmap? = null
        private var mDelay = DEFAULT_DELAY

        init {
            mPaint.style = Paint.Style.FILL
            mPaint.color = Color.BLACK
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            Injector.configComponent.inject(this)
            mConfigurator.subscribe(mDrawable, mSettings)

            mFrameDelayDisposable = mSettings.getFrameDelay().subscribe({d -> mDelay = d.toLong()})
            mBackgroundDisposable = mSettings.getBackgroundUri().subscribe({ u -> handleBackground(u) })
            mBackgroundColorDisposable = mSettings.getBackgroundColor().subscribe({ c -> mPaint.color = c })
        }

        override fun onDestroy() {
            super.onDestroy()
            mConfigurator.dispose()
            mFrameDelayDisposable?.dispose()
            mBackgroundDisposable?.dispose()
            mBackgroundColorDisposable?.dispose()
        }

        private fun handleBackground(uri: String) {
            if (uri == "") {
                mBackground = null
            } else if (mWidth != 0f && mHeight != 0f) {
                Picasso.with(this@WallpaperServiceImpl)
                        .load(uri)
                        .resize(mWidth.toInt(), mHeight.toInt())
                        .centerCrop()
                        .into(mImageLoadTarget)
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int,
                                      height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            mDrawable.setBounds(0, 0, width, height)
            mWidth = width.toFloat()
            mHeight = height.toFloat()
            handleBackground(mSettings.getBackgroundUri().blockingFirst())
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            mVisible = false
            mHandler.removeCallbacks(mDrawRunnable)
            mDrawable.stop()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            mVisible = visible
            if (visible) {
                mDrawable.start()
                mHandler.post(mDrawRunnable)
            } else {
                mHandler.removeCallbacks(mDrawRunnable)
                mDrawable.stop()
            }
        }

        private fun draw() {
            val startTime = SystemClock.uptimeMillis()
            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    drawBackground(canvas)
                    mDrawable.draw(canvas)
                    mDrawable.run()
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
            mHandler.removeCallbacks(mDrawRunnable)
            if (mVisible) {
                mHandler.postDelayed(mDrawRunnable,
                        Math.max(mDelay - (SystemClock.uptimeMillis() - startTime), MIN_DELAY))
            }
        }

        private fun drawBackground(c: Canvas) {
            val background = mBackground
            if (background == null) {
                c.drawRect(0f, 0f, mWidth, mHeight, mPaint)
            } else {
                c.drawBitmap(background, 0f, 0f, mPaint)
            }
        }

        private val mDrawRunnable = Runnable { this.draw() }

        private val mImageLoadTarget = object : Target {

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                // Unhandled
            }

            override fun onBitmapFailed(errorDrawable: Drawable?) {
                mBackground = null
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                mBackground = bitmap
            }
        }
    }
}
