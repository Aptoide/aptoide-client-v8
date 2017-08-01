package cm.aptoide.pt.view.account;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import rx.Single;

public class PhotoFileGenerator {
  private static final SimpleDateFormat DATE_FORMATTER =
      new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
  private final Context context;
  private final File storageDirectory;
  private final String fileProviderAuthority;

  public PhotoFileGenerator(Context context, File storageDirectory, String fileProviderAuthority) {
    this.context = context;
    this.storageDirectory = storageDirectory;
    this.fileProviderAuthority = fileProviderAuthority;
  }

  public Single<String> generateNewImageFileUriAsString() {
    return Single.fromCallable(() -> {
      String timeStamp = DATE_FORMATTER.format(new Date());
      String imageFileName = String.format("aptoide_image_%s", timeStamp);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        return FileProvider.getUriForFile(context, fileProviderAuthority,
            File.createTempFile(imageFileName, ".jpg", storageDirectory))
            .toString();
      } else {
        return Uri.fromFile(new File(storageDirectory + File.separator + imageFileName + ".jpg"))
            .toString();
      }
    });
  }
}
