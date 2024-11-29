package com.sqz.writingboard.file

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sqz.writingboard.R
import com.sqz.writingboard.component.Feedback
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val timeFormat = "yyyyMMdd_HHmm" + "ss" // No Android Studio grammar checking this!

@Composable
fun ShareDialog(
    onDismissRequest: () -> Unit,
    shareText: String,
    context: Context,
    feedback: Feedback,
    modifier: Modifier = Modifier,
    textFile: TextFile = TextFile(context)
) {
    val list = listOf(
        ListData(0, stringResource(R.string.export_file)),
        ListData(1, stringResource(R.string.share_text))
    )
    var mode by rememberSaveable { mutableIntStateOf(0) }
    val errorToast = Toast.makeText(
        context, "Error: Failed! Please catch log to identify reason!",
        Toast.LENGTH_SHORT
    )
    // Export
    var uri by remember { mutableStateOf<Uri?>(null) }
    val currentTime = remember {
        val sdf = SimpleDateFormat(timeFormat, Locale.getDefault())
        sdf.format(Date())
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { selectedUri: Uri? ->
        uri = selectedUri
        uri?.let {
            if (textFile.export(shareText, it) == 1) errorToast.show() else onDismissRequest()
        }
    }
    // Dialog
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                when (mode) {
                    0 -> launcher.launch("WritingBoard_$currentTime.txt")
                    1 -> if (textFile.share(shareText) == 1) errorToast.show() else onDismissRequest()
                    else -> {
                        Toast.makeText(context, "Error: Invalid mode", Toast.LENGTH_SHORT).show()
                    }
                }
                feedback.createClickSound()
            }) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest()
                feedback.createClickSound()
            }) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        title = { Text(text = stringResource(R.string.export_writingboard_text)) },
        text = {
            Column {
                Spacer(modifier = modifier.height(12.dp))
                SingleChoiceSegmentedButtonRow(
                    modifier = modifier
                        .height(42.dp)
                        .fillMaxWidth()
                ) {
                    list.forEach { index ->
                        SegmentedButton(
                            selected = index.index == mode,
                            onClick = {
                                mode = index.index
                                feedback.createClickSound()
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index.index,
                                count = list.size
                            ),
                            modifier = modifier.fillMaxSize()
                        ) { Text(text = index.string) }
                    }
                }
            }
        }
    )
}

private data class ListData(
    val index: Int,
    val string: String
)

@Preview
@Composable
private fun Preview() {
    ShareDialog({}, "", LocalContext.current, Feedback(LocalView.current))
}
