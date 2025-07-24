package com.sqz.writingboard.common.io

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.sqz.writingboard.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

const val importedFontName = "font.ttf"

class ImportFontManager(
    private val launcher: ManagedActivityResultLauncher<Array<String>, Uri?>,
    private val uri: MutableState<Uri?>,
    private val selectedFont: MutableState<String?>
) {
    companion object {
        @Composable
        fun getLauncher(uri: (Uri?) -> Unit): ManagedActivityResultLauncher<Array<String>, Uri?> {
            return rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { getUri: Uri? -> uri(getUri) }
        }

        private fun saveFontToInternalStorage(context: Context, uri: Uri) {
            val contentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            inputStream?.let {
                val fontFile = File(context.filesDir, importedFontName)
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
    }

    private fun invalidFontText(context: Context): String {
        return context.getString(R.string.font_is_invalid)
    }

    internal fun importListenerAction(context: Context) {
        if (uri.value != null) {
            saveFontToInternalStorage(context, uri.value!!)
            if (isValidFont(context)) this.setIsImported(context) else {
                Log.e("WritingBoard", "ImportFontManager: Font is invalid! trying to delete...")
                this.deleteFont(context)
                selectedFont.value = invalidFontText(context)
            }
            uri.value = null
        }
    }

    fun isValidFont(file: File): Boolean {
        return try {
            Typeface.Builder(file).build().style
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isValidFont(context: Context): Boolean {
        val fontFile = File(context.filesDir, importedFontName)
        return this.isValidFont(fontFile)
    }

    private fun setIsImported(context: Context) {
        selectedFont.value = context.getString(R.string.font_is_imported)
    }

    fun getState(context: Context): String {
        val fontFile = File(context.filesDir, importedFontName)
        if (selectedFont.value != invalidFontText(context)) {
            if (!fontFile.exists()) {
                selectedFont.value = context.getString(R.string.click_to_select_a_font)
            } else {
                this.setIsImported(context)
            }
        }
        return selectedFont.value ?: context.getString(R.string.click_to_select_a_font)
    }

    fun deleteFont(context: Context) {
        val fontFile = File(context.filesDir, importedFontName)
        if (fontFile.exists()) {
            fontFile.delete()
            Log.w("WritingBoard", "ImportFontManager: Font is deleted!")
        }
    }

    fun importFont() {
        launcher.launch(arrayOf("font/ttf"/*, "font/otf"*/))
    }
}

@Composable
fun rememberImportFontManager(context: Context = LocalContext.current): ImportFontManager {
    val uri = remember { mutableStateOf<Uri?>(null) }
    var selectedFont = remember { mutableStateOf<String?>(null) }
    val manager = ImportFontManager(
        ImportFontManager.getLauncher { uri.value = it }, uri, selectedFont
    )
    LaunchedEffect(uri.value) { manager.importListenerAction(context) }
    return manager
}
