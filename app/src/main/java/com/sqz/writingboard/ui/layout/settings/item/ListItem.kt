package com.sqz.writingboard.ui.layout.settings.item

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sqz.writingboard.common.feedback.Feedback
import com.sqz.writingboard.ui.component.TextTooltipBox
import com.sqz.writingboard.ui.theme.WritingBoardTheme
import com.sqz.writingboard.ui.theme.pxToDp

open class ListItem(private val feedback: Feedback) {

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
        colors = CardDefaults.outlinedCardColors(
            containerColor = WritingBoardTheme.color.settingsCardBackground
        ),
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
        modifier: Modifier = Modifier, textDescription: String? = null
    ) = Column {
        Row(
            modifier = modifier
                .padding(top = 8.dp, bottom = 8.dp, start = 15.dp, end = 15.dp)
                .heightIn(min = 60.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextTooltipBox(textDescription ?: text, textDescription != null, feedback) {
                Text(
                    text = text,
                    modifier = modifier.widthIn(
                        max = (LocalWindowInfo.current.containerSize.width * 0.68).toInt().pxToDp()
                    ),
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Spacer(modifier = modifier.weight(1f))
            Switch(
                checked = checked, onCheckedChange = {
                    feedback.onClickEffect()
                    onCheckedChange(it)
                }
            )
        }
    }

    @Composable
    protected fun segmentedButtonView(
        list: Array<out Any>, label: (Any) -> String, initSetter: Int, modifier: Modifier
    ): Int {
        var selectedIndex by remember { mutableIntStateOf(initSetter) }
        var labelHeight by remember { mutableIntStateOf(0) }
        val height = (labelHeight * 1.6).toInt().pxToDp() // why 1.6 is work??
        val labelModifier = Modifier.heightIn(min = if (height > 38.dp) height else 38.dp)
        SingleChoiceSegmentedButtonRow(modifier = modifier) {
            list.forEachIndexed { index, item ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index, count = list.size
                    ), label = {
                        var overflow by remember { mutableStateOf(false) }
                        TextTooltipBox(label(item), enable = overflow, feedback = feedback) {
                            Text(
                                text = label(item), overflow = TextOverflow.Visible, maxLines = 2,
                                fontSize = 15.sp / LocalConfiguration.current.fontScale,
                                lineHeight = 12.sp / LocalConfiguration.current.fontScale,
                                onTextLayout = { overflow = it.hasVisualOverflow },
                                modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                                    val heightPx = layoutCoordinates.size.height
                                    if (heightPx > labelHeight) labelHeight = heightPx.toInt()
                                }
                            )
                        }
                    }, modifier = labelModifier, onClick = {
                        feedback.onClickEffect()
                        selectedIndex = index
                    }, selected = index == selectedIndex
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
        switch: Boolean, switchText: String, onCheckedChange: (Boolean?) -> Boolean,
        switchTextDescription: String
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
        }, textDescription = switchTextDescription) else Spacer(modifier = Modifier.height(12.dp))
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

    @Composable
    protected fun ClickableCard(
        title: String, onClick: () -> Unit, icon: @Composable (ColumnScope.() -> Unit)
    ) = this.OutlinedCardView {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .heightIn(min = 70.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            val widthModifier = Modifier.widthIn(
                max = (LocalWindowInfo.current.containerSize.width * 0.8).toInt().pxToDp()
            )
            Text(
                text = title, fontSize = 17.sp, lineHeight = 21.sp,
                fontWeight = FontWeight.SemiBold, modifier = widthModifier.padding(16.dp),
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.weight(1f))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment =  Alignment.CenterHorizontally
            ) { icon() }
            Spacer(Modifier.width(16.dp))
        }
    }

    @Composable
    protected fun DoubleButtonCard(
        title: String, startText: String, endText: String,
        onClickStart: () -> Unit, onClickEnd: () -> Unit
    ) = this.ExtendableCardView(title = title) {
        val widthModifier = if (LocalWindowInfo.current.containerSize.width.pxToDp() > 280.dp) {
            Modifier.widthIn(min = 130.dp)
        } else Modifier
        Row(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(onClick = onClickStart, modifier = widthModifier) {
                Text(text = startText)
            }
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(onClick = onClickEnd, modifier = widthModifier) {
                Text(text = endText)
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.padding(bottom = 12.dp))
    }

    data class Item(
        val content: @Composable () -> Unit
    )
}
