package com.sqz.writingboard.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
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

val setting = WritingBoardSettingState()

@Composable
fun SettingFunction(modifier: Modifier = Modifier, context: Context) {
    val valueState: ValueState = viewModel()

    var allowMultipleLines by setting.rememberSwitchState("allow_multiple_lines", context)
    var cleanPointerFocus by setting.rememberSwitchState("clean_pointer_focus", context)
    var cleanAllText by setting.rememberSwitchState("clean_all_text", context)
    var editButton by setting.rememberSwitchState("edit_button", context)
    var theme by setting.rememberSegmentedButtonState("theme", context)
    var fontSize by setting.rememberSegmentedButtonState("font_size", context)
    var italics by setting.rememberSwitchState("italics", context)
    var fontStyle by setting.rememberSegmentedButtonState("font_style", context)
    var buttonStyle by setting.rememberSegmentedButtonState("button_style", context)

    LazyColumn(
        modifier = modifier.fillMaxSize()
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
                selectedOption = theme
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
            SegmentedButtonCardLayout(
                title = stringResource(R.string.button_style),
                options = listOf(R.string.button_hide, R.string.button_default),
                selectedOption = buttonStyle
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
            Text(
                text = stringResource(R.string.writingboard),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = modifier.padding(top = 16.dp, start = 18.dp)
            )
        }
        item {
            CardLayout(
                text = stringResource(R.string.edit_writingboard_button),
                checked = editButton,
                onCheckedChange = {
                    editButton = it
                    setting.writeSwitchState("edit_button", context, it)
                }
            )
        }
        item {
            CardLayout(
                text = stringResource(R.string.clean_all_texts_button),
                checked = cleanAllText,
                onCheckedChange = {
                    cleanAllText = it
                    setting.writeSwitchState("clean_all_text", context, it)
                }
            )
        }
        item {
            SegmentedButtonCardLayout(
                title = stringResource(R.string.choose_font_size),
                options = listOf(R.string.small, R.string.medium, R.string.large),
                selectedOption = fontSize
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
            CardLayout(
                text = stringResource(R.string.font_italics),
                checked = italics,
                onCheckedChange = {
                    italics = it
                    setting.writeSwitchState("italics", context, it)
                }
            )
        }
        item {
            SegmentedButtonCardLayout(
                title = stringResource(R.string.choose_font_size),
                options = listOf(
                    R.string.monospace,
                    R.string.font_default,
                    R.string.serif,
                    R.string.cursive,
                ),
                selectedOption = fontStyle
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
            Text(
                text = stringResource(R.string.keyboard_texts_action),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = modifier.padding(top = 16.dp, start = 18.dp)
            )
        }
        item {
            CardLayout(
                text = stringResource(R.string.clean_pointer_focus),
                checked = cleanPointerFocus,
                onCheckedChange = {
                    cleanPointerFocus = it
                    setting.writeSwitchState("clean_pointer_focus", context, it)
                }
            )
        }
        item {
            CardLayout(
                text = stringResource(R.string.allow_multiple_lines),
                checked = allowMultipleLines,
                onCheckedChange = {
                    allowMultipleLines = it
                    setting.writeSwitchState("allow_multiple_lines", context, it)
                }
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
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    startActivityForResult(context as Activity, intent, 0, null)
                },
                text = stringResource(R.string.language),
                painter = R.drawable.ic_language,
                contentDescription = "Language"
            )
        }
        item {
            ClickCardLayout(
                intent = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Z-Siqi/WritingBoard/"))
                    startActivityForResult(context as Activity, intent, 0, null)
                },
                text = stringResource(R.string.about),
                painter = R.drawable.github_mark,
                contentDescription = "About"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingBoardSetting(
    navController: NavController,
    modifier: Modifier = Modifier,
    context: Context
) {
    val valueState: ValueState = viewModel()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val themeColorSetting = settingState.readSegmentedButtonState("theme", context)
    val scrolledContainerColor = when (themeColorSetting) {
        0 -> MaterialTheme.colorScheme.surfaceDim
        1 -> MaterialTheme.colorScheme.secondaryContainer
        2 -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
    val titleContentColor = when (themeColorSetting) {
        0 -> MaterialTheme.colorScheme.secondary
        1 -> MaterialTheme.colorScheme.primary
        2 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
    val backgroundColor = when (themeColorSetting) {
        0 -> MaterialTheme.colorScheme.surfaceBright
        1 -> MaterialTheme.colorScheme.surfaceVariant
        2 -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = backgroundColor,
                    scrolledContainerColor = scrolledContainerColor,
                    titleContentColor = titleContentColor,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = backgroundColor)
        ) {
            SettingFunction(context = context)
        }
    }
    if (valueState.updateScreen) {
        navController.navigate("WritingBoardNone")
        Handler(Looper.getMainLooper()).postDelayed(50) {
            navController.popBackStack()
            Log.i("WritingBoardTag", "Re-Opening WritingBoard Text")
            valueState.updateScreen = false
        }
    }
}

@Preview
@Composable
fun WritingBoardSettingPreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    WritingBoardSetting(navController, context = context)
}