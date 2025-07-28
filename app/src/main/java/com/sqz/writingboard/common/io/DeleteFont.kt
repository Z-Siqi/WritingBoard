package com.sqz.writingboard.common.io

import android.content.Context
import android.util.Log
import java.io.File

/** Remove the font file from the context.filesDir **/
fun deleteFont(context: Context) {
    val fontFile = File(context.filesDir, importedFontName)
    if (fontFile.exists()) {
        fontFile.delete()
        Log.w("WritingBoard", "Font is deleted!")
    }
}
