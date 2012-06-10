package fr.arolla.afterwork.whereisit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import fr.arolla.afterwork.whereisit.services.ActionBarHelper;
import fr.arolla.afterwork.whereisit.services.PhotoHelper;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaPhoto;

/*
 * Iter. 1 etape 1)
 */
public class ShowPhotoActivity extends Activity {

	private ImageView showPhotoView;

	private int photoIndex;

	private PicasaPhoto photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_photo);

		showPhotoView = (ImageView) findViewById(R.id.show_photo);
		showPhotoView.setImageResource(android.R.drawable.ic_menu_report_image);

		// START Ex. 1 etape 3)
		photoIndex = getIntent().getIntExtra("photoIndex", -1);
		photo = WhereIsItApplication.getInstance().getPhoto(photoIndex);
		// END Ex. 1 etape 3)

		ActionBarHelper.fillActionBarProperties(getActionBar(), photoIndex, photo.getDescription());

		// START Ex. 1 etape 5)
		String link = photo.getLink();
		new ImgDownloadTask().execute(link);
		// END Ex. 1 etape 5)
	}

	/*
	 * Iter 1 etape 5)
	 */
	class ImgDownloadTask extends AsyncTask<String, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			return PhotoHelper.downloadBitmap(params[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			showPhotoView.setImageBitmap(result);

			// START Iter 2 etape 1)
			showPhotoView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent showMap = new Intent("WhereIsIt");
					showMap.putExtra("photoIndex", photoIndex);
					startActivity(showMap);
				}
			});
			// END Iter 2 etape 1)
		}
	}
}
