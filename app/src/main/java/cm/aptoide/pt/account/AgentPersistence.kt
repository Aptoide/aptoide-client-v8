package cm.aptoide.pt.account

import android.content.SharedPreferences

class AgentPersistence(private val secureSharedPreferences: SharedPreferences) {

  fun persistAgent(agent: String, state: String) {
    secureSharedPreferences.edit().putString("AGENT", agent).putString("STATE", state).apply()
  }

  fun getAgent(): Pair<String, String> {
    return Pair(secureSharedPreferences.getString("AGENT", ""),
        secureSharedPreferences.getString("STATE", ""))
  }
}
