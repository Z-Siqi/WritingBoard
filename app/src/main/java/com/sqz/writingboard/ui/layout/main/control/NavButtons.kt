package com.sqz.writingboard.ui.layout.main.control

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.R
import com.sqz.writingboard.common.feedback.AndroidFeedback
import com.sqz.writingboard.common.feedback.Feedback
import com.sqz.writingboard.preference.SettingOption
import com.sqz.writingboard.ui.MainViewModel
import com.sqz.writingboard.ui.component.TextTooltipBox
import com.sqz.writingboard.ui.layout.LocalState
import com.sqz.writingboard.ui.layout.handler.RequestHandler
import com.sqz.writingboard.ui.theme.getLeftDp
import com.sqz.writingboard.ui.theme.getRightDp
import com.sqz.writingboard.ui.theme.navBarHeightDp
import com.sqz.writingboard.ui.theme.navBarHeightDpIsEditing
import com.sqz.writingboard.ui.theme.navBarHeightDpLandscape
import com.sqz.writingboard.ui.theme.pxToDp

class NavButtons(
    private val state: LocalState,
    private val requestHandler: RequestHandler,
    private val settings: SettingOption,
    private val feedback: Feedback,
) {
    @Composable
    fun NavBar(enable: Boolean, modifier: Modifier = Modifier) = if (!enable) Box {} else {
        val density = LocalDensity.current
        val navHeight = if (state.isImeOn) navBarHeightDpIsEditing.dp else {
            navBarHeightDp.dp + WindowInsets.navigationBars.getBottom(density).pxToDp()
        }
        val context = LocalContext.current
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.ime)
                .height(navHeight)
                .shadow(7.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            val windowInsetsModifier = Modifier.let {
                if (state.isImeOn) it else it.windowInsetsPadding(WindowInsets.navigationBars)
            }
            Row(
                modifier = windowInsetsModifier.pointerInput(Unit) { detectTapGestures { _ -> } },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!state.isFocus) {
                    if (settings.editButton()) Spacer(Modifier.width(16.dp))
                    SettingsButton { requestHandler.onSettingsClick(feedback) }
                    if (settings.editButton()) Spacer(Modifier.weight(1f))
                }
                if (state.isFocus || state.isInReadOnlyMode && state.isEditable) {
                    Spacer(Modifier.weight(1f))
                    OnEditTextButton { requestHandler.finishClick(context, feedback) }
                }
                if (settings.editButton() && !state.isEditable) {
                    EditButton { requestHandler.onEditClick(feedback) }
                }
            }
        }
    }

    @Composable
    fun NavRail(enable: Boolean, modifier: Modifier = Modifier) = if (!enable) Box {} else {
        val navWidth = navBarHeightDpLandscape.dp + WindowInsets.displayCutout.getRightDp().dp
        val context = LocalContext.current
        Surface(
            modifier = modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.ime)
                .width(navWidth)
                .shadow(7.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            val windowInsetsModifier = Modifier.let {
                val stateBar = it.windowInsetsPadding(WindowInsets.statusBars)
                val navBar = if (state.isImeOn) stateBar else {
                    stateBar.windowInsetsPadding(WindowInsets.navigationBars)
                }
                if (WindowInsets.displayCutout.getLeftDp() > 1) navBar else {
                    navBar.windowInsetsPadding(WindowInsets.displayCutout)
                }
            }
            Column(
                modifier = windowInsetsModifier.pointerInput(Unit) { detectTapGestures { _ -> } },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!state.isFocus) {
                    if (settings.editButton()) Spacer(Modifier.width(16.dp))
                    SettingsButton { requestHandler.onSettingsClick(feedback) }
                    if (settings.editButton()) Spacer(Modifier.weight(1f))
                }
                if (state.isFocus) {
                    Spacer(Modifier.weight(1f))
                    OnEditTextButton { requestHandler.finishClick(context, feedback) }
                }
                if (settings.editButton() && !state.isEditable) {
                    EditButton { requestHandler.onEditClick(feedback) }
                }
            }
        }
    }
}

@Composable
private fun SettingsButton(onClick: () -> Unit) {
    TextTooltipBox(tooltipText = stringResource(R.string.settings)) {
        OutlinedButton(
            modifier = Modifier.padding(10.dp),
            onClick = onClick,
            shape = RoundedCornerShape(5.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.settings)
            )
        }
    }
}

@Composable
private fun OnEditTextButton(onClick: () -> Unit) {
    TextTooltipBox(tooltipText = stringResource(id = R.string.done)) {
        OutlinedButton(
            modifier = Modifier.padding(10.dp),
            onClick = onClick,
            shape = RoundedCornerShape(5.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = stringResource(R.string.done)
            )
        }
    }
}

@Composable
private fun EditButton(onClick: () -> Unit) {
    TextTooltipBox(tooltipText = stringResource(R.string.edit)) {
        OutlinedButton(
            modifier = Modifier.padding(10.dp),
            onClick = onClick,
            shape = RoundedCornerShape(5.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = stringResource(R.string.edit)
            )
        }
    }
}

@Composable
fun NavBarButton(
    state: LocalState,
    requestHandler: RequestHandler,
    settings: SettingOption,
    feedback: Feedback,
    modifier: Modifier = Modifier
) = NavButtons(state, requestHandler, settings, feedback).NavBar(true, modifier)

@Preview
@Composable
private fun NavBarButtonPreview() {
    val settings = SettingOption(LocalContext.current)
    NavBarButton(
        LocalState(), RequestHandler(viewModel { MainViewModel() }),
        settings, AndroidFeedback(settings, LocalView.current)
    )
}

@Composable
fun NavRailButton(
    state: LocalState,
    requestHandler: RequestHandler,
    settings: SettingOption,
    feedback: Feedback,
    modifier: Modifier = Modifier
) = NavButtons(state, requestHandler, settings, feedback).NavRail(true, modifier)

@Preview
@Composable
private fun NavRailButtonPreview() {
    val settings = SettingOption(LocalContext.current)
    NavRailButton(
        LocalState(), RequestHandler(viewModel { MainViewModel() }),
        settings, AndroidFeedback(settings, LocalView.current)
    )
}
