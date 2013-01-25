package com.lightcone.playingvideo;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.view.SurfaceView;
import android.os.Handler;

public class PlayingVideo extends Activity implements OnCompletionListener, OnPreparedListener, SurfaceHolder.Callback, MediaPlayerControl {
	
	//static private final String pathToFile = "http://192.168.1.249:8800/ice.webm";  // Video source file
	private static String TAG="Playing Video";
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private static MediaPlayer videoPlayer;
	private MediaController mController;
	private Button buttonPlay;
	public EditText editTextSongURL;
	private Intent myIntent;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
               
        surfaceView = (SurfaceView)findViewById(R.id.surfacePlayer);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //videoPlayer = (VideoView) findViewById(R.id.videoPlayer);   
       // MediaController mediaController = new MediaController(this);
        
        editTextSongURL = (EditText)findViewById(R.id.EditTextSongURL);
        editTextSongURL.setText("http://192.168.1.249:8800/ice.webm");

        buttonPlay = (Button)findViewById(R.id.buttonGo);
        buttonPlay.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		        		       		
        		if(videoPlayer!=null && videoPlayer.isPlaying()){
	        	    	videoPlayer.stop();
	        			Log.e(TAG, "Media Player is busy.Please try after sometime");
	        			return;
        	    }	
        		playVideo();
        	}
         });
    }
    
    private void playVideo() {
 	   	String url=editTextSongURL.getText().toString();

    	try {
    		videoPlayer = new MediaPlayer();
    		videoPlayer.setDataSource(url);
    		videoPlayer.setDisplay(surfaceHolder);
    		videoPlayer.prepare();
    		videoPlayer.setOnPreparedListener(this);
    		videoPlayer.setOnCompletionListener(this);
    		mController = new MediaController(this);
    	
    	} catch (IOException e) {
    		Log.e(TAG, "Could not open file " + url + " for playback.", e);
    	}
    }

	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.d(TAG, "onPrepared called");
		mController.setMediaPlayer(this);
	    mController.setAnchorView(findViewById(R.id.surfacePlayer));
	    videoPlayer.start();
	    //Do something after 5000ms
	    final Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {
	      @Override
	      public void run() {
	        
	      }
	    }, 5000);
	    
	    myIntent = new Intent(PlayingVideo.this, MyAlarmService.class);
        startService(myIntent);
	}
	
	/** This callback will be invoked when the file is finished playing */
	@Override
	public void onCompletion(MediaPlayer  mp) {
		// Statements to be executed when the video finishes.
		Log.d(TAG, "onCompletion called");
		releaseMediaPlayer();	
		Intent myintent = new Intent(this,PlayingVideo.class);
        myintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myintent);
	}
	
	@Override
	protected void onStop() {
	    super.onStop();
	    releaseMediaPlayer();
	}
	
	@Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if (videoPlayer != null) {
        	mController.hide();
     	    videoPlayer.stop();
            videoPlayer.release();
            videoPlayer = null;
            stopService(myIntent);
        }
    }
	
	/**  Use screen touches to toggle the video between playing and paused. */
	@Override
	public boolean onTouchEvent (MotionEvent ev){	
		mController.show();
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			if(videoPlayer.isPlaying()){
				videoPlayer.pause();
			} else {
				videoPlayer.start();
			}
			return true;		
		} else {
			return false;
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		 Log.d(TAG, "surfaceChanged called");
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated called");
		
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed called");
		
	}
	
	//mediacontroller implemented methods

    public void start() {
        videoPlayer.start();
    }

    public void pause() {
        videoPlayer.pause();
    }

    public int getDuration() {
        return videoPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return videoPlayer.getCurrentPosition();
    }

    public void seekTo(int i) {
    	videoPlayer.seekTo(i);
    }

    public boolean isPlaying() {
        return videoPlayer.isPlaying(); 
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }
		
}
	