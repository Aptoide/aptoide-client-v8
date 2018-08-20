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
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by filipegoncalves on 7/31/18.
 */
public class AppDownloadManagerTest {

  @Mock private FileDownloader fileDownloaderApk;
  @Mock private FileDownloader fileDownloaderMainObb;
  @Mock private FileDownloader fileDownloaderPatchObb;
  @Mock private FileDownloaderProvider fileDownloaderProvider;
  private DownloadAppFile apk;
  private DownloadAppFile mainObb;
  private DownloadAppFile patchObb;
  private AppDownloadManager appDownloadManager;
  private AppDownloadManager appDownloadManagerWithObbs;
  private AppDownloadManager appDownloadManagerWithNoFiles;
  private TestSubscriber<Object> testSubscriber;

  @Before public void setupAppDownloaderTest() {
    MockitoAnnotations.initMocks(this);
    apk = new DownloadAppFile("http://apkdownload.com/file/app.apk", "", "appMd5", 123,
        "cm.aptoide.pt", "app.apk", DownloadAppFile.FileType.APK);
    mainObb = new DownloadAppFile("http://apkdownload.com/file/mainObb.apk", "", "appMd5", 123,
        "cm.aptoide.pt", "mainObb", DownloadAppFile.FileType.OBB);
    patchObb = new DownloadAppFile("http://apkdownload.com/file/patchObb.apk", "", "appMd5", 123,
        "cm.aptoide.pt", "patchObb", DownloadAppFile.FileType.OBB);

    DownloadApp appToDownload = new DownloadApp(getFilesListWithApk());
    DownloadApp appToDownloadWithObbs = new DownloadApp(getFilesListWithObbs());
    DownloadApp appToDownloadEmptyError = new DownloadApp(Collections.emptyList());
    testSubscriber = TestSubscriber.create();
    appDownloadManager = new AppDownloadManager(fileDownloaderProvider, appToDownload,
        createFileDownloaderPersistence());
    appDownloadManagerWithObbs =
        new AppDownloadManager(fileDownloaderProvider, appToDownloadWithObbs,
            createFileDownloaderPersistence());
    appDownloadManagerWithNoFiles =
        new AppDownloadManager(fileDownloaderProvider, appToDownloadEmptyError,
            createFileDownloaderPersistence());
  }

  @Test public void startAppDownloadWithOneFile() throws Exception {

    when(fileDownloaderApk.startFileDownload()).thenReturn(Completable.complete());

    when(fileDownloaderProvider.createFileDownloader(apk.getMainDownloadPath(), apk.getFileType(),
        apk.getPackageName(), apk.getVersionCode(), apk.getFileName())).thenReturn(
        fileDownloaderApk);

    appDownloadManager.startAppDownload()
        .subscribe(testSubscriber);

    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();
    verify(fileDownloaderApk).startFileDownload();
  }

  @Test public void startAppDownloadWithMultipleFiles() throws Exception {

    when(fileDownloaderProvider.createFileDownloader(apk.getMainDownloadPath(), apk.getFileType(),
        apk.getPackageName(), apk.getVersionCode(), apk.getFileName())).thenReturn(
        fileDownloaderApk);

    when(fileDownloaderProvider.createFileDownloader(mainObb.getMainDownloadPath(),
        mainObb.getFileType(), mainObb.getPackageName(), mainObb.getVersionCode(),
        mainObb.getFileName())).thenReturn(fileDownloaderMainObb);

    when(fileDownloaderProvider.createFileDownloader(patchObb.getMainDownloadPath(),
        patchObb.getFileType(), patchObb.getPackageName(), patchObb.getVersionCode(),
        patchObb.getFileName())).thenReturn(fileDownloaderPatchObb);

    when(fileDownloaderProvider.createFileDownloader(apk.getMainDownloadPath(), apk.getFileType(),
        apk.getPackageName(), apk.getVersionCode(), apk.getFileName())).thenReturn(
        fileDownloaderApk);

    when(fileDownloaderApk.startFileDownload()).thenReturn(Completable.complete());
    when(fileDownloaderMainObb.startFileDownload()).thenReturn(Completable.complete());
    when(fileDownloaderPatchObb.startFileDownload()).thenReturn(Completable.complete());

    appDownloadManagerWithObbs.startAppDownload()
        .subscribe(testSubscriber);
    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();
    verify(fileDownloaderApk).startFileDownload();
    verify(fileDownloaderMainObb).startFileDownload();
    verify(fileDownloaderPatchObb).startFileDownload();
  }

  @Test public void startAppDownloadWithNoFiles() throws Exception {
    appDownloadManagerWithNoFiles.startAppDownload()
        .subscribe(testSubscriber);
    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();
    verifyZeroInteractions(fileDownloaderApk);
    verifyZeroInteractions(fileDownloaderMainObb);
    verifyZeroInteractions(fileDownloaderPatchObb);
  }

  @Test public void pauseAppDownloadWithOneFile() throws Exception {

    when(fileDownloaderProvider.createFileDownloader(apk.getMainDownloadPath(), apk.getFileType(),
        apk.getPackageName(), apk.getVersionCode(), apk.getFileName())).thenReturn(
        fileDownloaderApk);

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

  private HashMap<String, FileDownloader> createFileDownloaderPersistence() {
    HashMap<String, FileDownloader> persistence = new HashMap<>();
    persistence.put("http://apkdownload.com/file/app.apk", fileDownloaderApk);
    persistence.put("http://apkdownload.com/file/mainObb.apk", fileDownloaderMainObb);
    persistence.put("http://apkdownload.com/file/patchObb.apk", fileDownloaderPatchObb);

    return persistence;
  }
}