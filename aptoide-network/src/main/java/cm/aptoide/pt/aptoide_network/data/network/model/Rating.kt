package cm.aptoide.pt.aptoide_network.data.network.model

data class Rating(val avg: Double, val total: Long, val votes: List<Votes>?)

data class Votes(val value: Int, val count: Int)
