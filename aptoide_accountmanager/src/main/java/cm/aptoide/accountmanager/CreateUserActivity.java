package cm.aptoide.accountmanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cm.aptoide.accountmanager.BaseActivity;
import cm.aptoide.accountmanager.R;
import cm.aptoide.pt.imageloader.ImageLoader;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by pedroribeiro on 24/11/16.
 */

public class CreateUserActivity extends BaseActivity {

  private Toolbar mToolbar;
  private RelativeLayout mUserAvatar;
  private EditText mUsername;
  private Button mCreateButton;
  private ImageView mAvatar;
  private Subscription mInsertedUsername;
  private Subscription mAvatarSubscription;
  private Subscription mButtonSubscription;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    bindViews();
    setupToolbar();
    setupListeners();
  }

  @Override protected String getActivityTitle() {
    return getString(R.string.create_user_title);
  }

  @Override int getLayoutId() {
    return R.layout.fragment_create_user;
  }

  private void setupToolbar() {
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getActivityTitle());
    }
  }

  private void bindViews() {
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mUserAvatar = (RelativeLayout) findViewById(R.id.create_user_image_action);
    mUsername = (EditText) findViewById(R.id.create_user_username_inserted);
    mCreateButton = (Button) findViewById(R.id.create_user_create_profile);
    mAvatar = (ImageView) findViewById(R.id.create_user_image);
  }

  private void setupListeners() {
    mInsertedUsername = RxTextView.textChanges(mUsername)
        .subscribe(new Action1<CharSequence>() {
          @Override public void call(CharSequence input) {

          }
        });
    mAvatarSubscription = RxView.clicks(mUserAvatar)
        .subscribe(click -> {dispatchTakePictureIntent();});
    mButtonSubscription = RxView.clicks(mCreateButton)
        .subscribe(click -> {});
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mInsertedUsername.unsubscribe();
    mAvatarSubscription.unsubscribe();
    mButtonSubscription.unsubscribe();
  }

  static final int REQUEST_IMAGE_CAPTURE = 1;
  static final int REQUEST_TAKE_PHOTO = 1;

  private void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
  }

  //private void dispatchTakePictureIntent() {
   // Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Ensure that there's a camera activity to handle the intent
    //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      // Create the File where the photo should go
      //File photoFile = null;
      //try {
      //  photoFile = createImageFile();
      //} catch (IOException ex) {
        // Error occurred while creating the File
      //}
      // Continue only if the File was successfully created
      //if (photoFile != null) {
       // Uri photoURI = FileProvider.getUriForFile(this,
         //   "com.example.android.fileprovider",
       //     photoFile);
       // takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
       // startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
      //}
    //}
  //}

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      Bundle extras = data.getExtras();
      Bitmap imageBitmap = (Bitmap) extras.get("data");
      mAvatar.setImageBitmap(imageBitmap);
    }
  }

  String mCurrentPhotoPath;

  private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File image = File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */
    );

    // Save a file: path for use with ACTION_VIEW intents
    mCurrentPhotoPath = image.getAbsolutePath();
    return image;
  }
}
