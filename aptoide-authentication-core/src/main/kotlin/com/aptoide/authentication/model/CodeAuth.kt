package com.aptoide.authentication.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CodeAuth(val type: String, val state: String, val agent: String?, val signup: Boolean,
                    val data: Data, var email: String?) {
  @JsonClass(generateAdapter = true)
  data class Data(val type: String, val method: String)
}
