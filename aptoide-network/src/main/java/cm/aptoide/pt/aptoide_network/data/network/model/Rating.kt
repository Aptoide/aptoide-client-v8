package cm.aptoide.pt.aptoide_network.data.network.model

data class Rating(val avg: Double, val total: Double, var votes: List<Vote>)

data class Vote(var value: Int, var count: Int)
