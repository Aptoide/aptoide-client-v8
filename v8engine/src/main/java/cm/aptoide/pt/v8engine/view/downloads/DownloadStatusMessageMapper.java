package cm.aptoide.pt.v8engine.view.downloads;

import android.content.Context;

import static cm.aptoide.pt.database.realm.Download.BLOCK_COMPLETE;
import static cm.aptoide.pt.database.realm.Download.COMPLETED;
import static cm.aptoide.pt.database.realm.Download.CONNECTED;
import static cm.aptoide.pt.database.realm.Download.ERROR;
import static cm.aptoide.pt.database.realm.Download.FILE_MISSING;
import static cm.aptoide.pt.database.realm.Download.INVALID_STATUS;
import static cm.aptoide.pt.database.realm.Download.IN_QUEUE;
import static cm.aptoide.pt.database.realm.Download.NOT_DOWNLOADED;
import static cm.aptoide.pt.database.realm.Download.NOT_ENOUGH_SPACE_ERROR;
import static cm.aptoide.pt.database.realm.Download.PAUSED;
import static cm.aptoide.pt.database.realm.Download.PENDING;
import static cm.aptoide.pt.database.realm.Download.PROGRESS;
import static cm.aptoide.pt.database.realm.Download.RETRY;
import static cm.aptoide.pt.database.realm.Download.STARTED;
import static cm.aptoide.pt.database.realm.Download.WARN;

public class DownloadStatusMessageMapper {

  private final Context context;

  public DownloadStatusMessageMapper(Context context) {
    this.context = context;
  }

  public String map(int overallDownloadStatus, int downloadError) {
    String toReturn;
    switch (overallDownloadStatus) {
      case COMPLETED:
        toReturn = context.getString(cm.aptoide.pt.database.R.string.download_completed);
        break;
      case PAUSED:
        toReturn = context.getString(cm.aptoide.pt.database.R.string.download_paused);
        break;
      case PROGRESS:
        toReturn = context.getString(cm.aptoide.pt.database.R.string.download_progress);
        break;
      case PENDING:
      case IN_QUEUE:
        toReturn = context.getString(cm.aptoide.pt.database.R.string.download_queue);
        break;
      case INVALID_STATUS:
        toReturn =
            ""; //this state only appears while download manager doesn't get the download(before the AptoideDownloadManager#startDownload
        // method runs)
        break;
      case WARN:
      case BLOCK_COMPLETE:
      case CONNECTED:
      case RETRY:
      case STARTED:
      case NOT_DOWNLOADED:
      case ERROR:
      case FILE_MISSING:
      default:
        toReturn = getErrorMessage(downloadError);
    }
    return toReturn;
  }

  private String getErrorMessage(int downloadError) {
    String toReturn;
    if (downloadError == NOT_ENOUGH_SPACE_ERROR) {
      toReturn = context.getString(cm.aptoide.pt.database.R.string.out_of_space_error);
    } else {
      toReturn = context.getString(cm.aptoide.pt.dataprovider.R.string.simple_error_occured);
    }
    return toReturn;
  }
}
