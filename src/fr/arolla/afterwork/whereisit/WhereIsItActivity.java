package fr.arolla.afterwork.whereisit;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import fr.arolla.afterwork.whereisit.overlays.ItIsHereOverlay;
import fr.arolla.afterwork.whereisit.overlays.UserResultOverlay;
import fr.arolla.afterwork.whereisit.services.ActionBarHelper;
import fr.arolla.afterwork.whereisit.services.ScoreHelper;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaPhoto;

public class WhereIsItActivity extends MapActivity {

	private static final double FRANCE_LAT = 46.640568;
	private static final double FRANCE_LNG = 2.527843;

	private MapView mapView;
	private MapController mapController;
	private List<Overlay> overlays;

	/*
	 * Iter. 2 etape 2)
	 */
	private UserResultOverlay userResultsOverlay;

	// START Iter. 3 etape 2)
	private boolean validateItemVisble;
	private boolean nextItemVisible;
	// END Iter. 3 etape 2)

	private int photoIndex;
	private PicasaPhoto photo;
	private Location photoLocation;
	private GeoPoint photoGeoPoint;

	/*
	 * Iter 4 etape 4)
	 */
	private Dialog scoreDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.where_is_it);

		initPhotoInformations();

		ActionBarHelper.fillActionBarProperties(getActionBar(), photoIndex, photo.getDescription());

		initMapAndOverlayObjects();

		// START Iter. 2 etape 2)
		addUserResultOverlay();
		// END Iter. 2 etape 2)
	}

	private void initPhotoInformations() {
		photoIndex = getIntent().getIntExtra("photoIndex", -1);
		photo = WhereIsItApplication.getInstance().getPhoto(photoIndex);
		if (photo == null)
			throw new RuntimeException("no photo found corresponding to index " + photoIndex);
		photoLocation = new Location(LocationManager.GPS_PROVIDER);
		photoLocation.setLatitude(photo.getLatitude());
		photoLocation.setLongitude(photo.getLongitude());
		photoGeoPoint = getGeoPoint(photo.getLatitude(), photo.getLongitude());
	}

	void initMapAndOverlayObjects() {
		mapView = (MapView) findViewById(R.id.map_view);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);

		mapController = mapView.getController();
		mapController.animateTo(getGeoPoint(FRANCE_LAT, FRANCE_LNG));
		mapController.setZoom(7);

		overlays = mapView.getOverlays();
	}

	/*
	 * Iter 2. etape 2)
	 */
	void addUserResultOverlay() {

		// userResultsOverlay = new
		// UserResultOverlay(Resources.getSystem().getDrawable(
		// android.R.drawable.btn_star_big_off));

		// START Iter. 3 etape 2)
		userResultsOverlay = new UserResultOverlay(Resources.getSystem().getDrawable(
				android.R.drawable.btn_star_big_off), new SelectionHandler());
		overlays.add(userResultsOverlay);
		// END Iter. 3 etape 2)
	}

	/*
	 * Iter 3. etape 2)
	 */
	class SelectionHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (!validateItemVisble) {
				validateItemVisble = true;
				invalidateOptionsMenu();
			}
		}
	}

	// START Iter. 3 etape 1)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.where_is_it_menu, menu);
		return true;
	}

	// END Iter. 3 etape 1)

	// START Iter. 3 etape 2)
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_validate).setVisible(validateItemVisble);
		menu.findItem(R.id.action_next).setVisible(nextItemVisible);
		return true;
	}

	// END Iter. 3 etape 2)

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// START Iter. 3 etape 3)
		case R.id.action_validate:
			validatePosition();
			return true;
			// END Iter. 3 etape 3)
			// START Iter. 5 etape 2)
		case R.id.action_next:
			goToNextPhotoOrResult();
			return true;
			// END Iter. 5 etape 2)
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * Iter. 4 etape 1)
	 */
	private void validatePosition() {
		ItIsHereOverlay resultOverlay = new ItIsHereOverlay(Resources.getSystem().getDrawable(
				android.R.drawable.btn_star_big_on), photoGeoPoint);
		overlays.add(resultOverlay);
		mapController.animateTo(photoGeoPoint);

		// START Iter. 4 etape 2)
		userResultsOverlay.setEnabled(false);
		// END Iter. 4 etape 2)

		// START Iter. 3 etape 2)
		validateItemVisble = false;
		nextItemVisible = true;
		invalidateOptionsMenu();
		// END Iter. 3 etape 2)

		// START Iter. 4 etape 3)
		GeoPoint userAnswerPoint = userResultsOverlay.getPoint();
		Location userAnswerLocation = getLocationFromGeoPoint(userAnswerPoint);
		float distanceTo = photoLocation.distanceTo(userAnswerLocation);
		int distance = Math.round(distanceTo / 100) * 100;

		// new Toast(this).makeText(this, distance, 2000).show();
		// END Iter. 4 etape 3)

		// START Iter. 4 etape 4)
		createScoreDialog(distance);
		scoreDialog.show();

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				scoreDialog.cancel();
			}
		}, 2000);
		// END Iter. 4 etape 4)
	}

	/*
	 * Iter. 4 etape 4)
	 */
	private void createScoreDialog(int distance) {
		scoreDialog = new Dialog(this);
		scoreDialog.setContentView(R.layout.score_dialog);
		String distanceResult = getResources().getString(R.string.distance) + " " + distance + " "
				+ getResources().getString(R.string.meters);
		scoreDialog.setTitle(distanceResult);
		ImageView star1 = (ImageView) scoreDialog.findViewById(R.id.star1);
		ImageView star2 = (ImageView) scoreDialog.findViewById(R.id.star2);
		ImageView star3 = (ImageView) scoreDialog.findViewById(R.id.star3);
		Integer[] scoreStarIcons = ScoreHelper.getScoreStarIcons(distance);
		star1.setImageResource(scoreStarIcons[0]);
		star2.setImageResource(scoreStarIcons[1]);
		star3.setImageResource(scoreStarIcons[2]);
	}

	/*
	 * Iter. 5 etape 2) et 3)
	 */
	private void goToNextPhotoOrResult() {
		if (++photoIndex < WhereIsItApplication.getInstance().album.albumSize()) {
			Intent nextPhoto = new Intent("ShowPhoto");
			nextPhoto.putExtra("photoIndex", photoIndex);
			startActivity(nextPhoto);
		} else {
			startActivity(new Intent("Result"));
		}
	}

	private Location getLocationFromGeoPoint(GeoPoint userAnswerPoint) {
		Location result = new Location(LocationManager.GPS_PROVIDER);
		result.setLatitude(userAnswerPoint.getLatitudeE6() * 1E-6);
		result.setLongitude(userAnswerPoint.getLongitudeE6() * 1E-6);
		return result;
	}

	private GeoPoint getGeoPoint(double lat, double lng) {
		GeoPoint result = new GeoPoint(new Double(lat * 1E6).intValue(), new Double(lng * 1E6).intValue());
		return result;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}