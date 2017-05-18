package cm.aptoide.pt.v8engine.filemanager;

import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.networkclient.okhttp.cache.L2Cache;
import cm.aptoide.pt.utils.FileUtils;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by trinkes on 11/16/16.
 */

public class FileManagerTest {

  private static CacheHelper cacheHelper;
  private static AptoideDownloadManager downloadManager;
  private static FileUtils fileUtils;
  private String[] folders;

  @Before public void init() {
    folders = new String[] { "test" };
    cacheHelper = mock(CacheHelper.class);
    when(cacheHelper.cleanCache()).thenReturn(Observable.just(10L));

    downloadManager = mock(AptoideDownloadManager.class);
    when(downloadManager.invalidateDatabase()).thenReturn(Observable.just(null));

    fileUtils = mock(FileUtils.class);
  }

  @Test public void cleanCacheAndInvalidateDatabase() {
    FileManager fileManager =
        new FileManager(cacheHelper, fileUtils, folders, downloadManager, mock(L2Cache.class));

    TestSubscriber<Long> subscriber = TestSubscriber.create();
    fileManager.purgeCache()
        .subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    assertSubscriber(subscriber, 10L);
    verify(cacheHelper, times(1)).cleanCache();
    verify(downloadManager, times(1)).invalidateDatabase();
  }

  private void assertSubscriber(TestSubscriber<Long> subscriber, long value) {
    subscriber.awaitTerminalEvent();
    subscriber.assertCompleted();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);
    subscriber.assertValue(value);
  }

  @Test public void deleteCacheSizeGreaterZero() throws IOException {
    FileUtils fileUtils = mock(FileUtils.class);
    when(fileUtils.deleteFolder(folders[0])).thenReturn(Observable.just(10L));
    FileManager fileManager =
        new FileManager(cacheHelper, fileUtils, folders, downloadManager, mock(L2Cache.class));

    TestSubscriber<Long> subscriber = TestSubscriber.create();
    fileManager.deleteCache()
        .subscribe(subscriber);
    assertSubscriber(subscriber, 10L);
    verify(downloadManager, times(1)).invalidateDatabase();
  }

  @Test public void deleteCacheSizeEqualsZero() throws IOException {
    FileUtils fileUtils = mock(FileUtils.class);
    when(fileUtils.deleteFolder(folders[0])).thenReturn(Observable.just(0L));
    FileManager fileManager =
        new FileManager(cacheHelper, fileUtils, folders, downloadManager, mock(L2Cache.class));

    TestSubscriber<Long> subscriber = TestSubscriber.create();
    fileManager.deleteCache()
        .subscribe(subscriber);
    assertSubscriber(subscriber, 0L);
    verify(downloadManager, times(0)).invalidateDatabase();
  }
}
