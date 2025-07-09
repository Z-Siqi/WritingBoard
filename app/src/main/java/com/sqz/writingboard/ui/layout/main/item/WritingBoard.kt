package com.sqz.writingboard.ui.layout.main.item

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun WritingBoard(
    writingBoardPadding: WritingBoardPadding,
    backgroundColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    contentPadding: Dp = 16.dp,
    contentSize: (IntSize) -> Unit = {},
    content: @Composable (ColumnScope.() -> Unit)
) {
    val density = LocalDensity.current

    val animatedTop by animateDpAsState(writingBoardPadding.top, label = "Top")
    val animatedBottom by animateDpAsState(writingBoardPadding.bottom, label = "Bottom")
    val animatedStart by animateDpAsState(writingBoardPadding.start, label = "Start")
    val animatedEnd by animateDpAsState(writingBoardPadding.end, label = "End")

    val imePadding = Modifier.windowInsetsPadding(WindowInsets.ime)
    val animatedPadding = Modifier.padding(
        top = animatedTop,
        bottom = animatedBottom,
        start = animatedStart,
        end = animatedEnd
    )
    Box(modifier = imePadding then animatedPadding then modifier) {
        OutlinedCard(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            border = BorderStroke(4.dp, borderColor),
            modifier = Modifier.shadow(5.dp, RoundedCornerShape(26.dp)),
        ) {
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
                    .onGloballyPositioned { layoutCoordinates ->
                        val widthDp = with(density) { layoutCoordinates.size.width.toDp() }
                        val heightDp = with(density) { layoutCoordinates.size.height.toDp() }
                        contentSize(
                            IntSize(width = widthDp.value.toInt(), height = heightDp.value.toInt())
                        )
                    },
                content = content
            )
        }
    }
}

data class WritingBoardPadding(
    val top: Dp,
    val bottom: Dp,
    val start: Dp,
    val end: Dp
)

@Preview
@Composable
private fun Preview() {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        WritingBoard(
            writingBoardPadding = WritingBoardPadding(0.dp, 0.dp, 0.dp, 0.dp),
            backgroundColor = MaterialTheme.colorScheme.background,
            borderColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(10.dp)
        ) {}
    }
}
