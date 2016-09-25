package com.nuc.gu.myrecorder.view;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.nuc.gu.myrecorder.R;

import java.io.IOException;
import java.security.KeyStore;

/**
 * Created by GU on 2016/9/24.
 */
public class AudioRecorderButton extends Button implements AudioManger.AudioStateListener{

    private static final int DISTANCE_Y_CANCLE = 50;
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;

    private int mCurState = STATE_NORMAL;

    private boolean isRecording = false;

    private DialogManger mDialogManger;

    private AudioManger mAudioManger;

    private float mTime;
    //是否出发longClick
    private boolean mReady;

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDialogManger = new DialogManger(getContext());

        String dir = Environment.getExternalStorageDirectory() + "/recorder_audios";
        mAudioManger = AudioManger.getInstance(dir);
        mAudioManger.setOnAudioStateListener(this);
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    mReady = true;
                    mAudioManger.prepareAudio();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    /**
     * 录音完成后的回调
     */
    private AudioFinishRecorderListener mListener;
    public interface AudioFinishRecorderListener{
        void onFinish(float seconds,String filePath);
    }


    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener){
        this.mListener = listener;
    }

    /**
     * 获取音量大小的Runnable
     */
    private Runnable mGetVoiceLevleRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording){
                try {
                    Thread.sleep(100);
                    mTime+=0.1f;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGED = 0X111;
    private static final int MSG_DIALOG_DIMISS = 0X112;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_AUDIO_PREPARED:
                    //显示应该在audio end prepared之后
                    mDialogManger.showRecordingDialog();
                    isRecording = true;

                    new Thread(mGetVoiceLevleRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
                    mDialogManger.updateVoiceLevel(mAudioManger.getVoiceLevel(7));

                    break;
                case MSG_DIALOG_DIMISS:
                    mDialogManger.dismissDialog();
                    break;
            }
        }
    };

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (action){
            case MotionEvent.ACTION_DOWN:
//                isRecording = true;
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording){
                    if (wantToCancel(x,y)){
                        changeState(STATE_WANT_TO_CANCEL);
                    }else {
                        changeState(STATE_RECORDING);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (!mReady){
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecording || mTime<0.6f){
                    mDialogManger.tooShort();
                    mAudioManger.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
                }

                else if (mCurState == STATE_RECORDING){
                    mDialogManger.dismissDialog();
                    mAudioManger.release();
                    if (mListener != null){
                        mListener.onFinish(mTime,mAudioManger.getCurrentFilePath());
                    }

                }else if (mCurState == STATE_WANT_TO_CANCEL){
                    mDialogManger.dismissDialog();
                    mAudioManger.cancel();
                }
                reset();
                break;
        } return super.onTouchEvent(event);
    }

    /**
     * 回复状态及标志位
     */
    private void reset() {
        isRecording = false ;
        mReady = false;
        mTime = 0;
        changeState(STATE_NORMAL);
    }

    private boolean wantToCancel(int x, int y) {
        if (x<0||x>getWidth()){
            return true;
        }
        if (y<-DISTANCE_Y_CANCLE||y>getHeight()+DISTANCE_Y_CANCLE){
            return true;
        }
        return false;
    }

    private void changeState(int state ) {
        if (mCurState != state){
            mCurState = state;
            switch (state){
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recorder);
                    setText(R.string.str_recorder_recording);
                    if (isRecording){
                        mDialogManger.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder);
                    setText(R.string.str_recorder_cancel);
                    mDialogManger.wantToCancel();
                    break;
            }
        }
    }
}
