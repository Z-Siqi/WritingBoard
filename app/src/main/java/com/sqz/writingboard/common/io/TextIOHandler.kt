package com.sqz.writingboard.common.io

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val timeFormat = "yyyyMMdd_HHmm" + "ss" // No Android Studio grammar checking this!

class TextIOHandler(private val context: Context) {

    private fun export(outputText: String, uri: Uri, onException: (e: Exception) -> Unit) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(outputText.toByteArray())
            }
            cacheExport.update { null }
        } catch (e: Exception) {
            Log.e("WritingBoardTag", "TextIOHandler Err: $e")
            onException(e)
        }
    }

    private fun import(
        output: (text: String) -> Unit,
        uri: Uri,
        onException: (e: Exception) -> Unit
    ) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val maxSizeBytes = 256 * 1024
                val size = inputStream.available()
                if (size > maxSizeBytes) {
                    throw IllegalArgumentException("Selected file is too large: ${size / 1024} KB. Limit is 256 KB.")
                }
                val text = inputStream.bufferedReader().readText()
                output(text)
            }
        } catch (e: Exception) {
            Log.e("WritingBoardTag", "TextIOHandler Err: $e")
            onException(e)
        }
    }

    companion object {
        private var cacheExport = MutableStateFlow<String?>(null)

        fun closeExport() {
            cacheExport.update { null }
        }

        @Composable
        fun isInExport(): Boolean = cacheExport.collectAsState().value != null

        @Composable
        fun rememberExport(
            outputText: String,
            context: Context,
            onException: (e: Exception) -> Unit = {}
        ): ManagedActivityResultLauncher<String, Uri?> {
            val isExportingOpen = rememberSaveable { mutableStateOf(false) }
            if (!isExportingOpen.value) cacheExport.update {
                isExportingOpen.value = true
                outputText
            }
            return rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument("text/plain")
            ) { selectedUri: Uri? ->
                selectedUri?.let {
                    TextIOHandler(context).export(cacheExport.value.toString(), it, onException)
                } ?: closeExport()
            }
        }

        fun ManagedActivityResultLauncher<String, Uri?>.launchExport() {
            val currentTime = SimpleDateFormat(timeFormat, Locale.getDefault()).format(Date())
            this.launch("WritingBoard_$currentTime.txt")
        }

        @Composable
        fun rememberImport(
            importedText: (String) -> Unit,
            context: Context,
            onException: (e: Exception) -> Unit = {}
        ): ManagedActivityResultLauncher<Array<String>, Uri?> {
            return rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { selectedUri: Uri? ->
                selectedUri?.let {
                    TextIOHandler(context).import(importedText, it, onException)
                }
            }
        }

        fun ManagedActivityResultLauncher<Array<String>, Uri?>.launchImport() {
            this.launch(arrayOf("text/plain"))
        }
    }

    fun share(shareText: String, onException: (e: Exception) -> Unit = {}) {
        closeExport()
        val intent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val shareIntent = Intent.createChooser(intent, null)
        try {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            Log.e("WritingBoardTag", "TextIOHandler Err: $e")
            onException(e)
        }
    }
}
