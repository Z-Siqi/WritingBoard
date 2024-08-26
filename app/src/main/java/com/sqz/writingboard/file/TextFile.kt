package com.sqz.writingboard.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

class TextFile(
    context: Context
) {
    private val _context = context

    fun export(shareText: String, uri: Uri): Int {
        try {
            _context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(shareText.toByteArray())
            }
        } catch (e: Exception) {
            Log.e("WritingBoardTag", "ERROR: $e")
            return 1
        }
        return 0
    }

    fun share(shareText: String): Int {
        val intent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val shareIntent = Intent.createChooser(intent, null)
        try {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            _context.startActivity(shareIntent)
        } catch (e: Exception) {
            Log.e("WritingBoardTag", "ERROR: $e")
            return 1
        }
        return 0
    }

    fun import(uri: Uri, output: (text: String) -> Unit): Int {
        try {
            _context.contentResolver.openInputStream(uri)?.use { inputStream ->
                output(inputStream.bufferedReader().readText())
            }
        } catch (e: Exception) {
            Log.e("WritingBoardTag", "ERROR: $e")
            return 1
        }
        return 0
    }
}
