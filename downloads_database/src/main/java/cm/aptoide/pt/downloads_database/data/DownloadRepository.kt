package cm.aptoide.pt.downloads_database.data

import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {

  fun getAllDownloads(): Flow<List<DownloadEntity>>

  suspend fun getDownload(md5: String): DownloadEntity

  fun observeDownload(md5: String): Flow<DownloadEntity>

  fun removeDownload(md5: String)

  fun saveDownload(downloadEntity: DownloadEntity)

  fun getRunningDownloads(): Flow<List<DownloadEntity>>

  fun getInQueueDownloads(): Flow<List<DownloadEntity>>

  fun getUnmovedDownloads(): Flow<List<DownloadEntity>>
}