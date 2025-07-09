package com.sqz.writingboard.ui.layout.settings.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sqz.writingboard.ui.theme.pxToDpInt

open class ListItem {

    @Composable
    protected fun SwitchCardLayout(
        text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit
    ) = OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, start = 15.dp, end = 15.dp)
                .heightIn(min = 60.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.widthIn(
                    max = (LocalWindowInfo.current.containerSize.width.pxToDpInt() * 0.75).dp
                ),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary,
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }

    @Composable
    protected fun ListTitle(text: String) {
        Text(
            text = text,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(top = 16.dp, start = 18.dp)
        )
    }

    @Composable
    protected fun ListTitle(textRid: Int) {
        this.ListTitle(stringResource(textRid))
    }

    data class Item(
        val content: @Composable () -> Unit
    )
}
