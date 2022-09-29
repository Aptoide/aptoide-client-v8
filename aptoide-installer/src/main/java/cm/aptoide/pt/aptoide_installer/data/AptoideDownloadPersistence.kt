package cm.aptoide.pt.aptoide_installer.data

import cm.aptoide.pt.downloadmanager.DownloadPersistence
import cm.aptoide.pt.downloads_database.data.DownloadRepository
import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class AptoideDownloadPersistence(private val downloadRepository: DownloadRepository) :
  DownloadPersistence {

  override fun getAll(): Observable<List<DownloadEntity>> {
    return downloadRepository.getAllDownloads()
  }

  override fun getAsSingle(md5: String): Single<DownloadEntity> {
    return downloadRepository.getDownload(md5)
      .doOnError { throwable -> throwable.printStackTrace() }
  }

  override fun getAsObservable(md5: String): Observable<DownloadEntity> {
    return downloadRepository.observeDownload(md5)
  }

  override fun delete(md5: String): Completable {
    return Completable.fromAction {
      downloadRepository.removeDownload(md5)
    }
  }

  override fun save(download: DownloadEntity): Completable {
    return Completable.fromAction {
      downloadRepository.saveDownload(download)
    }
  }

  override fun getRunningDownloads(): Observable<List<DownloadEntity>> {
    return downloadRepository.getRunningDownloads()
      .doOnError { throwable ->
        throwable.printStackTrace()
      }
  }

  override fun getInQueueSortedDownloads(): Observable<List<DownloadEntity>> {
    return downloadRepository.getInQueueDownloads()
  }

  override fun getUnmovedFilesDownloads(): Observable<List<DownloadEntity>> {
    return downloadRepository.getUnmovedDownloads()
  }

  override fun getOutOfSpaceDownloads(): Observable<MutableList<DownloadEntity>> {
    TODO("Not yet implemented")
  }

  override fun getCompletedDownload(packageName: String): Observable<DownloadEntity> {
    return downloadRepository.getCompletedDownload(packageName)
  }
}