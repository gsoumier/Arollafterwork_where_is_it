package fr.arolla.afterwork.whereisit;

import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import fr.arolla.afterwork.whereisit.overlays.ItIsHereOverlay;
import fr.arolla.afterwork.whereisit.overlays.UserResultsOverlay;

public class WhereIsItActivity extends MapActivity {

	private TextView text;
	private Button validateButton;

	private MapView mapView;
	private MapController mapController;
	private List<Overlay> overlays;

	private UserResultsOverlay userResultsOverlay;

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

		text = (TextView) findViewById(R.id.text);

		initPhotoInformations();

		mapView = (MapView) findViewById(R.id.map_view);
		mapController = mapView.getController();

		GeoPoint paris = getGeoPoint(PARIS_LAT, PARIS_LNG);
		mapController.animateTo(paris);
		mapController.setZoom(12);

		Drawable starIconOff = Resources.getSystem().getDrawable(
				android.R.drawable.btn_star_big_off);
		userResultsOverlay = new UserResultsOverlay(starIconOff);

		overlays = mapView.getOverlays();
		overlays.add(userResultsOverlay);

		validateButton = (Button) findViewById(R.id.validate);
		validateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handleUserValidation();
			}
		});
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

	private void handleUserValidation() {
		GeoPoint userAnswerPoint = userResultsOverlay.getPoint();
		Location userAnswerLocation = getUserAnswerLocation(userAnswerPoint);
		float distanceTo = photoLocation.distanceTo(userAnswerLocation);
		distance = Math.round(distanceTo);
		text.setText(distance + " meters");
		Drawable starIconOn = Resources.getSystem().getDrawable(
				android.R.drawable.btn_star_big_on);
		ItIsHereOverlay resultOverlay = new ItIsHereOverlay(starIconOn,
				photoGeoPoint, photoDescription, "test");
		overlays.add(resultOverlay);
		mapView.setEnabled(false);
		validateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToNextPhoto();
			}
		});
	}

	private void goToNextPhoto() {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("distance", distance);
		setResult(RESULT_OK, resultIntent);
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