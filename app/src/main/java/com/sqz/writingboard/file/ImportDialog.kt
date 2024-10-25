package com.sqz.writingboard.file

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sqz.writingboard.R
import com.sqz.writingboard.component.Feedback
import com.sqz.writingboard.ui.component.drawVerticalScrollbar

@Composable
fun ImportDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (text: String) -> Unit,
    context: Context,
    feedback: Feedback,
    modifier: Modifier = Modifier,
    textFile: TextFile = TextFile(context)
) {
    var importedText by rememberSaveable { mutableStateOf("") }
    val errorToast = Toast.makeText(
        context, "Error: Failed! Please catch log to identify reason!",
        Toast.LENGTH_SHORT
    )
    var uri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { selectedUri: Uri? ->
        uri = selectedUri
        uri?.let {
            if (textFile.import(it) { text ->
                    importedText = text
                } == 1
            ) errorToast.show()
        }
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest()
                feedback.createClickSound()
            }) {
                Text(text = stringResource(id = R.string.dismiss))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (importedText.isNotEmpty()) {
                    onConfirm(importedText)
                    onDismissRequest()
                    Toast.makeText(
                        context, context.getString(R.string.success),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context, context.getString(R.string.select_text_file_first),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                feedback.createClickSound()
            }) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        title = {
            Text(
                text = stringResource(R.string.import_text_to_writingboard),
                fontSize = 22.sp
            )
        },
        text = {
            Column {
                Text(text = stringResource(R.string.preview), fontWeight = FontWeight.SemiBold)
                Spacer(modifier = modifier.height(5.dp))
                OutlinedCard(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .drawVerticalScrollbar(scrollState)
                            .verticalScroll(scrollState)
                            .clickable {
                                launcher.launch(arrayOf("text/plain"))
                                feedback.createClickSound()
                            }
                    ) {
                        if (importedText.isEmpty()) {
                            Text(
                                text = stringResource(R.string.select_a_text_file),
                                modifier = modifier.padding(5.dp),
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        } else Text(
                            text = importedText,
                            modifier = modifier.padding(5.dp)
                        )
                    }
                }
                Spacer(modifier = modifier.height(5.dp))
                Text(
                    text = stringResource(R.string.waring_to_import_text),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 12.sp
                )
            }
        }
    )
}

@Preview
@Composable
private fun Preview() {
    ImportDialog({}, {}, LocalContext.current, Feedback(LocalView.current))
}
