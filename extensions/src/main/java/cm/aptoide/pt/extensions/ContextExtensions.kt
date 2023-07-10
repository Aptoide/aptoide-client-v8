package cm.aptoide.pt.extensions

import android.content.Context
import android.net.ConnectivityManager

val Context.isActiveNetworkMetered
  get() = (getSystemService(
    Context.CONNECTIVITY_SERVICE
  ) as ConnectivityManager).isActiveNetworkMetered
