package cm.aptoide.pt.account.view

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import cm.aptoide.pt.logger.Logger
import java.io.File


class ImageInfoProvider(private val contentResolver: ContentResolver) {

  private val TAG = ImageInfoProvider::class.java.name

  fun getInfo(imagePath: String): ImageInfo? {

    val projection = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.HEIGHT,
        MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.SIZE)

    val uri = Uri.parse(imagePath)

    if (uri?.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
      val cursor = contentResolver.query(uri, projection, null, null, null)

      cursor?.let { imageCursor ->
        try {
          cursor.moveToFirst()

          val width = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media.WIDTH))
          val height =
              imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media.HEIGHT))
          val size = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media.SIZE))

          cursor.close()

          return ImageInfo(height, width, size)

        } catch (exception: Exception) {
          Logger.getInstance().e(TAG, exception)
        }
      }
    } else if (uri?.scheme.equals(ContentResolver.SCHEME_FILE)) {
      try {
        val file = File(imagePath)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, options)
        val imageHeight = options.outHeight
        val imageWidth = options.outWidth

        return ImageInfo(imageHeight, imageWidth, file.length())
      } catch (exception: Exception) {
        exception.printStackTrace()
      }
    }
    return null
  }
}