package com.sqz.writingboard.ui.layout.settings.item

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.sqz.writingboard.R
import com.sqz.writingboard.common.feedback.Feedback
import com.sqz.writingboard.common.io.rememberImportFontManager
import com.sqz.writingboard.glance.GlanceWidgetManager
import com.sqz.writingboard.glance.receiver.WritingBoardTextOnlyWidgetReceiver
import com.sqz.writingboard.glance.receiver.WritingBoardWidgetReceiver
import com.sqz.writingboard.preference.SettingOption
import com.sqz.writingboard.tile.QSTileRequestHelper
import com.sqz.writingboard.ui.MainViewModel
import com.sqz.writingboard.ui.theme.WritingBoardTheme
import com.sqz.writingboard.ui.theme.isAndroid13OrAbove
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsItem(
    private val settings: SettingOption,
    private val feedback: Feedback
) : ListItem(feedback) {

    @Composable
    private fun ExportText(viewModel: MainViewModel, context: Context) {
        var enableDialog by rememberSaveable { mutableStateOf(false) }
        val title = stringResource(R.string.export_writingboard_text)
        super.ClickableCard(
            title = title, icon = { Icon(painterResource(R.drawable.output), title) },
            onClick = { enableDialog = true }
        )
        if (enableDialog) ImportExportDialog(feedback).ExportDialog(
            outputText = viewModel.textFieldState().text.toString(),
            onDismissRequest = { enableDialog = false },
            context = context
        )
    }

    @Composable
    private fun ImportText(viewModel: MainViewModel, context: Context) {
        var enableDialog by rememberSaveable { mutableStateOf(false) }
        val title = stringResource(R.string.import_text_to_writingboard)
        super.ClickableCard(
            title = title, icon = { Icon(painterResource(R.drawable.input), title) },
            onClick = { enableDialog = true }
        )
        if (enableDialog) ImportExportDialog(feedback).ImportDialog(
            onDismiss = { enableDialog = false }, onConfirm = {
                viewModel.textFieldState().edit {
                    delete(0, length)
                    insert(0, it)
                }
                viewModel.saveTextToStorage(context)
                enableDialog = false
            }, context = context
        )
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
            ), onDefChange = {
                settings.fontStyle(it).also { GlanceWidgetManager.updateWidget(context) }
            }, showAll = settings.fontStyle() == 3, subOption = listOf(
                stringResource(R.string.cursive),
                stringResource(R.string.custom_font)
            ), onSubChange = {
                settings.fontStyleExtra(it).also { GlanceWidgetManager.updateWidget(context) }
            }, onSubCardClick = { importFontManager.importFont() },
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

    @Composable
    private fun MergeLineBreak() {
        var state by remember { mutableStateOf(settings.mergeLineBreak()) }
        super.SwitchCardLayout(
            text = stringResource(R.string.merge_line_break),
            checked = state,
            onCheckedChange = { state = settings.mergeLineBreak(it) }
        )
    }

    @Composable
    private fun InstantSaveText() {
        var state by remember { mutableStateOf(settings.instantSaveText()) }
        super.SwitchCardLayout(
            text = stringResource(R.string.instant_save_text),
            checked = state,
            onCheckedChange = { state = settings.instantSaveText(it) }
        )
    }

    @Composable
    private fun Vibration() {
        super.BasicSegmentedButtonLayout(
            title = stringResource(R.string.vibrate_settings), option = listOf(
                stringResource(R.string.less),
                stringResource(R.string.default_string),
                stringResource(R.string.more),
            ), onChange = { settings.vibrate(it) }
        )
    }

    @Composable
    private fun RequestAddQSTile(context: Context) {
        super.ClickableCard(
            title = stringResource(R.string.Add_QS_Tile), icon = {
                Icon(
                    painter = painterResource(R.drawable.writingboard_logo),
                    contentDescription = stringResource(R.string.Add_QS_Tile)
                )
            }, onClick = {
                feedback.onHeavyClickEffect()
                QSTileRequestHelper.requestTileAdd(context)
            }
        )
        val callbackState = QSTileRequestHelper.callbackState.collectAsState().value
        if (callbackState != null) {
            QSTileRequestHelper(callbackState).makeToast(context)
        }
    }

    @Composable
    private fun RequestAddWidget(context: Context) {
        val coroutineScope = rememberCoroutineScope()
        super.DoubleButtonCard(
            title = stringResource(R.string.request_widget),
            onClickStart = {
                coroutineScope.launch {
                    feedback.onHeavyClickEffect()
                    GlanceAppWidgetManager(context).requestPinGlanceAppWidget(
                        receiver = WritingBoardTextOnlyWidgetReceiver::class.java
                    )
                }
            },
            startText = stringResource(R.string.text_only),
            onClickEnd = {
                coroutineScope.launch {
                    feedback.onHeavyClickEffect()
                    GlanceAppWidgetManager(context).requestPinGlanceAppWidget(
                        receiver = WritingBoardWidgetReceiver::class.java
                    )
                }
            },
            endText = stringResource(R.string.default_string),
        )
    }

    @Composable
    private fun Language(context: Context) {
        val title =
            if (isAndroid13OrAbove) R.string.language_support else R.string.language_unsupported
        super.ClickableCard(
            title = stringResource(title), icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_language),
                    contentDescription = stringResource(R.string.language)
                )
                val fontSize = 12.sp / context.resources.configuration.fontScale
                Text(
                    text = stringResource(R.string.language), fontWeight = FontWeight.SemiBold,
                    fontSize = fontSize, maxLines = 1, lineHeight = fontSize,
                    color = MaterialTheme.colorScheme.secondary
                )
            }, onClick = {
                feedback.onHeavyClickEffect()
                if (isAndroid13OrAbove) {
                    val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                } else Toast.makeText(
                    context, context.getString(R.string.language_no_support), Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    @Composable
    private fun AboutApp(context: Context) {
        val link = "https://github.com/Z-Siqi/WritingBoard/"
        super.ClickableCard(
            title = stringResource(R.string.about_app) + " \n" + link.replace("https://", ""),
            icon = {
                Icon(
                    painter = painterResource(R.drawable.github_mark),
                    contentDescription = stringResource(R.string.about_app)
                )
            }, onClick = {
                feedback.onClickSound()
                val intent = Intent(
                    Intent.ACTION_VIEW, link.toUri()
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        )
    }

    @Composable
    private fun Donate(context: Context) {
        super.ClickableCard(
            title = stringResource(R.string.sponsor_me), icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_donate),
                    contentDescription = stringResource(R.string.about_app),
                    modifier = Modifier.size(32.dp)
                )
            }, onClick = {
                feedback.onClickSound()
                val intent = Intent(
                    Intent.ACTION_VIEW, "https://github.com/sponsors/Z-Siqi".toUri()
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        )
    }

    fun list(viewModel: MainViewModel, context: Context): List<Item> {
        return listOf(
            Item { super.ListTitle(R.string.quick_function) },
            Item { this.ExportText(viewModel, context) },
            Item { this.ImportText(viewModel, context) },
            Item { super.ListTitle(R.string.writingboard_app) },
            Item { this.Theme() },
            Item { this.ControlStyle(viewModel) },
            Item { this.ReadOnlyMode(viewModel) },
            Item { super.ListTitle(R.string.writingboard) },
            Item { this.FontSize() },
            Item { this.ItalicFont() },
            Item { this.FontStyle(context) },
            Item { this.FontWeight() },
            Item { super.ListTitle(R.string.keyboard_texts_action) },
            Item { this.MergeLineBreak() },
            Item { this.InstantSaveText() },
            Item { super.ListTitle(R.string.others) },
            Item { this.Vibration() },
            Item { this.RequestAddQSTile(context) },
            Item { this.RequestAddWidget(context) },
            Item { this.Language(context) },
            Item { super.ListTitle(R.string.about) },
            Item { this.AboutApp(context) },
            Item { this.Donate(context) },
        )
    }
}
