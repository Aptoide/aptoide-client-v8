package cm.aptoide.pt.downloads_database.data

import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity
import io.reactivex.Observable
import io.reactivex.Single

interface DownloadRepository {

  fun getAllDownloads(): Observable<List<DownloadEntity>>

  fun getDownload(md5: String): Single<DownloadEntity>

  fun observeDownload(md5: String): Observable<DownloadEntity>

  fun removeDownload(md5: String)

  fun saveDownload(downloadEntity: DownloadEntity)

  fun getRunningDownloads(): Observable<List<DownloadEntity>>

  fun getInQueueDownloads(): Observable<List<DownloadEntity>>

  fun getUnmovedDownloads(): Observable<List<DownloadEntity>>
}