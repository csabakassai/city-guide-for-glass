package com.doctusoft.cityguide;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.glass.app.Card;

public class SearchActivity extends Activity {

	static final String TAG = StartActivity.class.getSimpleName();
	final HttpManager mHttpManager = new HttpManager();
	private ArrayList<String> mVoiceResults;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getIntent() != null && getIntent().getExtras() != null) {
			mVoiceResults = getIntent().getExtras().getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
			new HttpHandlerAsyncTask(this, "http://doctusoft-city-guide2.appspot.com/search").execute(mVoiceResults.get(0));
		}
		finish();
	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent event) {
		if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
			// user tapped touchpad, do something
			return true;
		}

		return super.onKeyDown(keycode, event);
	}

}
