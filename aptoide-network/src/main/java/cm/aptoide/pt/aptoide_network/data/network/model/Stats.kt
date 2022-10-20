package cm.aptoide.pt.aptoide_network.data.network.model

import androidx.annotation.Keep

@Keep
data class Stats(val downloads: Int, val pdownloads: Int, val rating: Rating, val prating: Rating)
