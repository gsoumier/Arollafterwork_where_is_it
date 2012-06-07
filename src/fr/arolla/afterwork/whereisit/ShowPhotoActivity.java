package fr.arolla.afterwork.whereisit;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import fr.arolla.afterwork.whereisit.services.PhotoHelper;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaPhoto;

public class ShowPhotoActivity extends Activity {

	private ImageView showPhotoView;

	private PicasaPhoto photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_photo);
		showPhotoView = (ImageView) findViewById(R.id.show_photo);
		showPhotoView.setImageResource(android.R.drawable.ic_menu_report_image);

		int photoToShow = getIntent().getIntExtra("photoIndex", -1);
		photo = WhereIsItApplication.getInstance().getPhoto(photoToShow);
		String link = photo.getLink();
		new ImgDownloadTask().execute(link);
	}

	class ImgDownloadTask extends AsyncTask<String, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			return PhotoHelper.downloadBitmap(params[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			showPhotoView.setImageBitmap(result);
		}
	}

}
