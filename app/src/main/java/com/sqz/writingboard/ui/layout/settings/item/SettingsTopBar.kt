package com.sqz.writingboard.ui.layout.settings.item

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sqz.writingboard.R
import com.sqz.writingboard.ui.layout.handler.NavControllerHandler
import com.sqz.writingboard.ui.theme.WritingBoardTheme
import com.sqz.writingboard.ui.theme.isAndroid15OrAbove
import com.sqz.writingboard.ui.theme.isLandscape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(
    navControllerHandler: NavControllerHandler,
    state: LazyListState,
    nestedScrollConnection: (NestedScrollConnection) -> Unit,
    modifier: Modifier = Modifier
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
    val firstVisibleItemIndex = remember { derivedStateOf { state.firstVisibleItemIndex } }
    var scrolled by remember { mutableStateOf(true) }
    if (scrolled) {
        if (firstVisibleItemIndex.value > 0) scrolled = false
    } else {
        if (firstVisibleItemIndex.value <= 1) scrolled = true
    }
    LargeTopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = WritingBoardTheme.color.settingsBackground,
            scrolledContainerColor = WritingBoardTheme.color.settingsBgTopBarScrolled,
            titleContentColor = WritingBoardTheme.color.settingsTopBarContent
        ),
        title = {
            Text(
                text = stringResource(R.string.settings),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.displayCutoutPadding(),
            )
        },
        navigationIcon = {
            var onClick by remember { mutableStateOf(false) }
            IconButton(modifier = Modifier.displayCutoutPadding(), onClick = {
                if (!onClick) navControllerHandler.requestBack().also { onClick = true }
            }) {
                if (scrolled) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier.shadow(topAppBarState.heightOffsetLimit == topAppBarState.heightOffset)
    )
    if (!scrolled) Column(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .displayCutoutPadding(true)
            .height(65.dp)
            .pointerInput(Unit) { detectVerticalDragGestures { _, _ -> } },
        horizontalAlignment = Alignment.Start
    ) {
        var onClick by remember { mutableStateOf(false) }
        IconButton(
            modifier = modifier
                .padding(top = 8.dp, start = 4.dp),
            onClick = {
                if (!onClick) navControllerHandler.requestBack().also { onClick = true }
            }) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.back)
            )
        }
    }
    nestedScrollConnection(scrollBehavior.nestedScrollConnection)
}

private fun Modifier.shadow(show: Boolean): Modifier {
    if (show) return this.shadow(1.dp)
    return this
}

@Composable
private fun Modifier.displayCutoutPadding(landscapeOnly: Boolean = false): Modifier {
    if (isAndroid15OrAbove && (!landscapeOnly || isLandscape)) {
        return this.windowInsetsPadding(WindowInsets.displayCutout)
    }
    return this
}
