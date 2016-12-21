package cm.aptoide.accountmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.ws.CheckUserCredentialsRequest;
import cm.aptoide.accountmanager.ws.ErrorsMapper;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.SetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import com.jakewharton.rxbinding.view.RxView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by pedroribeiro on 29/11/16.
 */

public class CreateStoreActivity extends PermissionsBaseActivity
    implements AptoideAccountManager.ICreateStore {

  private static final String TAG = CreateStoreActivity.class.getSimpleName();

  private Toolbar mToolbar;
  private Button mCreateStore;
  private Button mSkip;
  private RelativeLayout mStoreAvatarLayout;
  private ImageView mStoreAvatar;
  private TextView mTakePictureText;
  private TextView mHeader;
  private TextView mChooseNameTitle;
  private EditText mStoreName;
  private View content;
  private CompositeSubscription mSubscriptions;

  //Theme related views
  private ImageView mOrangeShape;
  private ImageView mOrangeTick;
  private ImageView mGreenShape;
  private ImageView mGreenTick;
  private ImageView mRedShape;
  private ImageView mRedTick;
  private ImageView mIndigoShape;
  private ImageView mIndigoTick;
  private ImageView mTealShape;
  private ImageView mTealTick;
  private ImageView mPinkShape;
  private ImageView mPinkTick;
  private ImageView mLimeShape;
  private ImageView mLimeTick;
  private ImageView mAmberShape;
  private ImageView mAmberTick;
  private ImageView mBrownShape;
  private ImageView mBrownTick;
  private ImageView mLightblueShape;
  private ImageView mLightblueTick;

  private String CREATE_STORE_CODE = "1";
  private String storeName;
  private String username;
  private String password;
  private String storeAvatarPath;

  private boolean THEME_CLICKED_FLAG = false;
  private String storeTheme = "";

  private int CREATE_STORE_REQUEST_CODE = 0; //1: all 2: user and theme 3:user

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    mSubscriptions = new CompositeSubscription();
    bindViews();
    editViews();
    setupToolbar();
    setupListeners();
    setupThemeListeners();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mSubscriptions.clear();
  }

  @Override protected String getActivityTitle() {
    return getString(R.string.create_store_title);
  }

  @Override int getLayoutId() {
    return R.layout.activity_create_store;
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
    content = findViewById(android.R.id.content);

    //Theme related views
    mOrangeShape = (ImageView) findViewById(R.id.create_store_theme_orange);
    mOrangeTick = (ImageView) findViewById(R.id.create_store_theme_check_orange);
    mGreenShape = (ImageView) findViewById(R.id.create_store_theme_green);
    mGreenTick = (ImageView) findViewById(R.id.create_store_theme_check_green);
    mRedShape = (ImageView) findViewById(R.id.create_store_theme_red);
    mRedTick = (ImageView) findViewById(R.id.create_store_theme_check_red);
    mIndigoShape = (ImageView) findViewById(R.id.create_store_theme_indigo);
    mIndigoTick = (ImageView) findViewById(R.id.create_store_theme_check_indigo);
    mTealShape = (ImageView) findViewById(R.id.create_store_theme_teal);
    mTealTick = (ImageView) findViewById(R.id.create_store_theme_check_teal);
    mPinkShape = (ImageView) findViewById(R.id.create_store_theme_pink);
    mPinkTick = (ImageView) findViewById(R.id.create_store_theme_check_pink);
    mLimeShape = (ImageView) findViewById(R.id.create_store_theme_lime);
    mLimeTick = (ImageView) findViewById(R.id.create_store_theme_check_lime);
    mAmberShape = (ImageView) findViewById(R.id.create_store_theme_amber);
    mAmberTick = (ImageView) findViewById(R.id.create_store_theme_check_amber);
    mBrownShape = (ImageView) findViewById(R.id.create_store_theme_brown);
    mBrownTick = (ImageView) findViewById(R.id.create_store_theme_check_brown);
    mLightblueShape = (ImageView) findViewById(R.id.create_store_theme_lightblue);
    mLightblueTick = (ImageView) findViewById(R.id.create_store_theme_check_lightblue);
  }

  private void editViews() {
    mHeader.setText(
        AptoideUtils.StringU.getFormattedString(R.string.create_store_header, "Aptoide"));
    mChooseNameTitle.setText(
        AptoideUtils.StringU.getFormattedString(R.string.create_store_name, "Aptoide"));
  }

  private void setupListeners() {

    mSubscriptions.add(RxView.clicks(mStoreAvatarLayout).subscribe(click -> chooseAvatarSource()));
    mSubscriptions.add(RxView.clicks(mCreateStore).subscribe(click -> {
      storeName = mStoreName.getText().toString().trim();
      validateData();
      if (CREATE_STORE_REQUEST_CODE == 2
          || CREATE_STORE_REQUEST_CODE == 3
          || CREATE_STORE_REQUEST_CODE == 1) {
        ProgressDialog progressDialog = GenericDialogs.createGenericPleaseWaitDialog(this,
            getApplicationContext().getString(R.string.please_wait_upload));
        progressDialog.show();
        CheckUserCredentialsRequest.of(AptoideAccountManager.getAccessToken(), storeName,
            CREATE_STORE_CODE).execute(answer -> {
          if (answer.hasErrors()) {
            if (answer.getErrors() != null && answer.getErrors().size() > 0) {
              progressDialog.dismiss();
              if (answer.getErrors().get(0).code.equals("WOP-2")) {
                mSubscriptions.add(GenericDialogs.createGenericContinueMessage(this, "",
                    getApplicationContext().getResources().getString(R.string.ws_error_WOP_2)).subscribe());
              } else {
                onCreateFail(
                    ErrorsMapper.getWebServiceErrorMessageFromCode(answer.getErrors().get(0).code));
              }
            }
          } else {
            onCreateSuccess(progressDialog);
          }
        });
      }
    }));
    mSubscriptions.add(RxView.clicks(mSkip)
        .flatMap(click -> AptoideAccountManager.refreshAndSaveUserInfoData())
        .subscribe(refreshed -> {
          finish();
        }, throwable -> {
          finish();
        }));
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    FileUtils fileUtils = new FileUtils();
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      Uri avatarUrl = getPhotoFileUri(PermissionsBaseActivity.createAvatarPhotoName(photoAvatar));
      ImageLoader.loadWithCircleTransform(avatarUrl, mStoreAvatar);
      storeAvatarPath = fileUtils.getPathAlt(avatarUrl, getApplicationContext());
    } else if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
      Uri avatarUrl = data.getData();
      ImageLoader.loadWithCircleTransform(avatarUrl, mStoreAvatar);
      storeAvatarPath = fileUtils.getPath(avatarUrl, getApplicationContext());
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
          dispatchTakePictureIntent(getApplicationContext());
        } else {
          //TODO: Deal with permissions not being given by user
        }
    }
  }

  @Override public void onCreateSuccess(ProgressDialog progressDialog) {
    ShowMessage.asSnack(this, R.string.create_store_store_created);
    if (CREATE_STORE_REQUEST_CODE == 1) {
      SetStoreRequest.of(new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
              DataProvider.getContext()).getAptoideClientUUID(), AptoideAccountManager.getAccessToken(),
          storeName, storeTheme, storeAvatarPath).execute(answer -> {
        AptoideAccountManager.refreshAndSaveUserInfoData().subscribe(refreshed -> {
          progressDialog.dismiss();
          finish();
        }, throwable -> throwable.printStackTrace());
      }, throwable -> {
        onCreateFail(ErrorsMapper.getWebServiceErrorMessageFromCode(throwable.getMessage()));
        AptoideAccountManager.refreshAndSaveUserInfoData().subscribe(refreshed -> {
          progressDialog.dismiss();
        }, throwable1 -> throwable1.printStackTrace());
      });
    } else if (CREATE_STORE_REQUEST_CODE == 2 || CREATE_STORE_REQUEST_CODE == 3) {
      SimpleSetStoreRequest.of(AptoideAccountManager.getAccessToken(),
          new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
              DataProvider.getContext()).getAptoideClientUUID(), storeName, storeTheme)
          .execute(answer -> {
            AptoideAccountManager.refreshAndSaveUserInfoData().subscribe(refreshed -> {
              progressDialog.dismiss();
              finish();
            }, Throwable::printStackTrace);
          }, throwable -> {
            onCreateFail(ErrorsMapper.getWebServiceErrorMessageFromCode(throwable.getMessage()));
            AptoideAccountManager.refreshAndSaveUserInfoData().subscribe(refreshed -> {
              progressDialog.dismiss();
            }, Throwable::printStackTrace);
          });
    }
  }

  @Override public void onCreateFail(@StringRes int reason) {
    ShowMessage.asSnack(content, reason);
  }

  @Override public String getRepoName() {
    return storeName == null ? "" : mStoreName.getText().toString();
  }

  @Override public String getRepoTheme() {
    return storeTheme == null ? "" : storeTheme;
  }

  @Override public String getRepoAvatar() {
    return storeAvatarPath == null ? "" : storeAvatarPath;
  }

  private void setupThemeListeners() {
    mSubscriptions.add(RxView.clicks(mOrangeShape).subscribe(click -> {
      if (!THEME_CLICKED_FLAG) {
        mOrangeTick.setVisibility(View.VISIBLE);
        storeTheme = "orange";
        THEME_CLICKED_FLAG = true;
      } else if (THEME_CLICKED_FLAG && checkThemeClicked("orange")) {
        mOrangeTick.setVisibility(View.INVISIBLE);
        THEME_CLICKED_FLAG = false;
      }
    }));
    mSubscriptions.add(RxView.clicks(mGreenShape).subscribe(click -> {
      if (!THEME_CLICKED_FLAG) {
        mGreenTick.setVisibility(View.VISIBLE);
        storeTheme = "green";
        THEME_CLICKED_FLAG = true;
      } else if (THEME_CLICKED_FLAG && checkThemeClicked("green")) {
        mGreenTick.setVisibility(View.INVISIBLE);
        THEME_CLICKED_FLAG = false;
      }
    }));
    mSubscriptions.add(RxView.clicks(mRedShape).subscribe(click -> {
      if (!THEME_CLICKED_FLAG) {
        mRedTick.setVisibility(View.VISIBLE);
        storeTheme = "red";
        THEME_CLICKED_FLAG = true;
      } else if (THEME_CLICKED_FLAG && checkThemeClicked("red")) {
        mRedTick.setVisibility(View.INVISIBLE);
        THEME_CLICKED_FLAG = false;
      }
    }));
    mSubscriptions.add(RxView.clicks(mIndigoShape).subscribe(click -> {
      if (!THEME_CLICKED_FLAG) {
        mIndigoTick.setVisibility(View.VISIBLE);
        storeTheme = "indigo";
        THEME_CLICKED_FLAG = true;
      } else if (THEME_CLICKED_FLAG && checkThemeClicked("indigo")) {
        mIndigoTick.setVisibility(View.INVISIBLE);
        THEME_CLICKED_FLAG = false;
      }
    }));
    mSubscriptions.add(RxView.clicks(mTealShape).subscribe(click -> {
      if (!THEME_CLICKED_FLAG) {
        mTealTick.setVisibility(View.VISIBLE);
        storeTheme = "teal";
        THEME_CLICKED_FLAG = true;
      } else if (THEME_CLICKED_FLAG && checkThemeClicked("teal")) {
        mTealTick.setVisibility(View.INVISIBLE);
        THEME_CLICKED_FLAG = false;
      }
    }));
    mSubscriptions.add(RxView.clicks(mPinkShape).subscribe(click -> {
      if (!THEME_CLICKED_FLAG) {
        mPinkTick.setVisibility(View.VISIBLE);
        storeTheme = "pink";
        THEME_CLICKED_FLAG = true;
      } else if (THEME_CLICKED_FLAG && checkThemeClicked("pink")) {
        mPinkTick.setVisibility(View.INVISIBLE);
        THEME_CLICKED_FLAG = false;
      }
    }));
    mSubscriptions.add(RxView.clicks(mLimeShape).subscribe(click -> {
      if (!THEME_CLICKED_FLAG) {
        mLimeTick.setVisibility(View.VISIBLE);
        storeTheme = "lime";
        THEME_CLICKED_FLAG = true;
      } else if (THEME_CLICKED_FLAG && checkThemeClicked("lime")) {
        mLimeTick.setVisibility(View.INVISIBLE);
        THEME_CLICKED_FLAG = false;
      }
    }));
    mSubscriptions.add(RxView.clicks(mAmberShape).subscribe(click -> {
      if (!THEME_CLICKED_FLAG) {
        mAmberTick.setVisibility(View.VISIBLE);
        storeTheme = "amber";
        THEME_CLICKED_FLAG = true;
      } else if (THEME_CLICKED_FLAG && checkThemeClicked("amber")) {
        mAmberTick.setVisibility(View.INVISIBLE);
        THEME_CLICKED_FLAG = false;
      }
    }));
    mSubscriptions.add(RxView.clicks(mBrownShape).subscribe(click -> {
      if (!THEME_CLICKED_FLAG) {
        mBrownTick.setVisibility(View.VISIBLE);
        storeTheme = "brown";
        THEME_CLICKED_FLAG = true;
      } else if (THEME_CLICKED_FLAG && checkThemeClicked("brown")) {
        mBrownTick.setVisibility(View.INVISIBLE);
        THEME_CLICKED_FLAG = false;
      }
    }));
    mSubscriptions.add(RxView.clicks(mLightblueShape).subscribe(click -> {
      if (!THEME_CLICKED_FLAG) {
        mLightblueTick.setVisibility(View.VISIBLE);
        storeTheme = "light-blue";
        THEME_CLICKED_FLAG = true;
      } else if (THEME_CLICKED_FLAG && checkThemeClicked("light-blue")) {
        mLightblueTick.setVisibility(View.INVISIBLE);
        THEME_CLICKED_FLAG = false;
      }
    }));
  }

  private boolean checkThemeClicked(String color) {
    if (color.equals(storeTheme)) {
      return true;
    }
    return false;
  }

  private int validateData() {
    if (getRepoName().length() != 0) {
      if (getRepoTheme().length() != 0) {
        if (getRepoAvatar().length() != 0) {
          CREATE_STORE_REQUEST_CODE = 1;
        } else {
          CREATE_STORE_REQUEST_CODE = 2;
        }
      } else {
        CREATE_STORE_REQUEST_CODE = 3;
      }
    } else if (getRepoAvatar().length() != 0) {
      CREATE_STORE_REQUEST_CODE = 1;
    } else {
      CREATE_STORE_REQUEST_CODE = 0;
      onCreateFail(R.string.nothing_inserted_store);
    }
    return CREATE_STORE_REQUEST_CODE;
  }
}
