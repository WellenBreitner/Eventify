package com.example.eventify.eventOrganizer


import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

fun getRealPathFromUri(context: Context, uri: Uri): String {
    var path = ""
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor? = context.contentResolver.query(uri, proj, null, null, null)
    if (cursor != null) {
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        path = cursor.getString(columnIndex)
        cursor.close()
    }
    return path
}
