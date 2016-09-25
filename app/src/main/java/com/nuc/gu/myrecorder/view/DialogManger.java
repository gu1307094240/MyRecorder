package com.nuc.gu.myrecorder.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuc.gu.myrecorder.R;

/**
 * Created by GU on 2016/9/24.
 */
public class DialogManger {
    private Dialog mDialog;

    private ImageView mIcon;
    private ImageView mVioce;

    private TextView mLable;
    private Context mContext;

    public DialogManger(Context mContext) {
        this.mContext = mContext;
    }

    public void showRecordingDialog(){
        mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_recording,null);
        mDialog.setContentView(view);

        mIcon = (ImageView)mDialog.findViewById(R.id.id_recorder_dialog_icon);
        mVioce = (ImageView)mDialog.findViewById(R.id.id_recorder_dialog_voice);
        mLable = (TextView)mDialog.findViewById(R.id.id_recorder_dialog_label);

        mDialog.show();
    }

    public void recording(){
        if (mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVioce.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.recorder);
            mLable.setText("手指上滑 取消发送");
        }

    }

    public void wantToCancel(){
        if (mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVioce.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.cancel);
            mLable.setText("松开手指 取消发送");
        }
    }

    public void tooShort(){
        if (mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVioce.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.voice_to_short);
            mLable.setText("录音时间过短");
        }
    }
    public void dismissDialog(){
        if (mDialog!=null&&mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 通过level去更新
     * @param level
     */
    public void updateVoiceLevel(int level){

        if (mDialog!=null&&mDialog.isShowing()){
//            mIcon.setVisibility(View.VISIBLE);
//            mVioce.setVisibility(View.VISIBLE);
//            mLable.setVisibility(View.VISIBLE);

            int resId = mContext.getResources().getIdentifier("v"+level,"drawable",mContext.getPackageName());
            mVioce.setImageResource(resId);
        }
    }
}
