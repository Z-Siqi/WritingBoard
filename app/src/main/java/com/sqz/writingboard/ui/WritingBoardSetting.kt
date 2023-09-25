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
import androidx.compose.foundation.lazy.items
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

val setting = WritingBoardSettingState()

@Composable
fun SettingFunction(modifier: Modifier = Modifier, context: Context) {
    val valueState: ValueState = viewModel()
    val list = listOf("0") + ((1..100).map { it.toString() })

    var allowMultipleLines by setting.rememberSwitchState("allow_multiple_lines", context)
    var cleanPointerFocus by setting.rememberSwitchState("clean_pointer_focus", context)
    var cleanAllText by setting.rememberSwitchState("clean_all_text", context)
    var editButton by setting.rememberSwitchState("edit_button", context)
    var theme by setting.rememberSegmentedButtonState("theme", context)
    var fontSize by setting.rememberSegmentedButtonState("font_size", context)
    var italics by setting.rememberSwitchState("italics", context)
    var fontStyle by setting.rememberSegmentedButtonState("font_style", context)

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(items = list, itemContent = { item ->
            when (item) {
                "0" -> {
                    Text(
                        text = "WritingBoard App",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = modifier.padding(top = 32.dp, start = 18.dp)
                    )
                }
                "1" -> {
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

                "2" -> {
                    Text(
                        text = "The WritingBoard",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = modifier.padding(top = 16.dp, start = 18.dp)
                    )
                }

                "3" -> {
                    CardLayout(
                        text = stringResource(R.string.edit_writingboard_button),
                        checked = editButton,
                        onCheckedChange = {
                            editButton = it
                            setting.writeSwitchState("edit_button", context, it)
                        }
                    )
                }

                "4" -> {
                    CardLayout(
                        text = stringResource(R.string.clean_all_texts_button),
                        checked = cleanAllText,
                        onCheckedChange = {
                            cleanAllText = it
                            setting.writeSwitchState("clean_all_text", context, it)
                        }
                    )
                }

                "5" -> {
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

                "6" -> {
                    CardLayout(
                        text = stringResource(R.string.font_italics),
                        checked = italics,
                        onCheckedChange = {
                            italics = it
                            setting.writeSwitchState("italics", context, it)
                        }
                    )
                }

                "7" -> {
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

                "8" -> {
                    Text(
                        text = "Keyboard & Texts Action",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = modifier.padding(top = 16.dp, start = 18.dp)
                    )
                }

                "9" -> {
                    CardLayout(
                        text = stringResource(R.string.clean_pointer_focus),
                        checked = cleanPointerFocus,
                        onCheckedChange = {
                            cleanPointerFocus = it
                            setting.writeSwitchState("clean_pointer_focus", context, it)
                        }
                    )
                }

                "10" -> {
                    CardLayout(
                        text = stringResource(R.string.allow_multiple_lines),
                        checked = allowMultipleLines,
                        onCheckedChange = {
                            allowMultipleLines = it
                            setting.writeSwitchState("allow_multiple_lines", context, it)
                        }
                    )
                }

                "11" -> {
                    Text(
                        text = "Others",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = modifier.padding(top = 16.dp, start = 18.dp)
                    )
                }

                "12" -> {
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
            }
        })
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

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = when (settingState.readSegmentedButtonState(
                        "theme",
                        context
                    )) {
                        0 -> MaterialTheme.colorScheme.surfaceBright
                        1 -> MaterialTheme.colorScheme.surfaceVariant
                        2 -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    scrolledContainerColor = when (settingState.readSegmentedButtonState(
                        "theme",
                        context
                    )) {
                        0 -> MaterialTheme.colorScheme.surfaceDim
                        1 -> MaterialTheme.colorScheme.secondaryContainer
                        2 -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    },
                    titleContentColor = when (settingState.readSegmentedButtonState(
                        "theme",
                        context
                    )) {
                        0 -> MaterialTheme.colorScheme.secondary
                        1 -> MaterialTheme.colorScheme.primary
                        2 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    },
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
                .background(
                    color = when (settingState.readSegmentedButtonState("theme", context)) {
                        0 -> MaterialTheme.colorScheme.surfaceBright
                        1 -> MaterialTheme.colorScheme.surfaceVariant
                        2 -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                )
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