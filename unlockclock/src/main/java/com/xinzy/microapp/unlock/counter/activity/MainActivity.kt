package com.xinzy.microapp.unlock.counter.activity

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.xinzy.microapp.unlock.counter.R
import com.xinzy.microapp.unlock.counter.service.UnlockCounterService


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onSetWallpaper(v: View) {
//        runWithPermissions(Manifest.permission.SET_WALLPAPER, Manifest.permission.WRITE_SETTINGS) {
//            val wallpaperManager = getSystemService(Context.WALLPAPER_SERVICE) as WallpaperManager
////        wallpaperManager.set
//
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(this, UnlockCounterService::class.java))
            startActivity(intent)
//        }

//        val pickWallpaper = Intent(Intent.ACTION_SET_WALLPAPER)
//        val chooser = Intent.createChooser(pickWallpaper, "选择壁纸")
//        startActivity(chooser)
    }
}
