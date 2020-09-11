package cm.aptoide.pt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.XmlResourceParser
import cm.aptoide.accountmanager.AptoideAccountManager
import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.networking.IdsRepository
import cm.aptoide.pt.root.RootAvailabilityManager
import cm.aptoide.pt.updates.UpdateRepository
import cm.aptoide.pt.util.PreferencesXmlParser
import cm.aptoide.pt.view.MainActivity
import org.xmlpull.v1.XmlPullParserException
import rx.Completable
import java.io.IOException
import java.lang.Boolean

class FirstLaunchManager(private val defaultSharedPreferences: SharedPreferences,
                         private val idsRepository: IdsRepository,
                         private val followedStoresManager: FollowedStoresManager,
                         private val updateRepository: UpdateRepository,
                         private val rootAvailabilityManager: RootAvailabilityManager,
                         private val accountManager: AptoideAccountManager,
                         private val shortcutManager: AptoideShortcutManager,
                         private val context: Context) {

  fun runFirstLaunch(): Completable {
    return Completable.mergeDelayError(setSharedPreferencesValues(), generateAptoideUuid())
        .andThen(Completable.mergeDelayError(followedStoresManager.setDefaultFollowedStores(),
            updateRepository.sync(true, false),
            rootAvailabilityManager.updateRootAvailability(),
            accountManager.updateAccount().onErrorComplete(),
            createShortcut()))
        .doOnError { e -> CrashReport.getInstance().log(e) }
  }

  private fun generateAptoideUuid(): Completable {
    return Completable.fromAction { idsRepository.getUniqueIdentifier() }
  }

  private fun setSharedPreferencesValues(): Completable {
    return Completable.fromAction {
      val preferencesXmlParser = PreferencesXmlParser()
      val parser: XmlResourceParser = context.resources.getXml(R.xml.settings)
      try {
        val parsedPrefsList =
            preferencesXmlParser.parse(parser)
        for (keyValue in parsedPrefsList) {
          defaultSharedPreferences.edit()
              .putBoolean(keyValue[0], Boolean.valueOf(keyValue[1]))
              .apply()
        }
      } catch (e: IOException) {
        e.printStackTrace()
      } catch (e: XmlPullParserException) {
        e.printStackTrace()
      }
    }
  }

  private fun createShortcut(): Completable {
    return Completable.fromAction {
      if (shortcutManager.shouldCreateShortcut()) {
        createAppShortcut()
      }
    }
  }

  private fun createAppShortcut() {
    val shortcutIntent = Intent(context, MainActivity::class.java)
    shortcutIntent.action = Intent.ACTION_MAIN
    val intent = Intent()
    intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
    intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.resources.getString(R.string.app_name))
    intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
        Intent.ShortcutIconResource.fromContext(context,
            R.mipmap.ic_launcher))
    intent.action = "com.android.launcher.action.INSTALL_SHORTCUT"
    context.sendBroadcast(intent)
  }
}