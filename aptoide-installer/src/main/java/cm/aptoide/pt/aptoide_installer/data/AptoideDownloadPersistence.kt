package cm.aptoide.pt.aptoide_installer.data

import cm.aptoide.pt.downloadmanager.DownloadPersistence
import cm.aptoide.pt.downloads_database.data.DownloadRepository
import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.rx2.rxCompletable
import kotlinx.coroutines.rx2.rxObservable
import kotlinx.coroutines.rx2.rxSingle

class AptoideDownloadPersistence(private val downloadRepository: DownloadRepository) :
  DownloadPersistence {
  override fun getAll(): Observable<MutableList<DownloadEntity>> {
    return rxObservable { downloadRepository.getAllDownloads() }
  }

  override fun getAsSingle(md5: String): Single<DownloadEntity> {
    return rxSingle { downloadRepository.getDownload(md5) }
  }

  override fun getAsObservable(md5: String): Observable<DownloadEntity> {
    return rxObservable { downloadRepository.observeDownload(md5) }
  }

  override fun delete(md5: String): Completable {
    return rxCompletable { downloadRepository.removeDownload(md5) }
  }

  override fun delete(packageName: String, versionCode: Int): Completable {
    TODO()
  }

  override fun save(download: DownloadEntity): Completable {
    return rxCompletable { downloadRepository.saveDownload(download) }
  }

  override fun getRunningDownloads(): Observable<MutableList<DownloadEntity>> {
    return rxObservable { downloadRepository.getRunningDownloads() }
  }

  override fun getInQueueSortedDownloads(): Observable<MutableList<DownloadEntity>> {
    return rxObservable { downloadRepository.getInQueueDownloads() }
  }

  override fun getAsList(md5: String?): Observable<MutableList<DownloadEntity>> {
    TODO()
  }

  override fun getUnmovedFilesDownloads(): Observable<MutableList<DownloadEntity>> {
    return rxObservable { downloadRepository.getUnmovedDownloads() }
  }

  override fun getOutOfSpaceDownloads(): Observable<MutableList<DownloadEntity>> {
    TODO("Not yet implemented")
  }
}