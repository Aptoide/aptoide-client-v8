/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.jakewharton.rxbinding.view.RxView;

import java.io.File;
import java.util.ArrayList;

import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.BaseToolbarFragment;

/**
 * Created by trinkes on 7/12/16.
 */
public class SendFeedbackFragment extends BaseToolbarFragment {

	public static final String SCREENSHOT_PATH = "SCREENSHOT_PATH";
	public static final String LOGS_FILE_NAME = "logs.txt";
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

	@Override
	public void setupViews() {
		super.setupViews();
		setHasOptionsMenu(true);
		RxView.clicks(sendFeedbackBtn).subscribe(aVoid -> sendFeedback());
	}

	@Override
	public void setupToolbar() {
		super.setupToolbar();
		if (toolbar != null) {
			ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		subgectEdit = (EditText) view.findViewById(R.id.FeedBackSubject);
		messageBodyEdit = (EditText) view.findViewById(R.id.FeedBacktext);
		sendFeedbackBtn = (Button) view.findViewById(R.id.FeedBackSendButton);
		logsAndScreenshotsCb = (CheckBox) view.findViewById(R.id.FeedBackCheckBox);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void sendFeedback() {
		if (isContentValid()) {
			Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
			emailIntent.setType("message/rfc822");

			emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@aptoide.com"});

			String versionName = "";

			Installed installed = DeprecatedDatabase.InstalledQ.get(getContext().getPackageName(), realm);
			if (installed != null) {
				versionName = installed.getVersionName();
			}

			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Feedback]-" + versionName + ": " + subgectEdit.getText().toString());
			emailIntent.putExtra(Intent.EXTRA_TEXT, messageBodyEdit.getText().toString());
			//attach screenshots and logs
			if (logsAndScreenshotsCb.isChecked()) {
				ArrayList<Uri> uris = new ArrayList<Uri>();
				File ss = new File(screenShotPath);
				if (ss != null) {
					Uri urifile = Uri.fromFile(ss);
					uris.add(urifile);
				}

				File logs = AptoideUtils.SystemU.readLogs(Application.getConfiguration().getCachePath(), LOGS_FILE_NAME);
				if (logs != null) {
					Uri urifile = Uri.fromFile(logs);
					uris.add(urifile);
				}
				emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			}
			try {
				startActivity(emailIntent);
				getActivity().onBackPressed();
				//				Analytics.SendFeedback.sendFeedback();
			} catch (android.content.ActivityNotFoundException ex) {
				ShowMessage.asSnack(getView(), R.string.feedback_no_email);
			}
		} else {
			ShowMessage.asSnack(getView(), R.string.feedback_not_valid);
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public int getContentViewId() {
		return R.layout.activity_feed_back;
	}

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		screenShotPath = args.getString(SCREENSHOT_PATH);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean isContentValid() {
		return !TextUtils.isEmpty(subgectEdit.getText().toString());
	}
}
