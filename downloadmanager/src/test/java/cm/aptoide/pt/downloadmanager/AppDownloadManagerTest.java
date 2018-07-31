package cm.aptoide.pt.downloadmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Completable;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by filipegoncalves on 7/31/18.
 */
public class AppDownloadManagerTest {

  @Mock FileDownloader fileDownloader;
  private DownloadApp appToDownload;
  private DownloadApp appToDownloadWithObbs;
  private DownloadApp appToDownloadEmptyError;
  private DownloadAppFile apk;
  private DownloadAppFile mainObb;
  private DownloadAppFile patchObb;
  private AppDownloadManager appDownloadManager;
  private TestSubscriber<Object> testSubscriber;

  @Before public void setupAppDownloaderTest() {
    MockitoAnnotations.initMocks(this);
    appDownloadManager = new AppDownloadManager(fileDownloader);
    apk = new DownloadAppFile("http://apkdownload.com/file/app.apk", "appMd5");
    mainObb = new DownloadAppFile("http://apkdownload.com/file/mainObb.apk", "appMd5");
    patchObb = new DownloadAppFile("http://apkdownload.com/file/patchObb.apk", "appMd5");
    appToDownload = new DownloadApp(getFilesListWithApk());
    appToDownloadWithObbs = new DownloadApp(getFilesListWithObbs());
    appToDownloadEmptyError = new DownloadApp(Collections.emptyList());
    testSubscriber = TestSubscriber.create();
  }

  @Test public void startAppDownloadWithOneFile() throws Exception {

    when(fileDownloader.startFileDownload(apk)).thenReturn(Completable.complete());

    appDownloadManager.startAppDownload(appToDownload)
        .subscribe(testSubscriber);

    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();
    verify(fileDownloader).startFileDownload(apk);
  }

  @Test public void startAppDownloadWithMoreThanOneFile() throws Exception {
    when(fileDownloader.startFileDownload(apk)).thenReturn(Completable.complete());
    when(fileDownloader.startFileDownload(mainObb)).thenReturn(Completable.complete());
    when(fileDownloader.startFileDownload(patchObb)).thenReturn(Completable.complete());

    appDownloadManager.startAppDownload(appToDownloadWithObbs)
        .subscribe(testSubscriber);
    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();
    verify(fileDownloader).startFileDownload(apk);
    verify(fileDownloader).startFileDownload(mainObb);
    verify(fileDownloader).startFileDownload(patchObb);
  }

  @Test public void startAppDownloadWithNoFiles() throws Exception {
    appDownloadManager.startAppDownload(appToDownloadEmptyError)
        .subscribe();
    verifyZeroInteractions(fileDownloader);
  }

  @Test public void pauseAppDownload() throws Exception {
  }

  @Test public void getAppDownload() throws Exception {
  }

  @Test public void getCurrentActiveDownload() throws Exception {
  }

  @Test public void removeDownload() throws Exception {
  }

  private List<DownloadAppFile> getFilesListWithApk() {
    List<DownloadAppFile> appFileList = new ArrayList<>();
    appFileList.add(apk);
    return appFileList;
  }

  private List<DownloadAppFile> getFilesListWithObbs() {
    List<DownloadAppFile> appFileList = new ArrayList<>();
    appFileList.add(apk);
    appFileList.add(mainObb);
    appFileList.add(patchObb);
    return appFileList;
  }
}