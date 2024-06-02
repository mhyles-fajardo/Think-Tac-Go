package com.example.thinktacgo;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;

public class MyApplication extends Application {
    private static Context context;
    private static MediaPlayer introPlayer;
    private static MediaPlayer gamePlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }
    public static Context getAppContext() {
        return MyApplication.context;
    }

    //initialization of media players

    public static void initializeIntroPlayer(){
        introPlayer = MediaPlayer.create(MyApplication.context, R.raw.intro);
        introPlayer.setLooping(true);
        introPlayer.start();
    }

    public static void initializeGamePlayer() {
        gamePlayer = MediaPlayer.create(MyApplication.context, R.raw.sugar_cookie);
        gamePlayer.setLooping(true);
        gamePlayer.start();
    }

    public static void initializeGamePlayer2() {
        gamePlayer = MediaPlayer.create(MyApplication.context, R.raw.sugar_cookie);
        gamePlayer.setLooping(true);
        if (gamePlayer != null && !gamePlayer.isPlaying()) {
            gamePlayer.start();
        }
    }

//methods for onPause

    public static void pauseIntroPlayer(){
        if (introPlayer != null && introPlayer.isPlaying()) {
            introPlayer.pause();
        }
    }

    public static void pauseGamePlayer(){
        if (gamePlayer != null && gamePlayer.isPlaying()) {
            gamePlayer.pause();
        }
    }

//methods for onResume

    public static void playIntroPlayer(){
        if (introPlayer != null && !introPlayer.isPlaying()) {
            introPlayer.start();
        }
    }

    public static void playGamePlayer(){
        if (gamePlayer != null && !gamePlayer.isPlaying()) {
            gamePlayer.start();
        }
    }


//methods for onDestroy

    public static void stopIntroPlayer(){
        if (introPlayer != null) {
            introPlayer.stop();
            introPlayer.release();
            introPlayer = null;
        }
    }

    public static void stopGamePlayer(){
        if (gamePlayer != null) {
            gamePlayer.stop();
            gamePlayer.release();
            gamePlayer = null;
        }
    }
}
