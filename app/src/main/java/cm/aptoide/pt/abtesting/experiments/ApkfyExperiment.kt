package cm.aptoide.pt.abtesting.experiments

import android.content.SharedPreferences
import cm.aptoide.pt.abtesting.ABTestManager
import cm.aptoide.pt.abtesting.RakamExperiment
import cm.aptoide.pt.app.AppViewAnalytics
import io.rakam.api.Rakam
import org.json.JSONException
import org.json.JSONObject
import rx.Completable
import rx.Single

open class ApkfyExperiment(private val abTestManager: ABTestManager,
                           private val appViewAnalytics: AppViewAnalytics,
                           private val sharedPreferences: SharedPreferences) : RakamExperiment() {
  private val APKFY_EXPERIMENT_GROUP = "apkfy_experiment_group"
  private val EXPERIMENT_ID = "ASV-2119-apkfy"

  private var assignment: String? = null

  fun shouldShowApkfyInterstital(): Single<Boolean> {
    return abTestManager.getExperiment(EXPERIMENT_ID, type)
        .toSingle()
        .flatMap { experiment ->
          var experimentAssignment = "default"
          if (!experiment.isExperimentOver && experiment.isPartOfExperiment) {
            experimentAssignment = experiment.assignment
          }
          when (experimentAssignment) {
            "no_ads_on_first_session" -> {
              assignment = "no_ads_on_first_session"
              return@flatMap Single.just(false)
            }
            "default", "control" -> {
              assignment = "control"
              return@flatMap Single.just(true)
            }
            else -> {
              return@flatMap Single.just(true)
            }
          }
        }
  }

  fun setSuperProperties() {
    val savedGroup = sharedPreferences.getString(APKFY_EXPERIMENT_GROUP, null)
    if (savedGroup != null) {
      setRakamSuperProperty(savedGroup)
    } else {
      setRakamSuperProperty(assignment)
    }
  }

  fun setRakamSuperProperty(group: String?) {
    group?.let { g ->
      val client = Rakam.getInstance()
      client.superProperties = createRakamSuperProperties(client.superProperties, g)
    }
  }

  private fun createRakamSuperProperties(currentProperties: JSONObject?,
                                         group: String): JSONObject {
    val superProperties = currentProperties ?: JSONObject()
    try {
      superProperties.put("asv-2119-group", group)
    } catch (e: JSONException) {
      e.printStackTrace()
    }
    return superProperties
  }

  fun recordImpression(): Completable {
    return abTestManager.getExperiment(EXPERIMENT_ID, type)
        .filter { !it.isExperimentOver && it.isPartOfExperiment }
        .toCompletable()
        .doOnCompleted {
          appViewAnalytics.sendApkfyABTestImpressionEvent(assignment)
          setSuperProperties()
          sharedPreferences.edit().putString(APKFY_EXPERIMENT_GROUP, assignment).apply()
        }
  }

}