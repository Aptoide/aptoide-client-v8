package cm.aptoide.pt.account.view.store;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.ImagePickerErrorHandler;
import cm.aptoide.pt.account.view.ImagePickerNavigator;
import cm.aptoide.pt.account.view.ImagePickerPresenter;
import cm.aptoide.pt.account.view.ImageValidator;
import cm.aptoide.pt.account.view.PhotoFileGenerator;
import cm.aptoide.pt.account.view.UriToPathResolver;
import cm.aptoide.pt.account.view.exception.InvalidImageException;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.permission.AccountPermissionProvider;
import cm.aptoide.pt.permission.PermissionProvider;
import cm.aptoide.pt.presenter.CompositePresenter;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.BackButtonFragment;
import cm.aptoide.pt.view.CustomTextInputLayout;
import cm.aptoide.pt.view.custom.DividerItemDecoration;
import cm.aptoide.pt.view.dialog.ImagePickerDialog;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.parceler.Parcels;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static cm.aptoide.pt.dataprovider.model.v7.store.Store.SocialChannelType.FACEBOOK;
import static cm.aptoide.pt.dataprovider.model.v7.store.Store.SocialChannelType.TWITCH;
import static cm.aptoide.pt.dataprovider.model.v7.store.Store.SocialChannelType.TWITTER;
import static cm.aptoide.pt.dataprovider.model.v7.store.Store.SocialChannelType.YOUTUBE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ManageStoreFragment extends BackButtonFragment implements ManageStoreView {

  private static final String EXTRA_STORE_MODEL = "store_model";
  private static final String EXTRA_GO_TO_HOME = "go_to_home";
  private static final float STROKE_SIZE = 0.040f;
  private static final float SPACE_BETWEEN = 0.0f;

  private TextView chooseStoreNameTitle;
  private View selectStoreImageButton;
  private ImageView storeImage;
  private Button saveDataButton;
  private Button cancelChangesButton;
  private EditText storeName;
  private EditText storeDescription;
  private ProgressDialog waitDialog;

  private RecyclerView themeSelectorView;
  private ThemeSelectorViewAdapter themeSelectorAdapter;

  private ManageStoreViewModel currentModel;
  private boolean goToHome;
  private Toolbar toolbar;
  private ImagePickerDialog dialogFragment;
  private ImagePickerErrorHandler imagePickerErrorHandler;
  private ManageStoreNavigator manageStoreNavigator;
  private ImageValidator imageValidator;
  private ImagePickerNavigator imagePickerNavigator;
  private UriToPathResolver uriToPathResolver;
  private CrashReport crashReport;
  private AccountPermissionProvider accountPermissionProvider;
  private StoreManager storeManager;
  private String packageName;
  private String fileProviderAuthority;
  private PhotoFileGenerator photoFileGenerator;
  private TextInputLayout storeDescriptionWrapper;
  private View facebookRow;
  private CustomTextInputLayout facebookUsernameWrapper;
  private View twitchRow;
  private View twitterRow;
  private View youtubeRow;
  private RelativeLayout facebookTextAndPlus;
  private RelativeLayout twitchTextAndPlus;
  private RelativeLayout twitterTextAndPlus;
  private RelativeLayout youtubeTextAndPlus;
  private CustomTextInputLayout twitchUsernameWrapper;
  private CustomTextInputLayout twitterUsernameWrapper;
  private CustomTextInputLayout youtubeUsernameWrapper;
  private LinearLayout socialChannels;
  private EditText facebookUser;
  private EditText twitchUser;
  private EditText twitterUser;
  private EditText youtubeUser;
  private TextView facebookText;
  private TextView twitchText;
  private TextView twitterText;
  private TextView youtubeText;
  private ImageView facebookEndRowIcon;
  private ImageView twitchEndRowIcon;
  private ImageView twitterEndRowIcon;
  private ImageView youtubeEndRowIcon;
  private String savedFacebookText = "";
  private String savedTwitchText = "";
  private String savedTwitterText = "";
  private String savedYoutubeText = "";

  public static ManageStoreFragment newInstance(ManageStoreViewModel storeModel, boolean goToHome) {
    Bundle args = new Bundle();
    args.putParcelable(EXTRA_STORE_MODEL, Parcels.wrap(storeModel));
    args.putBoolean(EXTRA_GO_TO_HOME, goToHome);

    ManageStoreFragment fragment = new ManageStoreFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    currentModel = Parcels.unwrap(getArguments().getParcelable(EXTRA_STORE_MODEL));
    goToHome = getArguments().getBoolean(EXTRA_GO_TO_HOME, true);

    dialogFragment =
        new ImagePickerDialog.Builder(getContext()).setViewRes(ImagePickerDialog.LAYOUT)
            .setTitle(R.string.upload_dialog_title)
            .setNegativeButton(R.string.cancel)
            .setCameraButton(R.id.button_camera)
            .setGalleryButton(R.id.button_gallery)
            .build();

    imagePickerErrorHandler = new ImagePickerErrorHandler(getContext());
    accountPermissionProvider = new AccountPermissionProvider(((PermissionProvider) getActivity()));
    storeManager = ((AptoideApplication) getActivity().getApplicationContext()).getStoreManager();
    packageName = (getActivity().getApplicationContext()).getPackageName();
    fileProviderAuthority = BuildConfig.APPLICATION_ID + ".provider";
    photoFileGenerator = new PhotoFileGenerator(getActivity(),
        getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileProviderAuthority);
    crashReport = CrashReport.getInstance();
    uriToPathResolver = new UriToPathResolver(getActivity().getContentResolver());
    imagePickerNavigator = new ImagePickerNavigator(getActivityNavigator());
    imageValidator = new ImageValidator(ImageLoader.with(getActivity()), Schedulers.computation());
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    manageStoreNavigator =
        new ManageStoreNavigator(getFragmentNavigator(), application.getDefaultStoreName(),
            application.getDefaultThemeName());
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    bindViews(view);
    setupToolbarTitle();
    setupThemeSelector();
    setupViewsDefaultDataUsingCurrentModel();
    attachPresenters();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  /**
   * @param pictureUri Load image to UI and save image in model to handle configuration changes.
   */
  @Override public void loadImage(String pictureUri) {
    loadImageStateless(pictureUri);
    currentModel.setNewAvatar(true);
  }

  @Override public Observable<DialogInterface> dialogCameraSelected() {
    return dialogFragment.cameraSelected();
  }

  @Override public Observable<DialogInterface> dialogGallerySelected() {
    return dialogFragment.gallerySelected();
  }

  @Override public void showImagePickerDialog() {
    dialogFragment.show();
  }

  @Override public void showIconPropertiesError(InvalidImageException exception) {
    imagePickerErrorHandler.showIconPropertiesError(exception)
        .compose(bindUntilEvent(LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  @Override public Observable<Void> selectStoreImageClick() {
    return RxView.clicks(selectStoreImageButton);
  }

  @Override public void dismissLoadImageDialog() {
    dialogFragment.dismiss();
  }

  @Override public void loadImageStateless(String pictureUri) {
    ImageLoader.with(getActivity())
        .loadWithShadowCircleTransform(pictureUri, storeImage,
            getResources().getColor(R.color.aptoide_orange), SPACE_BETWEEN, STROKE_SIZE);
    currentModel.setPictureUri(pictureUri);
  }

  @Override public Observable<ManageStoreViewModel> saveDataClick() {
    return RxView.clicks(saveDataButton)
        .map(__ -> updateAndGetStoreModel());
  }

  @Override public Observable<Void> cancelClick() {
    return RxView.clicks(cancelChangesButton);
  }

  @Override public Observable<Void> facebookClick() {
    return RxView.clicks(facebookRow);
  }

  @Override public Observable<Void> twitchClick() {
    return RxView.clicks(twitchRow);
  }

  @Override public Observable<Void> twitterClick() {
    return RxView.clicks(twitterRow);
  }

  @Override public Observable<Void> youtubeClick() {
    return RxView.clicks(youtubeRow);
  }

  @Override public Completable showError(@StringRes int errorMessage) {
    return ShowMessage.asLongObservableSnack(this, errorMessage);
  }

  @Override public Completable showGenericError() {
    return ShowMessage.asLongObservableSnack(this, R.string.all_message_general_error);
  }

  @Override public void showWaitProgressBar() {
    if (waitDialog != null && !waitDialog.isShowing()) {
      waitDialog.show();
    }
  }

  @Override public void dismissWaitProgressBar() {
    if (waitDialog != null && waitDialog.isShowing()) {
      waitDialog.dismiss();
    }
  }

  public void manageFacebookViews() {
    facebookTextAndPlus.setVisibility(View.GONE);
    facebookUsernameWrapper.setVisibility(View.VISIBLE);
    facebookUser.requestFocus();
    showKeyboard(facebookUser);
  }

  @Override public void manageTwitchViews() {
    twitchTextAndPlus.setVisibility(View.GONE);
    twitchUsernameWrapper.setVisibility(View.VISIBLE);
    twitchUser.requestFocus();
    showKeyboard(twitchUser);
  }

  @Override public void manageTwitterViews() {
    twitterTextAndPlus.setVisibility(View.GONE);
    twitterUsernameWrapper.setVisibility(View.VISIBLE);
    twitterUser.requestFocus();
    showKeyboard(twitterUser);
  }

  @Override public void manageYoutubeViews() {
    youtubeTextAndPlus.setVisibility(View.GONE);
    youtubeUsernameWrapper.setVisibility(View.VISIBLE);
    youtubeUser.requestFocus();
    showKeyboard(youtubeUser);
  }

  @Override public void setViewLinkErrors(List<BaseV7Response.StoreLinks> storeLinks) {
    for (BaseV7Response.StoreLinks storeLink : storeLinks) {
      setViewError(storeLink.getType()
          .toString());
    }
  }

  @Override public Observable<Boolean> facebookUserFocusChanged() {
    return RxView.focusChanges(facebookUser);
  }

  @Override public void changeFacebookUI() {
    if (!facebookUser.hasFocus()) {
      if (!facebookUser.getText()
          .toString()
          .isEmpty()) {
        facebookText.setText(facebookUser.getText()
            .toString());
        setFacebookTextInputAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha);
      } else {
        String facebookPojoUrl = getUrl(FACEBOOK);
        if (facebookPojoUrl != null) {
          facebookText.setText(removeBaseUrl(facebookPojoUrl));
          setFacebookTextInputAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha);
        } else {
          facebookText.setText(getString(R.string.facebook));
          setFacebookTextInputAppearance(R.style.Aptoide_TextView_Regular_XS_Facebook);
        }
      }
      facebookTextAndPlus.setVisibility(View.VISIBLE);
      facebookUsernameWrapper.setVisibility(View.GONE);
    }
  }

  @Override public Observable<Boolean> twitchUserFocusChanged() {
    return RxView.focusChanges(twitchUser);
  }

  @Override public void changeTwitchUI() {
    if (!twitchUser.hasFocus()) {
      if (!twitchUser.getText()
          .toString()
          .isEmpty()) {
        twitchText.setText(twitchUser.getText()
            .toString());
        setTwitchTextInputAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha);
      } else {
        String twitchPojoUrl = getUrl(TWITCH);
        if (twitchPojoUrl != null) {
          twitchText.setText(removeBaseUrl(twitchPojoUrl));
          setTwitchTextInputAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha);
        } else {
          twitchText.setText(getString(R.string.twitch));
          setTwitchTextInputAppearance(R.style.Aptoide_TextView_Regular_XS_Twitch);
        }
      }
      twitchTextAndPlus.setVisibility(View.VISIBLE);
      twitchUsernameWrapper.setVisibility(View.GONE);
    }
  }

  @Override public Observable<Boolean> twitterUserFocusChanged() {
    return RxView.focusChanges(twitterUser);
  }

  @Override public void changeTwitterUI() {
    if (!twitterUser.hasFocus()) {
      if (!twitterUser.getText()
          .toString()
          .isEmpty()) {
        twitterText.setText(twitterUser.getText()
            .toString());
        setTwitterInputTextAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha);
      } else {
        String twitterPojoUrl = getUrl(TWITTER);
        if (twitterPojoUrl != null) {
          twitterText.setText(removeBaseUrl(twitterPojoUrl));
          setTwitterInputTextAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha);
        } else {
          twitterText.setText(getString(R.string.twitter));
          setTwitterInputTextAppearance(R.style.Aptoide_TextView_Regular_XS_Twitter);
        }
      }
      twitterTextAndPlus.setVisibility(View.VISIBLE);
      twitterUsernameWrapper.setVisibility(View.GONE);
    }
  }

  @Override public Observable<Boolean> youtubeUserFocusChanged() {
    return RxView.focusChanges(youtubeUser);
  }

  @Override public void changeYoutubeUI() {
    if (!youtubeUser.hasFocus()) {
      if (!youtubeUser.getText()
          .toString()
          .isEmpty()) {
        youtubeText.setText(youtubeUser.getText()
            .toString());
        setYoutubeTextInputAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha);
      } else {
        String youtubePojoUrl = getUrl(YOUTUBE);
        if (youtubePojoUrl != null) {
          youtubeText.setText(removeBaseUrl(youtubePojoUrl));
          setYoutubeTextInputAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha);
        } else {
          youtubeText.setText(getString(R.string.youtube));
          setYoutubeTextInputAppearance(R.style.Aptoide_TextView_Regular_XS_Youtube);
        }
      }
      youtubeTextAndPlus.setVisibility(View.VISIBLE);
      youtubeUsernameWrapper.setVisibility(View.GONE);
    }
  }

  private String getUrl(Store.SocialChannelType channelType) {
    for (SimpleSetStoreRequest.StoreLinks channel : currentModel.getStoreLinks()) {
      if (channel.getType()
          .equals(channelType)) {
        return channel.getUrl();
      }
    }
    return null;
  }

  private void setFacebookTextInputAppearance(int resId) {
    if (Build.VERSION.SDK_INT < 23) {
      facebookText.setTextAppearance(getContext(), resId);
    } else {
      facebookText.setTextAppearance(resId);
    }
  }

  private void setTwitchTextInputAppearance(int resId) {
    if (Build.VERSION.SDK_INT < 23) {
      twitchText.setTextAppearance(getContext(), resId);
    } else {
      twitchText.setTextAppearance(resId);
    }
  }

  private void setTwitterInputTextAppearance(int resId) {
    if (Build.VERSION.SDK_INT < 23) {
      twitterText.setTextAppearance(getContext(), resId);
    } else {
      twitterText.setTextAppearance(resId);
    }
  }

  private void setYoutubeTextInputAppearance(int resId) {
    if (Build.VERSION.SDK_INT < 23) {
      youtubeText.setTextAppearance(getContext(), resId);
    } else {
      youtubeText.setTextAppearance(resId);
    }
  }

  private void setViewError(String error) {
    if (error.equals(StoreValidationException.FACEBOOK_1)) {
      facebookUsernameWrapper.setErrorEnabled(true);
      facebookUsernameWrapper.setError(getString(R.string.edit_store_social_link_invalid_url_text));
    } else if (error.equals(StoreValidationException.FACEBOOK_2)) {
      facebookUsernameWrapper.setErrorEnabled(true);
      facebookUsernameWrapper.setError(
          getString(R.string.edit_store_page_doesnt_exist_error_short));
    } else if (error.equals(StoreValidationException.TWITCH_1)) {
      twitchUsernameWrapper.setErrorEnabled(true);
      twitchUsernameWrapper.setError(getString(R.string.edit_store_social_link_invalid_url_text));
    } else if (error.equals(StoreValidationException.TWITCH_2)) {
      twitchUsernameWrapper.setErrorEnabled(true);
      twitchUsernameWrapper.setError(getString(R.string.edit_store_social_link_channel_error));
    } else if (error.equals(StoreValidationException.TWITTER_1)) {
      twitterUsernameWrapper.setErrorEnabled(true);
      twitterUsernameWrapper.setError(getString(R.string.edit_store_social_link_invalid_url_text));
    } else if (error.equals(StoreValidationException.TWITTER_2)) {
      twitterUsernameWrapper.setErrorEnabled(true);
      twitterUsernameWrapper.setError(getString(R.string.edit_store_page_doesnt_exist_error_short));
    } else if (error.equals(StoreValidationException.YOUTUBE_1)) {
      youtubeUsernameWrapper.setErrorEnabled(true);
      youtubeUsernameWrapper.setError(getString(R.string.edit_store_social_link_invalid_url_text));
    } else if (error.equals(StoreValidationException.YOUTUBE_2)) {
      youtubeUsernameWrapper.setErrorEnabled(true);
      youtubeUsernameWrapper.setError(getString(R.string.edit_store_social_link_channel_error));
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_manage_store, container, false);
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (savedInstanceState != null) {
      try {
        currentModel = Parcels.unwrap(savedInstanceState.getParcelable(EXTRA_STORE_MODEL));
      } catch (NullPointerException ex) {
        currentModel = new ManageStoreViewModel();
      }
      goToHome = savedInstanceState.getBoolean(EXTRA_GO_TO_HOME, true);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(EXTRA_STORE_MODEL, Parcels.wrap(currentModel));
    outState.putBoolean(EXTRA_GO_TO_HOME, goToHome);
  }

  @Override public void onDestroyView() {
    dismissWaitProgressBar();
    if (dialogFragment != null) {
      dialogFragment.dismiss();
      dialogFragment = null;
    }
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    hideKeyboard();
  }

  @Override public void hideKeyboard() {
    super.hideKeyboard();
  }

  private void attachPresenters() {
    final ImagePickerPresenter imagePickerPresenter =
        new ImagePickerPresenter(this, crashReport, accountPermissionProvider, photoFileGenerator,
            imageValidator, AndroidSchedulers.mainThread(), uriToPathResolver, imagePickerNavigator,
            getActivity().getContentResolver(), ImageLoader.with(getContext()));

    final ManageStorePresenter presenter =
        new ManageStorePresenter(this, crashReport, storeManager, getResources(), uriToPathResolver,
            packageName, manageStoreNavigator, goToHome);

    attachPresenter(new CompositePresenter(Arrays.asList(imagePickerPresenter, presenter)));
  }

  public void setupThemeSelector() {

    themeSelectorView.setLayoutManager(
        new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    PublishRelay<StoreTheme> storeThemePublishRelay = PublishRelay.create();
    themeSelectorAdapter =
        new ThemeSelectorViewAdapter(storeThemePublishRelay, StoreTheme.getThemesFromVersion(8));
    themeSelectorView.setAdapter(themeSelectorAdapter);

    themeSelectorAdapter.storeThemeSelection()
        .doOnNext(storeTheme -> currentModel.setStoreTheme(storeTheme))
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe();

    themeSelectorView.addItemDecoration(new DividerItemDecoration(getContext(), 8,
        DividerItemDecoration.LEFT | DividerItemDecoration.RIGHT));

    themeSelectorAdapter.selectTheme(currentModel.getStoreTheme());
  }

  public void setupToolbarTitle() {
    toolbar.setTitle(getViewTitle(currentModel));

    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(false);
    actionBar.setTitle(toolbar.getTitle());
  }

  public void bindViews(View view) {
    chooseStoreNameTitle = (TextView) view.findViewById(R.id.create_store_choose_name_title);
    selectStoreImageButton = view.findViewById(R.id.create_store_image_action);
    storeImage = (ImageView) view.findViewById(R.id.create_store_image);
    storeName = (EditText) view.findViewById(R.id.create_store_name);
    storeDescription = (EditText) view.findViewById(R.id.edit_store_description);
    cancelChangesButton = (Button) view.findViewById(R.id.create_store_skip);
    saveDataButton = (Button) view.findViewById(R.id.create_store_action);
    themeSelectorView = (RecyclerView) view.findViewById(R.id.theme_selector);
    socialChannels = (LinearLayout) view.findViewById(R.id.edit_store_social_channels);
    facebookRow = view.findViewById(R.id.edit_store_facebook);
    facebookTextAndPlus =
        (RelativeLayout) view.findViewById(R.id.edit_store_facebook_text_plus_wrapper);
    facebookUsernameWrapper =
        (CustomTextInputLayout) view.findViewById(R.id.edit_store_facebook_username_wrapper);
    facebookUser = (EditText) view.findViewById(R.id.edit_store_facebook_username);
    facebookText = (TextView) view.findViewById(R.id.edit_store_facebook_title);
    facebookEndRowIcon = (ImageView) view.findViewById(R.id.edit_store_facebook_plus);
    twitchEndRowIcon = (ImageView) view.findViewById(R.id.edit_store_twitch_plus);
    twitchTextAndPlus =
        (RelativeLayout) view.findViewById(R.id.edit_store_twitch_text_plus_wrapper);
    twitchUsernameWrapper =
        (CustomTextInputLayout) view.findViewById(R.id.edit_store_twitch_username_wrapper);
    twitchUser = (EditText) view.findViewById(R.id.edit_store_twitch_username);
    twitchText = (TextView) view.findViewById(R.id.edit_store_twitch_title);
    twitchRow = view.findViewById(R.id.edit_store_twitch);
    twitterRow = view.findViewById(R.id.edit_store_twitter);
    twitterTextAndPlus =
        (RelativeLayout) view.findViewById(R.id.edit_store_twitter_text_plus_wrapper);
    twitterUsernameWrapper =
        (CustomTextInputLayout) view.findViewById(R.id.edit_store_twitter_username_wrapper);
    twitterUser = (EditText) view.findViewById(R.id.edit_store_twitter_username);
    twitterText = (TextView) view.findViewById(R.id.edit_store_twitter_title);
    twitterEndRowIcon = (ImageView) view.findViewById(R.id.edit_store_twitter_plus);
    youtubeRow = view.findViewById(R.id.edit_store_youtube);
    youtubeTextAndPlus =
        (RelativeLayout) view.findViewById(R.id.edit_store_youtube_text_plus_wrapper);
    youtubeUsernameWrapper =
        (CustomTextInputLayout) view.findViewById(R.id.edit_store_youtube_username_wrapper);
    youtubeUser = (EditText) view.findViewById(R.id.edit_store_youtube_username);
    youtubeText = (TextView) view.findViewById(R.id.edit_store_youtube_title);
    youtubeEndRowIcon = (ImageView) view.findViewById(R.id.edit_store_youtube_plus);

    waitDialog = GenericDialogs.createGenericPleaseWaitDialog(getActivity(),
        getApplicationContext().getString(R.string.please_wait_upload));
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
  }

  private ManageStoreViewModel updateAndGetStoreModel() {
    currentModel = ManageStoreViewModel.update(currentModel, storeName.getText()
        .toString(), storeDescription.getText()
        .toString());
    currentModel.setStoreTheme(themeSelectorAdapter.getSelectedTheme());
    currentModel.setStoreLinks(getStoreLinks());
    return currentModel;
  }

  /**
   * This method will add a object to the returned list for each social channel already defined by
   * the user.
   *
   * @return
   */
  private List<SimpleSetStoreRequest.StoreLinks> getStoreLinks() {
    List<SimpleSetStoreRequest.StoreLinks> storeLinksList = new ArrayList<>();
    if (!TextUtils.isEmpty(facebookUser.getText()
        .toString())) {
      storeLinksList.add(new SimpleSetStoreRequest.StoreLinks(FACEBOOK,
          setFacebookUrl(facebookUser.getText()
              .toString())));
    }
    if (!TextUtils.isEmpty(twitchUser.getText()
        .toString())) {
      storeLinksList.add(new SimpleSetStoreRequest.StoreLinks(Store.SocialChannelType.TWITCH,
          setTwitchUrl(twitchUser.getText()
              .toString())));
    }
    if (!TextUtils.isEmpty(twitterUser.getText()
        .toString())) {
      storeLinksList.add(new SimpleSetStoreRequest.StoreLinks(Store.SocialChannelType.TWITTER,
          setTwitterUrl(twitterUser.getText()
              .toString())));
    }
    if (!TextUtils.isEmpty(youtubeUser.getText()
        .toString())) {
      storeLinksList.add(new SimpleSetStoreRequest.StoreLinks(Store.SocialChannelType.YOUTUBE,
          setYoutubeUrl(youtubeUser.getText()
              .toString())));
    }
    if (storeLinksList.isEmpty()) {
      return Collections.emptyList();
    }
    return storeLinksList;
  }

  private String setYoutubeUrl(String youtubeUsername) {
    if (youtubeUsername.contains("http")) {
      return youtubeUsername;
    }
    return ManageStoreViewModel.YOUTUBE_BASE_URL + youtubeUsername;
  }

  private String setTwitterUrl(String twitterUsername) {
    if (twitterUsername.contains("http")) {
      return twitterUsername;
    }
    return ManageStoreViewModel.TWITTER_BASE_URL + twitterUsername;
  }

  private String setTwitchUrl(String twitchUsername) {
    if (twitchUsername.contains("http")) {
      return twitchUsername;
    }
    return ManageStoreViewModel.TWITCH_BASE_URL + twitchUsername;
  }

  private String setFacebookUrl(String facebookUsername) {
    if (facebookUsername.contains("http")) {
      return facebookUsername;
    }
    return ManageStoreViewModel.FACEBOOK_BASE_URL + facebookUsername;
  }

  private void setupViewsDefaultDataUsingCurrentModel() {

    storeName.setText(currentModel.getStoreName());

    if (!currentModel.storeExists()) {
      String appName = getString(R.string.app_name);
      chooseStoreNameTitle.setText(
          AptoideUtils.StringU.getFormattedString(R.string.create_store_name, getResources(),
              appName));
    } else {
      chooseStoreNameTitle.setText(
          AptoideUtils.StringU.getFormattedString(R.string.description, getResources()));
      storeName.setVisibility(View.GONE);
      storeDescription.setVisibility(View.VISIBLE);
      storeDescription.setText(currentModel.getStoreDescription());
      socialChannels.setVisibility(View.VISIBLE);
      setSocialChannelsUsernames();
      loadImageStateless(currentModel.getPictureUri());

      saveDataButton.setText(R.string.save_edit_store);
      cancelChangesButton.setText(R.string.cancel);
    }
  }

  private void setSocialChannelsUsernames() {
    List<SimpleSetStoreRequest.StoreLinks> storeLinksList = currentModel.getStoreLinks();
    if (!storeLinksList.isEmpty()) {
      for (SimpleSetStoreRequest.StoreLinks storeLinks : storeLinksList) {
        if (storeLinks.getType()
            .equals(FACEBOOK)) {
          savedFacebookText = removeBaseUrl(storeLinks.getUrl());
          setFacebookTextInputAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha);
          facebookText.setText(savedFacebookText);
          facebookEndRowIcon.setImageDrawable(
              getResources().getDrawable(R.drawable.edit_store_link_check));
        } else if (storeLinks.getType()
            .equals(Store.SocialChannelType.TWITCH)) {
          savedTwitchText = removeBaseUrl(storeLinks.getUrl());
          setTwitchTextInputAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha);
          twitchText.setText(savedTwitchText);
          twitchEndRowIcon.setImageDrawable(
              getResources().getDrawable(R.drawable.edit_store_link_check));
        } else if (storeLinks.getType()
            .equals(Store.SocialChannelType.TWITTER)) {
          savedTwitterText = removeBaseUrl(storeLinks.getUrl());
          setTwitterInputTextAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha);
          twitterText.setText(savedTwitterText);
          twitterEndRowIcon.setImageDrawable(
              getResources().getDrawable(R.drawable.edit_store_link_check));
        } else if (storeLinks.getType()
            .equals(Store.SocialChannelType.YOUTUBE)) {
          savedYoutubeText = removeBaseUrl(storeLinks.getUrl());
          setYoutubeTextInputAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha);
          youtubeText.setText(savedYoutubeText);
          youtubeEndRowIcon.setImageDrawable(
              getResources().getDrawable(R.drawable.edit_store_link_check));
        }
      }
    }
  }

  private String removeBaseUrl(String url) {
    String[] splitUrl = url.split("/");
    return splitUrl[splitUrl.length - 1];
  }

  private String getViewTitle(ManageStoreViewModel storeModel) {
    if (!storeModel.storeExists()) {
      return getString(R.string.create_store_title);
    } else {
      return getString(R.string.edit_store_title);
    }
  }

  private void showKeyboard(EditText editText) {
    InputMethodManager imm =
        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
  }
}