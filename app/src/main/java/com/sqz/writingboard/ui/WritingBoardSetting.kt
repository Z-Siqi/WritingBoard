package com.sqz.writingboard.ui

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.getString
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.os.postDelayed
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sqz.writingboard.R
import com.sqz.writingboard.classes.QSTileRequestResult
import com.sqz.writingboard.classes.QSTileService
import com.sqz.writingboard.classes.ValueState
import com.sqz.writingboard.classes.WritingBoardSettingState
import com.sqz.writingboard.component.Vibrate
import com.sqz.writingboard.dataStore
import com.sqz.writingboard.glance.WritingBoardTextOnlyWidgetReceiver
import com.sqz.writingboard.glance.WritingBoardWidget
import com.sqz.writingboard.glance.WritingBoardWidgetReceiver
import com.sqz.writingboard.settingState
import com.sqz.writingboard.ui.component.setting.ClickCardLayout
import com.sqz.writingboard.ui.component.setting.SegmentedButtonCardLayout
import com.sqz.writingboard.ui.component.drawVerticalScrollbar
import com.sqz.writingboard.ui.component.setting.DoubleButtonCard
import com.sqz.writingboard.ui.component.setting.ExtraButtonCardLayout
import com.sqz.writingboard.ui.component.setting.SwitchCardLayout
import com.sqz.writingboard.ui.theme.themeColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val setting = WritingBoardSettingState()

@Composable
private fun SettingFunction(
    state: LazyListState,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val valueState: ValueState = viewModel()

    val cardColors = CardDefaults.cardColors(containerColor = themeColor("cardColor"))
    var clickAction by remember { mutableStateOf(false) }
    val readVibrateSettings = settingState.readSegmentedButtonState("vibrate_settings", context)
    if (clickAction) {
        if (readVibrateSettings == 2) Vibrate()
        clickAction = false
    }

    var allowMultipleLines by setting.rememberSwitchState("allow_multiple_lines", context)
    var cleanAllText by setting.rememberSwitchState("clean_all_text", context)
    var editButton by setting.rememberSwitchState("edit_button", context)
    var theme by setting.rememberSegmentedButtonState("theme", context)
    var fontSize by setting.rememberSegmentedButtonState("font_size", context)
    var italics by setting.rememberSwitchState("italics", context)
    var fontStyle by setting.rememberSegmentedButtonState("font_style", context)
    var buttonStyle by setting.rememberSegmentedButtonState("button_style", context)
    var fontWeight by setting.rememberSegmentedButtonState("font_weight", context)
    var disableAutoSave by setting.rememberSwitchState("disable_auto_save", context)
    var alwaysVisibleText by setting.rememberSwitchState("always_visible_text", context)
    var vibrate by setting.rememberSegmentedButtonState("vibrate_settings", context)

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
                selectedOption = theme,
                colors = cardColors
            ) { index ->
                theme = index
                setting.writeSegmentedButtonState(
                    "theme",
                    context,
                    index
                )
                valueState.updateScreen = true
                clickAction = true
            }
        }
        item {
            ExtraButtonCardLayout(
                title = stringResource(R.string.button_style),
                options = listOf(
                    R.string.button_hide,
                    R.string.default_string,
                    R.string.button_bottom_bar
                ),
                selectedOption = buttonStyle,
                colors = cardColors,
                expanded = (settingState.readSegmentedButtonState("button_style", context) <= 1),
                switchText = stringResource(R.string.always_visible_text),
                checked = alwaysVisibleText,
                onCheckedChange = {
                    alwaysVisibleText = it
                    setting.writeSwitchState("always_visible_text", context, it)
                    clickAction = true
                },
            ) { index ->
                buttonStyle = index
                setting.writeSegmentedButtonState(
                    "button_style",
                    context,
                    index
                )
                clickAction = true
            }
        }
        item {
            SwitchCardLayout(
                text = stringResource(R.string.edit_writingboard_button),
                checked = editButton,
                onCheckedChange = {
                    editButton = it
                    setting.writeSwitchState("edit_button", context, it)
                    clickAction = true
                },
                colors = cardColors
            )
        }
        item {
            SwitchCardLayout(
                text = stringResource(R.string.clean_all_texts_button),
                checked = cleanAllText,
                onCheckedChange = {
                    cleanAllText = it
                    setting.writeSwitchState("clean_all_text", context, it)
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
                selectedOption = fontSize,
                colors = cardColors
            ) { index ->
                fontSize = index
                setting.writeSegmentedButtonState(
                    "font_size",
                    context,
                    index
                )
                clickAction = true
            }
        }
        item {
            SwitchCardLayout(
                text = stringResource(R.string.font_italics),
                checked = italics,
                onCheckedChange = {
                    italics = it
                    setting.writeSwitchState("italics", context, it)
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
                    R.string.monospace,
                    R.string.default_string,
                    R.string.serif,
                    R.string.cursive,
                ),
                selectedOption = fontStyle,
                colors = cardColors
            ) { index ->
                fontStyle = index
                setting.writeSegmentedButtonState(
                    "font_style",
                    context,
                    index
                )
                clickAction = true
                onClick = true
            }
            if (onClick) {
                LaunchedEffect(true) {
                    updateWidget(context)
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
                selectedOption = fontWeight,
                colors = cardColors
            ) { index ->
                fontWeight = index
                setting.writeSegmentedButtonState(
                    "font_weight",
                    context,
                    index
                )
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
                checked = allowMultipleLines,
                onCheckedChange = {
                    allowMultipleLines = it
                    setting.writeSwitchState("allow_multiple_lines", context, it)
                    clickAction = true
                },
                colors = cardColors
            )
        }
        item {
            SwitchCardLayout(
                text = stringResource(R.string.disable_auto_save),
                checked = disableAutoSave,
                onCheckedChange = {
                    disableAutoSave = it
                    setting.writeSwitchState("disable_auto_save", context, it)
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
                selectedOption = vibrate,
                colors = cardColors
            ) { index ->
                vibrate = index
                setting.writeSegmentedButtonState(
                    "vibrate_settings",
                    context,
                    index
                )
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
                contentDescription = "QS_Tile",
                colors = cardColors
            )
            if (onClick) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    QSTileService().RequestAdd()
                } else {
                    valueState.resultOfQST = -1
                    cannot = true
                }
                if (valueState.resultOfQST != -5) {
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
                        navController.navigate("ErrorWithSystemVersionA13")
                    }
                },
                text = stringResource(R.string.language),
                painter = R.drawable.ic_language,
                contentDescription = "Language",
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
                    startActivityForResult(context as Activity, intent, 0, null)
                    clickAction = true
                },
                text = stringResource(R.string.about_app),
                painter = R.drawable.github_mark,
                contentDescription = "About",
                colors = cardColors
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingBoardSetting(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val valueState: ValueState = viewModel()

    val state = rememberLazyListState()
    val appBarState = rememberTopAppBarState()
    val firstVisibleItemIndex = remember { derivedStateOf { state.firstVisibleItemIndex } }
    var scrolled by remember { mutableStateOf(true) }
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(appBarState)
    if (scrolled) {
        if (firstVisibleItemIndex.value > 0) scrolled = false
    } else if (firstVisibleItemIndex.value <= 1) scrolled = true
    val shadow = if (appBarState.heightOffsetLimit == appBarState.heightOffset) {
        modifier.shadow(1.dp)
    } else modifier

    Box {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = themeColor("settingBackgroundColor"),
                        scrolledContainerColor = themeColor("scrolledContainerColor"),
                        titleContentColor = themeColor("titleContentColor"),
                    ),
                    title = {
                        Text(
                            text = stringResource(R.string.settings),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold,
                        )
                    },
                    navigationIcon = {
                        var fixDoubleClickError by remember { mutableStateOf(true) }
                        IconButton(onClick = {
                            if (fixDoubleClickError) {
                                navController.popBackStack()
                                fixDoubleClickError = false
                            }
                        }) {
                            if (scrolled) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_arrow_back),
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    modifier = shadow
                )
            },
            contentWindowInsets = WindowInsets.statusBars
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(color = themeColor("settingBackgroundColor"))
            ) {
                SettingFunction(
                    state = state,
                    navController = navController
                )
            }
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                val bottomShadow = modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .shadow(10.dp)
                Spacer(modifier = bottomShadow)
            }
        }
        if (!scrolled) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .pointerInput(Unit){ detectVerticalDragGestures{ _, _ -> } },
                horizontalAlignment = Alignment.Start
            ) {
                var fixDoubleClickError by remember { mutableStateOf(true) }
                IconButton(
                    modifier = modifier.padding(top = 8.dp, start = 4.dp),
                    onClick = {
                        if (fixDoubleClickError) {
                            navController.popBackStack()
                            fixDoubleClickError = false
                        }
                    }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Back"
                    )
                }
            }
        } else { Column{ /* fix this function will lead crash in release apk */ } }
    }
    if (valueState.updateScreen) {
        navController.navigate("UpdateScreen")
        Handler(Looper.getMainLooper()).postDelayed(50) {
            navController.popBackStack()
            Log.i("WritingBoardTag", "Re-Opening WritingBoard Text")
            valueState.updateScreen = false
        }
    }
}

private suspend fun updateWidget(context: Context) {
    var text by mutableStateOf("")
    val savedText: String = context.dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(ContentValues.TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[stringPreferencesKey("saved_text")] ?: ""
        }.first()
    text = savedText
    context.dataStore.edit { preferences ->
        preferences[stringPreferencesKey("saved_text")] =
            savedText.plus("protected-data-success_PLEASE-report")
    }
    WritingBoardWidget().updateAll(context)
    delay(50)
    context.dataStore.edit { preferences ->
        preferences[stringPreferencesKey("saved_text")] = text
    }
    WritingBoardWidget().updateAll(context)
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
private fun WritingBoardSettingPreview() {
    val navController = rememberNavController()
    WritingBoardSetting(navController)
}