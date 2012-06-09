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
import fr.arolla.afterwork.whereisit.overlays.UserResultsOverlay;
import fr.arolla.afterwork.whereisit.services.ActionBarHelper;
import fr.arolla.afterwork.whereisit.services.ScoreHelper;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaPhoto;

public class WhereIsItActivity extends MapActivity {

	private static final double FRANCE_LAT = 46.640568;
	private static final double FRANCE_LNG = 2.527843;

	private MapView mapView;
	private MapController mapController;
	private List<Overlay> overlays;

	private UserResultsOverlay userResultsOverlay;

	private boolean validateItemVisble;
	private boolean nextItemVisible;

	private int photoIndex;
	private PicasaPhoto photo;
	private Location photoLocation;
	private GeoPoint photoGeoPoint;

	private Dialog scoreDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.where_is_it);

		initPhotoInformations();

		ActionBarHelper.fillActionBarProperties(getActionBar(), photoIndex, photo.getDescription());

		initMapAndOverlayObjects();
	}

	private void initPhotoInformations() {
		photoIndex = getIntent().getIntExtra("photoIndex", -1);
		photo = WhereIsItApplication.getInstance().getPhoto(photoIndex);
		photoLocation = new Location(LocationManager.GPS_PROVIDER);
		photoLocation.setLatitude(photo.getLatitude());
		photoLocation.setLongitude(photo.getLongitude());
		photoGeoPoint = getGeoPoint(photo.getLatitude(), photo.getLongitude());
	}

	void initMapAndOverlayObjects() {
		mapView = (MapView) findViewById(R.id.map_view);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();

		GeoPoint franceCenter = getGeoPoint(FRANCE_LAT, FRANCE_LNG);
		mapController.animateTo(franceCenter);
		mapController.setZoom(7);
		mapView.buildDrawingCache();
		mapView.setSatellite(true);

		overlays = mapView.getOverlays();

		userResultsOverlay = new UserResultsOverlay(Resources.getSystem().getDrawable(
				android.R.drawable.btn_star_big_off), new SelectionHandler());
		overlays.add(userResultsOverlay);
	}

	class SelectionHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (!validateItemVisble) {
				validateItemVisble = true;
				invalidateOptionsMenu();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.where_is_it_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_validate).setVisible(validateItemVisble);
		menu.findItem(R.id.action_next).setVisible(nextItemVisible);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_validate:
			validatePosition();
			return true;
		case R.id.action_next:
			goToNextPhotoOrResult();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void validatePosition() {
		GeoPoint userAnswerPoint = userResultsOverlay.getPoint();
		Location userAnswerLocation = getUserAnswerLocation(userAnswerPoint);
		float distanceTo = photoLocation.distanceTo(userAnswerLocation);
		int distance = Math.round(distanceTo / 100) * 100;
		ItIsHereOverlay resultOverlay = new ItIsHereOverlay(Resources.getSystem().getDrawable(
				android.R.drawable.btn_star_big_on), photoGeoPoint, photo.getDescription(), "test");
		overlays.add(resultOverlay);
		userResultsOverlay.setEnabled(false);
		mapController.animateTo(photoGeoPoint);

		validateItemVisble = false;
		nextItemVisible = true;
		invalidateOptionsMenu();

		createScoreDialog(distance);
		scoreDialog.show();

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				scoreDialog.cancel();
			}
		}, 2000);
	}

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

	private void goToNextPhotoOrResult() {
		if (++photoIndex < WhereIsItApplication.getInstance().album.albumSize()) {
			Intent nextPhoto = new Intent("ShowPhoto");
			nextPhoto.putExtra("photoIndex", photoIndex);
			startActivity(nextPhoto);
		} else {
			startActivity(new Intent("Result"));
		}
	}

	private Location getUserAnswerLocation(GeoPoint userAnswerPoint) {
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