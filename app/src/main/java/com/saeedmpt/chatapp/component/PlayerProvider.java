package com.saeedmpt.chatapp.component;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.saeedmpt.chatapp.model.PlayerEvent;
import com.saeedmpt.chatapp.utility.PaperBook;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerProvider {
    private Context contInst;
    private String urlLink = "";
    private String userName = "";
    private String messageId = "";
    private String groupId = "";

    public PlayerProvider(Context contInst) {
        this.contInst = contInst;
    }

    private MediaPlayer mPlayer;
    private long currentTime = 0, totalTime = 0;
    private Timer timer;
    private AsyncCreatePlayer async;

    public void startPlaying(String newUrlLink) {
        //check permission storage
        if (Global.checkPermissionStorage(contInst)) {
            if (mPlayer == null) {
                //تازه میخواد یه فایل جدید رو پلی کنه
                urlLink = newUrlLink;
                PaperBook.setLastLinkVoice(newUrlLink);
                //
                mPlayer = new MediaPlayer();
                //prepareMediaPlayerByLink
                if (async != null) {
                    async.cancel(true);
                    async.isCancelled();
                }
                async = new AsyncCreatePlayer();
                async.execute();
            } else {
                //check another link
                if (urlLink.equals(newUrlLink)) {
                    //همون فایل قبلیه
                    startPlayStopFile();
                } else {
                    //stop old player
                    stopPlayer();
                    //start new player
                    startPlaying(newUrlLink);
                }
            }
            mPlayer.setOnCompletionListener(mp -> stopPlayer());
        } else {
            Global.getPermissionStorage((Activity) contInst);
        }

    }


    public boolean checkPlayerIsPause() {
        boolean value;
        if (mPlayer != null) {
            currentTime = mPlayer.getCurrentPosition();
            totalTime = mPlayer.getDuration();

            PlayerEvent playerEvent = new PlayerEvent();
            playerEvent.setType(PlayerEvent.VARIABLE.type_player_info);
            playerEvent.setUserName(userName);
            playerEvent.setCurrentTime(currentTime);
            playerEvent.setTotalTime(totalTime);
            EventBus.getDefault().post(playerEvent);


            value = true;
        } else {
            value = false;
        }
        return value;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void stopPlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        emptyTimer();
        showIconPlayOrPause(true);
        if (async != null) {
            async.cancel(true);
            async.isCancelled();
        }
        PlayerEvent playerEvent = new PlayerEvent();
        playerEvent.setType(PlayerEvent.VARIABLE.type_player_stoped);
        EventBus.getDefault().post(playerEvent);
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    private class AsyncCreatePlayer extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            //برو به حالت لودینگ اماده سازی فایل
            PlayerEvent playerEvent = new PlayerEvent();
            playerEvent.setType(PlayerEvent.VARIABLE.type_player_prepare);
            playerEvent.setPrepareLoading(true);
            EventBus.getDefault().post(playerEvent);
            prepareMediaPlayerByLink();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //after prepare media
            //فایل آماده شد لودینگ قطع بشه
            PlayerEvent playerEvent = new PlayerEvent();
            playerEvent.setType(PlayerEvent.VARIABLE.type_player_prepare);
            playerEvent.setPrepareLoading(false);
            EventBus.getDefault().post(playerEvent);

            totalTime = mPlayer.getDuration();
            currentTime = mPlayer.getCurrentPosition();
            startPlayStopFile();
        }
    }

    private void emptyTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void prepareMediaPlayerByLink() {
        try {
            proxy = getProxy(contInst);
            proxyUrl = proxy.getProxyUrl(urlLink);
            mPlayer.setDataSource(proxyUrl);
            mPlayer.prepare();
            mPlayer.setWakeMode(contInst, PowerManager.PARTIAL_WAKE_LOCK);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startPlayStopFile() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            emptyTimer();
            showIconPlayOrPause(false);
        } else {
            mPlayer.start();
            intiTimer();
            showIconPlayOrPause(true);
        }

    }

    private void showIconPlayOrPause(boolean status) {
        PlayerEvent playerEvent = new PlayerEvent();
        playerEvent.setType(PlayerEvent.VARIABLE.type_player_status_icon);
        if (!status) {
            playerEvent.setIcon(PlayerEvent.VARIABLE.icon_play);
        } else {
            playerEvent.setIcon(PlayerEvent.VARIABLE.icon_pause);
        }
        EventBus.getDefault().post(playerEvent);
    }

    private void intiTimer() {
        //member field
        //somewhere in your code:
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (mPlayer != null) {
                        if (mPlayer.isPlaying()) {
                            //get current position of playback because player is playing

                            if (mPlayer != null) {
                                currentTime = mPlayer.getCurrentPosition();
                                Log.d("POSITION", String.valueOf(currentTime));
                                totalTime = mPlayer.getDuration();
                                if (mPlayer.isPlaying()) {
                                    showIconPlayOrPause(true);
                                } else {
                                    showIconPlayOrPause(false);
                                }
                            } else {
                                currentTime = 0;
                                totalTime = 0;
                            }
                            PlayerEvent playerEvent = new PlayerEvent();
                            playerEvent.setType(PlayerEvent.VARIABLE.type_player_info);
                            playerEvent.setUserName(userName);
                            playerEvent.setCurrentTime(currentTime);
                            playerEvent.setTotalTime(totalTime);
                            EventBus.getDefault().post(playerEvent);
                            //repeat yourself that again in 100 miliseconds
                        } else {
                            //player is not playing do nothing.......
                        }

                    } else {
                        emptyTimer();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 0, 900);
    }

    public void seekToPlayer(int progress, String thisLink) {
        if (urlLink.equals(thisLink))
            if (mPlayer != null) {
                final double v = (mPlayer.getDuration() * progress) / 100;
                int p = (int) v;
                mPlayer.seekTo(p);
            }
    }


    String proxyUrl;
    private HttpProxyCacheServer proxy;

    private HttpProxyCacheServer getProxy(Context context) {
        return proxy == null ? (proxy = newProxy(context)) : proxy;
    }

    private static HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(1024 * 1024 * 100)       //bit*bit*100 = 100Mb for cache
                .build();
    }
}
