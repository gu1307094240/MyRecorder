package com.nuc.gu.myrecorder.view;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by GU on 2016/9/24.
 */
public class MediaManager {
    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;

    public static void playSound(String filePath,MediaPlayer.OnCompletionListener onCompletionListener) throws Exception {

        if (mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        }else {
            mMediaPlayer.reset();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(onCompletionListener);
        mMediaPlayer.setDataSource(filePath);
        mMediaPlayer.prepare();
        mMediaPlayer.start();
    }

    public static void pause(){
        if (mMediaPlayer != null&& mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public static void resume(){
        if (mMediaPlayer != null && isPause){
            mMediaPlayer.start();
            isPause = false;
        }
    }

    public static void release(){
        if (mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer =null;
        }
    }

}
