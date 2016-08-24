/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.StoreUtils;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;

/**
 * Created with IntelliJ IDEA. User: rmateus Date: 29-11-2013 Time: 15:56 To change this template use File | Settings |
 * File Templates.
 */
public class PrivateStoreDialog extends DialogFragment {

	public static final String TAG = "PrivateStoreDialog";
	private ProgressDialog loadingDialog;
	private String storeName;
	private String storeUser;
	private String storePassSha1;

	public static PrivateStoreDialog newInstance(Fragment returnFragment, int requestCode, String storeName) {
		final PrivateStoreDialog fragment = new PrivateStoreDialog();
		Bundle args = new Bundle();

		args.putString(BundleArgs.STORE_NAME.name(), storeName);

		fragment.setArguments(args);
		fragment.setTargetFragment(returnFragment, requestCode);
		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle args = getArguments();
		if (args != null) {
			storeName = args.getString(BundleArgs.STORE_NAME.name());
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_pvt_store, null);

		AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setView(rootView)
				.setTitle(getString(R.string.subscribe_pvt_store))
				.setPositiveButton(android.R.string.ok, null)
				.create();

		alertDialog.setOnShowListener(dialog->{

			Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
			b.setOnClickListener(view->{

				storeUser = ((EditText) rootView.findViewById(R.id.edit_store_username)).getText().toString();
				storePassSha1 = AptoideUtils.AlgorithmU.computeSha1(((EditText) rootView.findViewById(R.id
						.edit_store_password))
						.getText()
						.toString());

				StoreUtils.subscribeStore(buildRequest(), getStoreMeta->{
					getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
					dismissLoadingDialog();
					dismiss();
				}, e->{
					dismissLoadingDialog();
					if (e instanceof AptoideWsV7Exception) {
						BaseV7Response baseResponse = ((AptoideWsV7Exception) e).getBaseResponse();

						if (StoreUtils.PRIVATE_STORE_WRONG_CREDENTIALS.equals(baseResponse.getError().getCode())) {
							storeUser = null;
							storePassSha1 = null;
							ShowMessage.asSnack(rootView, R.string.ws_error_invalid_grant);
						}
					} else {
						e.printStackTrace();
						ShowMessage.asSnack(getView(), R.string.error_occured);
						dismiss();
					}
				});

				showLoadingDialog();
			});
		});
		return alertDialog;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(BundleArgs.STORE_NAME.name(), storeName);
	}

	private void dismissLoadingDialog() {
		loadingDialog.dismiss();
	}

	private void showLoadingDialog() {
		if (loadingDialog == null) {
			loadingDialog = GenericDialogs.createGenericPleaseWaitDialog(getActivity());
		}
		loadingDialog.show();
	}

	private GetStoreMetaRequest buildRequest() {
		return GetStoreMetaRequest.of(storeName, storeUser, storePassSha1);
	}

	private enum BundleArgs {
		STORE_NAME,
	}
}
