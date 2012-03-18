package fr.arolla.afterwork.whereisit;

import fr.arolla.afterwork.whereisit.db.WhereIsItDBAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity {

	WhereIsItDBAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		dbAdapter = new WhereIsItDBAdapter(this);
		dbAdapter.open();
		
		
		ImageView image = (ImageView) findViewById(R.id.image);
		image.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent("WhereIsIt");
				startActivity(intent);
			}
		});
	}
}
