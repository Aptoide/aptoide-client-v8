package com.aptoide.authentication.model

data class CodeAuth(val type: String, val state: String?, val agent: String?, val signup: Boolean,
                    val data: Data) {
  data class Data(val type: String, val method: String)
}
