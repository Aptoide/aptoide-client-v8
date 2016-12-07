package cm.aptoide.accountmanager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import com.jakewharton.rxbinding.view.RxView;
import rx.subscriptions.CompositeSubscription;

import static cm.aptoide.accountmanager.CreateUserActivity.REQUEST_CAMERA_CODE;
import static cm.aptoide.accountmanager.CreateUserActivity.REQUEST_IMAGE_CAPTURE;

/**
 * Created by pedroribeiro on 29/11/16.
 */

public class CreateStoreActivity extends PermissionsBaseActivity {

  private Toolbar mToolbar;
  private Button mCreateStore;
  private Button mSkip;
  private RelativeLayout mStoreAvatarLayout;
  private ImageView mStoreAvatar;
  private TextView mTakePictureText;
  private TextView mHeader;
  private TextView mChooseNameTitle;
  private EditText mStoreName;
  private CompositeSubscription mSubscriptions;

  private String storeName;
  private String storeAvatarPath;
  private String aptoideStoreAvatar = "aptoide_user_store_avatar.png";

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    mSubscriptions = new CompositeSubscription();
    bindViews();
    editViews();
    setupToolbar();
    setupListeners();
  }

  @Override protected String getActivityTitle() {
    return getString(R.string.create_store_title);
  }

  @Override int getLayoutId() {
    return R.layout.fragment_create_store;
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
    mCreateStore = (Button) findViewById(R.id.create_store_action);
    mSkip = (Button) findViewById(R.id.create_store_skip);
    mStoreAvatarLayout = (RelativeLayout) findViewById(R.id.create_store_image_action);
    mTakePictureText = (TextView) findViewById(R.id.create_store_take_picture_text);
    mStoreName = (EditText) findViewById(R.id.create_store_name);
    mHeader = (TextView) findViewById(R.id.create_store_header_textview);
    mChooseNameTitle = (TextView) findViewById(R.id.create_store_choose_name_title);
    mStoreAvatar = (ImageView) findViewById(R.id.create_store_image);
  }

  private void editViews() {
    mHeader.setText(AptoideUtils.StringU.getFormattedString(R.string.create_store_header, "Aptoide"));
    mChooseNameTitle.setText(AptoideUtils.StringU.getFormattedString(R.string.create_store_name, "Aptoide"));
  }

  private void setupListeners() {
    mSubscriptions.add(RxView.clicks(mStoreAvatarLayout).subscribe(click -> chooseAvatarSource()));
    mSubscriptions.add(RxView.clicks(mCreateStore).subscribe(click -> {
      storeName = mStoreName.getText().toString();
      //TODO: Make request
    }));
    mSubscriptions.add(RxView.clicks(mSkip).subscribe(click -> finish()));
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      Uri avatarUrl = getPhotoFileUri(PermissionsBaseActivity.createAvatarPhotoName(aptoideUserAvatar));
      ImageLoader.loadWithCircleTransform(avatarUrl, mStoreAvatar);
      storeAvatarPath = avatarUrl.toString();
    } else if (requestCode == REQUEST_CAMERA_CODE && resultCode == RESULT_OK) {
      Uri avatarUrl = data.getData();
      ImageLoader.loadWithCircleTransform(avatarUrl, mStoreAvatar);
      FileUtils fileUtils = new FileUtils();
      fileUtils.copyFile(avatarUrl.toString(), Application.getConfiguration().getUserAvatarCachePath(), aptoideUserAvatar);
      storeAvatarPath = avatarUrl.toString();
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case STORAGE_REQUEST_CODE:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          changePermissionValue(true);
          callGallery();
        } else {
          //TODO: Deal with permissions not being given by user
        }
        return;
      case CAMERA_REQUEST_CODE:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          changePermissionValue(true);
          dispatchTakePictureIntent();
        } else {
          //TODO: Deal with permissions not being given by user
        }
    }
  }
}
