package fr.arolla.afterwork.whereisit;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import fr.arolla.afterwork.whereisit.services.PhotoHelper;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaPhoto;

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

		photoIndex = getIntent().getIntExtra("photoIndex", -1);
		photo = WhereIsItApplication.getInstance().getPhoto(photoIndex);

		ActionBar actionBar = getActionBar();
		actionBar.setTitle(photoIndex + 1 + "/" + WhereIsItApplication.getInstance().album.albumSize() + " "
				+ WhereIsItApplication.getInstance().album.title);
		actionBar.setSubtitle(photo.getDescription());

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
			showPhotoView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent showMap = new Intent("WhereIsIt");
					showMap.putExtra("photoIndex", photoIndex);
					startActivity(showMap);
				}
			});
		}
	}
}
