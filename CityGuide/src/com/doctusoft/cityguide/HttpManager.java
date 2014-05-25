package com.doctusoft.cityguide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class HttpManager {
	private static final String TAG = HttpManager.class.getSimpleName();
	private DefaultHttpClient mClient = new DefaultHttpClient(new BasicHttpParams());

	public InputStream httpGet(String url) {
		InputStream resultImputStream = null;
		try {
			HttpGet get = new HttpGet(url);

			HttpResponse response = mClient.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					resultImputStream = entity.getContent();
				}
			} else {
				Log.d(TAG, "Not HttpStatus.SC_OK");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e(TAG, "ClientProtocolException", e);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "HttpResponseException", e);
		}

		return resultImputStream;
	}

	public InputStream httpPost(List<NameValuePair> params, String host) {
		InputStream resultImputStream = null;
		try {
			HttpPost post = new HttpPost(host);
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			HttpResponse response = mClient.execute(post);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					resultImputStream = entity.getContent();
				}
				Log.d(TAG, "HttpStatus.SC_OK");
			} else {
				Log.d(TAG, "Not HttpStatus.SC_OK: " + response.getStatusLine().getStatusCode());
			}
		} catch (HttpResponseException e) {
			e.printStackTrace();
			Log.e(TAG, "HttpResponseException", e);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "IOException", e);
		}

		return resultImputStream;
	}
	
	public InputStream postJSONData(String json, String host) {
		InputStream resultImputStream = null;
	    try {
	    	Log.d(TAG, "host: " + host);
	        HttpPost httppost = new HttpPost(host);
	        StringEntity se = new StringEntity(json, HTTP.UTF_8); 
	        se.setContentType("application/json;charset=UTF-8"); 
	        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
	        httppost.setEntity(se); 


	        HttpResponse response = mClient.execute(httppost);
	        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					resultImputStream = entity.getContent();
				}
			} else {
				Log.d(TAG, "Not HttpStatus.SC_OK: " + response.getStatusLine().getStatusCode());
			}


	    } catch (ClientProtocolException e) {
	    	Log.e(TAG, "ClientProtocolException", e);
	    } catch (IOException e) {
	    	Log.e(TAG, "IOException", e);
	    }
	    
	    return resultImputStream;
	}

	public static String IS2Str(InputStream ins) {
		if (ins != null) {
			InputStreamReader is = new InputStreamReader(ins);
			StringBuilder sb = new StringBuilder();
			try {
				BufferedReader br = new BufferedReader(is);
				String read;

				read = br.readLine();

				while (read != null) {
					sb.append(read);
					read = br.readLine();

				}
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "IOException", e);
			}

			return sb.toString();
		} else {
			return "";
		}
	}

}
