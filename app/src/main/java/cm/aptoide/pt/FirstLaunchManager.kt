package cm.aptoide.pt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.content.res.XmlResourceParser
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import cm.aptoide.accountmanager.AptoideAccountManager
import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.networking.IdsRepository
import cm.aptoide.pt.root.RootAvailabilityManager
import cm.aptoide.pt.util.PreferencesXmlParser
import cm.aptoide.pt.view.MainActivity
import org.xmlpull.v1.XmlPullParserException
import rx.Completable
import java.io.IOException

class FirstLaunchManager(
    private val defaultSharedPreferences: SharedPreferences,
    private val idsRepository: IdsRepository,
    private val followedStoresManager: FollowedStoresManager,
    private val rootAvailabilityManager: RootAvailabilityManager,
    private val accountManager: AptoideAccountManager,
    private val shortcutManager: AptoideShortcutManager,
    private val context: Context
) {

    fun runFirstLaunch(): Completable {
        return Completable.mergeDelayError(setSharedPreferencesValues(), generateAptoideUuid())
            .andThen(
                Completable.mergeDelayError(
                    followedStoresManager.setDefaultFollowedStores(),
                    rootAvailabilityManager.updateRootAvailability(),
                    accountManager.updateAccount().onErrorComplete(),
                    createShortcut()
                )
            )
            .doOnError { e -> CrashReport.getInstance().log(e) }
    }

    private fun generateAptoideUuid(): Completable {
        return Completable.fromAction { idsRepository.uniqueIdentifier }
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
                        .putBoolean(keyValue[0], java.lang.Boolean.valueOf(keyValue[1]))
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
                if (Build.VERSION.SDK_INT < 26) {
                    createAppShortcut()
                }
            }
        }
    }

    private fun createAppShortcut() {
        val shortcutIntent = Intent(context, MainActivity::class.java)
        shortcutIntent.action = Intent.ACTION_MAIN
        val intent = Intent()
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.resources.getString(R.string.app_name))
        intent.putExtra(
            Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
            Intent.ShortcutIconResource.fromContext(
                context,
                R.mipmap.ic_launcher
            )
        )
        intent.action = "com.android.launcher.action.INSTALL_SHORTCUT"
        context.sendBroadcast(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createAppShortcut(context: Context) {
        val shortcutIntent = Intent(context, MainActivity::class.java)
        shortcutIntent.action = Intent.ACTION_MAIN

        val shortcutInfo = ShortcutInfo.Builder(context, "shortcut_id")
            .setIntent(shortcutIntent)
            .setShortLabel(context.resources.getString(R.string.app_name))
            .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))
            .build()

        val shortcutManager = context.getSystemService(ShortcutManager::class.java)
        if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported) {
            // Check if the shortcut is already added
            if (!isShortcutAdded(context, shortcutInfo)) {
                // Request to pin the shortcut
                shortcutManager.requestPinShortcut(shortcutInfo, null)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun isShortcutAdded(context: Context, shortcutInfo: ShortcutInfo): Boolean {
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)
        if (shortcutManager != null) {
            val pinnedShortcuts = shortcutManager.pinnedShortcuts
            for (info in pinnedShortcuts) {
                if (info.id == shortcutInfo.id) {
                    return true
                }
            }
        }
        return false
    }
}