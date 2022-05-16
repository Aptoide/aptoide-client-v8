package cm.aptoide.pt.aptoide_network.data.network.model

data class Rating(val avg: Double, val total: Long, var votes: List<Votes>)

data class Votes(var value: Int, var count: Int)
