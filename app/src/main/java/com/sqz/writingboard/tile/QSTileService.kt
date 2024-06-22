package com.sqz.writingboard.tile

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.MainActivity
import com.sqz.writingboard.R
import com.sqz.writingboard.ui.WritingBoardViewModel
import java.util.concurrent.Executors
import java.util.function.Consumer

@Suppress("DEPRECATION")
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun RequestAdd(viewModel: WritingBoardViewModel = viewModel()) {
        val context = LocalContext.current
        val statusBarManager = ContextCompat.getSystemService(
            context, StatusBarManager::class.java
        ) as StatusBarManager
        val callback = Consumer<Int> { result -> viewModel.resultOfQST = result }
        statusBarManager.requestAddTileService(
            ComponentName(context, "com.sqz.writingboard.classes.QSTileService"),
            ActivityCompat.getString(context, R.string.app_name),
            Icon.createWithResource(context, R.drawable.writingboard_logo),
            Executors.newSingleThreadExecutor(),
            callback
        )
    }
}
