package com.sqz.writingboard.ui.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.getString
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.os.postDelayed
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sqz.writingboard.NavRoute
import com.sqz.writingboard.NavScreen
import com.sqz.writingboard.R
import com.sqz.writingboard.component.Feedback
import com.sqz.writingboard.glance.WritingBoardTextOnlyWidgetReceiver
import com.sqz.writingboard.glance.WritingBoardWidget
import com.sqz.writingboard.glance.WritingBoardWidgetReceiver
import com.sqz.writingboard.tile.QSTileRequestResult
import com.sqz.writingboard.tile.QSTileService
import com.sqz.writingboard.ui.WritingBoardViewModel
import com.sqz.writingboard.ui.component.drawVerticalScrollbar
import com.sqz.writingboard.ui.setting.card.ClickCardLayout
import com.sqz.writingboard.ui.setting.card.DoubleButtonCard
import com.sqz.writingboard.ui.setting.card.ExtraButtonCardLayout
import com.sqz.writingboard.ui.setting.card.SegmentedButtonCardLayout
import com.sqz.writingboard.ui.setting.card.SwitchCardLayout
import com.sqz.writingboard.ui.setting.data.SettingOption
import com.sqz.writingboard.ui.theme.ThemeColor
import com.sqz.writingboard.ui.theme.themeColor

@Composable
fun SettingFunctionList(
    state: LazyListState,
    navController: NavController,
    context: Context,
    view: View,
    dialogType: (DialogType) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WritingBoardViewModel = viewModel()
) {
    val set = SettingOption(context)
    val cardColors = CardDefaults.cardColors(containerColor = themeColor(ThemeColor.CardColor))
    var clickAction by remember { mutableStateOf(false) }
    if (clickAction) {
        if (set.vibrate() == 2) Feedback(context).createOneTick() else {
            Feedback(view = view).createClickSound()
        }
        clickAction = false
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .drawVerticalScrollbar(state),
        state = state
    ) {
        item { Spacer(modifier = modifier.height(1.dp)) }
        item { Spacer(modifier = modifier.height(5.dp)) }
        item {
            Text(
                text = stringResource(R.string.quick_function),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = modifier.padding(top = 16.dp, start = 18.dp)
            )
        }
        item {
            ClickCardLayout(
                intent = { dialogType(DialogType.Export) },
                text = stringResource(R.string.export_writingboard_text),
                painter = R.drawable.output,
                contentDescription = stringResource(R.string.about),
                colors = cardColors
            )
        }
        item {
            Text(
                text = stringResource(R.string.writingboard_app),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = modifier.padding(top = 32.dp, start = 18.dp)
            )
        }
        item {
            SegmentedButtonCardLayout(
                title = stringResource(R.string.choose_theme),
                options = listOf(R.string.light_color, R.string.default_string, R.string.distinct),
                selectedOption = set.theme(),
                colors = cardColors
            ) { index ->
                set.theme(index)
                NavScreen.updateScreen()
                clickAction = true
            }
        }
        item {
            ExtraButtonCardLayout(
                title = stringResource(R.string.button_style),
                options = listOf(
                    R.string.button_hide, R.string.default_string, R.string.button_bottom_bar
                ),
                selectedOption = set.buttonStyle(),
                colors = cardColors,
                expanded = (set.buttonStyle() <= 1),
                switchText = stringResource(R.string.always_visible_text),
                checked = set.alwaysVisibleText(),
                onCheckedChange = {
                    set.alwaysVisibleText(it)
                    clickAction = true
                },
            ) { index ->
                set.buttonStyle(index)
                clickAction = true
            }
        }
        item {
            SwitchCardLayout(
                text = stringResource(R.string.edit_writingboard_button),
                checked = set.editButton(),
                onCheckedChange = {
                    set.editButton(it)
                    clickAction = true
                },
                colors = cardColors
            )
        }
        item {
            SwitchCardLayout(
                text = stringResource(R.string.clean_all_texts_button),
                checked = set.cleanAllText(),
                onCheckedChange = {
                    set.cleanAllText(it)
                    clickAction = true
                },
                colors = cardColors
            )
        }
        item {
            Text(
                text = stringResource(R.string.writingboard),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = modifier.padding(top = 16.dp, start = 18.dp)
            )
        }
        item {
            SegmentedButtonCardLayout(
                title = stringResource(R.string.font_size),
                options = listOf(R.string.small, R.string.medium, R.string.large),
                selectedOption = set.fontSize(),
                colors = cardColors
            ) { index ->
                set.fontSize(index)
                clickAction = true
            }
        }
        item {
            SwitchCardLayout(
                text = stringResource(R.string.font_italics),
                checked = set.italics(),
                onCheckedChange = {
                    set.italics(it)
                    clickAction = true
                },
                colors = cardColors
            )
        }
        item {
            var onClick by remember { mutableStateOf(false) }
            SegmentedButtonCardLayout(
                title = stringResource(R.string.font_style),
                options = listOf(
                    R.string.monospace, R.string.default_string, R.string.serif, R.string.cursive,
                ),
                selectedOption = set.fontStyle(),
                colors = cardColors
            ) { index ->
                set.fontStyle(index)
                clickAction = true
                onClick = true
            }
            if (onClick) {
                LaunchedEffect(true) {
                    WritingBoardWidget().updateAll(context)
                    onClick = false
                }
            }
        }
        item {
            SegmentedButtonCardLayout(
                title = stringResource(R.string.font_weight),
                options = listOf(
                    R.string.thin,
                    R.string.normal,
                    R.string.thick,
                ),
                selectedOption = set.fontWeight(),
                colors = cardColors
            ) { index ->
                set.fontWeight(index)
                clickAction = true
            }
        }
        item {
            Text(
                text = stringResource(R.string.keyboard_texts_action),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = modifier.padding(top = 16.dp, start = 18.dp)
            )
        }
        item {
            SwitchCardLayout(
                text = stringResource(R.string.allow_multiple_lines),
                checked = set.allowMultipleLines(),
                onCheckedChange = {
                    set.allowMultipleLines(it)
                    clickAction = true
                },
                colors = cardColors
            )
        }
        item {
            SwitchCardLayout(
                text = stringResource(R.string.disable_auto_save),
                checked = set.disableAutoSave(),
                onCheckedChange = {
                    set.disableAutoSave(it)
                    clickAction = true
                },
                colors = cardColors
            )
        }
        item {
            Text(
                text = stringResource(R.string.others),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = modifier.padding(top = 16.dp, start = 18.dp)
            )
        }
        item {
            SegmentedButtonCardLayout(
                title = stringResource(R.string.vibrate_settings),
                options = listOf(
                    R.string.disable,
                    R.string.default_string,
                    R.string.more
                ),
                selectedOption = set.vibrate(),
                colors = cardColors
            ) { index ->
                set.vibrate(index)
                clickAction = true
            }
        }
        item {
            var onClick by remember { mutableStateOf(false) }
            var cannot by remember { mutableStateOf(false) }
            ClickCardLayout(
                intent = { onClick = true },
                text = stringResource(R.string.Add_QS_Tile),
                painter = R.drawable.writingboard_logo,
                contentDescription = stringResource(R.string.qs_tile),
                colors = cardColors
            )
            if (onClick) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    QSTileService().RequestAdd()
                } else {
                    viewModel.resultOfQST = -1
                    cannot = true
                }
                if (viewModel.resultOfQST != -5) {
                    QSTileRequestResult().makeToast()
                    clickAction = true
                } else {
                    Handler(Looper.getMainLooper()).postDelayed(50) {
                        onClick = true
                    }
                }
                QSTileRequestResult().makeErrorLog()
                onClick = false
            } else if (cannot) {
                AlertDialog(
                    icon = { Icon(painterResource(id = R.drawable.warning), "") },
                    title = { Text(text = stringResource(R.string.not_support)) },
                    text = { Text(text = stringResource(R.string.not_support_detail)) },
                    dismissButton = {
                        TextButton(onClick = { cannot = false })
                        { Text(stringResource(R.string.dismiss)) }
                    },
                    onDismissRequest = { cannot = false },
                    confirmButton = { cannot = true }
                )
            }
        }
        item {
            var onClick1 by remember { mutableStateOf(false) }
            var onClick2 by remember { mutableStateOf(false) }
            DoubleButtonCard(
                title = stringResource(R.string.request_widget),
                buttonStartName = stringResource(R.string.text_only),
                buttonStartAction = { onClick1 = true },
                buttonEndName = stringResource(R.string.default_string),
                buttonEndAction = { onClick2 = true },
                colors = cardColors
            )
            if (onClick1) {
                clickAction = true
                LaunchedEffect(true) {
                    GlanceAppWidgetManager(context).requestPinGlanceAppWidget(
                        WritingBoardTextOnlyWidgetReceiver::class.java
                    )
                }
                onClick1 = false
            }
            if (onClick2) {
                clickAction = true
                LaunchedEffect(true) {
                    GlanceAppWidgetManager(context).requestPinGlanceAppWidget(
                        WritingBoardWidgetReceiver::class.java
                    )
                }
                onClick2 = false
            }
        }
        item {
            ClickCardLayout(
                intent = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
                        intent.data = Uri.fromParts("package", context.packageName, null)
                        startActivityForResult(context as Activity, intent, 0, null)
                        clickAction = true
                    } else {
                        Toast.makeText(
                            context,
                            getString(context, R.string.language_no_support),
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate(NavRoute.ErrorWithSystemVersionA13.name)
                    }
                },
                text = stringResource(R.string.language),
                painter = R.drawable.ic_language,
                contentDescription = stringResource(R.string.language),
                colors = cardColors
            )
        }
        item {
            Text(
                text = stringResource(R.string.about),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = modifier.padding(top = 16.dp, start = 18.dp)
            )
        }
        item {
            ClickCardLayout(
                intent = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/Z-Siqi/WritingBoard/")
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    //startActivityForResult(context as Activity, intent, 0, null)
                    context.startActivity(intent)
                    clickAction = true
                },
                text = stringResource(R.string.about_app),
                painter = R.drawable.github_mark,
                contentDescription = stringResource(R.string.about),
                colors = cardColors
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
private fun WritingBoardSettingPreview() {
    val navController = rememberNavController()
    WritingBoardSetting(navController, LocalContext.current, LocalView.current)
}
