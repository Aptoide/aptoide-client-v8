package com.aptoide.android.aptoidegames.feature_flags

import java.security.MessageDigest

/**
 * The percentile bucket the config server puts an install in for a given rollout salt. The client
 * never evaluates conditions - this mirrors the server so QA can predict which side of a
 * percentage rollout a device lands on.
 */
object ConfigBucket {

  private const val BUCKETS = 100L

  fun bucketOf(bucketId: String, salt: String): Int {
    val digest = MessageDigest.getInstance("SHA-256")
      .digest("$bucketId:$salt".toByteArray(Charsets.UTF_8))
    // First four bytes as an unsigned big-endian integer
    val head = digest.take(4).fold(0L) { acc, byte -> (acc shl 8) or (byte.toLong() and 0xFF) }
    return (head % BUCKETS).toInt()
  }
}
