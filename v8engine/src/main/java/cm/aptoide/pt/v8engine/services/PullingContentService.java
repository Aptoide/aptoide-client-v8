package cm.aptoide.pt.v8engine.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.NotificationTarget;

import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v3.PushNotificationsRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v3.GetPushNotificationsResponse;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.MainActivityFragment;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.receivers.DeepLinkIntentReceiver;
import cm.aptoide.pt.v8engine.receivers.PullingContentReceiver;

/**
 * Created by trinkes on 7/13/16.
 */
public class PullingContentService extends Service {

	public static final String PUSH_NOTIFICATIONS_ACTION = "PUSH_NOTIFICATIONS_ACTION";
	public static final String UPDATES_ACTION = "UPDATES_ACTION";
	public static final long UPDATES_INTERVAL = AlarmManager.INTERVAL_HALF_DAY;
	public static final long PUSH_NOTIFICATION_INTERVAL = AlarmManager.INTERVAL_DAY;
	public static final int PUSH_NOTIFICATION_ID = 86456;
	public static final int UPDATE_NOTIFICATION_ID = 123;

	public static void setAlarm(AlarmManager am, Context context, String action, long time) {
		Intent intent = new Intent(context, PullingContentService.class);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 5000, time, pendingIntent);
	}

	private boolean isAlarmUp(Context context, String action) {
		Intent intent = new Intent(context, PullingContentService.class);
		intent.setAction(action);
		return (PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		if (!isAlarmUp(this, PUSH_NOTIFICATIONS_ACTION)) {
			setAlarm(alarm, this, PUSH_NOTIFICATIONS_ACTION, PUSH_NOTIFICATION_INTERVAL);
		}
		if (!isAlarmUp(this, UPDATES_ACTION)) {
			setAlarm(alarm, this, UPDATES_ACTION, UPDATES_INTERVAL);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		String action = intent.getAction();
		if (action != null) {
			switch (action) {
				case UPDATES_ACTION:
					DataproviderUtils.checkUpdates(this::setUpdatesNotification);
					break;
				case PUSH_NOTIFICATIONS_ACTION:
					PushNotificationsRequest.of().execute(this::setPushNotification, true);
					break;
			}
		}
		return START_NOT_STICKY;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void setUpdatesNotification(ListAppsUpdates listAppsUpdates) {
		Intent resultIntent = new Intent(Application.getContext(), MainActivityFragment.class);
		resultIntent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_UPDATES, true);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(Application.getContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		int numberUpdates = listAppsUpdates.getList().size();
		if (numberUpdates > 0 && numberUpdates != ManagerPreferences.getLastUpdates()) {
			CharSequence tickerText = AptoideUtils.StringU.getFormattedString(R.string.has_updates, Application.getConfiguration().getMarketName());
			CharSequence contentTitle = Application.getConfiguration().getMarketName();
			CharSequence contentText = AptoideUtils.StringU.getFormattedString(R.string.new_updates, numberUpdates);
			if (numberUpdates == 1) {
				contentText = AptoideUtils.StringU.getFormattedString(R.string.one_new_update, numberUpdates);
			}

			Notification notification = new NotificationCompat.Builder(Application.getContext()).setContentIntent(resultPendingIntent)
					.setOngoing(false)
					.setSmallIcon(Application.getConfiguration().getIcon())
					.setContentTitle(contentTitle)
					.setContentText(contentText)
					.setTicker(tickerText)
					.build();

			notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
			final NotificationManager managerNotification = (NotificationManager) Application.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
			managerNotification.notify(UPDATE_NOTIFICATION_ID, notification);
			ManagerPreferences.setLastUpdates(numberUpdates);
		}
		stopSelf();
	}

	private void setPushNotification(GetPushNotificationsResponse response) {
		for (final GetPushNotificationsResponse.Notification pushNotification : response.getResults()) {
			Intent resultIntent = new Intent(Application.getContext(), PullingContentReceiver.class);
			resultIntent.setAction(PullingContentReceiver.NOTIFICATION_PRESSED_ACTION);
			resultIntent.putExtra(PullingContentReceiver.PUSH_NOTIFICATION_TRACK_URL, pushNotification.getTrackUrl());
			resultIntent.putExtra(PullingContentReceiver.PUSH_NOTIFICATION_TARGET_URL, pushNotification.getTargetUrl());

			PendingIntent resultPendingIntent = PendingIntent.getBroadcast(Application.getContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			Notification notification = new NotificationCompat.Builder(Application.getContext()).setContentIntent(resultPendingIntent)
					.setOngoing(false)
					.setSmallIcon(Application.getConfiguration().getIcon())
					.setContentTitle(pushNotification.getTitle())
					.setContentText(pushNotification.getMessage())
					.build();

			notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
			final NotificationManager managerNotification = (NotificationManager) Application.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

			if (Build.VERSION.SDK_INT >= 16 && pushNotification.getImages() != null && TextUtils.isEmpty(pushNotification.getImages().getIconUrl())) {

				String imageUrl = pushNotification.getImages().getBannerUrl();
				RemoteViews expandedView = new RemoteViews(Application.getContext().getPackageName(), R.layout.pushnotificationlayout);
				expandedView.setImageViewBitmap(R.id.icon, BitmapFactory.decodeResource(Application.getContext().getResources(), Application.getConfiguration()
						.getIcon()));
				expandedView.setTextViewText(R.id.text1, pushNotification.getTitle());
				expandedView.setTextViewText(R.id.description, pushNotification.getMessage());
				notification.bigContentView = expandedView;
				NotificationTarget notificationTarget = new NotificationTarget(Application.getContext(), expandedView, R.id.PushNotificationImageView,
						notification, PUSH_NOTIFICATION_ID);
				ImageLoader.loadImageToNotification(notificationTarget, imageUrl);
			}

			if (!response.getResults().isEmpty()) {
				ManagerPreferences.setLastPushNotificationId(response.getResults().get(0).getId().intValue());
			}
			managerNotification.notify(PUSH_NOTIFICATION_ID, notification);
		}
	}
}
