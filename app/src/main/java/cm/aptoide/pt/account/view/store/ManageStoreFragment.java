package cm.aptoide.pt.account.view.store;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
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
import cm.aptoide.accountmanager.SocialLink;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.ImagePickerErrorHandler;
import cm.aptoide.pt.account.view.ImagePickerPresenter;
import cm.aptoide.pt.account.view.exception.InvalidImageException;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.presenter.CompositePresenter;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
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
import javax.inject.Inject;
import org.parceler.Parcels;
import rx.Observable;

public class ManageStoreFragment extends BackButtonFragment implements ManageStoreView {

  private static final String EXTRA_STORE_MODEL = "store_model";
  private static final String EXTRA_GO_TO_HOME = "go_to_home";
  private static final float STROKE_SIZE = 0.040f;
  private static final float SPACE_BETWEEN = 0.0f;
  @Inject ImagePickerPresenter imagePickerPresenter;
  @Inject ManageStorePresenter manageStorePresenter;
  @Inject ScreenOrientationManager orientationManager;
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
  private View facebookRow;
  private View twitchRow;
  private View twitterRow;
  private View youtubeRow;
  private RelativeLayout facebookTextAndPlus;
  private RelativeLayout twitchTextAndPlus;
  private RelativeLayout twitterTextAndPlus;
  private RelativeLayout youtubeTextAndPlus;
  private CustomTextInputLayout facebookUsernameWrapper;
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
  private List<Store.SocialChannelType> storeDeleteLinksList;

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
    getFragmentComponent(savedInstanceState).inject(this);
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
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    bindViews(view);
    setupToolbarTitle();
    setupThemeSelector();
    setupViewsDefaultDataUsingCurrentModel();

    registerSocialEditTextClickListeners();

    registerSocialFocusChangeListeners();

    attachPresenters();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  private void registerSocialFocusChangeListeners() {
    facebookUser.setOnFocusChangeListener(
        (v, focus) -> changeSocialRowTextAndAppearance(facebookUser, facebookText,
            R.style.Aptoide_TextView_Regular_XS_Facebook, facebookTextAndPlus,
            facebookUsernameWrapper, R.string.facebook, Store.SocialChannelType.FACEBOOK));

    twitchUser.setOnFocusChangeListener(
        (v, focus) -> changeSocialRowTextAndAppearance(twitchUser, twitchText,
            R.style.Aptoide_TextView_Regular_XS_Twitch, twitchTextAndPlus, twitchUsernameWrapper,
            R.string.twitch, Store.SocialChannelType.TWITCH));

    twitterUser.setOnFocusChangeListener(
        (v, focus) -> changeSocialRowTextAndAppearance(twitterUser, twitterText,
            R.style.Aptoide_TextView_Regular_XS_Twitter, twitterTextAndPlus, twitterUsernameWrapper,
            R.string.twitter, Store.SocialChannelType.TWITTER));

    youtubeUser.setOnFocusChangeListener(
        (v, focus) -> changeSocialRowTextAndAppearance(youtubeUser, youtubeText,
            R.style.Aptoide_TextView_Regular_XS_Youtube, youtubeTextAndPlus, youtubeUsernameWrapper,
            R.string.youtube, Store.SocialChannelType.YOUTUBE));
  }

  private void registerSocialEditTextClickListeners() {
    facebookRow.setOnClickListener(
        __ -> showEditTextHideTextView(facebookTextAndPlus, facebookUsernameWrapper, facebookUser));

    twitchRow.setOnClickListener(
        __ -> showEditTextHideTextView(twitchTextAndPlus, twitchUsernameWrapper, twitchUser));

    twitterRow.setOnClickListener(
        __ -> showEditTextHideTextView(twitterTextAndPlus, twitterUsernameWrapper, twitterUser));

    youtubeRow.setOnClickListener(
        __ -> showEditTextHideTextView(youtubeTextAndPlus, youtubeUsernameWrapper, youtubeUser));
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
        .map(__ -> updateAndGetStoreModel())
        .doOnNext(__ -> hideKeyboard());
  }

  @Override public Observable<ManageStoreViewModel> cancelClick() {
    return RxView.clicks(cancelChangesButton)
        .map(__ -> currentModel)
        .doOnNext(__ -> hideKeyboard());
  }

  @Override public void showWaitProgressBar() {
    orientationManager.lock();
    if (waitDialog != null && !waitDialog.isShowing()) {
      waitDialog.show();
    }
  }

  @Override public void dismissWaitProgressBar() {
    orientationManager.unlock();
    if (waitDialog != null && waitDialog.isShowing()) {
      waitDialog.dismiss();
    }
  }

  @Override public void showFacebookError(String error) {
    facebookUsernameWrapper.setErrorEnabled(true);
    facebookUsernameWrapper.setError(error);
    facebookRow.performClick();
  }

  @Override public void showTwitterError(String error) {
    twitterUsernameWrapper.setErrorEnabled(true);
    twitterUsernameWrapper.setError(error);
    twitterRow.performClick();
  }

  @Override public void showTwitchError(String error) {
    twitchUsernameWrapper.setErrorEnabled(true);
    twitchUsernameWrapper.setError(error);
    twitchRow.performClick();
  }

  @Override public void showYoutubeError(String error) {
    youtubeUsernameWrapper.setErrorEnabled(true);
    youtubeUsernameWrapper.setError(error);
    youtubeRow.performClick();
  }

  @Override public void showError(String errorMessage) {
    Snackbar.make(socialChannels, errorMessage, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showSuccessMessage() {
    Snackbar.make(socialChannels, getString(R.string.done), Snackbar.LENGTH_LONG)
        .show();
  }

  private void showEditTextHideTextView(RelativeLayout relativeLayout,
      CustomTextInputLayout customTextInputLayout, EditText editText) {
    relativeLayout.setVisibility(View.GONE);
    customTextInputLayout.setVisibility(View.VISIBLE);
    editText.requestFocus();
    showKeyboard(editText);
  }

  private void changeSocialRowTextAndAppearance(EditText editText, TextView textView,
      @StyleRes int style, RelativeLayout relativeLayout,
      CustomTextInputLayout customTextInputLayout, @StringRes int socialNetworkName,
      Store.SocialChannelType socialChannelType) {
    if (!editText.hasFocus()) {
      if (!editText.getText()
          .toString()
          .isEmpty()) {
        textView.setText(editText.getText()
            .toString());
        setTextViewAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha, textView);
      } else {
        String pojoUrl = getUrl(socialChannelType);
        if (pojoUrl != null) {
          textView.setText(removeBaseUrl(pojoUrl));
          setTextViewAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha, textView);
        } else {
          textView.setText(getString(socialNetworkName));
          setTextViewAppearance(style, textView);
        }
      }
      relativeLayout.setVisibility(View.VISIBLE);
      customTextInputLayout.setVisibility(View.GONE);
    }
  }

  private String getUrl(Store.SocialChannelType channelType) {
    for (SocialLink channel : currentModel.getSocialLinks()) {
      if (channel.getType()
          .equals(channelType)) {
        return channel.getUrl();
      }
    }
    return null;
  }

  private void setTextViewAppearance(int resId, TextView textView) {
    if (Build.VERSION.SDK_INT < 23) {
      textView.setTextAppearance(getContext(), resId);
    } else {
      textView.setTextAppearance(resId);
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

    hideKeyboard();

    clearSocialEditTextClickListeners();

    clearSocialFocusListeners();

    super.onDestroyView();
  }

  private void clearSocialFocusListeners() {
    facebookUser.setOnFocusChangeListener(null);
    twitchUser.setOnFocusChangeListener(null);
    twitterUser.setOnFocusChangeListener(null);
    youtubeUser.setOnFocusChangeListener(null);

    facebookUser = null;
    twitchUser = null;
    twitterUser = null;
    youtubeUser = null;
  }

  private void clearSocialEditTextClickListeners() {
    facebookRow.setOnClickListener(null);
    twitchRow.setOnClickListener(null);
    twitterRow.setOnClickListener(null);
    youtubeRow.setOnClickListener(null);

    facebookRow = null;
    twitchRow = null;
    twitterRow = null;
    youtubeRow = null;
  }

  @Override public void hideKeyboard() {
    super.hideKeyboard();
  }

  private void attachPresenters() {

    attachPresenter(
        new CompositePresenter(Arrays.asList(imagePickerPresenter, manageStorePresenter)));
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
        getActivity().getApplicationContext()
            .getString(R.string.please_wait_upload));
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
  }

  private ManageStoreViewModel updateAndGetStoreModel() {
    currentModel = ManageStoreViewModel.update(currentModel, storeName.getText()
        .toString(), storeDescription.getText()
        .toString());
    currentModel.setStoreTheme(themeSelectorAdapter.getSelectedTheme());
    currentModel.setSocialDelLinks(checkLinksToDelete());
    currentModel.setSocialLinks(getStoreLinks());
    return currentModel;
  }

  private List<Store.SocialChannelType> checkLinksToDelete() {
    storeDeleteLinksList = new ArrayList<>();
    List<SocialLink> socialLinks = currentModel.getSocialLinks();
    for (SocialLink socialLink : socialLinks) {
      setStoreDeleteLinksList(socialLink);
    }
    return storeDeleteLinksList;
  }

  private void setStoreDeleteLinksList(SocialLink socialLink) {
    if (!socialLink.getUrl()
        .isEmpty()) {
      if (socialLink.getType()
          .equals(Store.SocialChannelType.FACEBOOK) && facebookUser.getText()
          .toString()
          .isEmpty()) {
        storeDeleteLinksList.add(Store.SocialChannelType.FACEBOOK);
      }
      if (socialLink.getType()
          .equals(Store.SocialChannelType.TWITCH) && twitchUser.getText()
          .toString()
          .isEmpty()) {
        storeDeleteLinksList.add(Store.SocialChannelType.TWITCH);
      }
      if (socialLink.getType()
          .equals(Store.SocialChannelType.TWITTER) && twitterUser.getText()
          .toString()
          .isEmpty()) {
        storeDeleteLinksList.add(Store.SocialChannelType.TWITTER);
      }
      if (socialLink.getType()
          .equals(Store.SocialChannelType.YOUTUBE) && youtubeUser.getText()
          .toString()
          .isEmpty()) {
        storeDeleteLinksList.add(Store.SocialChannelType.YOUTUBE);
      }
    }
  }

  /**
   * This method will add a object to the returned list for each social channel already defined by
   * the user.
   */
  private List<SocialLink> getStoreLinks() {
    List<SocialLink> storeLinksList = new ArrayList<>();
    if (!TextUtils.isEmpty(facebookUser.getText()
        .toString())) {
      storeLinksList.add(new SocialLink(Store.SocialChannelType.FACEBOOK,
          setSocialChannelUrl(Store.SocialChannelType.FACEBOOK, facebookUser.getText()
              .toString())));
    }
    if (!TextUtils.isEmpty(twitchUser.getText()
        .toString())) {
      storeLinksList.add(new SocialLink(Store.SocialChannelType.TWITCH,
          setSocialChannelUrl(Store.SocialChannelType.TWITCH, twitchUser.getText()
              .toString())));
    }
    if (!TextUtils.isEmpty(twitterUser.getText()
        .toString())) {
      storeLinksList.add(new SocialLink(Store.SocialChannelType.TWITTER,
          setSocialChannelUrl(Store.SocialChannelType.TWITTER, twitterUser.getText()
              .toString())));
    }
    if (!TextUtils.isEmpty(youtubeUser.getText()
        .toString())) {
      storeLinksList.add(new SocialLink(Store.SocialChannelType.YOUTUBE,
          setSocialChannelUrl(Store.SocialChannelType.YOUTUBE, youtubeUser.getText()
              .toString())));
    }
    if (storeLinksList.isEmpty()) {
      return Collections.emptyList();
    }
    return storeLinksList;
  }

  private String setSocialChannelUrl(Store.SocialChannelType socialChannelType, String userInput) {
    switch (socialChannelType) {
      case FACEBOOK:
        if (!Patterns.WEB_URL.matcher(userInput)
            .matches()) {
          return ManageStoreViewModel.FACEBOOK_BASE_URL + userInput;
        }
      case TWITCH:
        if (!Patterns.WEB_URL.matcher(userInput)
            .matches()) {
          return ManageStoreViewModel.TWITCH_BASE_URL + userInput;
        }
      case TWITTER:
        if (!Patterns.WEB_URL.matcher(userInput)
            .matches()) {
          return ManageStoreViewModel.TWITTER_BASE_URL + userInput;
        }
      case YOUTUBE:
        if (!Patterns.WEB_URL.matcher(userInput)
            .matches()) {
          return ManageStoreViewModel.YOUTUBE_BASE_URL + userInput;
        }
    }

    if (!userInput.contains("http")) {
      return "https://" + userInput;
    }

    return userInput;
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
      setSocialChannelsUserNames();
      loadImageStateless(currentModel.getPictureUri());

      saveDataButton.setText(R.string.save_edit_store);
      cancelChangesButton.setText(R.string.cancel);
    }
  }

  private void setSocialChannelsUserNames() {
    List<SocialLink> storeLinksList = currentModel.getSocialLinks();
    if (!storeLinksList.isEmpty()) {
      for (SocialLink storeLinks : storeLinksList) {
        if (storeLinks.getType()
            .equals(Store.SocialChannelType.FACEBOOK)) {
          setStoreSocialSentUrl(storeLinks, facebookText, facebookEndRowIcon, facebookUser);
        } else if (storeLinks.getType()
            .equals(Store.SocialChannelType.TWITCH)) {
          setStoreSocialSentUrl(storeLinks, twitchText, twitchEndRowIcon, twitchUser);
        } else if (storeLinks.getType()
            .equals(Store.SocialChannelType.TWITTER)) {
          setStoreSocialSentUrl(storeLinks, twitterText, twitterEndRowIcon, twitterUser);
        } else if (storeLinks.getType()
            .equals(Store.SocialChannelType.YOUTUBE)) {
          setStoreSocialSentUrl(storeLinks, youtubeText, youtubeEndRowIcon, youtubeUser);
        }
      }
    }
  }

  /**
   * Sets social channel values that came from webservices. Meaning, social channels already
   * attached to the users store
   */
  private void setStoreSocialSentUrl(SocialLink storeLinks, TextView textView, ImageView imageView,
      EditText editText) {
    setTextViewAppearance(R.style.Aptoide_TextView_Regular_S_BlackAlpha, textView);
    textView.setText(removeBaseUrl(storeLinks.getUrl()));
    editText.setText(removeBaseUrl(storeLinks.getUrl()));
    imageView.setImageDrawable(getResources().getDrawable(R.drawable.edit_store_link_check));
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
    if (imm != null) {
      imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }
  }
}
