package cm.aptoide.pt.feature_apps.domain

data class Rating(val avgRating: Double, val totalVotes: Long, val votes: List<Votes>?)

data class Votes(var value: Int, var count: Int)

