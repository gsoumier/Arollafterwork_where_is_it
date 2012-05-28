package fr.arolla.afterwork.whereisit;

import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import fr.arolla.afterwork.whereisit.overlays.ItIsHereOverlay;
import fr.arolla.afterwork.whereisit.overlays.UserResultsOverlay;

public class WhereIsItActivity extends MapActivity {

	private MapView mapView;
	private MapController mapController;
	private List<Overlay> overlays;

	private UserResultsOverlay userResultsOverlay;

	private boolean showValidateBtn = false;
	private boolean showNextBtn = false;

	private MenuItem validateItem;
	private MenuItem nextItem;

	private static final double PARIS_LAT = 48.860649;
	private static final double PARIS_LNG = 2.352448;

	private Location photoLocation;
	private GeoPoint photoGeoPoint;
	private String photoDescription;

	private int distance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.where_is_it);

		ActionBar actionBar = getActionBar();
		actionBar.setTitle("1/10");
		actionBar.setSubtitle("Placer sur la carte");

		initPhotoInformations();

		mapView = (MapView) findViewById(R.id.map_view);
		mapController = mapView.getController();
		mapView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!showValidateBtn && !showNextBtn) {
					showValidateBtn = true;
					invalidateOptionsMenu();
				}
			}
		});

		GeoPoint paris = getGeoPoint(PARIS_LAT, PARIS_LNG);
		mapController.animateTo(paris);
		mapController.setZoom(13);

		Drawable starIconOff = Resources.getSystem().getDrawable(
				android.R.drawable.btn_star_big_off);
		userResultsOverlay = new UserResultsOverlay(starIconOff);

		overlays = mapView.getOverlays();
		overlays.add(userResultsOverlay);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.where_is_it_menu, menu);
		validateItem = menu.findItem(R.id.action_validate);
		nextItem = menu.findItem(R.id.action_next);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (showValidateBtn) {
			validateItem.setVisible(true);
			nextItem.setVisible(false);
			return true;
		}
		if (showNextBtn) {
			validateItem.setVisible(false);
			nextItem.setVisible(true);
			return true;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_validate:
			validatePosition();
			return true;
		case R.id.action_next:
			goToNextPhoto();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initPhotoInformations() {
		Intent callIntent = getIntent();
		double photoLat = callIntent.getDoubleExtra("lat", PARIS_LAT);
		double photoLng = callIntent.getDoubleExtra("lng", PARIS_LNG);
		photoLocation = new Location(LocationManager.GPS_PROVIDER);
		photoLocation.setLatitude(photoLat);
		photoLocation.setLongitude(photoLng);
		photoGeoPoint = getGeoPoint(photoLat, photoLng);
		photoDescription = callIntent.getStringExtra("desc");
	}

	private void validatePosition() {
		GeoPoint userAnswerPoint = userResultsOverlay.getPoint();
		Location userAnswerLocation = getUserAnswerLocation(userAnswerPoint);
		float distanceTo = photoLocation.distanceTo(userAnswerLocation);
		distance = Math.round(distanceTo);
		String subTitle = getResources().getString(R.string.distance)
				+ distance + getResources().getString(R.string.meters);
		getActionBar().setSubtitle(subTitle);
		Drawable starIconOn = Resources.getSystem().getDrawable(
				android.R.drawable.btn_star_big_on);
		ItIsHereOverlay resultOverlay = new ItIsHereOverlay(starIconOn,
				photoGeoPoint, photoDescription, "test");
		overlays.add(resultOverlay);
		userResultsOverlay.setEnabled(false);
		mapController.animateTo(photoGeoPoint);
		showNextBtn = true;
		showValidateBtn = false;
		invalidateOptionsMenu();
	}

	private void goToNextPhoto() {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("distance", distance);
		setResult(RESULT_OK, resultIntent);
		this.finish();
	}

	private Location getUserAnswerLocation(GeoPoint userAnswerPoint) {
		Location result = new Location(LocationManager.GPS_PROVIDER);
		result.setLatitude(userAnswerPoint.getLatitudeE6() * 1E-6);
		result.setLongitude(userAnswerPoint.getLongitudeE6() * 1E-6);
		return result;
	}

	private GeoPoint getGeoPoint(double lat, double lng) {
		GeoPoint result = new GeoPoint(new Double(lat * 1E6).intValue(),
				new Double(lng * 1E6).intValue());
		return result;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}