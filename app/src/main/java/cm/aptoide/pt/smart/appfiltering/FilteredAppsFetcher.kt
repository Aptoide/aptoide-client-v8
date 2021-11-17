package cm.aptoide.pt.smart.appfiltering

import android.content.Context
import android.os.Build
import android.util.Log
import android.util.Pair
import cm.aptoide.pt.dataprovider.model.smart.*
import cm.aptoide.pt.dataprovider.ws.smart.SMARTAppsFilter
import cm.aptoide.pt.store.view.my.SMARTStore
import okhttp3.OkHttpClient
import rx.Observable
import rx.Subscription
import rx.schedulers.Schedulers
import java.util.Collections.emptyList
import com.fasterxml.jackson.databind.ObjectMapper

class FilteredAppsFetcher(httpClient: OkHttpClient, private val context: Context) {
    private companion object {
        const val TAG = "FilteredAppsFetcher"
        const val JSON = "filtered_apps.json"
    }

    private var subscription: Subscription? = null
    private val appsFilterClient = SMARTAppsFilter(httpClient)
    private val emptyAppsLists: Pair<List<FilteredApp>, List<RemovedApp>>
        get() = Pair.create(emptyList(), emptyList())

    fun populateFilteredAppsAsync() {

        subscription?.unsubscribe()
        // Only read from the online file (i.e. the appsFilterClient.observe() call) for production builds.
        // Use the local asset (i.e. getLocalfilteredAppsFromJson()) if you want to do some dev work, but don't
        // check it in. As it stands, we only have one list for filtered apps & one for removed apps, so if we try
        // to include the local asset + the online file we'll wind up overwriting one with the other -
        // the last file read will "win". Trying to merge the two lists would also be problematic since we're
        // likely to update the online one more often, which would then be out of sync with the local asset.
        // So we could wind up overwriting the newer file with this one.

        subscription = appsFilterClient.observe()
        //subscription = getLocalFilteredAppsFromJson()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { dto -> dto.asEntity() }
                .subscribe({ pair ->
                    Log.d(TAG, "Received $pair")
                    populateLists(pair)
                }, { throwable ->
                    Log.e(TAG, "Exception has occurred!", throwable)
                })
    }

    fun unsubscribe() {
        subscription?.unsubscribe()
    }

    private fun populateLists(pair: Pair<List<AppToRemove>, List<AppToRemove>>) {
        runCatching {
            FilteringList.populateFilteringList(pair.first)
            FilteringList.populateRemovingList(pair.second)
        }
    }

    private fun FilteredAppsDto?.asEntity() = if (this == null) {
        Observable.fromCallable { emptyAppsLists }
    } else {
        Observable.from(stores)
                .filter { store -> store.storeName == SMARTStore.STORE_NAME }
                .map { store ->
                    Observable.from(store.platforms)
                            .filter { platform -> Build.MODEL == platform.platform }
                            .map { platform -> Pair.create(platform.filtered, platform.removed) }
                            .toBlocking().singleOrDefault(emptyAppsLists)
                }
    }.map { pair ->
        val filtered = pair.first.map { AppToRemove(it.pkg, it.version) }
        val removed = pair.second.map { AppToRemove(it.pkg, "") }
        return@map Pair.create(filtered, removed)
    }

    private fun getLocalFilteredAppsFromJson() = Observable.fromCallable { loadJsonFromAsset() }
            .subscribeOn(Schedulers.io())
            .map { ObjectMapper().readValue(it, FilteredAppsDto::class.java) }

    private fun loadJsonFromAsset() = context.assets.open(JSON)
            .runCatching {
                val size: Int = available()
                val buffer = ByteArray(size)
                read(buffer)
                close()
                return@runCatching String(buffer, Charsets.UTF_8)
            }.fold({
                return@fold it
            }, {
                Log.e(TAG, "Cannot load $JSON", it)
                return@fold ""
            })

}