package cm.aptoide.pt.downloads_database.data

import android.util.Log
import cm.aptoide.pt.downloads_database.data.database.DownloadDao
import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity
import io.reactivex.Observable
import io.reactivex.Single

class AptoideDownloadRepository(private val downloadDao: DownloadDao) : DownloadRepository {

  override fun getAllDownloads(): Observable<List<DownloadEntity>> {
    return downloadDao.getAllDownloads()
  }

  override fun getDownload(md5: String): Single<DownloadEntity> {
    return downloadDao.getDownload(md5)
  }

  override fun observeDownload(md5: String): Observable<DownloadEntity> {
    return downloadDao.observeDownload(md5)
  }

  override fun removeDownload(md5: String) {
    downloadDao.removeDownload(md5)
  }

  override fun saveDownload(downloadEntity: DownloadEntity) {
    downloadDao.saveDownload(downloadEntity)
    Log.d("lol", "saveDownload: " + downloadEntity.overallDownloadStatus)
  }

  override fun getRunningDownloads(): Observable<List<DownloadEntity>> {
    return downloadDao.getRunningDownloads().doOnError { throwable ->
      Log.d("lol", "getRunningDownloads: error on getting from db")
      throwable.printStackTrace()
    }.doOnNext { list -> Log.d("lol", "getRunningDownloads: emitting " + list.size) }
  }

  override fun getInQueueDownloads(): Observable<List<DownloadEntity>> {
    return downloadDao.getInQueueDownloads()
  }

  override fun getUnmovedDownloads(): Observable<List<DownloadEntity>> {
    return downloadDao.getUnmovedDownloads()
  }
}