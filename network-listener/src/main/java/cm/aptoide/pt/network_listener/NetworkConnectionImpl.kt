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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectionImpl @Inject constructor(
  @ApplicationContext private val context: Context,
) : NetworkConnection {

  override val state: State
    get() = context.networkState

  var listener: ((State) -> Unit)? = null

  private val _states = MutableStateFlow(value = context.networkState)

  val states: Flow<State> get() = _states

  private val networkCallback = object : ConnectivityManager.NetworkCallback() {
    override fun onCapabilitiesChanged(
      network: Network,
      networkCapabilities: NetworkCapabilities,
    ) {
      super.onCapabilitiesChanged(network, networkCapabilities)
      if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)) {
          listener?.invoke(UNMETERED)
          _states.tryEmit(UNMETERED)
          WifiWorker.cancel(context)
        } else {
          listener?.invoke(METERED)
          _states.tryEmit(METERED)
          WifiWorker.enqueue(context)
        }
      }
    }

    override fun onLost(network: Network) {
      super.onLost(network)
      listener?.invoke(GONE)
      _states.tryEmit(GONE)
    }
  }

  init {
    (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
      .registerDefaultNetworkCallback(networkCallback)
  }

  override fun setOnChangeListener(onChange: (State) -> Unit) {
    listener = onChange
  }
}

val Context.networkState
  get() = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
    .run { getNetworkCapabilities(activeNetwork) }
    ?.takeIf { it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) }
    ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
    ?.let { if (it) UNMETERED else METERED }
    ?: GONE
