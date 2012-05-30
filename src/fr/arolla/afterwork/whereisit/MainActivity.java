package fr.arolla.afterwork.whereisit;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaAlbum;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaPhoto;
import fr.arolla.afterwork.whereisit.xml.parser.AlbumFeedParser;

public class MainActivity extends Activity {

	private static final int IMG_WAIT_DIALOG = 0;
	private static final int ALBUM_WAIT_DIALOG = 1;

	private static final String TAG = "WhereIsItMainActivity";

	private ImageView imageView;

	private static final int SHOW_MAP = 1;

	private String albumUrl = "https://picasaweb.google.com/data/feed/api/user/germain.soumier/albumid/5734132424019816081";

	private int albumSize;

	private float totalScore;

	private Iterator<PicasaPhoto> photoIterator;

	private PicasaPhoto currentPhoto;

	private Bitmap mBitmap;

	private Drawable mImgWait;

	private ProgressDialog mImgLoadDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		imageView = (ImageView) findViewById(R.id.image);
		mImgWait = Resources.getSystem().getDrawable(
				android.R.drawable.ic_menu_report_image);
		imageView.setImageDrawable(mImgWait);

		AlbumDownloadTask albumDownloadTask = new AlbumDownloadTask();
		albumDownloadTask.execute(albumUrl);
		showDialog(ALBUM_WAIT_DIALOG);
	}

	private void showNextPhotoOrResult() {
		if (photoIterator != null && photoIterator.hasNext()) {
			currentPhoto = photoIterator.next();
			showNextPhoto();
		} else {
			showResult();
		}
	}

	class ImgDownloadTask extends AsyncTask<String, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			// try {
			URLConnection urlC;
			try {
				urlC = new URL(params[0]).openConnection();
				InputStream is = urlC.getInputStream();
				mBitmap = BitmapFactory.decodeStream(is);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(TAG, "Error occured while creating drawable : "
						+ params[0], e);
			}

			return mBitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(mBitmap);
			imageView.setOnClickListener(new PhotoOnClickListener());
			mImgLoadDialog.cancel();
		}
	}

	class AlbumDownloadTask extends AsyncTask<String, Integer, PicasaAlbum> {

		@Override
		protected PicasaAlbum doInBackground(String... params) {
			PicasaAlbum picasaAlbum = null;
			try {
				// URL feedUrl = new URL(params[0]);
				// URLConnection urlC = feedUrl.openConnection();
				// InputStream is = urlC.getInputStream();
				// PicasaAlbumXmlHandler xmlHandler = new
				// PicasaAlbumXmlHandler();
				// picasaAlbum = xmlHandler.parse(is);
				AlbumFeedParser parser = new AlbumFeedParser();
				parser.parse(params[0]);
			} catch (Exception e) {
				Log.e(TAG,
						"Error occured while retriving picasa album from URL : "
								+ albumUrl, e);
			}
			return picasaAlbum;
		}

		@Override
		protected void onPostExecute(PicasaAlbum result) {
			if (result != null) {
				albumSize = result.albumSize();
				photoIterator = result.iterator();
			}
			mImgLoadDialog.cancel();
			showNextPhotoOrResult();
		}
	}

	private void showNextPhoto() {
		imageView.setImageDrawable(mImgWait);
		imageView.setOnClickListener(null);

		showDialog(IMG_WAIT_DIALOG);

		String thumbnailUrlStr = currentPhoto.getLink();
		new ImgDownloadTask().execute(thumbnailUrlStr);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		mImgLoadDialog = new ProgressDialog(this);
		mImgLoadDialog.setMessage("Please wait while loading...");
		mImgLoadDialog.setIndeterminate(true);
		mImgLoadDialog.setCancelable(true);
		return mImgLoadDialog;
	}

	private void showResult() {
		float averageScore = getAverageScore();
		Intent intent = new Intent("Result");
		intent.putExtra("score", averageScore);
		startActivity(intent);
	}

	private float getAverageScore() {
		float result = totalScore / albumSize;
		return result;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SHOW_MAP:
			if (resultCode == Activity.RESULT_OK) {
				int distance = data.getIntExtra("distance", 1000);
				float score = getPointsFromDistance(distance);
				totalScore += score;
				showNextPhotoOrResult();
				Toast.makeText(this, Float.toString(score), 500).show();
			}
			break;

		default:
			break;
		}
	}

	private float getPointsFromDistance(int distance) {
		if (distance > 1000)
			return 0;
		float coeff = 1f - (float) distance / 1000f;
		float result = 10 * coeff;
		return result;
	}

	class PhotoOnClickListener implements OnClickListener {

		public void onClick(View v) {
			Intent intent = new Intent("WhereIsIt");
			intent.putExtra("lat", currentPhoto.getLatitude());
			intent.putExtra("lng", currentPhoto.getLongitude());
			intent.putExtra("tip", currentPhoto.getDescription());
			startActivityForResult(intent, SHOW_MAP);
		}

	}
}
