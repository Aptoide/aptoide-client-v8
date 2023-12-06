package cm.aptoide.pt.network_listener

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import cm.aptoide.pt.install_manager.environment.NetworkConnection.State
import cm.aptoide.pt.install_manager.environment.NetworkConnection.State.GONE
import cm.aptoide.pt.install_manager.environment.NetworkConnection.State.METERED
import cm.aptoide.pt.install_manager.environment.NetworkConnection.State.UNMETERED

class NetworkConnectionImpl(
  private val context: Context,
) : NetworkConnection {

  override val state: State
    get() = context.networkState

  var listener: ((State) -> Unit)? = null

  private val networkCallback = object : ConnectivityManager.NetworkCallback() {
    override fun onCapabilitiesChanged(
      network: Network,
      networkCapabilities: NetworkCapabilities,
    ) {
      super.onCapabilitiesChanged(network, networkCapabilities)
      if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)) {
        listener?.invoke(UNMETERED)
        WifiWorker.cancel(context)
      } else {
        listener?.invoke(METERED)
        WifiWorker.enqueue(context)
      }
    }

    override fun onLost(network: Network) {
      super.onLost(network)
      listener?.invoke(GONE)
    }
  }

  init {
    (context.getSystemService(
      Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager).registerDefaultNetworkCallback(networkCallback)
  }

  override fun setOnChangeListener(onChange: (State) -> Unit) {
    listener = onChange
  }
}

val Context.networkState
  get() = (getSystemService(
    Context.CONNECTIVITY_SERVICE
  ) as ConnectivityManager).run {
    getNetworkCapabilities(activeNetwork)
      ?.takeIf { it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) }
      ?.let {
        if (it.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)) {
          UNMETERED
        } else {
          METERED
        }
      } ?: GONE
  }
