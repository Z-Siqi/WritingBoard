package com.sqz.writingboard.ui.setting.card

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sqz.writingboard.R
import com.sqz.writingboard.ui.theme.ThemeColor
import com.sqz.writingboard.ui.theme.themeColor
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun FontSelectionCard(
    modifier: Modifier = Modifier,
    title: String,
    defaultOptions: List<Int>,
    selectedDefaultOption: Int,
    onOptionSelected: (Int) -> Unit,
    extraOptions: List<Int>,
    selectedExtraOption: Int,
    onExtraOptionSelected: (Int) -> Unit,
    colors: CardColors,
) {
    val onExtended = selectedDefaultOption == 3
    Card(
        colors = colors,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .padding(16.dp)
            .height(if (!onExtended) 120.dp else 238.dp)
    ) {
        Column(modifier = modifier.fillMaxSize()) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = modifier
                    .padding(16.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.secondary
            )
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .wrapContentHeight(Alignment.Bottom)
            ) {
                SingleChoiceSegmentedButtonRow(
                    modifier = modifier
                        .height(43.dp)
                        .fillMaxWidth()
                ) {
                    defaultOptions.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = defaultOptions.size
                            ),
                            onClick = {
                                onOptionSelected(index)
                            },
                            selected = index == selectedDefaultOption,
                            modifier = modifier.fillMaxSize()
                        ) {
                            Text(
                                text = stringResource(label),
                                lineHeight = 10.sp,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                }
                OnExtendedItem(
                    onExtended = onExtended,
                    extraOptions = extraOptions,
                    selectedExtraOption = selectedExtraOption,
                    onOptionSelected = onExtraOptionSelected
                )
            }
        }
    }
}

@Composable
private fun OnExtendedItem(
    onExtended: Boolean,
    extraOptions: List<Int>,
    selectedExtraOption: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFont by remember { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            saveFontToInternalStorage(context, uri)
            selectedFont = context.getString(R.string.font_is_imported)
        }
    }
    if (onExtended) {
        Spacer(modifier = modifier.height(24.dp))
        SingleChoiceSegmentedButtonRow(
            modifier = modifier
                .height(43.dp)
                .fillMaxWidth()
        ) {
            extraOptions.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = extraOptions.size
                    ),
                    onClick = {
                        onOptionSelected(index)
                    },
                    selected = index == selectedExtraOption,
                    modifier = modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(label),
                        lineHeight = 10.sp,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
        Spacer(modifier = modifier.height(10.dp))
        OutlinedCard(
            modifier
                .fillMaxWidth()
                .height(50.dp)) {
            when (selectedExtraOption) {
                0 -> Text(
                    modifier = modifier.padding(8.dp),
                    text = stringResource(R.string.may_only_english),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                1 -> {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .clickable {
                                launcher.launch(arrayOf("font/ttf"/*, "font/otf"*/))
                            },
                        verticalArrangement = Arrangement.Center
                    ) {
                        (if (selectedFont == null) stringResource(R.string.click_to_select_a_font) else selectedFont)?.let {
                            Text(
                                modifier = modifier.padding(8.dp),
                                text = it,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        LaunchedEffect(Unit) {
            val fontFile = File(context.filesDir, "font.ttf")
            if (fontFile.exists()) {
                selectedFont = context.getString(R.string.font_is_imported)
            }
        }
    }
}

private fun saveFontToInternalStorage(context: Context, uri: Uri) {
    val contentResolver = context.contentResolver
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    inputStream?.let {
        val fontFile = File(context.filesDir, "font.ttf")
        val outputStream = FileOutputStream(fontFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.close()
        inputStream.close()
    }
}

@Preview
@Composable
private fun Preview() {
    FontSelectionCard(
        title = stringResource(R.string.font_style),
        defaultOptions = listOf(
            R.string.preview, R.string.preview, R.string.preview, R.string.preview,
        ),
        selectedDefaultOption = 3,
        extraOptions = listOf(R.string.preview, R.string.preview),
        selectedExtraOption = 0,
        onOptionSelected = { _ -> },
        onExtraOptionSelected = { _ -> },
        colors = CardDefaults.cardColors(containerColor = themeColor(ThemeColor.CardColor)),
    )
}
