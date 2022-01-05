package cm.aptoide.pt.downloads;

import com.liulishuo.filedownloader.BaseDownloadTask;

/**
 * Created by filipegoncalves on 8/6/18.
 */

public class MockInqueueTask implements BaseDownloadTask.InQueueTask {
  @Override public int enqueue() {
    return 0;
  }
}
