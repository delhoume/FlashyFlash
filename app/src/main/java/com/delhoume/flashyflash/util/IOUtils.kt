package com.delhoume.flashyflash.util

import android.content.Context
import android.content.res.AssetManager
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import kotlin.io.bufferedReader

// TODO; look at getExternalCacheDir()
private fun getStorageFolder(name: String): File {
    val rootStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val newFolder = File(rootStorage, name)
    if (!newFolder.exists())
        newFolder.mkdirs()
    return newFolder
}

fun getApplicationFolder(): File {
    return getStorageFolder("flashyflash")
}

fun getAtlasFolder(): File {
    return getStorageFolder("flashyflash/atlas")
}

fun getListsFolder(): File {
    return getStorageFolder("flashyflash/lists")
}

// relative to application folder
fun getStorageOutputStream(filename: String): OutputStream {
    val file = File(getApplicationFolder(), filename)
    return FileOutputStream(file)
}

fun getStorageInputStream(filename: String): InputStream? {
    val file = File(getApplicationFolder(), filename)
    if (file.exists())
        return FileInputStream(file)
    return null
}

fun readUserFlash(context: Context, filename: String): String {
    var newfile = File(context.filesDir, filename)
    return if (newfile.exists())
        newfile.readText(Charsets.UTF_8)
    else
        ""
}

fun writeUserflashContents(context: Context, filename : String, contents: String) {
    var newfile = File(context.filesDir, filename)
    newfile.writeText(contents)
}

fun writeToDownloads(context: Context, filename: String, contents: String) {
    val rootStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val newfile = File(rootStorage, filename)
    newfile.writeText(contents)
}

fun readFromDownloads(context: Context, filename: String) : String {
    val rootStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val newfile = File(rootStorage, filename)
    if (newfile.exists())
        return FileInputStream(newfile).bufferedReader().use{ it.readText() }
    return ""
}

fun copyAssetFile(assets: AssetManager, filename: String) {
    assets.open(filename).use { stream ->
        getStorageOutputStream(filename)
            .use { stream.copyTo(it) }
    }
}

