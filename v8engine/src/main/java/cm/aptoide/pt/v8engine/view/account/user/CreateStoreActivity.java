/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.view.account.user;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.ws.v3.CheckUserCredentialsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.SetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.account.ErrorsMapper;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import cm.aptoide.pt.v8engine.networking.StoreBodyInterceptor;
import cm.aptoide.pt.v8engine.view.MainActivity;
import cm.aptoide.pt.v8engine.view.account.AccountPermissionsBaseActivity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.rxbinding.view.RxView;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by pedroribeiro on 29/11/16.
 */

public class CreateStoreActivity extends AccountPermissionsBaseActivity {

  ProgressDialog progressDialog;
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
  private ImageView mDefaultShape;
  private ImageView mDefaultTick;
  private ImageView mBlackShape;
  private ImageView mBlackTick;
  private ImageView mBlueGreyShape;
  private ImageView mBlueGreyTick;
  private ImageView mDeepPurpleShape;
  private ImageView mDeepPurpleTick;
  private ImageView mLightGreenShape;
  private ImageView mLightGreenTick;
  private ImageView mGreyShape;
  private ImageView mGreyTick;
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
  private String storeName = "";
  private String storeAvatarPath;
  private String storeDescription;
  private long storeId;
  private boolean THEME_CLICKED_FLAG = false;
  private String storeTheme = "";
  private String from;
  private String storeRemoteUrl;

  private int CREATE_STORE_REQUEST_CODE = 0; //1: all (Multipart)  2: user and theme 3:user 4/5:edit
  private AptoideAccountManager accountManager;
  private BodyInterceptor<BaseBody> bodyInterceptorV7;
  private BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptorV3;
  private RequestBodyFactory requestBodyFactory;
  private ObjectMapper serializer;
  private IdsRepository idsRepository;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private OkHttpClient longTimeoutHttpClient;

  @Override public void onCreate(Bundle savedInstanceState) {
    getData();
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    accountManager = ((V8Engine) getApplicationContext()).getAccountManager();
    httpClient = ((V8Engine) getApplicationContext()).getDefaultClient();
    longTimeoutHttpClient = ((V8Engine) getApplicationContext()).getLongTimeoutClient();
    converterFactory = WebService.getDefaultConverter();
    bodyInterceptorV7 = ((V8Engine) getApplicationContext()).getBaseBodyInterceptorV7();
    bodyInterceptorV3 = ((V8Engine) getApplicationContext()).getBaseBodyInterceptorV3();
    idsRepository = ((V8Engine) getApplicationContext()).getIdsRepository();
    requestBodyFactory = new RequestBodyFactory();
    serializer = new ObjectMapper();
    serializer.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mSubscriptions = new CompositeSubscription();
    bindViews();
    editViews();
    setupToolbar();
    setupListeners();
    setupThemeListeners();
  }

  @Override public String getActivityTitle() {
    if (!from.equals("store")) {
      return getString(R.string.create_store_title);
    } else {
      return getString(R.string.edit_store_title);
    }
  }

  @Override public int getLayoutId() {
    return R.layout.activity_create_store;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mSubscriptions.clear();
    if (progressDialog != null) {
      if (progressDialog.isShowing()) {
        progressDialog.dismiss();
      }
    }
  }

  @Override public void loadImage(Uri imagePath) {
    ImageLoader.with(this).loadWithCircleTransform(imagePath, mStoreAvatar, false);
  }

  @Override public void showIconPropertiesError(String errors) {
    mSubscriptions.add(GenericDialogs.createGenericOkMessage(this,
        getString(R.string.image_requirements_error_popup_title), errors)
        .subscribe(__ -> {/* does nothing */}, err -> {
          CrashReport.getInstance().log(err);
        }));
  }

  private void getData() {
    from = getIntent().getStringExtra("from") == null ? "" : getIntent().getStringExtra("from");
    storeId = getIntent().getLongExtra("storeId", -1);
    storeRemoteUrl = getIntent().getStringExtra("storeAvatar");
    storeTheme = getIntent().getStringExtra("storeTheme") == null ? ""
        : getIntent().getStringExtra("storeTheme");
    storeDescription = getIntent() != null && getIntent().hasExtra("storeDescription") ? getIntent()
        .getStringExtra("storeDescription") : "";
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
    mDefaultShape = (ImageView) findViewById(R.id.create_store_theme_default);
    mDefaultTick = (ImageView) findViewById(R.id.create_store_theme_check_default);
    mBlackShape = (ImageView) findViewById(R.id.create_store_theme_black);
    mBlackTick = (ImageView) findViewById(R.id.create_store_theme_check_black);
    mBlueGreyShape = (ImageView) findViewById(R.id.create_store_theme_blue_grey);
    mBlueGreyTick = (ImageView) findViewById(R.id.create_store_theme_check_blue_grey);
    mDeepPurpleShape = (ImageView) findViewById(R.id.create_store_theme_deep_purple);
    mDeepPurpleTick = (ImageView) findViewById(R.id.create_store_theme_check_deep_purple);
    mLightGreenShape = (ImageView) findViewById(R.id.create_store_theme_light_green);
    mLightGreenTick = (ImageView) findViewById(R.id.create_store_theme_check_light_green);
    mGreyShape = (ImageView) findViewById(R.id.create_store_theme_grey);
    mGreyTick = (ImageView) findViewById(R.id.create_store_theme_check_grey);
  }

  /**
   * Changes views according to context (edit or create) and shows current values for the store
   * that's being edited
   */
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
      mStoreDescription.setText(storeDescription);
      if (storeRemoteUrl != null) {
        ImageLoader.with(this).loadUsingCircleTransform(storeRemoteUrl, mStoreAvatar);
      }
      handleThemeTick(storeTheme, "visible");
      mCreateStore.setText(R.string.save_edit_store);
      mSkip.setText(R.string.cancel);
    }
  }

  private void setupToolbar() {
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getActivityTitle());
    }
  }

  private void setupListeners() {
    mSubscriptions.add(
        RxView.clicks(mStoreAvatarLayout).subscribe(click -> chooseAvatarSource(), err -> {
          CrashReport.getInstance().log(err);
        }));
    mSubscriptions.add(RxView.clicks(mCreateStore).subscribe(click -> {
          AptoideUtils.SystemU.hideKeyboard(this);
          storeName = mStoreName.getText().toString().trim().toLowerCase();
          storeDescription = mStoreDescription.getText().toString().trim();
          validateData();
          progressDialog = GenericDialogs.createGenericPleaseWaitDialog(this,
              getApplicationContext().getString(R.string.please_wait_upload));
          if (CREATE_STORE_REQUEST_CODE == 1
              || CREATE_STORE_REQUEST_CODE == 2
              || CREATE_STORE_REQUEST_CODE == 3) {
            progressDialog.show();
            mSubscriptions.add(
                CheckUserCredentialsRequest.of(storeName, accountManager.getAccessToken(),
                    bodyInterceptorV3, httpClient, converterFactory).observe().subscribe(answer -> {
                  if (answer.hasErrors()) {
                    if (answer.getErrors() != null && answer.getErrors().size() > 0) {
                      progressDialog.dismiss();
                      if (answer.getErrors().get(0).code.equals("WOP-2")) {
                        mSubscriptions.add(GenericDialogs.createGenericContinueMessage(this, "",
                            getApplicationContext().getResources().getString(R.string.ws_error_WOP_2))
                            .subscribe(__ -> {/*does nothing*/}, err -> {
                              CrashReport.getInstance().log(err);
                            }));
                      } else if (answer.getErrors().get(0).code.equals("WOP-3")) {
                        ShowMessage.asSnack(this, ErrorsMapper.getWebServiceErrorMessageFromCode(
                            answer.getErrors().get(0).code));
                      } else {
                        ShowMessage.asObservableSnack(this,
                            ErrorsMapper.getWebServiceErrorMessageFromCode(
                                answer.getErrors().get(0).code)).subscribe(visibility -> {
                          if (visibility == ShowMessage.DISMISSED) {
                            goToMainActivity();
                          }
                        });
                      }
                    }
                  } else if (!(CREATE_STORE_REQUEST_CODE == 3)) {
                    onCreateSuccess(progressDialog);
                  } else {
                    progressDialog.dismiss();
                    ShowMessage.asLongObservableSnack(this, R.string.create_store_store_created)
                        .subscribe(visibility -> {
                          mSubscriptions.add(accountManager.syncCurrentAccount().subscribe(() -> {
                          }, err -> err.printStackTrace()));
                          if (visibility == ShowMessage.DISMISSED) {
                            Analytics.Account.createStore(!TextUtils.isEmpty(storeAvatarPath),
                                Analytics.Account.CreateStoreAction.CREATE);
                            goToMainActivity();
                          }
                        });
                  }
                }, throwable -> {
                  onCreateFail(ErrorsMapper.getWebServiceErrorMessageFromCode(throwable.getMessage()));
                  progressDialog.dismiss();
                }));
          } else {
            if (CREATE_STORE_REQUEST_CODE == 4) {
              setStoreData();
              progressDialog.show();
              mSubscriptions.add(
                  SetStoreRequest.of(accountManager.getAccessToken(), storeName, storeTheme,
                      storeAvatarPath, storeDescription, true, storeId, createStoreInterceptor(),
                      longTimeoutHttpClient, converterFactory).observe().subscribe(answer -> {
                    accountManager.syncCurrentAccount().subscribe(() -> {
                      progressDialog.dismiss();
                      goToMainActivity();
                    }, err -> err.printStackTrace());
                  }, throwable -> {
                    if (((AptoideWsV7Exception) throwable).getBaseResponse()
                        .getErrors()
                        .get(0)
                        .getCode()
                        .equals("API-1")) {
                      progressDialog.dismiss();
                      ShowMessage.asObservableSnack(this, R.string.ws_error_API_1)
                          .subscribe(visibility -> {
                            if (visibility == ShowMessage.DISMISSED) {
                              goToMainActivity();
                            }
                          });
                    } else {
                      onCreateFail(
                          ErrorsMapper.getWebServiceErrorMessageFromCode(throwable.getMessage()));
                      progressDialog.dismiss();
                    }
                  }));
            } else if (CREATE_STORE_REQUEST_CODE == 5) {
              /*
               * not multipart
               */
              setStoreData();
              progressDialog.show();
              mSubscriptions.add(
                  SimpleSetStoreRequest.of(storeId, storeTheme, storeDescription, bodyInterceptorV7,
                      httpClient, converterFactory).observe().subscribe(answer -> {
                    accountManager.syncCurrentAccount().subscribe(() -> {
                      progressDialog.dismiss();
                      goToMainActivity();
                    }, err -> err.printStackTrace());
                  }, throwable -> {
                    onCreateFail(
                        ErrorsMapper.getWebServiceErrorMessageFromCode(throwable.getMessage()));
                    progressDialog.dismiss();
                  }));
            }
          }
        }

    ));
    mSubscriptions.add(RxView.clicks(mSkip)
        .flatMap(click -> accountManager.syncCurrentAccount().andThen(Observable.just(true)))
        .doOnNext(__ -> Analytics.Account.createStore(!TextUtils.isEmpty(storeAvatarPath),
            Analytics.Account.CreateStoreAction.SKIP))
        .subscribe(__ -> goToMainActivity()));
  }

  private void setupThemeListeners() {
    mSubscriptions.add(RxView.clicks(mOrangeShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mOrangeTick.setVisibility(View.VISIBLE);
      storeTheme = "orange";
    }));
    mSubscriptions.add(RxView.clicks(mGreenShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mGreenTick.setVisibility(View.VISIBLE);
      storeTheme = "green";
    }));
    mSubscriptions.add(RxView.clicks(mRedShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mRedTick.setVisibility(View.VISIBLE);
      storeTheme = "red";
    }));
    mSubscriptions.add(RxView.clicks(mIndigoShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mIndigoTick.setVisibility(View.VISIBLE);
      storeTheme = "indigo";
    }));
    mSubscriptions.add(RxView.clicks(mTealShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mTealTick.setVisibility(View.VISIBLE);
      storeTheme = "teal";
    }));
    mSubscriptions.add(RxView.clicks(mPinkShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mPinkTick.setVisibility(View.VISIBLE);
      storeTheme = "pink";
    }));
    mSubscriptions.add(RxView.clicks(mLimeShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mLimeTick.setVisibility(View.VISIBLE);
      storeTheme = "lime";
    }));
    mSubscriptions.add(RxView.clicks(mAmberShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mAmberTick.setVisibility(View.VISIBLE);
      storeTheme = "amber";
    }));
    mSubscriptions.add(RxView.clicks(mBrownShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mBrownTick.setVisibility(View.VISIBLE);
      storeTheme = "brown";
    }));
    mSubscriptions.add(RxView.clicks(mLightblueShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mLightblueTick.setVisibility(View.VISIBLE);
      storeTheme = "light-blue";
    }));
    mSubscriptions.add(RxView.clicks(mDefaultShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mDefaultTick.setVisibility(View.VISIBLE);
      storeTheme = "default";
    }));
    mSubscriptions.add(RxView.clicks(mBlackShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mBlackTick.setVisibility(View.VISIBLE);
      storeTheme = "black";
    }));
    mSubscriptions.add(RxView.clicks(mBlueGreyShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mBlueGreyTick.setVisibility(View.VISIBLE);
      storeTheme = "blue-grey";
    }));
    mSubscriptions.add(RxView.clicks(mDeepPurpleShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mDeepPurpleTick.setVisibility(View.VISIBLE);
      storeTheme = "deep-purple";
    }));
    mSubscriptions.add(RxView.clicks(mLightGreenShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mLightGreenTick.setVisibility(View.VISIBLE);
      storeTheme = "light-green";
    }));
    mSubscriptions.add(RxView.clicks(mGreyShape).subscribe(click -> {
      handleThemeTick(storeTheme, "gone");
      mGreyTick.setVisibility(View.VISIBLE);
      storeTheme = "grey";
    }));
  }

  /**
   * This method resets previously ticked theme tick
   */
  private void handleThemeTick(String storeTheme, String visibility) {
    int visible = View.GONE;
    if (visibility.equals("visible")) {
      visible = View.VISIBLE;
    }
    switch (storeTheme) {
      case "orange":
        mOrangeTick.setVisibility(visible);
        break;
      case "green":
        mGreenTick.setVisibility(visible);
        break;
      case "red":
        mRedTick.setVisibility(visible);
        break;
      case "indigo":
        mIndigoTick.setVisibility(visible);
        break;
      case "teal":
        mTealTick.setVisibility(visible);
        break;
      case "pink":
        mPinkTick.setVisibility(visible);
        break;
      case "lime":
        mLimeTick.setVisibility(visible);
        break;
      case "amber":
        mAmberTick.setVisibility(visible);
        break;
      case "brown":
        mBrownTick.setVisibility(visible);
        break;
      case "light-blue":
        mLightblueTick.setVisibility(visible);
        break;
      case "default":
        mDefaultTick.setVisibility(visible);
        break;
      case "black":
        mBlackTick.setVisibility(visible);
        break;
      case "blue-grey":
        mBlueGreyTick.setVisibility(visible);
        break;
      case "deep-purple":
        mDeepPurpleTick.setVisibility(visible);
        break;
      case "grey":
        mGreyTick.setVisibility(visible);
        break;
      case "light-green":
        mLightGreenTick.setVisibility(visible);
        break;
      default:
        break;
    }
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

  private void goToMainActivity() {
    final Intent i = new Intent(this, MainActivity.class);
    startActivity(i);
    finish();
  }

  private void onCreateSuccess(ProgressDialog progressDialog) {
    ShowMessage.asSnack(this, R.string.create_store_store_created);
    if (CREATE_STORE_REQUEST_CODE == 1) {
      /*
       * Multipart
       */
      setStoreData();
      mSubscriptions.add(SetStoreRequest.of(accountManager.getAccessToken(), storeName, storeTheme,
          storeAvatarPath, createStoreInterceptor(), longTimeoutHttpClient, converterFactory)
          .observe()
          .timeout(90, TimeUnit.SECONDS)
          .subscribe(answer -> {
            accountManager.syncCurrentAccount().subscribe(() -> {
              progressDialog.dismiss();
              goToMainActivity();
            }, throwable -> throwable.printStackTrace());
          }, throwable -> {
            if (throwable.getClass().equals(SocketTimeoutException.class)) {
              progressDialog.dismiss();
              ShowMessage.asLongObservableSnack(this, R.string.store_upload_photo_failed)
                  .subscribe(visibility -> {
                    if (visibility == ShowMessage.DISMISSED) {
                      goToMainActivity();
                    }
                  });
            } else if (throwable.getClass().equals(TimeoutException.class)) {
              progressDialog.dismiss();
              ShowMessage.asLongObservableSnack(this, R.string.store_upload_photo_failed)
                  .subscribe(visibility -> {
                    if (visibility == ShowMessage.DISMISSED) {
                      goToMainActivity();
                    }
                  });
            } else if (((AptoideWsV7Exception) throwable).getBaseResponse()
                .getErrors()
                .get(0)
                .getCode()
                .equals("API-1")) {
              progressDialog.dismiss();
              ShowMessage.asLongObservableSnack(this, R.string.ws_error_API_1)
                  .subscribe(visibility -> {
                    if (visibility == ShowMessage.DISMISSED) {
                      goToMainActivity();
                    }
                  });
            } else {
              progressDialog.dismiss();
              ShowMessage.asLongObservableSnack(this,
                  ErrorsMapper.getWebServiceErrorMessageFromCode(throwable.getMessage()))
                  .subscribe(visibility -> {
                    if (visibility == ShowMessage.DISMISSED) {
                      goToMainActivity();
                    }
                  });
            }
            accountManager.syncCurrentAccount().subscribe(() -> {
              progressDialog.dismiss();
              goToMainActivity();
            }, throwable1 -> throwable1.printStackTrace());
          }));
    } else if (CREATE_STORE_REQUEST_CODE == 2 || CREATE_STORE_REQUEST_CODE == 3) {
      /*
       * not multipart
       */
      setStoreData();
      SimpleSetStoreRequest.of(storeName, storeTheme, bodyInterceptorV7, httpClient,
          converterFactory).execute(answer -> {
        accountManager.syncCurrentAccount().subscribe(() -> {
          progressDialog.dismiss();
          goToMainActivity();
        }, err -> err.printStackTrace());
      }, throwable -> {
        onCreateFail(ErrorsMapper.getWebServiceErrorMessageFromCode(throwable.getMessage()));
        accountManager.syncCurrentAccount().subscribe(() -> {
          progressDialog.dismiss();
        }, err -> err.printStackTrace());
      });
    }
  }

  private void onCreateFail(@StringRes int reason) {
    ShowMessage.asSnack(content, reason);
  }

  /**
   * This method sets stores data for the request
   */
  private void setStoreData() {
    if (storeName.length() == 0) {
      storeName = null;
    }

    if (storeTheme.equals("")) {
      storeTheme = null;
    }

    if (storeDescription.equals("")) {
      storeDescription = null;
    }
  }

  @NonNull private StoreBodyInterceptor<BaseBody> createStoreInterceptor() {
    return new StoreBodyInterceptor(idsRepository.getUniqueIdentifier(), accountManager,
        requestBodyFactory, storeTheme, storeDescription, serializer);
  }

  private String getRepoTheme() {
    return storeTheme == null ? "" : storeTheme;
  }

  private String getRepoDescription() {
    return storeDescription == null ? "" : mStoreDescription.getText().toString().trim();
  }

  private String getRepoAvatar() {
    return storeAvatarPath == null ? "" : storeAvatarPath;
  }

  private String getRepoName() {
    return storeName == null ? "" : mStoreName.getText().toString().trim().toLowerCase();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    FileUtils fileUtils = new FileUtils();
    Uri avatarUrl = null;
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      avatarUrl = getPhotoFileUri(photoAvatar);
      storeAvatarPath = fileUtils.getPath(avatarUrl, getApplicationContext());
    } else if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
      avatarUrl = data.getData();
      storeAvatarPath = fileUtils.getPath(avatarUrl, getApplicationContext());
    }
    checkAvatarRequirements(storeAvatarPath, avatarUrl);
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
}
