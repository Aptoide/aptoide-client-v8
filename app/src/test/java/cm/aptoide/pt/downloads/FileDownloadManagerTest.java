package cm.aptoide.pt.downloads;

import cm.aptoide.pt.download.FileDownloadManager;
import cm.aptoide.pt.download.FileDownloadTask;
import cm.aptoide.pt.downloadmanager.DownloadAppFile;
import com.liulishuo.filedownloader.BaseDownloadTask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by filipegoncalves on 8/1/18.
 */
public class FileDownloadManagerTest {

  @Mock private com.liulishuo.filedownloader.FileDownloader fileDownloader;
  @Mock private FileDownloadTask fileDownloadTask;
  @Mock private BaseDownloadTask mockBaseDownloadTask;
  private DownloadAppFile emptyLinkFile;
  private DownloadAppFile apkFile;
  private FileDownloadManager fileDownloaderManager;
  private TestSubscriber<Object> testSubscriber;

  @Before public void setupAppDownloaderTest() {
    MockitoAnnotations.initMocks(this);
    emptyLinkFile =
        new DownloadAppFile("", "", "", 0, "", "noFileName", DownloadAppFile.FileType.APK);
    fileDownloaderManager =
        new FileDownloadManager(fileDownloader, fileDownloadTask, "randomDownloadsPath");
    apkFile = new DownloadAppFile("http://apkdownload.com/file/mainObb.apk", "", "appMd5", 0, "",
        "fileName", DownloadAppFile.FileType.APK);
    testSubscriber = TestSubscriber.create();
  }

  @Test public void startFileDownload() throws Exception {
    when(fileDownloader.create(any())).thenReturn(mockBaseDownloadTask);
    when(mockBaseDownloadTask.asInQueueTask()).thenReturn(new MockInqueueTask());

    fileDownloaderManager.startFileDownload("http://apkdownload.com/file/mainObb.apk", 0,
        "cm.aptoide.pt", 0, "fileName")
        .subscribe(testSubscriber);
    testSubscriber.assertNoErrors();
    testSubscriber.assertCompleted();
    verify(fileDownloader).start(fileDownloadTask, false);
  }

  @Test public void startFileDownloadEmptyLink() throws Exception {

    fileDownloaderManager.startFileDownload("", 0, "", 0, "")
        .subscribe(testSubscriber);
    testSubscriber.assertError(IllegalArgumentException.class);
    verifyZeroInteractions(fileDownloader);
  }

  @Test public void pauseDownload() throws Exception {
    fileDownloaderManager.pauseDownload(apkFile)
        .subscribe(testSubscriber);
    testSubscriber.assertNoErrors();
    testSubscriber.assertCompleted();
    verify(fileDownloader).pause(fileDownloadTask);
  }

  @Test public void removeDownloadFile() throws Exception {
    fileDownloaderManager.removeDownloadFile(apkFile)
        .subscribe(testSubscriber);
    testSubscriber.assertNoErrors();
    testSubscriber.assertCompleted();
    verify(fileDownloader).clear(0, apkFile.getMainDownloadPath());
  }
}