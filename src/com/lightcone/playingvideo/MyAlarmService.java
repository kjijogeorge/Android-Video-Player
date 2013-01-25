package com.lightcone.playingvideo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyAlarmService extends Service implements MediaPlayer.OnPreparedListener {
	private MediaPlayer mMediaPlayer;
	private static String TAG="Alarm Service";
	
	@Override
    public void onCreate() {
		Toast.makeText(this, "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();
		
    }
	
	@Override
	public IBinder onBind(Intent intent) {
	 // TODO Auto-generated method stub
	 Toast.makeText(this, "MyAlarmService.onBind()", Toast.LENGTH_LONG).show();
	 return null;
	}
	
	@Override
	public void onDestroy() {
	 super.onDestroy();
	 Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		 super.onStart(intent, startId);
		 Log.d(TAG, "OnStart called");
		 Toast.makeText(this, "MyAlarmService.onStart()", Toast.LENGTH_LONG).show();
		    
		 mMediaPlayer = new MediaPlayer();
		    mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
		    	
		    	public void onPrepared(MediaPlayer mp) {
		    		//have mediaPlayer be a class variable
		            Log.d(TAG, "onPrepared Listener called");
				 	if (mMediaPlayer.isPlaying()) {
				 		Toast.makeText(MyAlarmService.this,"Video is playing!", Toast.LENGTH_LONG).show();
					}
		    	} 	
		  });	
	}

	
	@Override
	public boolean onUnbind(Intent intent) {
	 // TODO Auto-generated method stub
	 Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG).show();
	 return super.onUnbind(intent);
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

}