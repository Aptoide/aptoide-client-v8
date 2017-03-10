/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.BaseToolbarFragment;
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
  private final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
  private Button sendFeedbackBtn;
  private CheckBox logsAndScreenshotsCb;
  private String screenShotPath;
  private EditText messageBodyEdit;
  private EditText subgectEdit;

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

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void setupViews() {
    super.setupViews();
    setHasOptionsMenu(true);
    RxView.clicks(sendFeedbackBtn).subscribe(aVoid -> askPermission(), err -> {
      CrashReport.getInstance().log(err);
    });
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(SCREENSHOT_PATH, screenShotPath);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      screenShotPath = savedInstanceState.getString(SCREENSHOT_PATH);
    }
    return super.onCreateView(inflater, container, savedInstanceState);
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

  private void askPermission() {
    if (isContentValid()) {
      if (logsAndScreenshotsCb.isChecked() && !(getActivity().checkCallingOrSelfPermission(
          EXTERNAL_STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED)) {
        requestPermissions(new String[] { EXTERNAL_STORAGE_PERMISSION }, 1);
      } else {
        sendFeedback();
      }
    } else {
      ShowMessage.asSnack(getView(), R.string.feedback_not_valid);
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case 1: {
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && permissions[0].equals(EXTERNAL_STORAGE_PERMISSION)) {
          sendFeedback();
        } else {
          logsAndScreenshotsCb.setChecked(false);
          sendFeedback();
        }
        break;
      }
      default:
        ShowMessage.asSnack(getView(), R.string.unknown_error);
        break;
    }
  }

  private void sendFeedback() {
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
    Subscription unManagedSubscription = installedAccessor.get(getContext().getPackageName())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> {
          Logger.e("SendFeedbackFragment", throwable);
          CrashReport.getInstance().log(throwable);
        })
        .subscribe(installed1 -> {
          String versionName = "";
          if (installed1 != null) {
            versionName = installed1.getVersionName();
          }

          emailIntent.putExtra(Intent.EXTRA_SUBJECT,
              "[Feedback]-" + versionName + ": " + subgectEdit.getText().toString());
          emailIntent.putExtra(Intent.EXTRA_TEXT, messageBodyEdit.getText().toString());
          //attach screenshots and logs
          if (logsAndScreenshotsCb.isChecked()) {
            ArrayList<Uri> uris = new ArrayList<Uri>();
            if (screenShotPath != null) {
              File ss = new File(screenShotPath);
              if (ss != null) {
                uris.add(getUriFromFile(ss));
              }
            }

            File logs = AptoideUtils.SystemU.readLogs(Application.getConfiguration().getCachePath(),
                LOGS_FILE_NAME);
            if (logs != null) {
              uris.add(getUriFromFile(logs));
            }
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
          }
          try {
            startActivity(emailIntent);
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            getActivity().getSupportFragmentManager().popBackStack();
            //				Analytics.SendFeedback.sendFeedback();
          } catch (ActivityNotFoundException ex) {
            ShowMessage.asSnack(getView(), R.string.feedback_no_email);
          }
        });
  }

  public boolean isContentValid() {
    return !TextUtils.isEmpty(subgectEdit.getText().toString());
  }

  private Uri getUriFromFile(File file) {
    Uri photoURI;
    //read: https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en
    if (Build.VERSION.SDK_INT > 23) {
      //content://....apk for nougat
      photoURI = FileProvider.getUriForFile(getContext(),
          V8Engine.getConfiguration().getAppId() + ".provider", file);
    } else {
      //file://....apk for < nougat
      photoURI = Uri.fromFile(file);
    }
    return photoURI;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override public int getContentViewId() {
    return R.layout.activity_feed_back;
  }
}
