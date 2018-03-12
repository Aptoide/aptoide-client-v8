package cm.aptoide.pt.view.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AdultContentAnalytics;
import cm.aptoide.pt.account.view.MyAccountView;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.file.FileManager;
import cm.aptoide.pt.notification.AptoideNotification;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.updates.UpdateRepository;
import cm.aptoide.pt.view.dialog.EditableTextDialog;
import cm.aptoide.pt.view.fragment.FragmentView;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by franciscocalado on 12/03/18.
 */

public class NewSettingsFragment extends FragmentView
    implements SharedPreferences.OnSharedPreferenceChangeListener, MyAccountView {

  protected Toolbar toolbar;
  @Inject NavigationTracker navigationTracker;
  private UpdateRepository repository;
  private String defaultThemeName;
  private AdultContentAnalytics adultContentAnalytics;
  private Context context;
  private CompositeSubscription subscriptions;
  private FileManager fileManager;
  private AptoideAccountManager accountManager;

  private RxAlertDialog adultContentConfirmationDialog;
  private EditableTextDialog enableAdultContentPinDialog;
  private EditableTextDialog setPinDialog;
  private EditableTextDialog removePinDialog;

  private Preference pinPreferenceView;
  private Preference removePinPreferenceView;
  private CheckBoxPreference adultContentPreferenceView;
  private CheckBoxPreference adultContentWithPinPreferenceView;
  private CheckBoxPreference socialCampaignNotifications;
  private boolean trackAnalytics;
  private NotificationSyncScheduler notificationSyncScheduler;
  private SharedPreferences sharedPreferences;
  private String marketName;
  private Database database;

  //Account views
  private View myAccountView;
  private View myStoreView;
  private ImageView myAccountAvatar;
  private ImageView myStoreAvatar;
  private TextView myAccountName;
  private TextView myAccountTitle;
  private TextView myStoreName;
  private TextView myStoreTitle;

  //TODO: Add string resources to the settings XML and fragment (ALL STRINGS HARDCODED!!!)

  public static Fragment newInstance() {
    return new NewSettingsFragment();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    adultContentAnalytics = application.getAdultContentAnalytics();
    defaultThemeName = application.getDefaultThemeName();
    marketName = application.getMarketName();
    trackAnalytics = true;
    database = ((AptoideApplication) getContext().getApplicationContext()).getDatabase();
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    fileManager = ((AptoideApplication) getContext().getApplicationContext()).getFileManager();
    subscriptions = new CompositeSubscription();
    adultContentConfirmationDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.are_you_adult)
            .setPositiveButton(R.string.yes)
            .setNegativeButton(R.string.no)
            .build();
    enableAdultContentPinDialog =
        new PinDialog.Builder(getContext()).setMessage(R.string.request_adult_pin)
            .setPositiveButton(R.string.all_button_ok)
            .setNegativeButton(R.string.cancel)
            .setView(R.layout.dialog_requestpin)
            .setEditText(R.id.pininput)
            .build();
    removePinDialog = new PinDialog.Builder(getContext()).setMessage(R.string.request_adult_pin)
        .setPositiveButton(R.string.all_button_ok)
        .setNegativeButton(R.string.cancel)
        .setView(R.layout.dialog_requestpin)
        .setEditText(R.id.pininput)
        .build();
    setPinDialog = new PinDialog.Builder(getContext()).setMessage(R.string.asksetadultpinmessage)
        .setPositiveButton(R.string.all_button_ok)
        .setNegativeButton(R.string.cancel)
        .setView(R.layout.dialog_requestpin)
        .setEditText(R.id.pininput)
        .build();

    notificationSyncScheduler =
        ((AptoideApplication) getContext().getApplicationContext()).getNotificationSyncScheduler();
    repository = RepositoryFactory.getUpdateRepository(getContext(),
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());
    //navigationTracker.registerScreen(ScreenTagHistory.Builder.build(this.getClass()
    //    .getSimpleName()));
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    myAccountView = view.findViewById(R.id.my_profile);
    myStoreView = view.findViewById(R.id.my_store);

    myAccountAvatar = (ImageView) myAccountView.findViewById(R.id.user_icon);
    myAccountName = (TextView) myAccountView.findViewById(R.id.name);
    myStoreAvatar = (ImageView) myStoreView.findViewById(R.id.user_icon);
    myStoreName = (TextView) myStoreView.findViewById(R.id.name);

    myStoreTitle = (TextView) myStoreView.findViewById(R.id.description);
    myStoreTitle.setText("My store");

    myAccountTitle = (TextView) myAccountView.findViewById(R.id.description);
    myAccountTitle.setText("My account");

    myAccountName.setText("Testing business");
    myStoreName.setText("Testing business - Store version");
  }

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.new_settings_layout, container, false);
  }

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

  }

  @Override public void showAccount(Account account) {

  }

  @Override public Observable<Void> signOutClick() {
    return null;
  }

  @Override public Observable<Void> moreNotificationsClick() {
    return null;
  }

  @Override public Observable<Void> storeClick() {
    return null;
  }

  @Override public Observable<Void> userClick() {
    return null;
  }

  @Override public Observable<AptoideNotification> notificationSelection() {
    return null;
  }

  @Override public void showNotifications(List<AptoideNotification> notifications) {

  }

  @Override public Observable<Void> editStoreClick() {
    return null;
  }

  @Override public Observable<GetStore> getStore() {
    return null;
  }

  @Override public Observable<Void> editUserProfileClick() {
    return null;
  }

  @Override public void showHeader() {

  }

  @Override public void hideHeader() {

  }

  @Override public void refreshUI(Store store) {

  }
}