package cm.aptoide.pt.actions;

import android.accounts.NetworkErrorException;

import lombok.AllArgsConstructor;
import rx.Observable;
import rx.Subscriber;
import rx.observables.SyncOnSubscribe;

/**
 * Created by diogoloureiro on 09/09/16.
 */
@AllArgsConstructor
public class RequestDownloadAccessOnSubscribe implements Observable.OnSubscribe<Void> {

	private final PermissionRequest permissionRequest;

	@Override
	public void call(Subscriber<? super Void> subscriber) {
		permissionRequest.requestDownloadAccess(() -> {
			if (!subscriber.isUnsubscribed()) {
				subscriber.onNext(null);
				subscriber.onCompleted();
			}
		}, () -> {
			subscriber.onError(new NetworkErrorException("Permission denied to download file."));
		});
	}
}
