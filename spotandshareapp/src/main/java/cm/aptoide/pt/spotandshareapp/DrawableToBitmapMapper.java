package cm.aptoide.pt.spotandshareapp;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import java.io.ByteArrayOutputStream;

/**
 * Created by filipe on 10-07-2017.
 */

public class DrawableToBitmapMapper {

  public byte[] convertDrawableToBitmap(Drawable drawable) {
    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    byte[] bitmapdata = stream.toByteArray();
    return bitmapdata;
  }
}
