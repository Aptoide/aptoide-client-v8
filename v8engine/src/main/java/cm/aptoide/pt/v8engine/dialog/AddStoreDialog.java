/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/06/2016.
 */

package cm.aptoide.pt.v8engine.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.StoreUtils;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;

/**
 * Created with IntelliJ IDEA. User: rmateus Date: 18-10-2013 Time: 17:27 To change this template use File | Settings |
 * File Templates.
 */

// // TODO: 19-05-2016 neuro IMPORTS TODOS MARADOS!
public class AddStoreDialog extends DialogFragment {

	private final int PRIVATE_STORE_REQUEST_CODE = 20;
	private String storeName;
	private Dialog loadingDialog;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PRIVATE_STORE_REQUEST_CODE) {
			switch (resultCode) {
				case Activity.RESULT_OK:
					dismiss();
					break;
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (getDialog() != null) {
			getDialog().getWindow().setTitle(getString(R.string.subscribe_store));
		}

		return inflater.inflate(R.layout.dialog_add_store, container, false);
	}

	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(R.id.button_dialog_add_store).setOnClickListener(v->{
			String givenStoreName = ((EditText) view.findViewById(R.id.edit_store_uri)).getText().toString();
			if (givenStoreName.length() > 0) {
				AddStoreDialog.this.storeName = givenStoreName;
				getStore(givenStoreName);
				showLoadingDialog();
			}
		});

		view.findViewById(R.id.button_top_stores).setOnClickListener(v->{
			Uri uri = Uri.parse("http://m.aptoide.com/more/toprepos/q=" + Api.Q);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			if (isAdded()) {
				dismiss();
			}
			startActivity(intent);
		});
	}

	private void getStore(String storeName) {
		GetStoreMetaRequest getStoreMetaRequest = buildRequest(storeName);

		executeRequest(getStoreMetaRequest);
	}

	private void executeRequest(GetStoreMetaRequest getStoreMetaRequest) {
		StoreUtils.subscribeStore(getStoreMetaRequest, getStoreMeta1->{
			ShowMessage.show(getView(), AptoideUtils.StringU.getFormattedString(R.string.store_subscribed,
					storeName));

			dismissLoadingDialog();
			dismiss();
		}, e->{
			if (e instanceof AptoideWsV7Exception) {
				BaseV7Response baseResponse = ((AptoideWsV7Exception) e).getBaseResponse();

				BaseV7Response.Error error = baseResponse.getError();
				if (StoreUtils.PRIVATE_STORE_ERROR.equals(error.getCode())) {
					DialogFragment dialogFragment = PrivateStoreDialog.newInstance(AddStoreDialog
							.this, PRIVATE_STORE_REQUEST_CODE, storeName);
					dialogFragment.show(getFragmentManager(), PrivateStoreDialog.TAG);
				} else {
					ShowMessage.show(getView(), error.getDescription());
				}
				dismissLoadingDialog();
			} else {
				dismissLoadingDialog();
				Toast.makeText(V8Engine.getContext(), R.string.error_occured, Toast.LENGTH_LONG).show();
			}
		});
	}

	private GetStoreMetaRequest buildRequest(String storeName) {
		return GetStoreMetaRequest.of(storeName);
	}

	private GetStoreMetaRequest buildRequest(String storeName, String storeUser, String storePassSha1) {
		GetStoreMetaRequest getStoreMetaRequest = buildRequest(storeName);
		// TODO: 20-05-2016 neuro request default faxavor!
		getStoreMetaRequest.getBody().setStoreUser(storeUser).setStorePassSha1(storePassSha1);
		return getStoreMetaRequest;
	}

	private void showLoadingDialog() {

		if (loadingDialog == null) {
			loadingDialog = GenericDialogs.createGenericPleaseWaitDialog(getActivity());
		}

		loadingDialog.show();
	}

	void dismissLoadingDialog() {
		loadingDialog.dismiss();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			storeName = savedInstanceState.getString(BundleArgs.STORE_NAME.name());
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(BundleArgs.STORE_NAME.name(), storeName);
	}

	private enum BundleArgs {
		STORE_NAME,
	}
}