package cm.aptoide.pt.account.view

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import cm.aptoide.pt.logger.Logger

class ImageInfoProvider(private val contentResolver: ContentResolver) {

  private val TAG = ImageInfoProvider::class.java.name

  fun getInfo(imagePath: String): ImageInfo? {

    val uri = Uri.parse(imagePath)

    val projection = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.HEIGHT,
        MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.SIZE)

    val cursor = contentResolver.query(Uri.parse(imagePath), projection, null, null, null)

    cursor?.let { c ->
      try {
        cursor.moveToFirst()

        val width = c.getInt(c.getColumnIndex(MediaStore.Images.Media.WIDTH))
        val height = c.getInt(c.getColumnIndex(MediaStore.Images.Media.HEIGHT))
        val size = c.getLong(c.getColumnIndex(MediaStore.Images.Media.SIZE))

        cursor.close()

        Logger.getInstance()
            .e("lol", "Width is : $width height is $height size is $size")

        return ImageInfo(height, width, size)

      } catch (exception: Exception) {
        Logger.getInstance().e(TAG, exception)
      }
    }
    return null
  }
}