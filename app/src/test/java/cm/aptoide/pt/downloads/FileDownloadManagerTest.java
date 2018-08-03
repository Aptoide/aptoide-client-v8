package cm.aptoide.pt.downloads;

import cm.aptoide.pt.download.FileDownloadManager;
import cm.aptoide.pt.download.FileDownloadTask;
import cm.aptoide.pt.downloadmanager.DownloadAppFile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Created by filipegoncalves on 8/1/18.
 */
public class FileDownloadManagerTest {

  @Mock com.liulishuo.filedownloader.FileDownloader fileDownloader;
  @Mock FileDownloadTask fileDownloadTask;
  private DownloadAppFile emptyLinkFile;
  private DownloadAppFile apkFile;
  private FileDownloadManager fileDownloaderManager;
  private TestSubscriber<Object> testSubscriber;

  @Before public void setupAppDownloaderTest() {
    MockitoAnnotations.initMocks(this);
    emptyLinkFile = new DownloadAppFile("", "", "", 0, "");
    fileDownloaderManager = new FileDownloadManager(fileDownloader, fileDownloadTask);
    apkFile = new DownloadAppFile("http://apkdownload.com/file/mainObb.apk", "", "appMd5", 0, "");
    testSubscriber = TestSubscriber.create();
  }

  @Test public void startFileDownload() throws Exception {
    fileDownloaderManager.startFileDownload(apkFile)
        .subscribe(testSubscriber);
    testSubscriber.assertNoErrors();
    testSubscriber.assertCompleted();
    verify(fileDownloader).start(fileDownloadTask, false);
  }

  @Test public void startFileDownloadEmptyLink() throws Exception {

    fileDownloaderManager.startFileDownload(emptyLinkFile)
        .subscribe(testSubscriber);
    testSubscriber.assertError(IllegalArgumentException.class);
    verifyZeroInteractions(fileDownloader);
  }

  @Test public void pauseDownload() throws Exception {
  }

  @Test public void removeDownloadFile() throws Exception {
  }
}