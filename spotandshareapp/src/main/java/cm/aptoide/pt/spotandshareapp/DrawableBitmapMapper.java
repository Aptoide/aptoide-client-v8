package cm.aptoide.pt.spotandshareapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import java.io.ByteArrayOutputStream;

/**
 * Created by filipe on 10-07-2017.
 */

public class DrawableBitmapMapper {

  private Context context;

  public DrawableBitmapMapper(Context context) {
    this.context = context;
  }

  public byte[] convertDrawableToBitmap(Drawable drawable) {
    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    byte[] bitmapdata = stream.toByteArray();
    return bitmapdata;
  }

  public Drawable convertBitmapToDrawable(byte[] array) {
    return new BitmapDrawable(context.getResources(),
        BitmapFactory.decodeByteArray(array, 0, array.length));
  }
}
