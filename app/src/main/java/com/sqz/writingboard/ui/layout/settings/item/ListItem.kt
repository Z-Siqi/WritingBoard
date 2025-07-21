package com.sqz.writingboard.ui.layout.settings.item

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sqz.writingboard.ui.component.TextTooltipBox
import com.sqz.writingboard.ui.theme.pxToDpInt

open class ListItem {

    @Composable
    private fun OutlinedCardView(
        modifier: Modifier = Modifier,
        content: @Composable (ColumnScope.() -> Unit),
    ) = OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .padding(16.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.outlinedCardColors(),
        content = content
    )

    @Composable
    private fun ExtendableCardView(
        title: String, content: @Composable (ColumnScope.() -> Unit)
    ) = this.OutlinedCardView {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                .wrapContentHeight(Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.secondary
        )
        content()
    }

    @Composable
    private fun SwitchView(
        text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier
    ) = Column {
        Row(
            modifier = modifier
                .padding(top = 8.dp, bottom = 8.dp, start = 15.dp, end = 15.dp)
                .heightIn(min = 60.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = modifier.widthIn(
                    max = (LocalWindowInfo.current.containerSize.width.pxToDpInt() * 0.72).dp
                ),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary,
            )
            Spacer(modifier = modifier.weight(1f))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }

    @Composable
    private fun segmentedButtonView(
        list: Array<out Any>, label: (Any) -> String, initSetter: Int, modifier: Modifier
    ): Int {
        var overflow by remember { mutableStateOf(false) }
        var selectedIndex by remember { mutableIntStateOf(initSetter) }
        SingleChoiceSegmentedButtonRow(modifier = modifier) {
            list.forEachIndexed { index, item ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index, count = list.size
                    ), label = {
                        TextTooltipBox(label(item), enable = overflow) {
                            Text(
                                text = label(item), overflow = TextOverflow.Ellipsis, maxLines = 1,
                                fontSize = 15.sp / LocalConfiguration.current.fontScale,
                                onTextLayout = { overflow = it.hasVisualOverflow }
                            )
                        }
                    }, onClick = { selectedIndex = index }, selected = index == selectedIndex
                )
            }
        }
        return selectedIndex
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

    @Composable
    protected fun SwitchCardLayout(
        text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit
    ) = this.OutlinedCardView {
        SwitchView(text, checked, onCheckedChange)
    }

    @Composable
    protected fun BasicSegmentedButtonLayout(
        title: String, option: List<String>, onChange: (Int?) -> Int,
    ) = this.ExtendableCardView(title = title) {
        var selectedIndex by remember { mutableIntStateOf(onChange(null)) }
        selectedIndex = segmentedButtonView(
            list = option.toTypedArray(),
            label = { label -> option.find { it == label } ?: "N/A" },
            initSetter = selectedIndex,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, start = 15.dp, end = 15.dp)
        ).let { onChange(it) }
    }

    @Composable
    protected fun SegmentedButtonWithSwitch(
        title: String, option: List<String>, onChange: (Int?) -> Int,
        switch: Boolean, switchText: String, onCheckedChange: (Boolean?) -> Boolean
    ) = this.ExtendableCardView(title = title) {
        var selectedIndex by remember { mutableIntStateOf(onChange(null)) }
        selectedIndex = segmentedButtonView(
            list = option.toTypedArray(),
            label = { label -> option.find { it == label } ?: "N/A" },
            initSetter = selectedIndex,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp)
        ).let { onChange(it) }
        var checked by remember { mutableStateOf(onCheckedChange(null)) }
        if (switch) SwitchView(text = switchText, checked = checked, onCheckedChange = {
            checked = onCheckedChange(it)
        }) else Spacer(modifier = Modifier.height(12.dp))
    }

    @Composable
    protected fun SegmentedButtonWithSegmentedButtonAndCard(
        title: String, defOption: List<String>, onDefChange: (Int?) -> Int,
        showAll: Boolean, subOption: List<String>, onSubChange: (Int?) -> Int,
        onSubCardClick: () -> Unit, enableSubCardClick: Boolean, onSubCardText: String
    ) = this.ExtendableCardView(title = title) {
        var selectedIndex by remember { mutableIntStateOf(onDefChange(null)) }
        selectedIndex = segmentedButtonView(
            list = defOption.toTypedArray(),
            label = { label -> defOption.find { it == label } ?: "N/A" },
            initSetter = selectedIndex,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp)
        ).let { onDefChange(it) }
        if (showAll) {
            var selectedSubIndex by remember { mutableIntStateOf(onSubChange(null)) }
            selectedSubIndex = segmentedButtonView(
                list = subOption.toTypedArray(),
                label = { label -> subOption.find { it == label } ?: "N/A" },
                initSetter = selectedSubIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp)
            ).let { onSubChange(it) }
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 58.dp)
                    .padding(start = 15.dp, end = 15.dp, bottom = 12.dp),
                onClick = onSubCardClick, enabled = enableSubCardClick,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp), text = onSubCardText, fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else Spacer(modifier = Modifier.height(12.dp))
    }

    data class Item(
        val content: @Composable () -> Unit
    )
}
