package com.sqz.writingboard.ui.layout.settings.item

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.glance.appwidget.updateAll
import com.sqz.writingboard.R
import com.sqz.writingboard.file.rememberImportFontManager
import com.sqz.writingboard.glance.WritingBoardWidget
import com.sqz.writingboard.preference.SettingOption
import com.sqz.writingboard.ui.MainViewModel
import com.sqz.writingboard.ui.theme.WritingBoardTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsItem(private val settings: SettingOption) : ListItem() {

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateWidget(context: Context) {
        GlobalScope.launch { WritingBoardWidget().updateAll(context) }
    }

    @Composable
    private fun Theme() {
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
    private fun ControlStyle(viewModel: MainViewModel) {
        var controlStyle by remember { mutableIntStateOf(settings.buttonStyle()) }
        super.SegmentedButtonWithSwitch(
            title = stringResource(R.string.button_style), option = listOf(
                stringResource(R.string.button_hide),
                stringResource(R.string.default_string),
                stringResource(R.string.button_bottom_bar)
            ), onChange = {
                if (it == 2) viewModel.boardSizeHandler.resetPadding()
                settings.buttonStyle(it).also { controlStyle = it }
            }, switch = settings.buttonStyle() != 2,
            switchText = stringResource(R.string.always_visible_text),
            onCheckedChange = {
                viewModel.boardSizeHandler.resetPadding()
                settings.alwaysVisibleText(it)
            }
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

    @Composable
    private fun FontStyle(context: Context) {
        val importFontManager = rememberImportFontManager()
        super.SegmentedButtonWithSegmentedButtonAndCard(
            title = stringResource(R.string.font_style), defOption = listOf(
                stringResource(R.string.monospace),
                stringResource(R.string.default_string),
                stringResource(R.string.serif),
                stringResource(R.string.more_font)
            ), onDefChange = { settings.fontStyle(it).also { updateWidget(context) } },
            showAll = settings.fontStyle() == 3, subOption = listOf(
                stringResource(R.string.cursive),
                stringResource(R.string.custom_font)
            ), onSubChange = { settings.fontStyleExtra(it).also { updateWidget(context) } },
            onSubCardClick = { importFontManager.importFont() },
            enableSubCardClick = settings.fontStyleExtra() == 1,
            onSubCardText = if (settings.fontStyleExtra() == 0) {
                stringResource(R.string.may_only_english)
            } else importFontManager.getState(context)
        )
    }

    @Composable
    private fun FontWeight() {
        super.BasicSegmentedButtonLayout(
            title = stringResource(R.string.font_weight), option = listOf(
                stringResource(R.string.thin),
                stringResource(R.string.normal),
                stringResource(R.string.thick),
            ), onChange = { settings.fontWeight(it) }
        )
    }

    fun list(viewModel: MainViewModel, context: Context): List<Item> {
        return listOf(
            Item { super.ListTitle(R.string.writingboard_app) },
            Item { this.Theme() },
            Item { this.ControlStyle(viewModel) },
            Item { this.ReadOnlyMode(viewModel) },
            Item { super.ListTitle(R.string.writingboard) },
            Item { this.FontSize() },
            Item { this.ItalicFont() },
            Item { this.FontStyle(context) },
            Item { this.FontWeight() },
            //Item { super.ListTitle(R.string.keyboard_texts_action) }, TODO: write new
            //Item { super.ListTitle(R.string.others) },
        )
    }
}
