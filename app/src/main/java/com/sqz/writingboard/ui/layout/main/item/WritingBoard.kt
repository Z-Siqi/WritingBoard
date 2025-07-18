package com.sqz.writingboard.ui.layout.main.item

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sqz.writingboard.R
import com.sqz.writingboard.ui.layout.handler.NavControllerHandler
import com.sqz.writingboard.ui.theme.Blue
import com.sqz.writingboard.ui.theme.Pink
import com.sqz.writingboard.ui.theme.PinkText
import com.sqz.writingboard.ui.theme.Purple
import com.sqz.writingboard.ui.theme.White

@Composable
fun WritingBoard(
    writingBoardPadding: WritingBoardPadding,
    backgroundColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    contentPadding: Dp = 16.dp,
    imePadding: Boolean = true,
    contentSize: (IntSize) -> Unit = {},
    content: @Composable (ColumnScope.() -> Unit)
) {
    val density = LocalDensity.current

    val animatedTop by animateDpAsState(writingBoardPadding.top, label = "Top")
    val animatedBottom by animateDpAsState(writingBoardPadding.bottom, label = "Bottom")
    val animatedStart by animateDpAsState(writingBoardPadding.start, label = "Start")
    val animatedEnd by animateDpAsState(writingBoardPadding.end, label = "End")

    val imePadding = Modifier.let {
        if (imePadding) it.windowInsetsPadding(WindowInsets.ime)
        else it
    }
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

@Composable
fun WritingBoard(navControllerHandler: NavControllerHandler) = Column(modifier = Modifier.let {
    val systemBars = it.windowInsetsPadding(WindowInsets.systemBars)
    val displayCutout = it.windowInsetsPadding(WindowInsets.displayCutout)
    systemBars then displayCutout
}) {
    Button(
        modifier = Modifier.padding(start = 20.dp, top = 5.dp),
        onClick = { navControllerHandler.requestBack() }
    ) { Text(stringResource(R.string.back)) }
    WritingBoard(
        writingBoardPadding = WritingBoardPadding(5.dp, 12.dp, 18.dp, 18.dp),
        backgroundColor = MaterialTheme.colorScheme.background,
        borderColor = MaterialTheme.colorScheme.primary,
        contentPadding = 0.dp,
    ) {
        Box {
            Column(modifier = Modifier.fillMaxSize()) {
                val basicModifier = Modifier.let {
                    val weight = it.weight(1f)
                    val fillMaxWidth = it.fillMaxWidth()
                    weight then fillMaxWidth
                }
                val blueColor = basicModifier.background(color = Blue)
                val pinkColor = basicModifier.background(color = Pink)
                val whiteColor = basicModifier.background(color = White)
                Spacer(blueColor)
                Spacer(pinkColor)
                Spacer(whiteColor)
                Spacer(pinkColor)
                Spacer(blueColor)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                val weightModifier = Modifier.weight(1f)
                val itemBlue = weightModifier.padding(top = 30.dp)
                val itemPink = weightModifier.padding(top = 15.dp)
                Text(
                    text = stringResource(R.string.easter_eggs), fontSize = 18.sp,
                    fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold,
                    modifier = itemPink.verticalScroll(rememberScrollState()), color = PinkText
                )
                Text(
                    text = stringResource(R.string.may_all_people_equal),
                    fontSize = 19.sp, fontFamily = FontFamily.Cursive, fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = itemBlue.verticalScroll(rememberScrollState()), color = Purple
                )
                Text(
                    text = stringResource(R.string.to_the_special_you),
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold,
                    modifier = weightModifier.verticalScroll(rememberScrollState()),
                    color = PinkText
                )
                Text(
                    text = stringResource(R.string.may_everyone),
                    fontSize = 20.sp, fontFamily = FontFamily.Cursive,
                    fontStyle = FontStyle.Italic, fontWeight = FontWeight.SemiBold,
                    modifier = itemBlue.verticalScroll(rememberScrollState()), color = Purple
                )
                Text(
                    text = stringResource(R.string.see_easter_egg_again),
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    modifier = itemPink.verticalScroll(rememberScrollState()),
                    color = PinkText
                )
            }
        }
    }
}

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
