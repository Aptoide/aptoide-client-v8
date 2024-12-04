package cm.aptoide.pt.extensions

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import java.io.File

fun ApplicationInfo.getApkSize(): Long = publicSourceDir.let(::File).length()

fun ApplicationInfo.getSplitsSize(): Long = splitPublicSourceDirs
  ?.map(::File)
  ?.map(File::length)
  ?.plus(0) // to make list non empty
  ?.reduce { acc, it -> acc + it }
  ?: 0

fun ApplicationInfo.loadIconDrawable(packageManager: PackageManager): Drawable? = runCatching {
  loadUnbadgedIcon(packageManager)
    .let { drawable ->
      if (drawable is AdaptiveIconDrawable) {
        InsetDrawable(
          LayerDrawable(listOf(drawable.background, drawable.foreground).toTypedArray()),
          -27f / 108f
        )
      } else {
        drawable
      }
    }
}.getOrNull()
