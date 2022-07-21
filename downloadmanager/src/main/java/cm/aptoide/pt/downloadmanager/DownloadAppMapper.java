package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity;
import java.util.List;

/**
 * Created by filipegoncalves on 9/12/18.
 */

public class DownloadAppMapper {

  private final DownloadAppFileMapper downloadAppFileMapper;

  public DownloadAppMapper(DownloadAppFileMapper downloadAppFileMapper) {
    this.downloadAppFileMapper = downloadAppFileMapper;
  }

  public DownloadApp mapDownload(DownloadEntity download) {
    List<DownloadAppFile> fileList =
        downloadAppFileMapper.mapFileToDownloadList(download.getFilesToDownload());
    return new DownloadApp(download.getPackageName(), download.getVersionCode(), fileList,
        download.getMd5(), download.getSize(), download.getAttributionId());
  }
}
