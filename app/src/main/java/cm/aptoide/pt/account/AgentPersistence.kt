package cm.aptoide.pt.account

import android.content.SharedPreferences

open class AgentPersistence(private val secureSharedPreferences: SharedPreferences) {

  fun persistAgent(agent: String, state: String, email: String?) {
    secureSharedPreferences.edit().putString("AGENT", agent).putString("STATE", state)
        .putString("EMAIL", email).apply()
  }

  fun getAgent(): String? {
    return secureSharedPreferences.getString("AGENT", "")
  }

  fun getState(): String? {
    return secureSharedPreferences.getString("STATE", "")
  }

  fun getEmail(): String? {
    return secureSharedPreferences.getString("EMAIL", "")
  }

}
