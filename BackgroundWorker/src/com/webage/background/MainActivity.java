package com.webage.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView numberTxt;
	ThreadControl tControl = new ThreadControl();
	int count = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		numberTxt = (TextView) findViewById(R.id.numberTxt);

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				Runnable action = new Runnable() {
					public void run() {
						numberTxt.setText(String.valueOf(count));
					}
				};

				try {
					//Do some long running work
					while (true) {
						//Pause work if control is paused.
						tControl.waitIfPaused();
						//Stop work if control is cancelled.
						if (tControl.isCancelled()) {
							break;
						}
						++count;
						if (count % 1000 == 0) {
							runOnUiThread(action);
						}
					}
				} catch (Exception e) {

				}
				return null;
			}
		};
		
		task.execute();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//Cancel control
		tControl.cancel();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		//No need to pause if we are getting destroyed and will cancel
		//thread control anyway.
		if (!isFinishing()) {
			//Pause control.
			tControl.pause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		tControl.resume();
	}
}