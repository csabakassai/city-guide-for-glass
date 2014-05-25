package com.doctusoft.cityguide;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.android.glass.app.Card;

public class OtherActivity extends Activity {

	private View cardView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cardView = new Card(this).setText("Tap to carry out an action").getView();
		setContentView(cardView);
	}
}
