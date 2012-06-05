package fr.arolla.afterwork.whereisit;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import fr.arolla.afterwork.whereisit.xml.parser.PicasaAlbumXmlHandler;

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

	private Map<String, Position> locationMockMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fillLocationMockMap();
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
		if (photoIterator.hasNext()) {
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
				URL feedUrl = new URL(params[0]);
				URLConnection urlC = feedUrl.openConnection();
				InputStream is = urlC.getInputStream();
				PicasaAlbumXmlHandler xmlHandler = new PicasaAlbumXmlHandler();
				picasaAlbum = xmlHandler.parse(is);
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
				int score = getPointsFromDistance(distance);
				totalScore += score;
				showNextPhotoOrResult();
				Toast.makeText(this, "" + score, 500).show();
			}
			break;

		default:
			break;
		}
	}

	private int getPointsFromDistance(int distance) {
		int maxDistanceForPoints = 100000;
		if (distance > maxDistanceForPoints)
			return 0;
		int points = maxDistanceForPoints - distance / maxDistanceForPoints;
		int result = (points * 100) / maxDistanceForPoints;
		return result;
	}

	class PhotoOnClickListener implements OnClickListener {

		public void onClick(View v) {
			Intent intent = new Intent("WhereIsIt");
			String photoId = currentPhoto.getId();
			locationMockMap.get(photoId);
			intent.putExtra("lat", getLatitude(currentPhoto));
			intent.putExtra("lng", getLongitude(currentPhoto));
			intent.putExtra("tip", currentPhoto.getDescription());
			startActivityForResult(intent, SHOW_MAP);
		}

		private Double getLatitude(PicasaPhoto currentPhoto) {
			if (currentPhoto.getLatitude() != null) {
				return currentPhoto.getLatitude();
			}
			Position position = locationMockMap.get(currentPhoto.getId());
			if (position == null) {
				return null;
			}
			return position.getLat();
		}

		private Double getLongitude(PicasaPhoto currentPhoto) {
			if (currentPhoto.getLongitude() != null) {
				return currentPhoto.getLongitude();
			}
			Position position = locationMockMap.get(currentPhoto.getId());
			if (position == null) {
				return null;
			}
			return position.getLng();
		}

	}

	private void fillLocationMockMap() {
		locationMockMap = new HashMap<String, Position>();
		locationMockMap
				.put("https://picasaweb.google.com/data/entry/api/user/114060422820973437445/albumid/5734132424019816081/photoid/5748107040104845138",
						new Position(48.865916, 2.225677));
		locationMockMap
				.put("https://picasaweb.google.com/data/entry/api/user/114060422820973437445/albumid/5734132424019816081/photoid/5748477498962023090",
						new Position(48.612729, -1.505180));
		locationMockMap
				.put("https://picasaweb.google.com/data/entry/api/user/114060422820973437445/albumid/5734132424019816081/photoid/5748478389030221938",
						new Position(45.767500, 4.828889));
		locationMockMap
				.put("https://picasaweb.google.com/data/entry/api/user/114060422820973437445/albumid/5734132424019816081/photoid/5748478868755550402",
						new Position(45.328611, 3.708611));
		locationMockMap
				.put("https://picasaweb.google.com/data/entry/api/user/114060422820973437445/albumid/5734132424019816081/photoid/5748479441511849042",
						new Position(-22.947447, -43.1564));

	}

	class Position {
		double lat;
		double lng;

		public Position(double lat, double lng) {
			this.lat = lat;
			this.lng = lng;
		}

		public double getLat() {
			return lat;
		}

		public double getLng() {
			return lng;
		}

	}
}
