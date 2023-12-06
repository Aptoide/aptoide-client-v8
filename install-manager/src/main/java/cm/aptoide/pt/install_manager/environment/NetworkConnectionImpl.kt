package cm.aptoide.pt.install_manager.environment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import cm.aptoide.pt.install_manager.environment.NetworkConnection.State
import cm.aptoide.pt.install_manager.environment.NetworkConnection.State.GONE
import cm.aptoide.pt.install_manager.environment.NetworkConnection.State.METERED
import cm.aptoide.pt.install_manager.environment.NetworkConnection.State.UNMETERED

class NetworkConnectionImpl(
  context: Context,
) : NetworkConnection {

  private lateinit var listener: (State) -> Unit

  private val networkCallback = object : ConnectivityManager.NetworkCallback() {
    override fun onCapabilitiesChanged(
      network: Network,
      networkCapabilities: NetworkCapabilities,
    ) {
      super.onCapabilitiesChanged(network, networkCapabilities)
      val unmetered =
        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
      if (unmetered) {
        listener.invoke(UNMETERED)
      } else {
        listener.invoke(METERED)
      }
    }

    override fun onLost(network: Network) {
      super.onLost(network)
      listener.invoke(GONE)
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
