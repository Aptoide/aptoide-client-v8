package cm.aptoide.pt.downloads_database.data

import cm.aptoide.pt.downloads_database.data.database.DownloadDao
import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity
import kotlinx.coroutines.flow.Flow

class AptoideDownloadRepository(private val downloadDao: DownloadDao) : DownloadRepository {

  override fun getAllDownloads(): Flow<List<DownloadEntity>> {
    return downloadDao.getAllDownloads()
  }

  override suspend fun getDownload(md5: String): DownloadEntity {
    return downloadDao.getDownload(md5)
  }

  override fun observeDownload(md5: String): Flow<DownloadEntity> {
    return downloadDao.observeDownload(md5)
  }

  override fun removeDownload(md5: String) {
    downloadDao.removeDownload(md5)
  }

  override fun saveDownload(downloadEntity: DownloadEntity) {
    downloadDao.saveDownload(downloadEntity)
  }

  override fun getRunningDownloads(): Flow<List<DownloadEntity>> {
    return downloadDao.getRunningDownloads()
  }

  override fun getInQueueDownloads(): Flow<List<DownloadEntity>> {
    return downloadDao.getInQueueDownloads()
  }

  override fun getUnmovedDownloads(): Flow<List<DownloadEntity>> {
    return downloadDao.getUnmovedDownloads()
  }
}