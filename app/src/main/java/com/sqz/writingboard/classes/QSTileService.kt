package com.sqz.writingboard.classes

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.sqz.writingboard.MainActivity

@Suppress("DEPRECATION")
class QSTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()
    }

    override fun onStopListening() {
        super.onStopListening()
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()
    }

    @SuppressLint("NewApi", "StartActivityAndCollapseDeprecated")
    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivityAndCollapse(
                PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivityAndCollapse(intent)
        }
    }
}