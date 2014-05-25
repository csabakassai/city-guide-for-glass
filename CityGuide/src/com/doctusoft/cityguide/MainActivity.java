package com.doctusoft.cityguide;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.android.glass.app.Card;

public class MainActivity extends Activity implements OnClickListener {
	

	
	private static final String TAG = MainActivity.class.getSimpleName();
	private final HttpManager mHttpManager = new HttpManager();
	private ArrayList<String> mVoiceResults;
	private View mCardView;
	private Card mCard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getIntent() != null && getIntent().getExtras() != null) {
			mVoiceResults = getIntent().getExtras().getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
		
			mCard = new Card(this);
			mCardView = mCard.setText(mVoiceResults.get(0)).getView();
			// To receive touch events from the touchpad, the view should be
			// focusable.
			mCardView.setOnClickListener(this);
			mCardView.setFocusable(true);
			mCardView.setFocusableInTouchMode(true);
			setContentView(mCardView);
			// //By XML
			// setContentView(R.layout.activity_main);
			// TextView firstTextView = (TextView) findViewById(R.id.first_tv);
			// firstTextView.setText(mVoiceResults.get(0));
	
			// Networking tasks must run a non UI thread.
			new HttpHandlerAsyncTask().execute(mVoiceResults.get(0));
		}
		
		// // Download image
		// try {
		// URL url = new URL("http://example.com/image.png");
		// new DownloadImageAsyncTask().execute(url);
		// } catch (MalformedURLException e) {
		// Log.e(TAG, e.getMessage());
		// }

		// //Close card
		// finishAffinity();

		// //Start navigation with intent
		// Intent intent = new Intent(Intent.ACTION_VIEW);
		// intent.setData(Uri.parse("google.navigation:q="+lat+","+lon +
		// "&mode=w"));
		// startActivity(intent);
		
		// //Start other activity
		// Intent intent = new Intent(MainActivity.this, OtherActivity.class);
		// startActivity(intent);
	}

	@Override
	protected void onResume() {		
		super.onResume();
		if(mCardView != null)
			mCardView.requestFocus();
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent event) {
		if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
			// user tapped touchpad, do something
			return true;
		}

		return super.onKeyDown(keycode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	class HttpHandlerAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			final String voiceResult = params[0];
			AccountManager accountManager = AccountManager.get(getApplicationContext());
			// Use your Glassware's account type.
			Account[] accounts = accountManager.getAccountsByType("com.google");
			TextView title = (TextView) findViewById(R.id.first_tv);
			if(title != null) {
				title.setText(voiceResult);
			}

			Log.e(TAG, "accountManager: "+ accountManager);
			Log.e(TAG, "accounts length: "+ accounts.length);
					
			try {
				StringBuilder stringBuilder = new StringBuilder("http://doctusoft-city-guide2.appspot.com/voice");
				String json = "{voiceResult: " + voiceResult + "}";
				mHttpManager.postJSONData(json, stringBuilder.toString());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}

	}

//	class DownloadImageAsyncTask extends AsyncTask<URL, Void, Bitmap> {
//
//		@Override
//		protected Bitmap doInBackground(URL... params) {
//			return downloadPicFromURL(params[0]);
//		}
//
//		@Override
//		protected void onPostExecute(Bitmap result) {
//			super.onPostExecute(result);
//			mCard.addImage(result);
//		}
//
//		private Bitmap downloadPicFromURL(URL url) {
//			try {
//				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//				int length = connection.getContentLength();
//				InputStream is = (InputStream) url.getContent();
//				byte[] imageData = new byte[length];
//				int buffersize = (int) Math.ceil(length / (double) 100);
//				int downloaded = 0;
//				int read;
//				while (downloaded < length) {
//					if (length < buffersize) {
//						read = is.read(imageData, downloaded, length);
//					} else if ((length - downloaded) <= buffersize) {
//						read = is.read(imageData, downloaded, length - downloaded);
//					} else {
//						read = is.read(imageData, downloaded, buffersize);
//					}
//					downloaded += read;
//				}
//				Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, length);
//				if (bitmap != null) {
//					Log.d(TAG, "Bitmap created");
//				} else {
//					Log.d(TAG, "Bitmap not created");
//				}
//				is.close();
//				return bitmap;
//			} catch (MalformedURLException e) {
//				Log.e(TAG, "Malformed exception: ", e);
//			} catch (IOException e) {
//				Log.e(TAG, "IOException: ", e);
//			} catch (Exception e) {
//				Log.e(TAG, "Exception: ", e);
//			}
//			return null;
//
//		}
//	}
}
