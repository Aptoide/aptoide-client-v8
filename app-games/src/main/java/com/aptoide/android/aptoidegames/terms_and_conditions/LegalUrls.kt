package com.aptoide.android.aptoidegames.terms_and_conditions

import android.content.Context
import android.net.Uri
import com.aptoide.android.aptoidegames.BuildConfig

val Context.tcUrl get() = BuildConfig.TC_URL.addLanguage(this)

val Context.ppUrl get() = BuildConfig.PP_URL.addLanguage(this)

val paymentsTermsOfUseUrl get() = ""

private fun String.addLanguage(context: Context): String {
  val resources = context.resources
  return Uri.parse(this).buildUpon().appendQueryParameter(
    "lang", resources.configuration.locales.get(0).language
      + "-"
      + resources.configuration.locales.get(0).country
  ).build().toString()
}
