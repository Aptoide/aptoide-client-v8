package cm.aptoide.pt.account.view

import cm.aptoide.pt.account.view.exception.InvalidImageException
import cm.aptoide.pt.account.view.exception.InvalidImageException.ImageError
import rx.Completable
import rx.Scheduler
import java.util.*

open class ImageValidator(private val scheduler: Scheduler,
                          private val imageInfoProvider: ImageInfoProvider) {

  open fun validateOrGetException(imagePath: String): Completable? {
    return Completable.defer {
      validate(imagePath)
      return@defer Completable.complete()
    }
        .subscribeOn(scheduler)
  }

  @Throws(InvalidImageException::class)
  private fun getInfo(imagePath: String): ImageInfo {
    val info = imageInfoProvider.getInfo(imagePath)
    if (info == null) {
      val errors: MutableList<ImageError> = ArrayList()
      errors.add(ImageError.ERROR_DECODING)
      throw InvalidImageException(errors)
    }
    return info
  }

  @Throws(InvalidImageException::class)
  private fun validate(imagePath: String) {
    val imageInfo = getInfo(imagePath)
    val errors: MutableList<ImageError> = ArrayList()

    if (imageInfo.height < MIN_IMAGE_HEIGHT) {
      errors.add(ImageError.MIN_HEIGHT)
    } else if (imageInfo.height > MAX_IMAGE_HEIGHT) {
      errors.add(ImageError.MAX_HEIGHT)
    }

    if (imageInfo.width < MIN_IMAGE_WIDTH) {
      errors.add(ImageError.MIN_WIDTH)
    } else if (imageInfo.width > MAX_IMAGE_WIDTH) {
      errors.add(ImageError.MAX_WIDTH)
    }

    if (imageInfo.size > MAX_IMAGE_SIZE_IN_BYTES) {
      errors.add(ImageError.MAX_IMAGE_SIZE)
    }

    if (!errors.isEmpty()) {
      throw InvalidImageException(errors)
    }
  }

  companion object {
    private const val MIN_IMAGE_HEIGHT = 600
    private const val MAX_IMAGE_HEIGHT = 10240
    private const val MIN_IMAGE_WIDTH = 600
    private const val MAX_IMAGE_WIDTH = 10240
    private const val MAX_IMAGE_SIZE_IN_BYTES = 5242880
  }

}