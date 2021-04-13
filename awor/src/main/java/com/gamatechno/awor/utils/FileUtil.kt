package com.gamatechno.awor.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException


object FileUtil {

    fun deleteImageCache(context: Context){
        val directory : File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val files = directory.listFiles()
        if (files!=null) {
            for (i in files.indices) {
                files[i].delete()
            }
        }
    }

    @Throws(IOException::class)
    fun createTempFile(context: Context, eks: String): File {
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "FILE",
            ".$eks",
            storageDir
        )
    }

    /*fun createCompressFile(context: Context, fileSource: File) : File{
        return Compressor(context)
            .setMaxWidth(640)
            .setMaxHeight(480)
            .setQuality(75)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .setDestinationDirectoryPath(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/ImageCompressor"
            )
            .compressToFile(fileSource)
    }*/

    fun getSizeFile(file: File): Long{
        return file.length() //byte
    }

    fun getMimeType(eks: String?): String? {
        var type: String? = null
        if (eks != null && eks.isNotEmpty()) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(eks)
        }
        return type
    }

    fun getExtensionType(url: String?): String? {
        return MimeTypeMap.getFileExtensionFromUrl(url)
    }

    fun getExtensionFromUri(context: Context, uri: Uri): String? {
        val extension: String?

        extension = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(
                Uri.fromFile(File(uri.path!!)).toString()
            )
        }
        return extension
    }

    fun createPartString(string: String): RequestBody {
        return string.toRequestBody(MultipartBody.FORM)
    }

    fun createPartFile(
        partName: String,
        filePath: String
    ): MultipartBody.Part {
        return if (filePath!="" && filePath!="edit") {
            val file = File(filePath)
            val requestFile = file.asRequestBody(MultipartBody.FORM)
            createFormData(partName, file.name, requestFile)
        } else{
            val requestFile = filePath.toRequestBody(MultipartBody.FORM)
            createFormData(partName, "", requestFile)
        }
    }
}