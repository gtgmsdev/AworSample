package com.gamatechno.awor.utils

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.gamatechno.awor.R
import java.io.*
import java.net.URL

class DownloadUtil(var context: Context) : AsyncTask<String, Int, Boolean>() {
    private var progressDialog: ProgressDialog? = null
    private var fileDownload: File? = null
    private var errMsg: String = "Error Download File"

    override fun onPreExecute() {
        super.onPreExecute()
        progressDialog = ProgressDialog(context)
        progressDialog!!.setTitle(context.getString(R.string.download_title))
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    override fun doInBackground(vararg f_url: String?): Boolean {
        var count: Int
        try {
            val url = URL(f_url[0])
            val connection = url.openConnection()
            connection.connect()
            val lengthOfFile = connection.contentLength
            val input: InputStream = BufferedInputStream(url.openStream(), 8192)
            val fileName : String =
                f_url[0]!!.substring(f_url[0]!!.lastIndexOf('/') + 1, f_url[0]!!.length)
            fileDownload = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!,
                fileName
            )
            val output: OutputStream = FileOutputStream(fileDownload!!)
            val data = ByteArray(1024)
            var total: Long = 0
            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                publishProgress((total * 100 / lengthOfFile).toInt())
                output.write(data, 0, count)
            }
            output.flush()
            output.close()
            input.close()
            return true
        } catch (e: Exception) {
        }

        return false
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(values[0])
        progressDialog!!.progress = values[0]!!
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        progressDialog!!.dismiss()

        if (result!!) {
            val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    context,
                    context.packageName + ".fileprovider",
                    fileDownload!!
                )
            } else {
                Uri.fromFile(fileDownload)
            }
            try {
                val mime = context.contentResolver.getType(uri)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, mime)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
//                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                Toast.makeText(context, R.string.activity_not_found, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
        else{
            Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
        }
    }

}