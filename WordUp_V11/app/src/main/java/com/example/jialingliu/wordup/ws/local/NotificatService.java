package com.example.jialingliu.wordup.ws.local;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.example.jialingliu.wordup.model.Word;
import com.example.jialingliu.wordup.ui.MainActivity;
import com.example.jialingliu.wordup.util.HttpDownloader;
import com.example.jialingliu.wordup.util.MySharedpreference;
import com.example.jialingliu.wordup.util.TaskUtils;
import com.example.jialingliu.wordup.util.ToastUtils;
import com.example.jialingliu.wordup.ws.remote.Api;
import com.google.gson.Gson;

import java.util.Date;
import java.util.Map;

/**
 * Created by jialingliu on 4/17/16.
 */
public class NotificatService extends Service {
    private Word mWord;

    public static boolean isRun      = false;
    public static boolean hasNewWord = true;

    private volatile boolean stopRequested;
    private boolean isFirstTime = true;

    private static long firstTime;

    private Thread thread;

    private String mTodayGsonString;
    private String mYesterdayGsonString;

    private MySharedpreference mSharedpreference;

    private LocalBinder localBinder = new LocalBinder();

    private int mChangeWordTime;

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedpreference = new MySharedpreference(this);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return localBinder;
    }

    public class LocalBinder extends Binder {
        public NotificatService getService() {
            return NotificatService.this;
        }

        @Override
        protected boolean onTransact(int code, final Parcel data, final Parcel reply,
                                     int flags) throws RemoteException {
            TaskUtils.executeAsyncTask(
                    new AsyncTask<Object, Object, Object>() {
                        boolean change;

                        @Override
                        protected Object doInBackground(Object... params) {
                            mChangeWordTime = data.readInt();
                            Bundle bundle = data.readBundle();
                            change = bundle.getBoolean("change");
                            startNotification();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            if (mChangeWordTime > 0) {
                                if (change)
                                    ToastUtils.showLong("change successfully");
                                else
                                    ToastUtils.showShort("refresh successfully");
                            } else {
                                ToastUtils.showShort("Too much changes! Wait a second...");
                            }
                            reply.writeInt(200);
                        }
                    }
            );
            return super.onTransact(code, data, reply, flags);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // startNotification();
        thread = new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        Date date = new Date();

                        while (stopRequested == false) {
                            if (isFirstTime) {
                                firstTime = date.getDate();
                                startNotification();
                                isFirstTime = false;
                            }
                            date = new Date();
                            int currentTime = date.getDate();
                            if (currentTime != firstTime) {
                                startNotification();
                                firstTime = currentTime;
                            }

                            try {
                                Log.i("WordUp-->", "onStartCommand is runing");
                                Thread.sleep(238 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    public void changeNewAndOldWord() {
        Map map = mSharedpreference.getWordJson();
        if (((String) map.get("today_json")).equals(mTodayGsonString)
                || mTodayGsonString == null
                || mTodayGsonString.isEmpty()) {
            return;
        }
        if (mWord != null) {
            mWord.save();// save the new word to wordlist.db
        }

        Map map2 = mSharedpreference.getInfo();
        int honor = (Integer) map2.get("honor");
        honor++;
        mSharedpreference.saveHonor(honor);

        mYesterdayGsonString = (String) map.get("today_json");
        mSharedpreference.saveYesterdayJson(mYesterdayGsonString);
        mSharedpreference.saveTodayJson(mTodayGsonString);
        hasNewWord = true;
    }

    private int getCurrentWordId() {
        return mSharedpreference.getCurrentWordId();
    }

    private String getDownloadString() {
        HttpDownloader httpDownloader = new HttpDownloader();
        return httpDownloader.download(Api.RECOMMENDAPI);
    }

    public void startNotification() {
        mTodayGsonString = getDownloadString();
        if (mTodayGsonString == null || mTodayGsonString.isEmpty()) {
            try {
                Thread.sleep(100 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mTodayGsonString = getDownloadString();
        }
        try {
            mWord = new Word();
            Gson gson = new Gson();
            mWord = gson.fromJson(mTodayGsonString, Word.class);
        } catch (Exception e) {
            int id = getCurrentWordId();
            id -= 3;
            if (id < 6)
                id = 6;
            mSharedpreference.setCurrentWordId(id);
        }

        if (mWord != null) {
            Message message = Message.obtain();
            message.obj = mWord;
            if (MainActivity.todaywordhandler != null)
                MainActivity.todaywordhandler.sendMessage(message);
        }


        changeNewAndOldWord();

        if (MainActivity.tw != null)
            MainActivity.tw = mWord;
    }

    @Override
    public void onDestroy() {
        ToastUtils.showShort("service destroyed");
        stopRequested = true;
        thread.interrupt();
        isRun = false;
        super.onDestroy();
    }

}
