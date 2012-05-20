package fr.arolla.afterwork.whereisit;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.google.gdata.data.Link;
import com.google.gdata.data.geo.Point;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.GphotoEntry;
import com.google.gdata.data.photos.PhotoFeed;

import fr.arolla.afterwork.whereisit.services.PicasaWebServiceFacade;

public class MainActivity extends Activity {

	private static final String TAG = "WhereIsItMainActivity";

	private ImageView imageView;

	private static final int SHOW_MAP = 1;

	private String albumUrl = "https://picasaweb.google.com/data/feed/api/user/germain.soumier/albumid/5734132424019816081";

	private int albumSize;

	private float totalScore;

	private Iterator<GphotoEntry> photoIterator;

	private PhotoFeed currentPhotoFeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		imageView = (ImageView) findViewById(R.id.image);
		imageView.setOnClickListener(new PhotoOnClickListener());

		try {
			URL feedUrl = new URL(albumUrl);
			AlbumFeed feed = PicasaWebServiceFacade.getService().getFeed(
					feedUrl, AlbumFeed.class);
			List<GphotoEntry> entries = feed.getEntries();
			albumSize = entries.size();
			photoIterator = entries.iterator();
		} catch (Exception e) {
			Log.e(TAG, "Error occured while retriving picasa album from URL : "
					+ albumUrl, e);
		}

		showNextPhotoOrResult();

	}

	private void showNextPhotoOrResult() {
		updateCurrentPhotoFeed();
		if (currentPhotoFeed != null) {
			showNextPhoto();
		} else {
			showResult();
		}
	}

	private void updateCurrentPhotoFeed() {
		currentPhotoFeed = null;
		if (photoIterator.hasNext()) {
			GphotoEntry photoEntry = photoIterator.next();
			Link link = photoEntry.getLink(Link.Rel.FEED, null);
			String photoUrl = link.getHref();
			try {
				URL url = new URL(photoUrl);
				currentPhotoFeed = PicasaWebServiceFacade.getService().getFeed(
						url, PhotoFeed.class);
			} catch (Exception e) {
				Log.e(TAG,
						"Error occured while retriving picasa photo from URL : "
								+ photoUrl, e);
			}
		}
	}

	private void showNextPhoto() {
		String thumbnailUrlStr = currentPhotoFeed.getMediaThumbnails().get(1)
				.getUrl();
		Drawable photoDrawable = null;
		try {
			URL thumbnailUrl = new URL(thumbnailUrlStr);
			URLConnection urlC = thumbnailUrl.openConnection();
			InputStream is = urlC.getInputStream();
			photoDrawable = Drawable.createFromStream(is, thumbnailUrlStr);
		} catch (Exception e) {
			Log.e(TAG, "Error occured while creating drawable : "
					+ thumbnailUrlStr, e);
		}
		imageView.setImageDrawable(photoDrawable);
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
			}
			break;

		default:
			break;
		}
	}

	private float getPointsFromDistance(int distance) {
		if (distance > 1000)
			return 0;
		float coeff = 1 - (distance / 1000);
		float result = 20 * coeff;
		return result;
	}

	class PhotoOnClickListener implements OnClickListener {

		public void onClick(View v) {
			Intent intent = new Intent("WhereIsIt");
			Point geoLocation = currentPhotoFeed.getGeoLocation();
			intent.putExtra("lat", geoLocation.getLatitude());
			intent.putExtra("lng", geoLocation.getLongitude());
			intent.putExtra("tip", currentPhotoFeed.getDescription()
					.getPlainText());
			startActivityForResult(intent, SHOW_MAP);
		}

	}
}
