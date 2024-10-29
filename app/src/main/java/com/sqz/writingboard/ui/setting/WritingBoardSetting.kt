package com.sqz.writingboard.ui.setting

import android.content.Context
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sqz.writingboard.R
import com.sqz.writingboard.component.Feedback
import com.sqz.writingboard.file.ImportDialog
import com.sqz.writingboard.file.ShareDialog
import com.sqz.writingboard.ui.WritingBoardViewModel
import com.sqz.writingboard.ui.theme.ThemeColor
import com.sqz.writingboard.ui.theme.isAndroid15OrAbove
import com.sqz.writingboard.ui.theme.themeColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingBoardSetting(
    navController: NavController,
    context: Context,
    view: View,
    modifier: Modifier = Modifier,
    viewModel: WritingBoardViewModel = viewModel()
) {
    val feedback = Feedback(view)
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
                        containerColor = themeColor(ThemeColor.SettingBackgroundColor),
                        scrolledContainerColor = themeColor(ThemeColor.ScrolledContainerColor),
                        titleContentColor = themeColor(ThemeColor.TitleContentColor),
                    ),
                    title = {
                        Text(
                            text = stringResource(R.string.settings),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold,
                            modifier = if (isAndroid15OrAbove) modifier.windowInsetsPadding(
                                WindowInsets.displayCutout
                            ) else modifier
                        )
                    },
                    navigationIcon = {
                        var fixDoubleClickError by remember { mutableStateOf(true) }
                        IconButton(
                            onClick = {
                                if (fixDoubleClickError) {
                                    navController.popBackStack()
                                    fixDoubleClickError = false
                                }
                            },
                            modifier = if (isAndroid15OrAbove) modifier.windowInsetsPadding(
                                WindowInsets.displayCutout
                            ) else modifier
                        ) {
                            if (scrolled) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_arrow_back),
                                    contentDescription = stringResource(R.string.back)
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
            val coroutineScope = rememberCoroutineScope()
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(color = themeColor(ThemeColor.SettingBackgroundColor))
            ) {
                var exportDialog by rememberSaveable { mutableStateOf(false) }
                var importDialog by rememberSaveable { mutableStateOf(false) }
                SettingFunctionList(
                    state = state,
                    navController = navController,
                    context = context,
                    view = view,
                    dialogType = {
                        when (it) {
                            DialogType.Export -> exportDialog = true
                            DialogType.Import -> importDialog = true
                        }
                    },
                    modifier = if (isAndroid15OrAbove) modifier.windowInsetsPadding(WindowInsets.displayCutout) else modifier
                )
                if (exportDialog) {
                    feedback.createClickSound()
                    ShareDialog(
                        onDismissRequest = { exportDialog = false },
                        shareText = viewModel.textFieldState.text.toString(),
                        context = context,
                        feedback = feedback
                    )
                }
                if (importDialog) {
                    feedback.createClickSound()
                    ImportDialog(
                        onDismissRequest = { importDialog = false },
                        onConfirm = {
                            coroutineScope.launch {
                                viewModel.textFieldState.clearText()
                                viewModel.textFieldState.edit { insert(0, it) }
                                viewModel.saveText(
                                    viewModel.textFieldState.text.toString(),
                                    context
                                )
                            }
                        },
                        context = context,
                        feedback = feedback
                    )
                }
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
                    .pointerInput(Unit) { detectVerticalDragGestures { _, _ -> } },
                horizontalAlignment = Alignment.Start
            ) {
                var fixDoubleClickError by remember { mutableStateOf(true) }
                IconButton(
                    modifier = modifier.padding(
                        top = 8.dp,
                        start = 4.dp
                    ) then if (isAndroid15OrAbove) modifier.windowInsetsPadding(WindowInsets.displayCutout) else modifier,
                    onClick = {
                        if (fixDoubleClickError) {
                            navController.popBackStack()
                            fixDoubleClickError = false
                        }
                    }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        } else Spacer(modifier = modifier)
    }
}

enum class DialogType {
    Export, Import
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
private fun WritingBoardSettingPreview() {
    val navController = rememberNavController()
    WritingBoardSetting(navController, LocalContext.current, LocalView.current)
}
