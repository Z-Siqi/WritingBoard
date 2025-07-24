package com.sqz.writingboard.tile

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sqz.writingboard.MainActivity
import com.sqz.writingboard.R
import java.util.concurrent.Executors
import java.util.function.Consumer

class QSTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()
    }

    override fun onStopListening() {
        super.onStopListening()
        Log.w("WritingBoardTag", "onStopListening")
    }

    override fun onClick() {
        super.onClick()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivityAndCollapse(
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            )
        } else {
            this.onClickInOldVersion()
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("NewApi", "StartActivityAndCollapseDeprecated")
    private fun onClickInOldVersion() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityAndCollapse(intent)
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun requestAdd(context: Context, returnValue: (Int) -> Unit) {
            val statusBarManager = ContextCompat.getSystemService(
                context, StatusBarManager::class.java
            ) as StatusBarManager
            val callback = Consumer<Int> { result -> returnValue(result) }
            statusBarManager.requestAddTileService(
                ComponentName(context, "com.sqz.writingboard.tile.QSTileService"),
                ActivityCompat.getString(context, R.string.app_name),
                Icon.createWithResource(context, R.drawable.writingboard_logo),
                Executors.newSingleThreadExecutor(),
                callback
            )
        }
    }
}
