package com.lightcone.playingvideo;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;
import android.view.SurfaceView;
import android.os.Handler;
import android.provider.Settings.SettingNotFoundException;

public class PlayingVideo extends Activity implements OnCompletionListener, OnPreparedListener, SurfaceHolder.Callback, MediaPlayerControl {
	
	private static final double BATTERY_CONST = 313;
	private static final double BRIGHT_CONST = 0.8892;
	private static final double BITRATE_CONST = 0.0156;
	private static final int ID_MENU_PRESENT = 1;
	//static private final String pathToFile = "http://192.168.1.249:8800/ice.webm";  // Video source file
	private static String TAG="Playing Video";
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private static MediaPlayer videoPlayer;
	private MediaController mController;
	static Button buttonPlay;
	static EditText editTextSongURL;
	private MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
	double bRate;
	int curBrightness;
	double rTime;
	double bLevel;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
               
        
        surfaceView = (SurfaceView)findViewById(R.id.surfacePlayer);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
         
        editTextSongURL = (EditText)findViewById(R.id.EditTextSongURL);
        editTextSongURL.setText("http://192.168.1.249:8800/ice_med.webm");

        buttonPlay = (Button)findViewById(R.id.buttonGo);
        buttonPlay.setOnClickListener(new View.OnClickListener(){
        	public void onClick(final View v) {
        		        		       		
        		if(videoPlayer!=null && videoPlayer.isPlaying()){
        				//videoPlayer.stop();
	        	    	videoPlayer.reset();
	        			Log.e(TAG, "Media Player is busy.Please try after sometime");
	        			Toast.makeText(getBaseContext(), "Media Player is busy.Please try after sometime",
	                            Toast.LENGTH_SHORT).show();
        	    }	
        		
        		
        		Log.d(TAG, "File played :  " + editTextSongURL.getText().toString());
        		playVideo(editTextSongURL.getText().toString());
        		//Do something after 5000ms
        	    final Handler handler = new Handler();
        	    handler.postDelayed(new Runnable() {
        	      @Override
        	      public void run() {
        	    	  if(videoPlayer!=null) {
        	    		  Log.d(TAG, "Runnable called");
            	    	  registerForContextMenu(v);
            	    	  openContextMenu(v);
        	    	  }  
        	      }	  
        	    }, 5000);
        	}
        		
         });
    }
    
     void playVideo(String url) {
 	   	

    	try {
    		videoPlayer = new MediaPlayer();
    		videoPlayer.setDataSource(url);
    		videoPlayer.setDisplay(surfaceHolder);
    		videoPlayer.prepare();
    		videoPlayer.setOnPreparedListener(this);
    		videoPlayer.setOnCompletionListener(this);
    		//videoPlayer.setOnErrorListener((OnErrorListener) this);
    		metaRetriever.setDataSource(url);
    		
    		bRate = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE))/1000.0;
    		mController = new MediaController(this);
    	
    	} catch (IOException e) {
    		Toast.makeText(this, "Could not open file " + url + " for playback.",
                    Toast.LENGTH_SHORT).show();
    		Log.e(TAG, "Could not open file " + url + " for playback.", e);
    	}
    }

	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.d(TAG, "onPrepared called");
		mController.setMediaPlayer(this);
	    mController.setAnchorView(findViewById(R.id.surfacePlayer));
	    videoPlayer.start();
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
    protected void onPause() {
        super.onPause();
        if (videoPlayer != null && videoPlayer.isPlaying()) {
        	mController.hide();
        	videoPlayer.pause();
        }
    }
	
	@Override
	protected void onResume() {
	    super.onResume();
	    if (videoPlayer != null && videoPlayer.isPlaying()) {
        	mController.show();
        	videoPlayer.start();
        }
	}

	@Override
	protected void onStop() {
	    super.onStop();
	    releaseMediaPlayer();
	}
		
	@Override
    protected void onDestroy() {
        releaseMediaPlayer();
        super.onDestroy();
    }

    private void releaseMediaPlayer() {
        if (videoPlayer != null) {
        	mController.hide();
     	    videoPlayer.stop();
            videoPlayer.release();
            videoPlayer = null;
        }
    }
	
	/**  Use screen touches to toggle the video between playing and paused. */
	@Override
	public boolean onTouchEvent (MotionEvent ev){	
		
		if(videoPlayer != null && ev.getAction() == MotionEvent.ACTION_DOWN){
			if(videoPlayer.isPlaying()){
				videoPlayer.pause();
			} else {
				videoPlayer.start();
			}
			mController.show();
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
	

	//MediaController implemented methods
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
    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
       // AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.options, menu);
        try {
			 curBrightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
        
        videoPlayer.pause();  
        menu.setHeaderTitle("Switch context");
        bLevel = BatteryCalc();
        rTime = BATTERY_CONST - (curBrightness * BRIGHT_CONST + bRate * BITRATE_CONST);
        rTime = rTime * bLevel; 
        //Log.d(TAG, "Battery level is at " + bLevel );
        
        menu.add(0, ID_MENU_PRESENT, 0 , "Present Config(" + 
        		(int)Math.round(rTime) / 60 + "hr " +
				(int)Math.round(rTime) % 60 +" min)");
        
        menu.add(0, R.id.brightness, 0, "Change Config");
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
       // AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
     
        switch (item.getItemId()) {
        case ID_MENU_PRESENT:
        	 	videoPlayer.start();
        	 	return true;
        case R.id.brightness:
	        	try {	
	        		 curBrightness = android.provider.Settings.System.getInt(getContentResolver(), 
	        				 android.provider.Settings.System.SCREEN_BRIGHTNESS);
	        		 BrightDialogue bDialog = BrightDialogue.newInstance(curBrightness, editTextSongURL.getText().toString());
	     			 bDialog.show(getFragmentManager(), "bDialog");
	     			 videoPlayer.reset();
	        	 } catch (SettingNotFoundException e) {
					e.printStackTrace();
	        	 	}
				return true;
        
     /*   case R.id.bitrate:
        	 String[] temp = editTextSongURL.getText().toString().split(".webm");
        	 temp[0] += "_2000.webm";
        	 editTextSongURL.setText(temp[0]);
        	 Toast.makeText(this, "The bitrate is " + bRate,
                    Toast.LENGTH_SHORT).show();
     		 Log.d(TAG, "String is " + temp[0] + "bitrate " + bRate );
     		 videoPlayer.release();
     		 playVideo();
       	 	 return true;*/
       	 	 
       // default: return super.onContextItemSelected(item);
        }
        return false;
    }
    
    private double BatteryCalc() {
    	Intent batteryIntent = getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	    int rawlevel = batteryIntent.getIntExtra("level", -1);
	    double scale = batteryIntent.getIntExtra("scale", -1);
	    double level = -1;
	    if (rawlevel >= 0 && scale > 0) {
	    level = rawlevel / scale;
	    }
	    return level;
    } 
  	
}
	