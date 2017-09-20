package cm.aptoide.pt.account.view;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import cm.aptoide.pt.logger.Logger;

public class UriToPathResolver {

  private static final String TAG = UriToPathResolver.class.getName();
  private final ContentResolver contentResolver;

  public UriToPathResolver(ContentResolver contentResolver) {
    this.contentResolver = contentResolver;
  }

  public String getMediaStoragePath(Uri contentUri) {
    if (contentUri == null) {
      throw new NullPointerException("content Uri is null");
    }

    Cursor cursor = null;
    try {
      final String columnName = MediaStore.Images.Media.DATA;
      String[] projection = { columnName };
      cursor = contentResolver.query(contentUri, projection, null, null, null);
      int column_index = cursor.getColumnIndexOrThrow(columnName);
      if (cursor != null) {
        cursor.moveToFirst();
        return cursor.getString(column_index);
      }
    } catch (Exception ex) {
      Logger.e(TAG, ex);
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }

    // default situation
    return contentUri.toString();
  }

  public String getCameraStoragePath(Uri uri) {
    Cursor cursor = contentResolver.query(uri, null, null, null, null);
    cursor.moveToFirst();
    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
    return cursor.getString(idx);
  }
}
