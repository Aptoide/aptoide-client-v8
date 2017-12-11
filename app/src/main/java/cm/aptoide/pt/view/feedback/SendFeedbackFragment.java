/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.view.feedback;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.fragment.BaseToolbarFragment;
import com.jakewharton.rxbinding.view.RxView;
import java.io.File;
import java.util.ArrayList;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by trinkes on 7/12/16.
 */
public class SendFeedbackFragment extends BaseToolbarFragment {

  public static final String SCREENSHOT_PATH = "SCREENSHOT_PATH";
  public static final String LOGS_FILE_NAME = "logs.txt";
  private static final String CARD_ID = "card_id";
  private final String KEY_SCREENSHOT_PATH = "screenShotPath";
  private Button sendFeedbackBtn;
  private CheckBox logsAndScreenshotsCb;
  private String screenShotPath;
  private EditText messageBodyEdit;
  private EditText subgectEdit;
  private Subscription unManagedSubscription;
  private InstalledRepository installedRepository;
  private String cardId;
  private NavigationTracker aptoideNavigationTracker;

  public static SendFeedbackFragment newInstance(String screenshotFilePath) {
    SendFeedbackFragment sendFeedbackFragment = new SendFeedbackFragment();
    Bundle bundle = new Bundle();
    bundle.putString(SCREENSHOT_PATH, screenshotFilePath);
    sendFeedbackFragment.setArguments(bundle);
    return sendFeedbackFragment;
  }

  public static SendFeedbackFragment newInstance(String screenShotPath, String cardId) {
    SendFeedbackFragment sendFeedbackFragment = new SendFeedbackFragment();
    Bundle bundle = new Bundle();
    bundle.putString(SCREENSHOT_PATH, screenShotPath);
    bundle.putString(CARD_ID, cardId);
    sendFeedbackFragment.setArguments(bundle);
    return sendFeedbackFragment;
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void setArguments(Bundle args) {
    super.setArguments(args);
    screenShotPath = args.getString(SCREENSHOT_PATH);
    cardId = args.getString(CARD_ID);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_SCREENSHOT_PATH, screenShotPath);
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    installedRepository =
        RepositoryFactory.getInstalledRepository(getContext().getApplicationContext());
    aptoideNavigationTracker =
        ((AptoideApplication) getContext().getApplicationContext()).getNavigationTracker();
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (savedInstanceState != null) {
      screenShotPath = savedInstanceState.getString(KEY_SCREENSHOT_PATH);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (unManagedSubscription != null) {
      unManagedSubscription.unsubscribe();
    }
  }

  @Override public void setupViews() {
    super.setupViews();
    RxView.clicks(sendFeedbackBtn)
        .subscribe(aVoid -> sendFeedback(), err -> {
          CrashReport.getInstance()
              .log(err);
        });
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    subgectEdit = (EditText) view.findViewById(R.id.FeedBackSubject);
    messageBodyEdit = (EditText) view.findViewById(R.id.FeedBacktext);
    sendFeedbackBtn = (Button) view.findViewById(R.id.FeedBackSendButton);
    logsAndScreenshotsCb = (CheckBox) view.findViewById(R.id.FeedBackCheckBox);
  }

  private void sendFeedback() {
    if (isContentValid()) {
      Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
      emailIntent.setType("message/rfc822");

      final AptoideApplication application =
          (AptoideApplication) getContext().getApplicationContext();
      emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {
          application.getFeedbackEmail()
      });
      final String cachePath = application.getCachePath();
      unManagedSubscription = installedRepository.getInstalled(getContext().getPackageName())
          .first()
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(installed1 -> {
            String versionName = "";
            if (installed1 != null) {
              versionName = installed1.getVersionName();
            }

            emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                "[Feedback]-" + versionName + ": " + subgectEdit.getText()
                    .toString());
            emailIntent.putExtra(Intent.EXTRA_TEXT, messageBodyEdit.getText()
                .toString());
            //attach screenshots and logs
            if (logsAndScreenshotsCb.isChecked()) {
              ArrayList<Uri> uris = new ArrayList<Uri>();
              if (screenShotPath != null) {
                File ss = new File(screenShotPath);
                uris.add(getUriFromFile(ss));
              }
              File logs = AptoideUtils.SystemU.readLogs(cachePath, LOGS_FILE_NAME,
                  cardId != null ? cardId : aptoideNavigationTracker.getPrettyScreenHistory());

              if (logs != null) {
                uris.add(getUriFromFile(logs));
              }

              emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            }
            try {
              startActivity(emailIntent);
              getActivity().onBackPressed();
              //				Analytics.SendFeedback.sendFeedback();
            } catch (ActivityNotFoundException ex) {
              ShowMessage.asSnack(getView(), R.string.feedback_no_email);
            }
          });
    } else {
      ShowMessage.asSnack(getView(), R.string.feedback_not_valid);
    }
  }

  public boolean isContentValid() {
    return !TextUtils.isEmpty(subgectEdit.getText()
        .toString());
  }

  private Uri getUriFromFile(File file) {
    Uri photoURI;
    //read: https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en
    if (Build.VERSION.SDK_INT > 23) {
      //content://....apk for nougat
      photoURI =
          FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", file);
    } else {
      //file://....apk for < nougat
      photoURI = Uri.fromFile(file);
    }
    return photoURI;
  }

  @Override public int getContentViewId() {
    return R.layout.activity_feed_back;
  }
}
