package cm.aptoide.pt.aptoide_network.data.network.model

import androidx.annotation.Keep


@Keep
data class Rating(val avg: Double, val total: Long, val votes: List<Votes>?)

@Keep
data class Votes(val value: Int, val count: Int)
