package com.aptoide.android.aptoidegames.installer.notifications

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.CachePolicy.ENABLED
import coil.request.ImageRequest
import coil.request.SuccessResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageDownloader @Inject constructor(
  @ApplicationContext private val context: Context,
) {
  suspend fun downloadImageFrom(url: String?): Bitmap? {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
      .memoryCachePolicy(ENABLED)
      .data(url)
      .build()

    val result = loader.execute(request)
    return if (result is SuccessResult) {
      (result.drawable as? BitmapDrawable)?.bitmap
    } else {
      null
    }
  }
}
