package com.sqz.writingboard.ui.layout.settings.item

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sqz.writingboard.R
import com.sqz.writingboard.common.feedback.Feedback
import com.sqz.writingboard.common.io.TextIOHandler
import com.sqz.writingboard.common.io.TextIOHandler.Companion.launchExport
import com.sqz.writingboard.common.io.TextIOHandler.Companion.launchImport
import com.sqz.writingboard.ui.component.drawVerticalScrollbar

class ImportExportDialog(private val feedback: Feedback) : ListItem(feedback) {

    @Composable
    private fun Dialog(
        onDismissRequest: () -> Unit,
        onConfirm: () -> Unit,
        title: String,
        content: @Composable (ColumnScope.() -> Unit)
    ) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    feedback.onClickEffect()
                }) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismissRequest()
                    feedback.onClickEffect()
                }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            title = { Text(text = title) },
            text = { Column(content = content) }
        )
    }

    private fun showToast(text: String, context: Context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    private fun showToast(context: Context) {
        val text = "Error: Failed! Please catch log to identify reason!"
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    @Composable
    fun ExportDialog(outputText: String, onDismissRequest: () -> Unit, context: Context) {
        val rememberExport = TextIOHandler.rememberExport(outputText, context) {
            this.showToast(context)
        }
        var selectedOption by rememberSaveable { mutableIntStateOf(0) }
        var waitingClose by rememberSaveable { mutableStateOf(false) }
        if (waitingClose) {
            if (!TextIOHandler.isInExport()) onDismissRequest()
        }
        this.Dialog(
            onDismissRequest = {
                TextIOHandler.closeExport()
                onDismissRequest()
            }, onConfirm = {
                when (selectedOption) {
                    0 -> rememberExport.launchExport()
                    1 -> TextIOHandler(context).share(outputText) { this.showToast(context) }
                    else -> this.showToast("Error: Invalid mode", context)
                }
                waitingClose = true
            },
            title = stringResource(R.string.export_writingboard_text)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            val option = listOf(
                stringResource(R.string.export_file), stringResource(R.string.share_text)
            )
            selectedOption = super.segmentedButtonView(
                modifier = Modifier
                    .height(42.dp)
                    .fillMaxWidth(),
                list = option.toTypedArray(),
                label = { label -> option.find { it == label } ?: "N/A" },
                initSetter = selectedOption
            )
        }
    }

    @Composable
    fun ImportDialog(onDismiss: () -> Unit, onConfirm: (text: String) -> Unit, context: Context) {
        var text by rememberSaveable { mutableStateOf<String?>(null) }
        val rememberExport = TextIOHandler.rememberImport(
            context = context,
            onException = { this.showToast(context) },
            importedText = { text = it },
        )
        this.Dialog(
            onDismissRequest = onDismiss,
            onConfirm = {
                if (text != null && text!!.isNotEmpty()) {
                    onConfirm(text!!)
                    this.showToast(context.getString(R.string.success), context)
                } else {
                    this.showToast(context.getString(R.string.select_text_file_first), context)
                }
            },
            title = stringResource(R.string.import_text_to_writingboard)
        ) {
            Text(text = stringResource(R.string.preview), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(5.dp))
            OutlinedCard(modifier = Modifier.fillMaxWidth() then Modifier.height(120.dp)) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawVerticalScrollbar(scrollState)
                        .verticalScroll(scrollState)
                        .clickable {
                            rememberExport.launchImport()
                            feedback.onClickEffect()
                        },
                    verticalArrangement = Arrangement.Top
                ) {
                    if (text == null || text != null && text!!.isEmpty()) {
                        Text(
                            text = stringResource(R.string.select_a_text_file),
                            modifier = Modifier.padding(5.dp),
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    } else Text(
                        text = text!!,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = stringResource(R.string.waring_to_import_text),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 12.sp
            )
        }
    }
}
