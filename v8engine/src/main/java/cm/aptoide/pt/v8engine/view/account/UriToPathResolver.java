package cm.aptoide.pt.v8engine.view.account;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;

public class UriToPathResolver {

  private final ContentResolver contentResolver;
  private final CrashReport crashReport;

  public UriToPathResolver(ContentResolver contentResolver, CrashReport crashReport) {
    this.contentResolver = contentResolver;
    this.crashReport = crashReport;
  }

  public String getMediaStoragePath(Uri contentUri) {
    if (contentUri == null) {
      throw new NullPointerException("content Uri is null");
    }

    Cursor cursor = null;
    try {
      String[] projection = { MediaStore.Images.Media.DATA };
      cursor = contentResolver.query(contentUri, projection, null, null, null);
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
    } catch (NullPointerException ex) {
      crashReport.log(ex);
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    // default situation
    return contentUri.getPath();
  }
}
