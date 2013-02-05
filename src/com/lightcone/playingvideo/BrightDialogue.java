package com.lightcone.playingvideo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lightcone.playingvideo.PlayingVideo;

public class BrightDialogue extends DialogFragment {

	private static String TAG="Bright Dialog";
	
	private static final double BATTERY_CONST = 313;
	private static final double BRIGHT_CONST = 0.8892;
	private static final double BITRATE_CONST = 0.0156;
	private MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
	static int currentBrightness;
    int BrightValue;
	static String textUrl;
	SeekBar editTextBrightness = null;
	TextView textViewTime = null;
	RadioGroup radioGroup;
	double bRate;
	double rTime;
	double bLevel;

    static BrightDialogue newInstance(int currentBrightness, String textUrl) {
    	BrightDialogue p = new BrightDialogue();
        Bundle args = new Bundle();
        args.putInt("currentBrightness", currentBrightness);
        args.putString("textUrl", textUrl);
        p.setArguments(args);
        return p;
    }

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	currentBrightness = getArguments().getInt("currentBrightness");
    	textUrl = getArguments().getString("textUrl");
    	metaRetriever.setDataSource(textUrl);
		bRate = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE))/1000.0;
		super.onCreate(savedInstanceState);
    }

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View v = inflater.inflate(R.layout.bright_layout, null);
        
        textViewTime = (TextView)v.findViewById(R.id.textViewTime);
        editTextBrightness = (SeekBar)v.findViewById(R.id.editTextBrightness);
        
        /*editTextBrightness.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
			@Override
			public void afterTextChanged(Editable arg0) {
				 if (arg0.equals("")){ // detect an empty string and set it to "0" instead
				    arg0.append("0");
				  }
				 try {
					 curBrightness = Integer.parseInt(arg0.toString());
				 
					 if(curBrightness > 255) {
						 textViewBrightness.setText("Enter brightness(0-255)");
						 arg0.clear();
						 arg0.append("255");
					 }
					 else {
						 rTime = BATTERY_CONST - (curBrightness * BRIGHT_CONST + bRate * BITRATE_CONST);
						 rTime = rTime * bLevel; 
						 textViewBrightness.setText("Running time:" + 
								 (int)Math.round(rTime) / 60 + "hr " +
								 (int)Math.round(rTime) % 60 +" min)");
					 }
				 } catch (NumberFormatException e){ }
				   catch (StackOverflowError e) {}
			     
			}
        }); */
        
        editTextBrightness.setProgress(currentBrightness);
        editTextBrightness.setMax(245);
        editTextBrightness.setKeyProgressIncrement(5);
        editTextBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

        	 @Override
        	 public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        		 BrightValue = arg1+10;
        		 setBrightness(v , BrightValue);
        	 }

        	 @Override
        	 public void onStartTrackingTouch(SeekBar arg0) {
        	 }

        	 @Override
        	 public void onStopTrackingTouch(SeekBar arg0) {
        		 try {
        		     bLevel = BatteryCalc(v.getContext());
        		     rTime = BATTERY_CONST - (BrightValue * BRIGHT_CONST + bRate * BITRATE_CONST);
					 rTime = rTime * bLevel; 
					 Log.d(TAG, "Value of bitrate is " + bRate );
					 textViewTime.setText("Running time:" + 
								 (int)Math.round(rTime) / 60 + "hr " +
								 (int)Math.round(rTime) % 60 +" min)");
				 } catch (NumberFormatException e){ }
				   catch (StackOverflowError e) {}

        	 }});
        
        radioGroup = (RadioGroup)v.findViewById(R.id.radiogroup);
        radioGroup.check(R.id.rbMedium);
        
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
        	
            //int checkedRadioButton = radioGroup.getCheckedRadioButtonId();
            //String[] temp = textUrl.split("_");
            String radioButtonSelected = "";
            
        	public void onCheckedChanged (RadioGroup group, int checkedId) {
        		String[] temp = textUrl.split("_");
                switch (checkedId) {
                case R.id.rbLow : 
                		radioButtonSelected = "Bitrate_Low";
                		temp[0] += "_low.webm"; 
                		Log.d(TAG, "Value of bitrate is " + radioButtonSelected );
                		break;
				case R.id.rbMedium : 
						radioButtonSelected = "Bitrate_Medium";
						temp[0] += "_med.webm";
						Log.d(TAG, "Value of bitrate is " + radioButtonSelected );
				        break;
				case R.id.rbHigh : 
						radioButtonSelected = "Bitrate_High";
						temp[0] += "_high.webm";
						Log.d(TAG, "Value of bitrate is " + radioButtonSelected );
				        break;
                }
                Log.d(TAG, "File Selected is " + temp[0] );
                textUrl = temp[0];
               
                try {
	                metaRetriever.setDataSource(temp[0]);
	        		bRate = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE))/1000.0;
	        	 } catch(Exception e) {
                Log.e(TAG, "Fails to setDataSource for file " + temp[0]);
	        	 }  
        		
        		try {
					 rTime = BATTERY_CONST - (BrightValue * BRIGHT_CONST + bRate * BITRATE_CONST);
					 rTime = rTime * bLevel; 
					 Log.d(TAG, "Value of bitrate is " + bRate );
					 textViewTime.setText("Running time:" + 
								 (int)Math.round(rTime) / 60 + "hr " +
								 (int)Math.round(rTime) % 60 +" min)");
				 } catch (NumberFormatException e){ }
				   catch (StackOverflowError e) {}
        		 
        	}
        	
        });
        
              
        return new AlertDialog.Builder(getActivity())
                .setTitle("Change Brightness and Bitrate...")
                .setView(v)
                .setCancelable(true)
                .setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	setBrightness(v , BrightValue);
                    	currentBrightness = BrightValue;
                    	Log.d(TAG, "File played :  " + textUrl);
                    	PlayingVideo.editTextSongURL.setText(textUrl);
   	     			 	PlayingVideo.buttonPlay.performClick();
   	     			 	Toast.makeText( v.getContext(), "Brightness : " + currentBrightness +
   	     			 		"\nBitrate : " + bRate + 
   	     			 		"\nRunning Time : " 
   	     			 		+ (int)Math.round(rTime) / 60 + "hr " 
   	     			 		+ (int)Math.round(rTime) % 60 +" min",
   	     			 		Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        	setBrightness(v, currentBrightness );
                        	dialog.cancel();
                        }
                    }).create();
     }
    

	 public static void setBrightness(final View view , int bright) {
	        // get the content resolver
	        final ContentResolver cResolver = view.getContext().getContentResolver();
	        // get the brightness mode
	        int brightnessMode;
	        
	        try {
	        	brightnessMode = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
	        	if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
	        		Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, 
	        				Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	        	}
	        } catch (SettingNotFoundException e) {
	        	e.printStackTrace();
	        }
	        
	        final Window window = ((Activity) view.getContext()).getWindow();
	        if(bright >=10)
	        	{
	        		Log.d(TAG, "Change brightness to " + bright);
	        		LayoutParams layoutpars = window.getAttributes();
	    			// set the brightness of this window
	    			layoutpars.screenBrightness = Float.valueOf(bright) * (1f / 255f);
	    			// apply attribute changes to this window
	    			window.setAttributes(layoutpars);
	    			Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
	    			android.provider.Settings.System.putInt(cResolver, "screen_brightness",
	    			           bright);
	    			cResolver.notifyChange(uri, null);
	        	}
	        
	    }
	 
	 private double BatteryCalc(Context context) {
			Intent batteryIntent = context.getApplicationContext().registerReceiver(null,
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