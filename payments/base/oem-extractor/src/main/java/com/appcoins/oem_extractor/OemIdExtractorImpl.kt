package com.appcoins.oem_extractor

import android.content.Context
import com.aptoide.apk.injector.extractor.IExtractorCache
import com.aptoide.apk.injector.extractor.domain.IExtract
import com.aptoide.apk.injector.extractor.utils.Environment
import java.io.File
import java.util.Properties
import java.util.zip.ZipFile

class OemIdExtractorImpl(
  private val context: Context,
  private val extractor: IExtract,
  private val environment: Environment,
  private val extractorCache: IExtractorCache,
) : OemIdExtractor {

  companion object {
    private const val KEY = "oemid"
  }

  override fun extractOemId(packageName: String?): String? {
    return packageName?.let {
      try {
        val oemId = extractOemIdV2(it)
        check(oemId?.isNotEmpty() == true)
        oemId
      } catch (e: Throwable) {
        extractOemIdV1(it)
      }
    }
  }

  private fun extractOemIdV1(packageName: String): String? {
    return try {
      val sourceDir = getPackageName(packageName)
      val myZipFile = ZipFile(sourceDir)
      val entry = myZipFile.getEntry("META-INF/attrib")
      if (entry == null) {
        null
      } else {
        val inputStream = myZipFile.getInputStream(entry)
        val properties = Properties().also { it.load(inputStream) }
        if (properties.containsKey(KEY)) {
          properties.getProperty(KEY)
        } else {
          null
        }
      }
    } catch (e: Exception) {
      null
    }
  }

  private fun extractOemIdV2(packageName: String): String? {
    val sourceDir = getPackageName(packageName)
    val result = extractor.extract(File(sourceDir), environment, extractorCache)
    val splitResult = result.split(",")
    return if (splitResult.isEmpty()) {
      null
    } else {
      splitResult[0]
    }
  }

  private fun getPackageName(packageName: String): String {
    return context.packageManager
      .getPackageInfo(packageName, 0)
      .applicationInfo.sourceDir
  }
}
