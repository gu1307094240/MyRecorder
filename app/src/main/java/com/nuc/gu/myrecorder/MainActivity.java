package com.nuc.gu.myrecorder;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.projection.MediaProjectionManager;
import android.media.session.MediaSessionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nuc.gu.myrecorder.view.AudioRecorderButton;
import com.nuc.gu.myrecorder.view.MediaManager;
import com.nuc.gu.myrecorder.view.RecorderAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayAdapter<Recorder> mAdapter;
    private List<Recorder>mData = new ArrayList<Recorder>();

    private AudioRecorderButton mAudioRecorderButton;

    private View mAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView)findViewById(R.id.id_listview);
        mAudioRecorderButton = (AudioRecorderButton)findViewById(R.id.id_recorder_button);
        mAudioRecorderButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                Recorder recorder = new Recorder(seconds,filePath);
                mData.add(recorder);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mData.size()-1);
            }
        });

        mAdapter = new RecorderAdapter(this,mData);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAnimView!=null){
                    mAnimView.setBackgroundResource(R.drawable.adj);
                    mAnimView = null;
                }
                //播放動畫
                mAnimView = view.findViewById(R.id.id_recorder_anim);
                mAnimView.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
                anim.start();
                //播放音頻
                try {
                    MediaManager.playSound(mData.get(position).filePath, new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mAnimView.setBackgroundResource(R.drawable.adj);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

    public class Recorder{
        float time;
        String filePath;

        Recorder(float time, String filePath) {
            super();
            this.time = time;
            this.filePath = filePath;
        }

        public float getTime() {
            return time;
        }

        public void setTime(float time) {
            this.time = time;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
