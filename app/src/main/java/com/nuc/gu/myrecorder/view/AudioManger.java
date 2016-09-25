package com.nuc.gu.myrecorder.view;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by GU on 2016/9/24.
 */
public class AudioManger {
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    private static AudioManger mInstance;

    private boolean isPrepared;

    private AudioManger(String dir){
        mDir = dir;
    }

    /**
     * 回调准备完毕
     */

    public interface AudioStateListener{
        void wellPrepared();
    }

    public AudioStateListener mListener;

    public void setOnAudioStateListener(AudioStateListener listener){
        mListener = listener;
    }

    public static AudioManger getInstance(String dir){
        if (mInstance == null){
            synchronized (AudioManger.class){
                if (mInstance == null){
                    mInstance = new AudioManger(dir);
                }
            }
        }
        return mInstance;
    }

    public void prepareAudio() throws IOException {
        isPrepared = false;
        File dir = new File(mDir);
        if (!dir.exists()){
            dir.mkdirs();
        }
        String fileName = gengrateFileName();
        File file = new File(dir,fileName);
        mCurrentFilePath = file.getAbsolutePath();
        mMediaRecorder = new MediaRecorder();
        //设置输出文件
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        //设置MediaRecorder的音频源湿麦克风
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置音频的格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        //设置音频的编码
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        mMediaRecorder.prepare();
        mMediaRecorder.start();
        //准备结束
        isPrepared = true;
        if (mListener!=null){
            mListener.wellPrepared();
        }
    }

    private String gengrateFileName() {
        return UUID.randomUUID().toString()+".amr";
    }

    public int getVoiceLevel(int maxLevel){
        if (isPrepared){
            try {
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return 1;
    }
    public void release(){
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }
    public void cancel(){
        release();
        if (mCurrentFilePath!=null){
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }
    public String getCurrentFilePath(){
        return mCurrentFilePath;
    }
}
