package fr.arolla.afterwork.whereisit;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import fr.arolla.afterwork.whereisit.services.PhotoHelper;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaAlbum;

public class WelcomeActivity extends Activity {

	private final String albumUrl = "https://picasaweb.google.com/data/feed/api/user/germain.soumier/albumid/5734132424019816081";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		new AlbumDownloadTask().execute(albumUrl);
	}

	class AlbumDownloadTask extends AsyncTask<String, Integer, PicasaAlbum> {

		@Override
		protected PicasaAlbum doInBackground(String... params) {
			PicasaAlbum picasaAlbum = PhotoHelper.downloadAlbum(params[0]);
			return picasaAlbum;
		}

		@Override
		protected void onPostExecute(PicasaAlbum result) {
			WhereIsItApplication.getInstance().setAlbum(result);

			// START Iter. 0
			View buttonPlay = findViewById(R.id.button_play);
			buttonPlay.setVisibility(View.VISIBLE);
			// START Iter. 1
			buttonPlay.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					startShowPhotoActivity();
				}
			});
			// END Iter. 1
			// END Iter. 0
		}

	}

	/*
	 * START Iter. 1
	 */
	void startShowPhotoActivity() {
		Intent intent = new Intent("ShowPhoto");
		intent.putExtra("photoIndex", 0);
		startActivity(intent);
	}

}
