package providers.fairrepair.service.fairrepairpartner.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import providers.fairrepair.service.fairrepairpartner.FairRepairApplication;
import providers.fairrepair.service.fairrepairpartner.R;
import providers.fairrepair.service.fairrepairpartner.app.MainActivity;
import providers.fairrepair.service.fairrepairpartner.model.Customer;
import providers.fairrepair.service.fairrepairpartner.utils.ApplicationMetadata;
import providers.fairrepair.service.fairrepairpartner.utils.NotificationUtils;

/**
 * Created by admin on 12/20/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private Customer customer = null;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().containsKey("notification_type")) {
            String payLoad = "";
            String message = "";
            int notificationType = Integer.parseInt(remoteMessage.getData().get("notification_type"));
            payLoad = new NotificationUtils().getData(remoteMessage.getData());
            Intent intent = null;

            if (FairRepairApplication.isVisible) {
                if (notificationType == ApplicationMetadata.NOTIFICATION_NEW_OFFER) {
                    Log.i(TAG, notificationType + "");
                     customer = new Gson().fromJson(payLoad,Customer.class);
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            FairRepairApplication.getBus().post(customer);
                            Log.i(TAG,  "inside handle" + customer.customer_id);
                        }
                    });
                }else if (notificationType == ApplicationMetadata.NOTIFICATION_OFFER_ACCEPTED) {
                    Log.i(TAG, notificationType + "");
                    customer = new Gson().fromJson(payLoad,Customer.class);
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            FairRepairApplication.getBus().post(customer);
                            Log.i(TAG,  "inside handle" + customer.customer_id);
                        }
                    });
                }

            } else {
                intent = new Intent(this, MainActivity.class);
                switch (notificationType) {
                    case ApplicationMetadata.NOTIFICATION_NEW_OFFER:
                        intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, payLoad);
                        intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
                        message = remoteMessage.getData().get("message");
                        break;
                    case ApplicationMetadata.NOTIFICATION_REQ_ACCEPTED:
                        intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, payLoad);
                        intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
                        message = remoteMessage.getData().get("message");
                        break;
                    case ApplicationMetadata.NOTIFICATION_REQ_COMPLETED:
                        break;
                    case ApplicationMetadata.NOTIFICATION_OFFER_ACCEPTED:
                        intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, payLoad);
                        intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
                        message = remoteMessage.getData().get("message");
                        break;
                    default:
                }
                int time = 60;
                PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
                // Sets an ID for the notification, so it can be updated
                final int notifyID = 101;
                // build notification
                // the addAction re-use the same intent to keep the example short
                final Notification.Builder mBuilder = new Notification.Builder(this)
                        .setContentTitle(message)
                        .setContentText("time left to accept")
                        .setSmallIcon(R.drawable.ic_login_logo)
                        //.setColor(getColor(R.color.colorPrimary))
                        .setContentIntent(pIntent)
                        .setAutoCancel(true);

                final NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


                // Start a lengthy operation in a background thread
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                int incr;
                                // Do the "lengthy" operation 20 times
                                FairRepairApplication.timeToAcceptRequest = 60;
                                for (incr = 0; incr < 60; incr++) {
                                    FairRepairApplication.timeToAcceptRequest -= 1;
                                    // Sets the progress indicator to a max value, the
                                    // current completion percentage, and "determinate"
                                    // state
                                    mBuilder.setProgress(60, incr, false);
                                    // Displays the progress bar for the first time.
                                    notificationManager.notify(notifyID, mBuilder.build());
                                    // Sleeps the thread, simulating an operation
                                    // that takes time
                                    try {
                                        // Sleep for 5 seconds
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        Log.d(TAG, "sleep failure");
                                    }
                                }
                                // When the loop is finished, updates the notification
                                mBuilder.setContentText("Request expired")
                                        // Removes the progress bar
                                        .setProgress(0, 0, false);
                                mBuilder.setContentIntent(null);
                                notificationManager.notify(notifyID, mBuilder.build());
                            }
                        }
// Starts the thread by calling the run() method in its Runnable
                ).start();

            }

        }
    }
}
