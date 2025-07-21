package com.sqz.writingboard.ui.layout.settings

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sqz.writingboard.preference.SettingOption
import com.sqz.writingboard.ui.MainViewModel
import com.sqz.writingboard.ui.component.drawVerticalScrollbar
import com.sqz.writingboard.ui.layout.settings.item.SettingsItem
import com.sqz.writingboard.ui.layout.settings.item.SettingsTopBar
import com.sqz.writingboard.ui.theme.isAndroid15OrAbove
import com.sqz.writingboard.ui.theme.isLandscape

@Composable
fun SettingsLayout(viewModel: MainViewModel, context: Context) {
    val settings = SettingOption(context = context)
    val state = rememberLazyListState()
    var nestedScrollConnection by remember { mutableStateOf<NestedScrollConnection?>(null) }
    Scaffold(
        modifier = nestedScrollConnection?.let { Modifier.nestedScroll(it) } ?: Modifier,
        topBar = {
            SettingsTopBar(
                navControllerHandler = viewModel.navControllerHandler,
                state = state,
                nestedScrollConnection = { nestedScrollConnection = it }
            )
        },
        contentWindowInsets = WindowInsets.statusBars,
    ) { paddingValues ->
        val settingsItem = SettingsItem(settings = settings)
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .drawVerticalScrollbar(state)
                .displayCutoutPadding(),
            state = state
        ) {
            item { Spacer(modifier = Modifier.height(1.dp)) }
            item { Spacer(modifier = Modifier.height(5.dp)) }
            items(settingsItem.list(viewModel, context)) {
                it.content()
            }
            item { Spacer(modifier = Modifier.navBarHeight()) }
        }
    }
}

@Composable
private fun Modifier.navBarHeight(): Modifier {
    return this.height((WindowInsets.navigationBars.getBottom(LocalDensity.current) / LocalDensity.current.density).dp)
}

@Composable
private fun Modifier.displayCutoutPadding(): Modifier {
    if (isAndroid15OrAbove && isLandscape) return this.windowInsetsPadding(WindowInsets.displayCutout)
    return this
}
