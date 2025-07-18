package com.sqz.writingboard.ui.layout.main.control

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.sqz.writingboard.R
import com.sqz.writingboard.preference.PreferenceLocal
import com.sqz.writingboard.preference.SettingOption
import com.sqz.writingboard.ui.layout.LocalState
import com.sqz.writingboard.ui.layout.handler.RequestHandler
import com.sqz.writingboard.ui.theme.PurpleForManual
import com.sqz.writingboard.ui.theme.RedForManual

@Composable
fun HidedButton(
    state: LocalState,
    requestHandler: RequestHandler,
    settings: SettingOption,
    modifier: Modifier = Modifier
) {
    val modifierWithWindowInsets = modifier.let {
        val statusBars = it.windowInsetsPadding(WindowInsets.statusBars)
        val navigationBars = it.windowInsetsPadding(WindowInsets.navigationBars)
        statusBars then navigationBars
    }
    Column(modifier = modifierWithWindowInsets.fillMaxSize()) {
        HidedButtonLayout(
            onClick = { requestHandler.onSettingsClick() },
            weight = 1f,
            bgColor = hidedButtonManual(ButtonType.Settings)
        )
        Spacer(modifier = Modifier.weight(2f))
        if (settings.editButton() && !state.isEditable) HidedButtonLayout(
            onClick = { requestHandler.onEditClick() },
            weight = 1.5f,
            bgColor = hidedButtonManual(ButtonType.Edit)
        ) else Spacer(
            modifier = Modifier.weight(1.5f)
        )
    }
}

private enum class ButtonType { Settings, Edit }

@Composable
private fun hidedButtonManual(buttonType: ButtonType): Color {
    val localPrefs = PreferenceLocal(LocalContext.current)
    if (localPrefs.settingsButtonManual() || localPrefs.editButtonManual()) {
        val getter: Boolean = when (buttonType) {
            ButtonType.Settings -> localPrefs.settingsButtonManual()
            ButtonType.Edit -> localPrefs.editButtonManual()
        }
        var show by remember { mutableStateOf(getter) }
        if (show) {
            val text = when (buttonType) {
                ButtonType.Settings -> stringResource(R.string.settings_button_manual)
                ButtonType.Edit -> stringResource(R.string.edit_button_manual)
            }
            val color = when (buttonType) {
                ButtonType.Settings -> RedForManual
                ButtonType.Edit -> PurpleForManual
            }
            val onDismiss: () -> Unit = {
                show = when (buttonType) {
                    ButtonType.Settings -> localPrefs.settingsButtonManual(false)
                    ButtonType.Edit -> localPrefs.editButtonManual(false)
                }
            }
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    Button(onClick = onDismiss) { Text(text = stringResource(R.string.dismiss)) }
                },
                title = {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = color)
                },
                text = { Text(text = text, style = MaterialTheme.typography.titleMedium) }
            )
            return color
        }
    }
    return Color.Transparent
}

@Composable
private fun ColumnScope.HidedButtonLayout(onClick: () -> Unit, weight: Float, bgColor: Color) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .weight(weight),
        colors = CardDefaults.cardColors(Color.Transparent)
    ) { Spacer(modifier = Modifier.fillMaxSize() then Modifier.background(bgColor)) }
}
