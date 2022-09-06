package cm.aptoide.pt.downloadmanager;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by filipegoncalves on 7/31/18.
 */
public class AppDownloadManagerTest {

  @Mock private RetryFileDownloader fileDownloaderApk;
  @Mock private RetryFileDownloader fileDownloaderMainObb;
  @Mock private RetryFileDownloader fileDownloaderPatchObb;
  @Mock private RetryFileDownloaderProvider fileDownloaderProvider;
  @Mock private FileDownloadCallback fileDownloadCallback;
  @Mock private DownloadAnalytics downloadAnalytics;
  private DownloadAppFile apk;
  private DownloadAppFile mainObb;
  private DownloadAppFile patchObb;
  private AppDownloadManager appDownloadManager;
  private AppDownloadManager appDownloadManagerWithObbs;
  private AppDownloadManager appDownloadManagerWithNoFiles;
  private TestSubscriber<Object> testSubscriber;

  @Before public void setupAppDownloaderTest() {
    MockitoAnnotations.initMocks(this);
    apk = new DownloadAppFile("http://apkdownload.com/file/app.apk", "", "apkMd5", 123,
        "cm.aptoide.pt", "app.apk", DownloadAppFile.FileType.APK);
    mainObb = new DownloadAppFile("http://apkdownload.com/file/mainObb.apk", "", "mainObbMd5", 123,
        "cm.aptoide.pt", "mainObb", DownloadAppFile.FileType.OBB);
    patchObb =
        new DownloadAppFile("http://apkdownload.com/file/patchObb.apk", "", "patchObbMd5", 123,
            "cm.aptoide.pt", "patchObb", DownloadAppFile.FileType.OBB);

    DownloadApp appToDownload =
        new DownloadApp("cm.aptoide.pt", 9005, getFilesListWithApk(), "md5Apk", 1231123,
            "jonenzoemid");
    DownloadApp appToDownloadWithObbs =
        new DownloadApp("cm.aptoide.pt", 9005, getFilesListWithObbs(), "md5WithObb", 12313,
            "jonenzoemid");
    DownloadApp appToDownloadEmptyError =
        new DownloadApp("cm.aptoide.pt", 9005, Collections.emptyList(), "md5Empty", 123133,
            "jonenzoemid");
    testSubscriber = TestSubscriber.create();

    appDownloadManager = new AppDownloadManager(
        (md5, mainDownloadPath, fileType, packageName, versionCode, fileName, fileDownloadCallback, alternativeLink, attributionId) -> fileDownloaderApk, appToDownload, createFileDownloaderPersistence());

    appDownloadManagerWithObbs = new AppDownloadManager(
        (md5, mainDownloadPath, fileType, packageName, versionCode, fileName, fileDownloadCallback, alternativeLink, attributionId) -> fileDownloaderApk, appToDownloadWithObbs, createFileDownloaderPersistence());

    appDownloadManagerWithNoFiles = new AppDownloadManager(
        (md5, mainDownloadPath, fileType, packageName, versionCode, fileName, fileDownloadCallback, alternativeLink, attributionId) -> fileDownloaderApk, appToDownloadEmptyError, createFileDownloaderPersistence());
  }

  @Test public void startAppDownloadWithOneFile() throws Exception {
    when(fileDownloaderApk.observeFileDownloadProgress()).thenReturn(
        Observable.just(fileDownloadCallback));

    appDownloadManager.startAppDownload();

    verify(fileDownloaderApk, times(1)).startFileDownload();
  }

  @Test public void startAppDownloadWithMultipleFiles() throws Exception {
    when(fileDownloaderApk.observeFileDownloadProgress()).thenReturn(
        Observable.just(fileDownloadCallback));

    appDownloadManagerWithObbs.startAppDownload();

    verify(fileDownloaderApk, times(3)).startFileDownload();
  }

  @Test public void startAppDownloadWithNoFiles() throws Exception {
    appDownloadManagerWithNoFiles.startAppDownload();

    verifyZeroInteractions(fileDownloaderApk);
    verifyZeroInteractions(fileDownloaderMainObb);
    verifyZeroInteractions(fileDownloaderPatchObb);
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

  private HashMap<String, RetryFileDownloader> createFileDownloaderPersistence() {
    HashMap<String, RetryFileDownloader> persistence = new HashMap<>();
    persistence.put("http://apkdownload.com/file/app.apk", fileDownloaderApk);
    persistence.put("http://apkdownload.com/file/mainObb.apk", fileDownloaderMainObb);
    persistence.put("http://apkdownload.com/file/patchObb.apk", fileDownloaderPatchObb);

    return persistence;
  }
}