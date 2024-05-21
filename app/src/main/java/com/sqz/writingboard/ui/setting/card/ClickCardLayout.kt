package com.sqz.writingboard.ui.setting.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sqz.writingboard.R
import com.sqz.writingboard.ui.theme.themeColor

@Composable
fun ClickCardLayout(
    intent: () -> Unit,
    text: String,
    painter: Int,
    contentDescription: String,
    colors: CardColors,
    modifier: Modifier = Modifier,
) {
    val screenCard = if (LocalConfiguration.current.screenWidthDp < 368) {
        modifier.height(110.dp)
    } else {
        modifier.height(85.dp)
    }
    val screenIcon = if (LocalConfiguration.current.screenWidthDp < 392) {
        modifier
            .padding(end = 10.dp, top = 10.dp)
            .wrapContentHeight(Alignment.Top)
    } else {
        modifier.padding(end = 27.dp)
    }
    Card(
        colors = colors,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            then screenCard
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable(onClick = intent)
        ) {
            Text(
                text = text,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = modifier
                    .padding(16.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.secondary
            )
            Icon(
                painter = painterResource(painter),
                contentDescription = contentDescription,
                modifier = modifier
                    .fillMaxSize()
                    .wrapContentWidth(Alignment.End)
                    then screenIcon,
            )
        }
    }
}
@Preview
@Composable
private fun Preview() {
    val cardColors = CardDefaults.cardColors(containerColor = themeColor("cardColor"))
    ClickCardLayout(
        intent = {},
        text = "test\ntest",
        painter = R.drawable.ic_launcher_foreground,
        contentDescription = "About",
        colors = cardColors
    )
}