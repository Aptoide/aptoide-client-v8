package cm.aptoide.pt.spotandshareapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    return convertBitmapToByteArray(bitmap);
  }

  public Drawable convertBitmapToDrawable(byte[] array) {
    return new BitmapDrawable(context.getResources(),
        BitmapFactory.decodeByteArray(array, 0, array.length));
  }

  private byte[] convertBitmapToByteArray(Bitmap bitmap) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
    byte[] bitmapdata = stream.toByteArray();
    return bitmapdata;
  }

  public byte[] convertUriToByteArray(Uri uri) {
    Bitmap bitmap = null;
    try {
      bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return convertBitmapToByteArray(bitmap);
  }

  public Drawable convertUriToDrawable(Uri uri) {
    byte[] array = convertUriToByteArray(uri);
    return convertBitmapToDrawable(array);
  }
}
