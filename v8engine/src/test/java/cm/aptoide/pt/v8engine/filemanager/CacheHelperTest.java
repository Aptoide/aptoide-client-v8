package cm.aptoide.pt.v8engine.filemanager;

import cm.aptoide.pt.utils.FileUtils;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import rx.observers.TestSubscriber;

import static cm.aptoide.pt.v8engine.filemanager.CacheHelper.VALUE_TO_CONVERT_MB_TO_BYTES;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by trinkes on 11/14/16.
 */

public class CacheHelperTest {
  private CacheHelper cacheHelper;

  @Test public void shouldReturnZeroBytesWhenCacheFoldersHasNoFilesEmpty() {
    List<CacheHelper.FolderToManage> folders = new LinkedList<>();
    CacheHelper.FolderToManage mockedFolder = mock(CacheHelper.FolderToManage.class);
    File mockedFile = mock(File.class);
    when(mockedFolder.getFolder()).thenReturn(mockedFile);
    folders.add(mockedFolder);
    FileUtils fileUtilsMock = mock(FileUtils.class);
    when(fileUtilsMock.dirSize(mockedFile)).thenReturn(0L);
    cacheHelper = new CacheHelper(0, folders, fileUtilsMock);

    TestSubscriber subscriber = TestSubscriber.create();
    cacheHelper.cleanCache()
        .subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertCompleted();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);
    subscriber.assertValue(0L);
  }

  @Test public void shouldReturnZeroBytesWhenCacheFoldersEmpty() {
    List<CacheHelper.FolderToManage> folders = new LinkedList<>();
    FileUtils fileUtilsMock = mock(FileUtils.class);
    cacheHelper = new CacheHelper(0, folders, fileUtilsMock);

    TestSubscriber subscriber = TestSubscriber.create();
    cacheHelper.cleanCache()
        .subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertCompleted();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);
    subscriber.assertValue(0L);
  }

  @Test public void shouldReturnZeroWhenCacheNotExpiredButSizeExceeded() {
    List<CacheHelper.FolderToManage> folders = new LinkedList<>();
    CacheHelper.FolderToManage mockedFolder = mock(CacheHelper.FolderToManage.class);
    File mockedFile = mock(File.class);
    when(mockedFolder.getFolder()).thenReturn(mockedFile);
    when(mockedFolder.getCacheTime()).thenReturn(1000L);
    folders.add(mockedFolder);
    FileUtils fileUtilsMock = mock(FileUtils.class);
    when(fileUtilsMock.dirSize(mockedFile)).thenReturn(10L);
    cacheHelper = new CacheHelper(9L, folders, fileUtilsMock);

    TestSubscriber subscriber = TestSubscriber.create();
    cacheHelper.cleanCache()
        .subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertCompleted();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);
    subscriber.assertValue(0L);
  }

  @Test public void shouldReturnZeroWhenCacheNotExpiredButSizeNotExceeded() {
    List<CacheHelper.FolderToManage> folders = new LinkedList<>();
    CacheHelper.FolderToManage mockedFolder = mock(CacheHelper.FolderToManage.class);
    File mockedFile = mock(File.class);
    when(mockedFolder.getFolder()).thenReturn(mockedFile);
    when(mockedFolder.getCacheTime()).thenReturn(1000L);
    folders.add(mockedFolder);
    FileUtils fileUtilsMock = mock(FileUtils.class);
    when(fileUtilsMock.dirSize(mockedFile)).thenReturn(10L);
    cacheHelper = new CacheHelper(11L, folders, fileUtilsMock);

    TestSubscriber subscriber = TestSubscriber.create();
    cacheHelper.cleanCache()
        .subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertCompleted();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);
    subscriber.assertValue(0L);
  }

  @Test public void shouldReturnSizeWhenCacheExpiredAndSizeExceeded() {
    List<CacheHelper.FolderToManage> folders = new LinkedList<>();
    CacheHelper.FolderToManage mockedFolder = mock(CacheHelper.FolderToManage.class);
    File mockedFile = mock(File.class);
    File mockedInsideFile = mock(File.class);
    when(mockedInsideFile.length()).thenReturn(2L * VALUE_TO_CONVERT_MB_TO_BYTES);
    when(mockedInsideFile.delete()).thenReturn(true);
    when(mockedInsideFile.getAbsolutePath()).thenReturn("path");
    when(mockedFile.getAbsolutePath()).thenReturn("path1");
    when(mockedFile.length()).thenReturn(2L * VALUE_TO_CONVERT_MB_TO_BYTES);
    when(mockedFile.exists()).thenReturn(true);
    when(mockedFile.listFiles()).thenReturn(new File[] { mockedInsideFile });
    when(mockedFolder.getFolder()).thenReturn(mockedFile);
    when(mockedFolder.getCacheTime()).thenReturn(0L);
    folders.add(mockedFolder);
    FileUtils fileUtilsMock = mock(FileUtils.class);
    when(fileUtilsMock.dirSize(mockedFile)).thenReturn((2L * VALUE_TO_CONVERT_MB_TO_BYTES));
    cacheHelper = new CacheHelper(1L, folders, fileUtilsMock);

    TestSubscriber subscriber = TestSubscriber.create();
    cacheHelper.cleanCache()
        .subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertCompleted();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);
    subscriber.assertValue(2L * VALUE_TO_CONVERT_MB_TO_BYTES);
  }

  @Test public void shouldNotDeleteFolderIfManaged() {
    List<CacheHelper.FolderToManage> folders = new LinkedList<>();
    CacheHelper.FolderToManage mockedFolder = mock(CacheHelper.FolderToManage.class);
    CacheHelper.FolderToManage mockedInternalFolder = mock(CacheHelper.FolderToManage.class);
    File mockedFile = mock(File.class);
    File mockedInsideFile = mock(File.class);
    when(mockedInsideFile.length()).thenReturn(2L * VALUE_TO_CONVERT_MB_TO_BYTES);
    when(mockedInsideFile.delete()).thenReturn(true);
    when(mockedInsideFile.getAbsolutePath()).thenReturn("path");
    when(mockedFile.getAbsolutePath()).thenReturn("path1");
    when(mockedFile.length()).thenReturn(2L * VALUE_TO_CONVERT_MB_TO_BYTES);
    when(mockedFile.exists()).thenReturn(true);
    when(mockedFile.listFiles()).thenReturn(new File[] { mockedInsideFile });
    when(mockedFolder.getFolder()).thenReturn(mockedFile);
    when(mockedFolder.getCacheTime()).thenReturn(0L);
    when(mockedInternalFolder.getFolder()).thenReturn(mockedInsideFile);
    folders.add(mockedFolder);
    folders.add(mockedInternalFolder);
    FileUtils fileUtilsMock = mock(FileUtils.class);
    when(fileUtilsMock.dirSize(mockedFile)).thenReturn((2L * VALUE_TO_CONVERT_MB_TO_BYTES));
    cacheHelper = new CacheHelper(1L, folders, fileUtilsMock);

    TestSubscriber subscriber = TestSubscriber.create();
    cacheHelper.cleanCache()
        .subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertCompleted();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);
    subscriber.assertValue(0L);
  }

  @Test public void shouldDeleteMultipleManagedFiles() {
    FileUtils fileUtilsMock = mock(FileUtils.class);
    List<CacheHelper.FolderToManage> folders = new LinkedList<>();

    File mockedManagedFolder1 = mock(File.class);
    File mockedManagedFolder2 = mock(File.class);
    File mockedFolder1 = mock(File.class);
    File mockedFolder2 = mock(File.class);

    when(mockedManagedFolder1.listFiles()).thenReturn(new File[] { mockedFolder1 });
    when(mockedManagedFolder2.listFiles()).thenReturn(new File[] { mockedFolder2, mockedFolder2 });

    when(mockedManagedFolder1.getAbsolutePath()).thenReturn("root/path1");
    when(mockedManagedFolder2.getAbsolutePath()).thenReturn("root/path2");
    when(mockedFolder1.getAbsolutePath()).thenReturn("root/path1/file1");
    when(mockedFolder2.getAbsolutePath()).thenReturn("root/path1/file2");

    when(mockedManagedFolder1.exists()).thenReturn(true);
    when(mockedManagedFolder2.exists()).thenReturn(true);
    when(mockedFolder1.exists()).thenReturn(true);
    when(mockedFolder2.exists()).thenReturn(true);

    when(mockedManagedFolder1.length()).thenReturn(1L * VALUE_TO_CONVERT_MB_TO_BYTES);
    when(mockedManagedFolder2.length()).thenReturn(1L * VALUE_TO_CONVERT_MB_TO_BYTES);
    when(mockedFolder1.length()).thenReturn(10L * VALUE_TO_CONVERT_MB_TO_BYTES);
    when(mockedFolder2.length()).thenReturn(20L * VALUE_TO_CONVERT_MB_TO_BYTES);

    when(mockedManagedFolder1.delete()).thenReturn(true);
    when(mockedManagedFolder2.delete()).thenReturn(true);
    when(mockedFolder1.delete()).thenReturn(true);
    when(mockedFolder2.delete()).thenReturn(true);

    CacheHelper.FolderToManage mockedToManageFolder1 = mock(CacheHelper.FolderToManage.class);
    CacheHelper.FolderToManage mockedToManageFolder2 = mock(CacheHelper.FolderToManage.class);

    when(mockedToManageFolder1.getCacheTime()).thenReturn(0L);
    when(mockedToManageFolder2.getCacheTime()).thenReturn(0L);

    when(mockedToManageFolder1.getFolder()).thenReturn(mockedManagedFolder1);
    when(mockedToManageFolder2.getFolder()).thenReturn(mockedManagedFolder2);

    folders.add(mockedToManageFolder1);
    folders.add(mockedToManageFolder2);

    when(fileUtilsMock.dirSize(mockedManagedFolder1)).thenReturn(
        (10 + 1L) * VALUE_TO_CONVERT_MB_TO_BYTES);
    when(fileUtilsMock.dirSize(mockedManagedFolder2)).thenReturn(
        (20 + 1L) * VALUE_TO_CONVERT_MB_TO_BYTES);
    cacheHelper = new CacheHelper(0, folders, fileUtilsMock);

    TestSubscriber subscriber = TestSubscriber.create();
    cacheHelper.cleanCache()
        .subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertCompleted();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);
    subscriber.assertValue(50L * VALUE_TO_CONVERT_MB_TO_BYTES);
  }

  @Test public void shouldDeleteMultipleManagedFilesWithFilesNotExpired() {
    FileUtils fileUtilsMock = mock(FileUtils.class);
    List<CacheHelper.FolderToManage> folders = new LinkedList<>();

    File mockedManagedFolder1 = mock(File.class);
    File mockedManagedFolder2 = mock(File.class);
    File mockedFolder1 = mock(File.class);
    File mockedFolder2 = mock(File.class);

    when(mockedManagedFolder1.listFiles()).thenReturn(new File[] { mockedFolder1 });
    when(mockedManagedFolder2.listFiles()).thenReturn(new File[] { mockedFolder2, mockedFolder2 });

    when(mockedManagedFolder1.getAbsolutePath()).thenReturn("root/path1");
    when(mockedManagedFolder2.getAbsolutePath()).thenReturn("root/path2");
    when(mockedFolder1.getAbsolutePath()).thenReturn("root/path1/file1");
    when(mockedFolder2.getAbsolutePath()).thenReturn("root/path1/file2");

    when(mockedManagedFolder1.exists()).thenReturn(true);
    when(mockedManagedFolder2.exists()).thenReturn(true);
    when(mockedFolder1.exists()).thenReturn(true);
    when(mockedFolder2.exists()).thenReturn(true);

    when(mockedManagedFolder1.length()).thenReturn(1L * VALUE_TO_CONVERT_MB_TO_BYTES);
    when(mockedManagedFolder2.length()).thenReturn(1L * VALUE_TO_CONVERT_MB_TO_BYTES);
    when(mockedFolder1.length()).thenReturn(10L * VALUE_TO_CONVERT_MB_TO_BYTES);
    when(mockedFolder2.length()).thenReturn(20L * VALUE_TO_CONVERT_MB_TO_BYTES);

    when(mockedFolder1.lastModified()).thenReturn(System.currentTimeMillis());
    when(mockedFolder2.lastModified()).thenReturn(System.currentTimeMillis());

    when(mockedManagedFolder1.delete()).thenReturn(true);
    when(mockedManagedFolder2.delete()).thenReturn(true);
    when(mockedFolder1.delete()).thenReturn(true);
    when(mockedFolder2.delete()).thenReturn(true);

    CacheHelper.FolderToManage mockedToManageFolder1 = mock(CacheHelper.FolderToManage.class);
    CacheHelper.FolderToManage mockedToManageFolder2 = mock(CacheHelper.FolderToManage.class);

    when(mockedToManageFolder1.getCacheTime()).thenReturn(10000L);
    when(mockedToManageFolder2.getCacheTime()).thenReturn(0L);

    when(mockedToManageFolder1.getFolder()).thenReturn(mockedManagedFolder1);
    when(mockedToManageFolder2.getFolder()).thenReturn(mockedManagedFolder2);

    folders.add(mockedToManageFolder1);
    folders.add(mockedToManageFolder2);

    when(fileUtilsMock.dirSize(mockedManagedFolder1)).thenReturn(
        (10 + 1L) * VALUE_TO_CONVERT_MB_TO_BYTES);
    when(fileUtilsMock.dirSize(mockedManagedFolder2)).thenReturn(
        (20 + 1L) * VALUE_TO_CONVERT_MB_TO_BYTES);
    cacheHelper = new CacheHelper(10, folders, fileUtilsMock);

    TestSubscriber subscriber = TestSubscriber.create();
    cacheHelper.cleanCache()
        .subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertCompleted();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);
    subscriber.assertValue(40L * VALUE_TO_CONVERT_MB_TO_BYTES);
  }
}