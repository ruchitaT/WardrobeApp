package `in`.ruchita.wardrobe.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.*

object Utils{
    public fun bitmapToUriConverter(mBitmap: Bitmap?, context: Context): Uri? {
        var uri: Uri? = null
        try {
            val options = BitmapFactory.Options()
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            val newBitmap = Bitmap.createScaledBitmap(
                mBitmap!!, 200, 200,
                true
            )
            val file = File(
                context.getFilesDir(), ("Image"
                        + Date().toString()) + ".jpeg"
            )
            val out: FileOutputStream = context.openFileOutput(
                file.getName(),
                Context.MODE_PRIVATE
            )
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            //get absolute path
            val realPath: String = file.getAbsolutePath()
            val f = File(realPath)
            uri = Uri.fromFile(f)
        } catch (e: Exception) {
            Log.e("Your Error Message", e.message.toString())
        }
        return uri
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight
                && halfWidth / inSampleSize >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
