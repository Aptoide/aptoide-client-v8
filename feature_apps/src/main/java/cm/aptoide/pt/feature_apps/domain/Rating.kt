package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.aptoide_network.data.network.model.Votes

data class Rating(val avgRating: Double, val totalVotes: Long, val votes: List<Votes>)

data class Votes(var value: Int, var count: Int)

