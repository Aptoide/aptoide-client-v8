package cm.aptoide.pt.account.view.user;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.ImagePickerErrorHandler;
import cm.aptoide.pt.account.view.ImagePickerPresenter;
import cm.aptoide.pt.account.view.exception.InvalidImageException;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.presenter.CompositePresenter;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.BackButtonFragment;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.dialog.ImagePickerDialog;
import com.jakewharton.rxbinding.support.design.widget.RxSnackbar;
import com.jakewharton.rxbinding.view.RxView;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import javax.inject.Inject;
import org.parceler.Parcel;
import org.parceler.Parcels;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class ManageUserFragment extends BackButtonFragment
    implements ManageUserView, NotBottomNavigationView {

  private static final String EXTRA_USER_MODEL = "user_model";
  private static final String EXTRA_IS_EDIT = "is_edit";
  @DrawableRes private static final int DEFAULT_IMAGE_PLACEHOLDER = R.drawable.create_user_avatar;
  @Inject ImagePickerPresenter imagePickerPresenter;
  @Inject ManageUserPresenter manageUserPresenter;
  @Inject ScreenOrientationManager orientationManager;
  private ImageView userPicture;
  private RelativeLayout userPictureLayout;
  private EditText userName;
  private Button createUserButton;
  private ProgressDialog uploadWaitDialog;
  private Button cancelUserProfile;
  private ViewModel currentModel;
  private boolean isEditProfile;
  private Toolbar toolbar;
  private ImagePickerDialog dialogFragment;
  private ImagePickerErrorHandler imagePickerErrorHandler;
  private View calendarLayout;
  private DatePickerDialog datePickerDialog;
  private TextView calendarDateView;
  private CheckBox newsletterCheckBox;
  private View birthdayLayout;
  private View newsLetterLayout;

  public static ManageUserFragment newInstanceToEdit() {
    return newInstance(true);
  }

  public static ManageUserFragment newInstanceToCreate() {
    return newInstance(false);
  }

  private static ManageUserFragment newInstance(boolean editUser) {
    Bundle args = new Bundle();
    args.putBoolean(EXTRA_IS_EDIT, editUser);

    ManageUserFragment manageUserFragment = new ManageUserFragment();
    manageUserFragment.setArguments(args);
    return manageUserFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    Context context = getContext();

    if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_USER_MODEL)) {
      currentModel = Parcels.unwrap(savedInstanceState.getParcelable(EXTRA_USER_MODEL));
    } else {
      currentModel = new ViewModel();
    }

    Bundle args = getArguments();

    isEditProfile = args != null && args.getBoolean(EXTRA_IS_EDIT, false);
    imagePickerErrorHandler = new ImagePickerErrorHandler(context);

    dialogFragment =
        new ImagePickerDialog.Builder(getContext()).setViewRes(ImagePickerDialog.LAYOUT)
            .setTitle(R.string.upload_dialog_title)
            .setNegativeButton(R.string.cancel)
            .setCameraButton(R.id.button_camera)
            .setGalleryButton(R.id.button_gallery)
            .build();

    uploadWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(context,
        context.getString(R.string.please_wait_upload));
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Calendar calendar = Calendar.getInstance();
    bindViews(view);
    setupToolbar();
    if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_USER_MODEL)) {
      currentModel = Parcels.unwrap(savedInstanceState.getParcelable(EXTRA_USER_MODEL));
      loadImageStateless(currentModel.getPictureUri());
      setUserName(currentModel.getName());
      if (!isEditProfile) {
        if (currentModel.hasDate()) {
          setupCalendar(calendar, currentModel.getYear(), currentModel.getMonth(),
              currentModel.getDay());
          calendarDateView.setText(currentModel.getDate());
        }
      }
    } else {
      currentModel = new ViewModel();
    }
    if (isEditProfile) {
      createUserButton.setText(getString(R.string.edit_profile_save_button));
      cancelUserProfile.setVisibility(View.VISIBLE);
    } else {
      birthdayLayout.setVisibility(View.VISIBLE);
      newsLetterLayout.setVisibility(View.VISIBLE);
      setupDatePickerDialog(calendar);
    }
    attachPresenters();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  private void setupDatePickerDialog(Calendar calendar) {
    DatePickerDialog.OnDateSetListener datePickerDialogListener =
        new DatePickerDialog.OnDateSetListener() {
          @Override public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            setupCalendar(calendar, year, month, day);
            setupCalendarDateString(year, month, day);
          }
        };
    datePickerDialog =
        new DatePickerDialog(getContext(), R.style.DatePickerDialog, datePickerDialogListener,
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH));
  }

  private void setupCalendarDateString(int year, int month, int day) {
    String calendarDate = year + "/" + month + "/" + day;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    Date date = null;
    try {
      date = dateFormat.parse(calendarDate);
    } catch (ParseException parseException) {
      Snackbar.make(createUserButton, getString(R.string.unknown_error), Snackbar.LENGTH_SHORT)
          .show();
      currentModel.setDateError();
    }
    if (date != null) {
      //Sets the date depending on the region of the user
      calendarDate = DateFormat.getDateInstance(DateFormat.SHORT)
          .format(date);
      //Sets the date on the format needed for the request
      calendarDateView.setText(calendarDate);

      currentModel.setDateInRequestFormat(dateFormat.format(date));
      currentModel.setDate(calendarDate);
      currentModel.setDate(year, month, day);
    }
  }

  private void setupCalendar(Calendar calendar, int year, int month, int day) {
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    calendar.set(Calendar.DAY_OF_MONTH, day);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_manage_user, container, false);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(EXTRA_USER_MODEL, Parcels.wrap(currentModel));
  }

  private void bindViews(View view) {
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    userPictureLayout = (RelativeLayout) view.findViewById(R.id.create_user_image_action);
    userName = (EditText) view.findViewById(R.id.create_user_username_inserted);
    createUserButton = (Button) view.findViewById(R.id.create_user_create_profile);
    cancelUserProfile = (Button) view.findViewById(R.id.create_user_cancel_button);
    userPicture = (ImageView) view.findViewById(R.id.create_user_image);
    birthdayLayout = view.findViewById(R.id.birthday_layout);
    newsLetterLayout = view.findViewById(R.id.newsletter_layout);
    calendarLayout = view.findViewById(R.id.calendar_layout);
    calendarDateView = (TextView) view.findViewById(R.id.calendar_date);
    newsletterCheckBox = (CheckBox) view.findViewById(R.id.newsletter_checkbox);
  }

  private void setupToolbar() {
    if (isEditProfile) {
      toolbar.setTitle(getString(R.string.edit_profile_title));
    } else {
      toolbar.setTitle(R.string.create_user_title);
    }
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(false);
    actionBar.setTitle(toolbar.getTitle());
  }

  private void attachPresenters() {

    attachPresenter(
        new CompositePresenter(Arrays.asList(manageUserPresenter, imagePickerPresenter)));
  }

  @Override public void onDestroyView() {
    if (uploadWaitDialog != null && uploadWaitDialog.isShowing()) {
      uploadWaitDialog.dismiss();
    }
    birthdayLayout = null;
    newsLetterLayout = null;
    calendarLayout = null;
    calendarDateView = null;
    newsletterCheckBox = null;
    super.onDestroyView();
  }

  @Override public void setUserName(String name) {
    currentModel.setName(name);
    userName.setText(name);
  }

  @Override public Observable<ViewModel> saveUserDataButtonClick() {
    return RxView.clicks(createUserButton)
        .map(__ -> updateModelAndGet());
  }

  @Override public Observable<Void> cancelButtonClick() {
    return RxView.clicks(cancelUserProfile);
  }

  @Override public void showProgressDialog() {
    orientationManager.lock();
    hideKeyboard();
    uploadWaitDialog.show();
  }

  @Override public void hideProgressDialog() {
    uploadWaitDialog.dismiss();
    orientationManager.unlock();
  }

  @Override public Completable showErrorMessage(String error) {
    return Single.fromCallable(() -> Snackbar.make(createUserButton, error, Snackbar.LENGTH_LONG))
        .flatMapCompletable(snackbar -> {
          snackbar.show();
          return RxSnackbar.dismisses(snackbar)
              .toCompletable();
        });
  }

  @Override public void loadImageStateless(String pictureUri) {
    currentModel.setPictureUri(pictureUri);
    ImageLoader.with(getActivity())
        .loadUsingCircleTransformAndPlaceholder(pictureUri, userPicture, DEFAULT_IMAGE_PLACEHOLDER);
  }

  @Override public Observable<Void> calendarLayoutClick() {
    return RxView.clicks(calendarLayout);
  }

  @Override public void showCalendar() {
    datePickerDialog.show();
  }

  @Override public void showEmptyBirthdayMessage() {
    Snackbar.make(createUserButton, getString(R.string.createuser_message_profile_no_dob),
        Snackbar.LENGTH_SHORT)
        .show();
  }

  /**
   * @param pictureUri Load image to UI and save image in model to handle configuration changes.
   */
  @Override public void loadImage(String pictureUri) {
    loadImageStateless(pictureUri);
    currentModel.setNewPicture(true);
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
    return RxView.clicks(userPictureLayout);
  }

  @Override public void dismissLoadImageDialog() {
    dialogFragment.dismiss();
  }

  @Nullable public ViewModel updateModelAndGet() {
    return ViewModel.from(currentModel, userName.getText()
        .toString(), calendarDateView.getText()
        .toString(), newsletterCheckBox.isChecked());
  }

  @Parcel protected static class ViewModel {
    String name;
    String pictureUri;
    String date;
    String requestDate;
    boolean hasNewsletterSubscribe;
    boolean hasNewPicture;
    boolean hasDateError;
    private int year;
    private int month;
    private int day;

    public ViewModel() {
      name = "";
      pictureUri = "";
      date = "";
      requestDate = "";
      year = -1;
      month = -1;
      day = -1;
      hasNewPicture = false;
      hasNewsletterSubscribe = false;
      hasDateError = false;
    }

    public ViewModel(String name, String pictureUri) {
      this.name = name;
      this.pictureUri = pictureUri;
      this.hasNewPicture = false;
      date = "";
      requestDate = "";
      year = -1;
      month = -1;
      day = -1;
      hasNewPicture = false;
      hasNewsletterSubscribe = false;
      hasDateError = false;
    }

    public static ViewModel from(ViewModel otherModel, String otherName, String date,
        boolean hasNewsletterSubscribe) {
      otherModel.setName(otherName);
      otherModel.setDate(date);
      otherModel.setNewsLetterSubscribe(hasNewsletterSubscribe);
      return otherModel;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPictureUri() {
      return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
      this.pictureUri = pictureUri;
    }

    public void setNewPicture(boolean hasNewPicture) {
      this.hasNewPicture = hasNewPicture;
    }

    public boolean hasNewPicture() {
      return hasNewPicture;
    }

    public String getDate() {
      return date;
    }

    public void setDate(String date) {
      this.date = date;
    }

    public String getRequestDate() {
      return requestDate;
    }

    public boolean getNewsletterSubscribe() {
      return hasNewsletterSubscribe;
    }

    void setNewsLetterSubscribe(boolean hasNewsLetterSubscribe) {
      this.hasNewsletterSubscribe = hasNewsLetterSubscribe;
    }

    void setDate(int year, int month, int day) {
      this.year = year;
      this.month = month;
      this.day = day;
    }

    public int getYear() {
      return year;
    }

    public int getMonth() {
      return month;
    }

    public int getDay() {
      return day;
    }

    public boolean hasDate() {
      return year != -1 && month != -1 && day != -1;
    }

    public void setDateInRequestFormat(String requestDate) {
      this.requestDate = requestDate;
    }

    public boolean hasDateError() {
      return hasDateError;
    }

    public void setDateError() {
      this.hasDateError = true;
    }
  }
}
