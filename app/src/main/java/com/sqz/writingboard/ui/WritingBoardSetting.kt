package com.sqz.writingboard.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sqz.writingboard.R
import com.sqz.writingboard.WritingBoardSettingState

val setting = WritingBoardSettingState()

@Composable
fun SettingFunction(modifier: Modifier = Modifier, context: Context) {
    val list = listOf("1") + ((2..100).map { it.toString() })

    var allowMultipleLines by remember {
        mutableStateOf(
            setting.readSwitchState(
                "allow_multiple_lines",
                context
            )
        )
    }
    var cleanAllText by remember {
        mutableStateOf(
            setting.readSwitchState(
                "clean_all_text",
                context
            )
        )
    }
    var editButton by remember {
        mutableStateOf(
            setting.readSwitchState(
                "edit_button",
                context
            )
        )
    }
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(items = list, itemContent = { item ->
            when (item) {
                "1" -> {
                    CardLayout(
                        text = stringResource(R.string.allow_multiple_lines),
                        checked = allowMultipleLines,
                        onCheckedChange = {
                            allowMultipleLines = it
                            setting.writeSwitchState("allow_multiple_lines", context, it)
                        }
                    )
                }

                "2" -> {
                    CardLayout(
                        text = stringResource(R.string.clean_all_texts_button),
                        checked = cleanAllText,
                        onCheckedChange = {
                            cleanAllText = it
                            setting.writeSwitchState("clean_all_text", context, it)
                        }
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
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(80.dp)
                            .clickable {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intent.data = Uri.fromParts("package", context.packageName, null)
                                startActivityForResult(context as Activity, intent, 0, null)
                            }
                    ) {
                        Box(
                            modifier = modifier.fillMaxSize()
                        ) {
                            Text(
                                text = stringResource(R.string.language),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = modifier
                                    .padding(16.dp)
                                    .wrapContentHeight(Alignment.CenterVertically),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
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
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            SettingFunction(context = context)
        }
    }
}

@Composable
fun CardLayout(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(80.dp)
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = modifier
                    .padding(10.dp)
                    .size(width = 280.dp, height = 60.dp)
            )
            Switch(
                modifier = modifier
                    .fillMaxSize()
                    .wrapContentWidth(Alignment.End)
                    .padding(16.dp),
                checked = checked,
                onCheckedChange = onCheckedChange
            )
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