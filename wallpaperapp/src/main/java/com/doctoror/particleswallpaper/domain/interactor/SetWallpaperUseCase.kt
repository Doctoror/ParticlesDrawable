package com.doctoror.particleswallpaper.domain.interactor

import android.app.Activity
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.util.Log
import com.doctoror.particleswallpaper.data.engine.WallpaperServiceImpl
import io.reactivex.Observable

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 */
class SetWallpaperUseCase(val a: Activity, val requestCode: Int) : UseCase<Boolean> {

    private val tag = "SetWallpaperUseCase"

    override fun useCase(): Observable<Boolean> = Observable.fromCallable({ -> action()})

    private fun action(): Boolean {
        val intent = Intent()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            intent.action = WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(a.packageName, WallpaperServiceImpl::class.java.canonicalName))
        } else {
            intent.action = WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER
        }
        try {
            a.startActivityForResult(intent, requestCode)
            return true
        } catch (e: ActivityNotFoundException) {
            Log.w(tag, e)
            return false
        }
    }
}