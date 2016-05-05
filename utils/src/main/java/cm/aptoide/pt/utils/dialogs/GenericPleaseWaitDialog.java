package cm.aptoide.pt.utils.dialogs;

import android.app.ProgressDialog;
import android.content.Context;

import cm.aptoide.pt.utils.R;

/**
 * Created by trinkes on 5/5/16.
 */
public class GenericPleaseWaitDialog extends ProgressDialog {

	public GenericPleaseWaitDialog(Context context) {
		super(context);
		setMessage(context.getString(R.string.please_wait));
	}
}
