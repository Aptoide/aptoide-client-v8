package cm.aptoide.pt.aptoide_installer.data

import android.util.Log
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
    downloadRepository.observeDownload(md5)
      .doOnNext { Log.d("lol", "getAsObservable: inside on next") }
      .doOnError { throwable ->
        Log.d("lol", "getAsObservable: got error")
        throwable.printStackTrace()
      }.subscribe {
        Log.d(
          "lol",
          "getAsObservable: emitted getasobservable"
        )
      }
    return downloadRepository.observeDownload(md5)
  }

  override fun delete(md5: String): Completable {
    return Completable.fromAction {
      downloadRepository.removeDownload(md5)
    }
  }

  override fun delete(packageName: String, versionCode: Int): Completable {
    TODO()
  }

  override fun save(download: DownloadEntity): Completable {
    return Completable.fromAction {
      downloadRepository.saveDownload(download)
    }
  }

  override fun getRunningDownloads(): Observable<List<DownloadEntity>> {
    return downloadRepository.getRunningDownloads()
      .doOnError { throwable ->
        Log.d("lol", "getRunningDownloads: error here")
        throwable.printStackTrace()
      }.doOnNext { Log.d("lol", "getRunningDownloads: emitted") }
  }

  override fun getInQueueSortedDownloads(): Observable<List<DownloadEntity>> {
    return downloadRepository.getInQueueDownloads()
  }

  override fun getAsList(md5: String?): Observable<MutableList<DownloadEntity>> {
    TODO("Not necessary delete")
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