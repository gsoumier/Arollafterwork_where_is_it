package fr.arolla.afterwork.whereisit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);

		Intent callIntent = getIntent();
		Float score = callIntent.getFloatExtra("score", 0f);
		TextView scoreText = (TextView) findViewById(R.id.score);
		scoreText.setText(score + "");

	}

}
