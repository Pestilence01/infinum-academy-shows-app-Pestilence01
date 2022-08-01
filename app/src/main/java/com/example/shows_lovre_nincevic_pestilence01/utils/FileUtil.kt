package com.example.shows_lovre_nincevic_pestilence01.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.os.Environment
import android.util.Log
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private const val AVATAR_FILENAME = "avatar.jpg"
private const val COMPRESSED_IMAGE_QUALITY_PERCENTAGE = 15

object FileUtil {

    fun getImageFile(context: Context?): File? {
        if (context == null) return null

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), AVATAR_FILENAME)
        if (file.exists().not()) {
            Log.e("FileUtil", "Image file does not exist.")
            return null
        }
        return makeImageSmaller(file)
    }

    /**
     * Api has restriction to 1MB size so we have to make image smaller before upload.
     */
    private fun makeImageSmaller(file: File): File {
        val bitmap = fixRotation(file)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSED_IMAGE_QUALITY_PERCENTAGE, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }

    /**
     * Some devices use camera in landscape mode by default so we need to rotate it properly.
     */
    private fun fixRotation(file: File): Bitmap {
        val ei = ExifInterface(file.path)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val bitmap = BitmapFactory.decodeFile(file.path)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }
    }

    fun createImageFile(context: Context): File? {
        try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), AVATAR_FILENAME)
            if (file.exists().not() && file.createNewFile().not()) {
                Log.e("FileUtil", "Failed to create image file.")

                return null
            }
            return file
        } catch (e: IOException) {
            Log.e("FileUtil", e.message.orEmpty())
            return null
        }

    }
}