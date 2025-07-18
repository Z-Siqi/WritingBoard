package com.sqz.writingboard.ui.layout.settings.item

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.sqz.writingboard.R
import com.sqz.writingboard.preference.SettingOption
import com.sqz.writingboard.ui.MainViewModel
import com.sqz.writingboard.ui.theme.WritingBoardTheme
import kotlinx.coroutines.flow.update

class SettingsItem(private val settings: SettingOption) : ListItem() {

    @Composable
    private fun Int.string(): String = stringResource(this)

    @Composable
    private fun Theme(viewModel: MainViewModel) {
        super.BasicSegmentedButtonLayout(
            title = stringResource(R.string.choose_theme),
            option = listOf(
                stringResource(R.string.light_color),
                stringResource(R.string.default_string),
                stringResource(R.string.distinct)
            ),
            onChange = { WritingBoardTheme.updateState(settings.theme(it)) }
        )
    }

    @Composable
    private fun ControlStyle() {
        var controlStyle by remember { mutableIntStateOf(settings.buttonStyle()) }
        super.SegmentedButtonWithSwitch(
            title = stringResource(R.string.button_style), option = listOf(
                stringResource(R.string.button_hide),
                stringResource(R.string.default_string),
                stringResource(R.string.button_bottom_bar)
            ), onChange = { settings.buttonStyle(it).also { controlStyle = it } },
            switch = settings.buttonStyle() != 2,
            switchText = stringResource(R.string.always_visible_text),
            onCheckedChange = { settings.alwaysVisibleText(it) }
        )
    }

    @Composable
    private fun ReadOnlyMode(viewModel: MainViewModel) {
        var state by remember { mutableStateOf(settings.editButton()) }
        super.SwitchCardLayout(
            text = stringResource(R.string.edit_writingboard_button),
            checked = state,
            onCheckedChange = { setter ->
                state = settings.editButton(setter)
                viewModel.state.update { it.copy(isInReadOnlyMode = setter, isEditable = !setter) }
            }
        )
    }

    @Composable
    private fun FontSize() {
        super.BasicSegmentedButtonLayout(
            title = stringResource(R.string.font_size), option = listOf(
                stringResource(R.string.small),
                stringResource(R.string.medium),
                stringResource(R.string.large)
            ), onChange = { settings.fontSize(it) }
        )
    }

    @Composable
    private fun ItalicFont() {
        var state by remember { mutableStateOf(settings.italics()) }
        super.SwitchCardLayout(
            text = stringResource(R.string.font_italics),
            checked = state,
            onCheckedChange = { state = settings.italics(it) }
        )
    }

    fun list(viewModel: MainViewModel): List<Item> {
        return listOf(
            Item { super.ListTitle(R.string.writingboard_app) },
            Item { this.Theme(viewModel) },
            Item { this.ControlStyle() },
            Item { this.ReadOnlyMode(viewModel) },
            Item { super.ListTitle(R.string.writingboard) },
            Item { this.FontSize() },
            Item { this.ItalicFont() },
        )
    }
}
