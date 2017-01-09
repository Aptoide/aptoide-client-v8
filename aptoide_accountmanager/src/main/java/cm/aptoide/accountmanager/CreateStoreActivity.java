package cm.aptoide.accountmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.ws.AptoideWsV3Exception;
import cm.aptoide.accountmanager.ws.CheckUserCredentialsRequest;
import cm.aptoide.accountmanager.ws.ErrorsMapper;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.SetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import com.jakewharton.rxbinding.view.RxView;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
  private EditText mStoreDescription;
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
  private String storeAvatarPath;
  private String storeDescription;
  private long storeId;

  private boolean THEME_CLICKED_FLAG = false;
  private String storeTheme = "";
  private String from;

  private int CREATE_STORE_REQUEST_CODE = 0; //1: all (Multipart)  2: user and theme 3:user 4/5:edit

  @Override public void onCreate(Bundle savedInstanceState) {
    getData();
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    mSubscriptions = new CompositeSubscription();
    bindViews();
    editViews();
    setupToolbar();
    setupListeners();
    setupThemeListeners();
  }

  private void getData() {
    from = getIntent().getStringExtra("from") == null ? "" : getIntent().getStringExtra("from");
    storeId = getIntent().getLongExtra("storeId", -1);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mSubscriptions.clear();
  }

  @Override protected String getActivityTitle() {
    if (!from.equals("store")) {
      return getString(R.string.create_store_title);
    } else {
      return getString(R.string.edit_store_title);
    }
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
    mStoreDescription = (EditText) findViewById(R.id.edit_store_description);
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
    if (!from.equals("store")) {
      mHeader.setText(
          AptoideUtils.StringU.getFormattedString(R.string.create_store_header, "Aptoide"));
      mChooseNameTitle.setText(
          AptoideUtils.StringU.getFormattedString(R.string.create_store_name, "Aptoide"));
    } else {
      mHeader.setText(R.string.edit_store_header);
      mChooseNameTitle.setText(
          AptoideUtils.StringU.getFormattedString(R.string.create_store_description_title));
      mStoreName.setVisibility(View.GONE);
      mStoreDescription.setVisibility(View.VISIBLE);
      mCreateStore.setText(R.string.save_edit_store);
      mSkip.setText(R.string.cancel);
    }
  }

  private void setupListeners() {
    mSubscriptions.add(RxView.clicks(mStoreAvatarLayout).subscribe(click -> chooseAvatarSource()));
    mSubscriptions.add(RxView.clicks(mCreateStore).subscribe(click -> {
          AptoideUtils.SystemU.hideKeyboard(this);
          storeName = mStoreName.getText().toString().trim().toLowerCase();
          storeDescription = mStoreDescription.getText().toString();
          validateData();
          ProgressDialog progressDialog = GenericDialogs.createGenericPleaseWaitDialog(this,
              getApplicationContext().getString(R.string.please_wait_upload));
          if (CREATE_STORE_REQUEST_CODE == 1
              || CREATE_STORE_REQUEST_CODE == 2
              || CREATE_STORE_REQUEST_CODE == 3) {
            progressDialog.show();
            CheckUserCredentialsRequest.of(AptoideAccountManager.getAccessToken(), storeName,
                CREATE_STORE_CODE).execute(answer -> {
              if (answer.hasErrors()) {
                if (answer.getErrors() != null && answer.getErrors().size() > 0) {
                  progressDialog.dismiss();
                  if (answer.getErrors().get(0).code.equals("WOP-2")) {
                    mSubscriptions.add(GenericDialogs.createGenericContinueMessage(this, "",
                        getApplicationContext().getResources().getString(R.string.ws_error_WOP_2))
                        .subscribe());
                  } else if (answer.getErrors().get(0).code.equals("WOP-3")) {
                    ShowMessage.asSnack(this,
                        ErrorsMapper.getWebServiceErrorMessageFromCode(answer.getErrors().get(0).code));
                  } else {
                    ShowMessage.asObservableSnack(this,
                        ErrorsMapper.getWebServiceErrorMessageFromCode(answer.getErrors().get(0).code))
                        .subscribe(visibility -> {
                          if (visibility == ShowMessage.DISMISSED) {
                            finish();
                          }
                        });
                  }
                }
              } else if (!(CREATE_STORE_REQUEST_CODE == 3)) {
                onCreateSuccess(progressDialog);
              } else {
                progressDialog.dismiss();
                ShowMessage.asLongObservableSnack(this, R.string.create_store_store_created).subscribe(visibility -> {
                  if (visibility == ShowMessage.DISMISSED) {
                    finish();
                  }
                });
              }
            });
          } else if (CREATE_STORE_REQUEST_CODE == 4) {
            setStoreData();
            progressDialog.show();
            mSubscriptions.add(SetStoreRequest.of(
                new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                    DataProvider.getContext()).getAptoideClientUUID(),
                AptoideAccountManager.getAccessToken(), storeName, storeTheme, storeAvatarPath,
                storeDescription, true, storeId).observe().subscribe(answer -> {
              AptoideAccountManager.refreshAndSaveUserInfoData().subscribe(refreshed -> {
                progressDialog.dismiss();
                finish();
              }, Throwable::printStackTrace);
            }, throwable -> {
              if (((AptoideWsV7Exception) throwable).getBaseResponse()
                  .getErrors()
                  .get(0)
                  .getCode()
                  .equals("API-1")) {
                progressDialog.dismiss();
                ShowMessage.asObservableSnack(this, R.string.ws_error_API_1).subscribe(visibility -> {
                  if (visibility == ShowMessage.DISMISSED) {
                    finish();
                  }
                });
              } else {
                onCreateFail(ErrorsMapper.getWebServiceErrorMessageFromCode(throwable.getMessage()));
                progressDialog.dismiss();
              }
            }));
          } else if (CREATE_STORE_REQUEST_CODE == 5) {
            /*
             * not multipart
             */
            setStoreData();
            progressDialog.show();
            mSubscriptions.add(SimpleSetStoreRequest.of(AptoideAccountManager.getAccessToken(),
                new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                    DataProvider.getContext()).getAptoideClientUUID(), storeId, storeTheme,
                storeDescription).observe().subscribe(answer -> {
              AptoideAccountManager.refreshAndSaveUserInfoData().subscribe(refreshed -> {
                progressDialog.dismiss();
                AptoideAccountManager.sendLoginBroadcast();
                finish();
              }, Throwable::printStackTrace);
            }, throwable -> {
              onCreateFail(ErrorsMapper.getWebServiceErrorMessageFromCode(throwable.getMessage()));
              progressDialog.dismiss();
            }));
          }
        }

    ));
    mSubscriptions.add(RxView.clicks(mSkip)
        .flatMap(click -> AptoideAccountManager.refreshAndSaveUserInfoData())
        .subscribe(refreshed -> {
          finish();
        }, throwable -> {
          finish();
        }));
  }

  /**
   * This method sets stores data for the request
   */
  private void setStoreData() {
    if (storeName.length() == 0){
      storeName = null;
    }

    if (storeTheme.equals("")) {
      storeTheme = null;
    }

    if (storeDescription.equals("")) {
      storeDescription = null;
    }
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
      /*
       * Multipart
       */
      setStoreData();
      mSubscriptions.add(SetStoreRequest.of(
          new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
              DataProvider.getContext()).getAptoideClientUUID(),
          AptoideAccountManager.getAccessToken(), storeName, storeTheme, storeAvatarPath)
          .observe()
          .timeout(90, TimeUnit.SECONDS)
          .subscribe(answer -> {
            AptoideAccountManager.refreshAndSaveUserInfoData().subscribe(refreshed -> {
              progressDialog.dismiss();
              AptoideAccountManager.sendLoginBroadcast();
              finish();
            }, throwable -> throwable.printStackTrace());
          }, throwable -> {
            if (throwable.getClass().equals(SocketTimeoutException.class)) {
              progressDialog.dismiss();
              ShowMessage.asLongObservableSnack(this, R.string.store_upload_photo_failed)
                  .subscribe(visibility -> {
                    if (visibility == ShowMessage.DISMISSED) {
                      finish();
                    }
                  });
            } else if (throwable.getClass().equals(TimeoutException.class)) {
              progressDialog.dismiss();
              ShowMessage.asLongObservableSnack(this, R.string.store_upload_photo_failed)
                  .subscribe(visibility -> {
                    if (visibility == ShowMessage.DISMISSED) {
                      finish();
                    }
                  });
            } else if (((AptoideWsV7Exception) throwable).getBaseResponse()
                .getErrors()
                .get(0)
                .getCode()
                .equals("API-1")) {
              progressDialog.dismiss();
              ShowMessage.asLongObservableSnack(this, R.string.ws_error_API_1).subscribe(visibility -> {
                if (visibility == ShowMessage.DISMISSED) {
                  finish();
                }
              });
            } else {
              progressDialog.dismiss();
              ShowMessage.asLongObservableSnack(this,
                  ErrorsMapper.getWebServiceErrorMessageFromCode(throwable.getMessage()))
                  .subscribe(visibility -> {
                    if (visibility == ShowMessage.DISMISSED) {
                      finish();
                    }
                  });
            }
            AptoideAccountManager.refreshAndSaveUserInfoData().subscribe(refreshed -> {
              progressDialog.dismiss();
              AptoideAccountManager.sendLoginBroadcast();
              finish();
            }, throwable1 -> throwable1.printStackTrace());
          }));
    } else if (CREATE_STORE_REQUEST_CODE == 2 || CREATE_STORE_REQUEST_CODE == 3) {
      /*
       * not multipart
       */
      setStoreData();
      SimpleSetStoreRequest.of(AptoideAccountManager.getAccessToken(),
          new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
              DataProvider.getContext()).getAptoideClientUUID(), storeName, storeTheme)
          .execute(answer -> {
            AptoideAccountManager.refreshAndSaveUserInfoData().subscribe(refreshed -> {
              progressDialog.dismiss();
              AptoideAccountManager.sendLoginBroadcast();
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

  @Override public String getRepoDescription() {
    return storeDescription == null ? "" : mStoreDescription.getText().toString();
  }

  private void setupThemeListeners() {
    mSubscriptions.add(RxView.clicks(mOrangeShape).subscribe(click -> {
        resetPreviousTickedTheme(storeTheme);
        mOrangeTick.setVisibility(View.VISIBLE);
        storeTheme = "orange";
    }));
    mSubscriptions.add(RxView.clicks(mGreenShape).subscribe(click -> {
        resetPreviousTickedTheme(storeTheme);
        mGreenTick.setVisibility(View.VISIBLE);
        storeTheme = "green";
    }));
    mSubscriptions.add(RxView.clicks(mRedShape).subscribe(click -> {
        resetPreviousTickedTheme(storeTheme);
        mRedTick.setVisibility(View.VISIBLE);
        storeTheme = "red";
    }));
    mSubscriptions.add(RxView.clicks(mIndigoShape).subscribe(click -> {
        resetPreviousTickedTheme(storeTheme);
        mIndigoTick.setVisibility(View.VISIBLE);
        storeTheme = "indigo";
    }));
    mSubscriptions.add(RxView.clicks(mTealShape).subscribe(click -> {
        resetPreviousTickedTheme(storeTheme);
        mTealTick.setVisibility(View.VISIBLE);
        storeTheme = "teal";
    }));
    mSubscriptions.add(RxView.clicks(mPinkShape).subscribe(click -> {
        resetPreviousTickedTheme(storeTheme);
        mPinkTick.setVisibility(View.VISIBLE);
        storeTheme = "pink";
    }));
    mSubscriptions.add(RxView.clicks(mLimeShape).subscribe(click -> {
      resetPreviousTickedTheme(storeTheme);
        mLimeTick.setVisibility(View.VISIBLE);
        storeTheme = "lime";
    }));
    mSubscriptions.add(RxView.clicks(mAmberShape).subscribe(click -> {
        resetPreviousTickedTheme(storeTheme);
        mAmberTick.setVisibility(View.VISIBLE);
        storeTheme = "amber";
    }));
    mSubscriptions.add(RxView.clicks(mBrownShape).subscribe(click -> {
        resetPreviousTickedTheme(storeTheme);
        mBrownTick.setVisibility(View.VISIBLE);
        storeTheme = "brown";
    }));
    mSubscriptions.add(RxView.clicks(mLightblueShape).subscribe(click -> {
        resetPreviousTickedTheme(storeTheme);
        mLightblueTick.setVisibility(View.VISIBLE);
        storeTheme = "light-blue";
    }));
  }

  /**
   * This method validates the data inserted (or not) when the create store button was pressed and
   * return a code for the corresponding request.
   */
  private int validateData() {
    if (from.equals("store")) {
      if (getRepoDescription().length() != 0 || getRepoTheme().length() > 0) {
        if (getRepoAvatar().length() != 0) {
          return CREATE_STORE_REQUEST_CODE = 4;
        } else {
          return CREATE_STORE_REQUEST_CODE = 5;
        }
      }
    } else {
      if (getRepoName().length() != 0) {
        if (getRepoAvatar().length() != 0) {
          CREATE_STORE_REQUEST_CODE = 1;
        } else if (getRepoTheme().length() != 0) {
          CREATE_STORE_REQUEST_CODE = 2;
        } else {
          CREATE_STORE_REQUEST_CODE = 3;
        }
      } else {
        CREATE_STORE_REQUEST_CODE = 0;
        onCreateFail(R.string.nothing_inserted_store);
      }
    }
    return CREATE_STORE_REQUEST_CODE;
  }

  /**
   * This method resets previously ticked theme tick
   */
  private void resetPreviousTickedTheme(String storeTheme) {
    switch (storeTheme) {
      case "orange":
        mOrangeTick.setVisibility(View.GONE);
        break;
      case "green":
        mGreenTick.setVisibility(View.GONE);
        break;
      case "red":
        mRedTick.setVisibility(View.GONE);
        break;
      case "indigo":
        mIndigoTick.setVisibility(View.GONE);
        break;
      case "teal":
        mTealTick.setVisibility(View.GONE);
        break;
      case "pink":
        mPinkTick.setVisibility(View.GONE);
        break;
      case "lime":
        mLimeTick.setVisibility(View.GONE);
        break;
      case "amber":
        mAmberTick.setVisibility(View.GONE);
        break;
      case "brown":
        mBrownTick.setVisibility(View.GONE);
        break;
      case "light-blue":
        mLightblueTick.setVisibility(View.GONE);
        break;
      default:
        break;
    }
  }
}
