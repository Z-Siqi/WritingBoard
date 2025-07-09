package com.sqz.writingboard.ui.layout.settings.item

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.sqz.writingboard.R
import com.sqz.writingboard.preferences.SettingOption
import com.sqz.writingboard.ui.MainViewModel
import kotlinx.coroutines.flow.update

class SettingsItem(private val settings: SettingOption) : ListItem() {

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

    fun list(viewModel: MainViewModel): List<Item> {
        return listOf(
            Item { super.ListTitle(R.string.writingboard_app) },
            Item { this.ReadOnlyMode(viewModel) }
        )
    }
}
