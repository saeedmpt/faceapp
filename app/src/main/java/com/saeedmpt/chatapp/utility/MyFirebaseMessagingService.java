package com.saeedmpt.chatapp.utility;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private Context contInst;
    private String title;
    private String body;
    private String img_url;
    private Integer group_id = 0;
    private Integer notificationId;


    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        Log.i("SendPushToken: ", "onNewToken");
        new SendPushToken(newToken);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("TAG", "From: " + remoteMessage.getFrom());
        //super.onMessageReceived(remoteMessage);
        /*if (remoteMessage.getNotification() != null) {
            contInst = this;
            Map<String, String> data = remoteMessage.getData();

            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
            if (remoteMessage.getNotification().getImageUrl() != null)
                img_url = remoteMessage.getNotification().getImageUrl().toString();
            if (data.get("group_id") != null) {
                group_id = Integer.valueOf(data.get("group_id"));
                notificationId = group_id;//for group messages list
            } else {
                notificationId = new Random().nextInt(60000);//for notification Id
            }
            //link = data.get("link");
            notif();
        }*/
    }

    @Override
    public void handleIntent(Intent intent) {
        //add a log, and you'll se e the method will be triggered all the time (both foreground and background).
        Log.d("FCM", "handleIntent");

        //if you don't know the format of your FCM message,
        //just print it out, and you'll know how to parse it
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d("FCM", "Key: " + key + " Value: " + value);
            }
        }

        //the background notification is created by super method
        //but you can't remove the super method.
        //the super method do other things, not just creating the notification
        super.handleIntent(intent);

        //remove the Notificaitons
        removeFirebaseOrigianlNotificaitons();

        if (bundle == null)
            return;

        //pares the message
        CloudMsg cloudMsg = parseCloudMsg(bundle);

        contInst = this;

        title = cloudMsg.title;
        body = cloudMsg.msg;
        if (cloudMsg.group_id != null && !cloudMsg.group_id.isEmpty()) {
            group_id = Integer.valueOf(cloudMsg.group_id);
        }
        //link = data.get("link");
    }

    /**
     * parse the message which is from FCM
     *
     * @param bundle
     */
    private CloudMsg parseCloudMsg(Bundle bundle) {
        String title = null, msg = null;

        //if the message is sent from Firebase platform, the key will be that
        msg = (String) bundle.get("gcm.notification.body");

        if (bundle.containsKey("gcm.notification.title"))
            title = (String) bundle.get("gcm.notification.title");

        //parse your custom message
        String group_id = "";
        if (bundle.containsKey("group_id")) {
            group_id = (String) bundle.get("group_id");
        }
        /*if (BuildConfig.DEBUG) {
            group_id = "47";
        }*/

        //package them into a object(CloudMsg is your own structure), it is easy to send to Activity.
        CloudMsg cloudMsg = new CloudMsg(title, msg, group_id);
        return cloudMsg;
    }

    private void removeFirebaseOrigianlNotificaitons() {

        //check notificationManager is available
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null)
            return;

        //check api level for getActiveNotifications()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //if your Build version is less than android 6.0
            //we can remove all notifications instead.
            //notificationManager.cancelAll();
            return;
        }

        //check there are notifications
        StatusBarNotification[] activeNotifications =
                notificationManager.getActiveNotifications();
        if (activeNotifications == null)
            return;

        //remove all notification created by library(super.handleIntent(intent))
        for (StatusBarNotification tmp : activeNotifications) {
            Log.d("FCM StatusBarNon",
                    "tag/id: " + tmp.getTag() + " / " + tmp.getId());
            String tag = tmp.getTag();
            int id = tmp.getId();

            //trace the library source code, follow the rule to remove it.
            if (tag != null && tag.contains("FCM-Notification"))
                notificationManager.cancel(tag, id);
        }
    }


    private class CloudMsg {
        private final String title;
        private final String msg;
        private final String group_id;

        public CloudMsg(String title, String msg, String group_id) {

            this.title = title;
            this.msg = msg;
            this.group_id = group_id;
        }
    }
}
