package com.aptoide.android.aptoidegames.installer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import cm.aptoide.pt.installer.PreApprovalIconProvider
import coil.imageLoader
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoilPreApprovalIconProvider @Inject constructor(
  @ApplicationContext private val context: Context,
) : PreApprovalIconProvider {

  override suspend fun getIcon(url: String): Bitmap? {
    val request = ImageRequest.Builder(context)
      .data(url)
      .build()

    val result = context.imageLoader.execute(request)
    return (result.drawable as? BitmapDrawable)?.bitmap
  }
}
