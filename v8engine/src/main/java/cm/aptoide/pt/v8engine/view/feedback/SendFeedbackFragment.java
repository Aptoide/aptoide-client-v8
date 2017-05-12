/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.feedback;

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
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.fragment.BaseToolbarFragment;
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
  private final String KEY_SCREENSHOT_PATH = "screenShotPath";
  private Button sendFeedbackBtn;
  private CheckBox logsAndScreenshotsCb;
  private String screenShotPath;
  private EditText messageBodyEdit;
  private EditText subgectEdit;
  private Subscription unManagedSubscription;

  public static SendFeedbackFragment newInstance(String screenshotFilePath) {
    SendFeedbackFragment sendFeedbackFragment = new SendFeedbackFragment();
    Bundle bundle = new Bundle();
    bundle.putString(SCREENSHOT_PATH, screenshotFilePath);
    sendFeedbackFragment.setArguments(bundle);
    return sendFeedbackFragment;
  }

  @Override public void setArguments(Bundle args) {
    super.setArguments(args);
    screenShotPath = args.getString(SCREENSHOT_PATH);
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
    setHasOptionsMenu(true);
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

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_SCREENSHOT_PATH, screenShotPath);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void sendFeedback() {
    if (isContentValid()) {
      Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
      emailIntent.setType("message/rfc822");

      emailIntent.putExtra(Intent.EXTRA_EMAIL,
          new String[] { V8Engine.getConfiguration().getFeedbackEmail() });

      //String versionName = "";
      //Installed installed = DeprecatedDatabase.InstalledQ.get(getContext().getPackageName(), realm);
      //if (installed != null) {
      //  versionName = installed.getVersionName();
      //}
      //
      //emailIntent.putExtra(Intent.EXTRA_SUBJECT,
      //    "[Feedback]-" + versionName + ": " + subgectEdit.getText().toString());
      //emailIntent.putExtra(Intent.EXTRA_TEXT, messageBodyEdit.getText().toString());
      ////attach screenshots and logs
      //if (logsAndScreenshotsCb.isChecked()) {
      //  ArrayList<Uri> uris = new ArrayList<Uri>();
      //  File ss = new File(screenShotPath);
      //  if (ss != null) {
      //    Uri urifile = Uri.fromFile(ss);
      //    uris.add(urifile);
      //  }
      //
      //  File logs = AptoideUtils.SystemU.readLogs(Application.getConfiguration().getCachePath(),
      //      LOGS_FILE_NAME);
      //  if (logs != null) {
      //    Uri urifile = Uri.fromFile(logs);
      //    uris.add(urifile);
      //  }
      //  emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
      //}
      //try {
      //  startActivity(emailIntent);
      //  getActivity().onBackPressed();
      //  //				Analytics.SendFeedback.sendFeedback();
      //} catch (android.content.ActivityNotFoundException ex) {
      //  ShowMessage.asSnack(getView(), R.string.feedback_no_email);
      //}

      InstalledAccessor installedAccessor = AccessorFactory.getAccessorFor(Installed.class);
      //attach screenshots and logs
      //				Analytics.SendFeedback.sendFeedback();
      unManagedSubscription = installedAccessor.get(getContext().getPackageName())
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
              File logs = AptoideUtils.SystemU.readLogs(Application.getConfiguration()
                  .getCachePath(), LOGS_FILE_NAME);
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
      photoURI = FileProvider.getUriForFile(getContext(), V8Engine.getConfiguration()
          .getAppId() + ".provider", file);
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
