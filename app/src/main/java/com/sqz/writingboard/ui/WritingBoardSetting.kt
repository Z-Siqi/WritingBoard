package com.sqz.writingboard.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.os.postDelayed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sqz.writingboard.R
import com.sqz.writingboard.ValueState
import com.sqz.writingboard.WritingBoardSettingState
import com.sqz.writingboard.settingState
import com.sqz.writingboard.ui.component.setting.ClickCardLayout
import com.sqz.writingboard.ui.component.setting.SegmentedButtonCardLayout
import com.sqz.writingboard.ui.component.drawVerticalScrollbar
import com.sqz.writingboard.ui.component.setting.ExtraButtonCardLayout
import com.sqz.writingboard.ui.component.setting.SwitchCardLayout
import com.sqz.writingboard.ui.theme.themeColor

private val setting = WritingBoardSettingState()

@Composable
private fun SettingFunction(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val valueState: ValueState = viewModel()
    val state = rememberLazyListState()

    val cardColors = CardDefaults.cardColors(containerColor = themeColor("cardColor"))

    var allowMultipleLines by setting.rememberSwitchState("allow_multiple_lines", context)
    var cleanPointerFocus by setting.rememberSwitchState("clean_pointer_focus", context)
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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .drawVerticalScrollbar(state),
        state = state
    ) {
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
                options = listOf(R.string.light_color, R.string.theme_default, R.string.distinct),
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
            }
        }
        item {
            ExtraButtonCardLayout(
                title = stringResource(R.string.button_style),
                options = listOf(
                    R.string.button_hide,
                    R.string.button_default,
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
                },
            ) { index ->
                buttonStyle = index
                setting.writeSegmentedButtonState(
                    "button_style",
                    context,
                    index
                )
            }
        }
        item {
            SwitchCardLayout(
                text = stringResource(R.string.edit_writingboard_button),
                checked = editButton,
                onCheckedChange = {
                    editButton = it
                    setting.writeSwitchState("edit_button", context, it)
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
            }
        }
        item {
            SwitchCardLayout(
                text = stringResource(R.string.font_italics),
                checked = italics,
                onCheckedChange = {
                    italics = it
                    setting.writeSwitchState("italics", context, it)
                },
                colors = cardColors
            )
        }
        item {
            SegmentedButtonCardLayout(
                title = stringResource(R.string.font_style),
                options = listOf(
                    R.string.monospace,
                    R.string.font_default,
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
                text = stringResource(R.string.clean_pointer_focus),
                checked = cleanPointerFocus,
                onCheckedChange = {
                    cleanPointerFocus = it
                    setting.writeSwitchState("clean_pointer_focus", context, it)
                },
                colors = cardColors
            )
        }
        item {
            SwitchCardLayout(
                text = stringResource(R.string.allow_multiple_lines),
                checked = allowMultipleLines,
                onCheckedChange = {
                    allowMultipleLines = it
                    setting.writeSwitchState("allow_multiple_lines", context, it)
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
            ClickCardLayout(
                intent = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package", context.packageName, null)
                        startActivityForResult(context as Activity, intent, 0, null)
                    } else {
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
            ClickCardLayout(
                intent = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/Z-Siqi/WritingBoard/")
                    )
                    startActivityForResult(context as Activity, intent, 0, null)
                },
                text = stringResource(R.string.about),
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
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
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
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = themeColor("settingBackgroundColor"))
        ) {
            SettingFunction(navController)
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Bottom
        ) {
            val bottomShadow = modifier
                .fillMaxWidth()
                .height(2.dp)
                .shadow(10.dp)
            Spacer(modifier = bottomShadow)
        }
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

@Preview
@Composable
private fun WritingBoardSettingPreview() {
    val navController = rememberNavController()
    WritingBoardSetting(navController)
}