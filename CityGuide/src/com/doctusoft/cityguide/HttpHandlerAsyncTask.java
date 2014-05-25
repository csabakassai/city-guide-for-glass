package com.doctusoft.cityguide;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

class HttpHandlerAsyncTask extends AsyncTask<String, Void, String> {

	final HttpManager mHttpManager = new HttpManager();

	private String url;
	private Activity activity;

	public HttpHandlerAsyncTask(Activity activity, String url) {
		this.activity = activity;
		this.url = url;
	}

	@Override
	protected String doInBackground(String... params) {
		final String voiceResult = params[0];
		AccountManager accountManager = AccountManager.get(activity.getApplicationContext());
		// Use your Glassware's account type.
		Account[] accounts = accountManager.getAccountsByType("com.google");
		TextView title = (TextView) activity.findViewById(R.id.first_tv);
		if (title != null) {
			title.setText(voiceResult);
		}

		Log.e(StartActivity.TAG, "accountManager: " + accountManager);
		Log.e(StartActivity.TAG, "accounts length: " + accounts.length);

		try {
			StringBuilder stringBuilder = new StringBuilder(url);
			String json = "{voiceResult: " + voiceResult + "}";
			mHttpManager.postJSONData(json, stringBuilder.toString());
		} catch (Exception e) {
			Log.e(StartActivity.TAG, e.getMessage());
		}
		return null;
	}

}