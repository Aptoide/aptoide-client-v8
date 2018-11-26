package cm.aptoide.pt.downloadmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Completable;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

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
        new DownloadApp(packageName, versionCode, getFilesListWithApk(), "md5Apk");
    DownloadApp appToDownloadWithObbs =
        new DownloadApp(packageName, versionCode, getFilesListWithObbs(), "md5WithObb");
    DownloadApp appToDownloadEmptyError =
        new DownloadApp(packageName, versionCode, Collections.emptyList(), "md5Empty");
    testSubscriber = TestSubscriber.create();

    appDownloadManager = new AppDownloadManager(new RetryFileDownloaderProvider() {
      @Override
      public RetryFileDownloader createRetryFileDownloader(String md5, String mainDownloadPath,
          int fileType, String packageName, int versionCode, String fileName,
          PublishSubject<FileDownloadCallback> fileDownloadCallback, String alternativeLink) {
        return fileDownloaderApk;
      }
    }, appToDownload, createFileDownloaderPersistence(), downloadErrorAnalytics,
        downloadCompleteAnalytics);

    appDownloadManagerWithObbs = new AppDownloadManager(new RetryFileDownloaderProvider() {
      @Override
      public RetryFileDownloader createRetryFileDownloader(String md5, String mainDownloadPath,
          int fileType, String packageName, int versionCode, String fileName,
          PublishSubject<FileDownloadCallback> fileDownloadCallback, String alternativeLink) {
        return fileDownloaderApk;
      }
    }, appToDownloadWithObbs, createFileDownloaderPersistence(), downloadErrorAnalytics,
        downloadCompleteAnalytics);

    appDownloadManagerWithNoFiles = new AppDownloadManager(new RetryFileDownloaderProvider() {
      @Override
      public RetryFileDownloader createRetryFileDownloader(String md5, String mainDownloadPath,
          int fileType, String packageName, int versionCode, String fileName,
          PublishSubject<FileDownloadCallback> fileDownloadCallback, String alternativeLink) {
        return fileDownloaderApk;
      }
    }, appToDownloadEmptyError, createFileDownloaderPersistence(), downloadErrorAnalytics,
        downloadCompleteAnalytics);
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

  @Test public void pauseAppDownloadWithOneFile() throws Exception {

    PublishSubject<FileDownloadCallback> fileDownloadCallbackPublishSubjectEmpty =
        PublishSubject.create();

    when(fileDownloaderProvider.createRetryFileDownloader(apk.getDownloadMd5(),
        apk.getMainDownloadPath(), apk.getFileType(), apk.getPackageName(), apk.getVersionCode(),
        apk.getFileName(), fileDownloadCallbackPublishSubjectEmpty,
        apk.getAlternativeDownloadPath())).thenReturn(fileDownloaderApk);

    when(fileDownloaderApk.pauseDownload()).thenReturn(Completable.complete());

    appDownloadManager.pauseAppDownload()
        .subscribe(testSubscriber);

    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();
    verify(fileDownloaderApk).pauseDownload();
  }

  @Test public void pauseAppDownloadWithMultipleFiles() throws Exception {

    when(fileDownloaderApk.pauseDownload()).thenReturn(Completable.complete());
    when(fileDownloaderMainObb.pauseDownload()).thenReturn(Completable.complete());
    when(fileDownloaderPatchObb.pauseDownload()).thenReturn(Completable.complete());

    appDownloadManagerWithObbs.pauseAppDownload()
        .subscribe(testSubscriber);

    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();
    verify(fileDownloaderApk).pauseDownload();
    verify(fileDownloaderMainObb).pauseDownload();
    verify(fileDownloaderPatchObb).pauseDownload();
  }

  @Test public void pauseAppDownloadWithNoFiles() throws Exception {
    appDownloadManagerWithNoFiles.pauseAppDownload()
        .subscribe(testSubscriber);

    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();
    verifyZeroInteractions(fileDownloaderApk);
    verifyZeroInteractions(fileDownloaderMainObb);
    verifyZeroInteractions(fileDownloaderPatchObb);
  }

  @Test public void removeDownloadWithOneFile() throws Exception {

    when(fileDownloaderApk.removeDownloadFile()).thenReturn(Completable.complete());

    appDownloadManager.removeAppDownload()
        .subscribe(testSubscriber);

    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();
    verify(fileDownloaderApk).removeDownloadFile();
  }

  @Test public void removeDownloadWithMultipleFiles() throws Exception {

    when(fileDownloaderApk.removeDownloadFile()).thenReturn(Completable.complete());
    when(fileDownloaderMainObb.removeDownloadFile()).thenReturn(Completable.complete());
    when(fileDownloaderPatchObb.removeDownloadFile()).thenReturn(Completable.complete());

    appDownloadManagerWithObbs.removeAppDownload()
        .subscribe(testSubscriber);

    verify(fileDownloaderApk).removeDownloadFile();
    verify(fileDownloaderMainObb).removeDownloadFile();
    verify(fileDownloaderPatchObb).removeDownloadFile();
    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();
  }

  @Test public void removeDownloadWithNoFiles() throws Exception {
    when(fileDownloaderApk.removeDownloadFile()).thenReturn(Completable.complete());

    appDownloadManagerWithNoFiles.removeAppDownload()
        .subscribe(testSubscriber);

    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();
    verifyZeroInteractions(fileDownloaderApk);
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