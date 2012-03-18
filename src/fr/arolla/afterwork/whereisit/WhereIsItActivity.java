package fr.arolla.afterwork.whereisit;

import java.util.List;

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

	private MapView mapView;

	private MapController mapController;

	private UserResultsOverlay userResultsOverlay;

	private Location eiffelTowerLocation;
	private GeoPoint eiffelTowerGeoPoint;

	private static final double EIFFEL_TOWER_LAT = 48.858871;
	private static final double EIFFEL_TOWER_LNG = 2.294598;

	private static final double PARIS_LAT = 48.860649;
	private static final double PARIS_LNG = 2.352448;

	private View questionLayout;
	private TextView resultText;

	private List<Overlay> overlays;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.where_is_it);

		questionLayout = findViewById(R.id.question);
		resultText = (TextView) findViewById(R.id.result);

		initEiffelTowerLocationAndPoint();

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

		Button validateButton = (Button) findViewById(R.id.validate);
		validateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handleUserValidation();
			}

		});
	}

	private void initEiffelTowerLocationAndPoint() {
		eiffelTowerLocation = new Location(LocationManager.GPS_PROVIDER);
		eiffelTowerLocation.setLatitude(EIFFEL_TOWER_LAT);
		eiffelTowerLocation.setLongitude(EIFFEL_TOWER_LNG);
		eiffelTowerGeoPoint = getGeoPoint(EIFFEL_TOWER_LAT, EIFFEL_TOWER_LNG);
	}

	protected void handleUserValidation() {
		GeoPoint userAnswerPoint = userResultsOverlay.getPoint();
		Location userAnswerLocation = getUserAnswerLocation(userAnswerPoint);
		float distanceTo = eiffelTowerLocation.distanceTo(userAnswerLocation);
		int roundDistance = Math.round(distanceTo);
		questionLayout.setVisibility(View.INVISIBLE);
		resultText.setText(roundDistance + " meters");
		resultText.setVisibility(View.VISIBLE);
		Drawable starIconOn = Resources.getSystem().getDrawable(
				android.R.drawable.btn_star_big_on);
		ItIsHereOverlay resultOverlay = new ItIsHereOverlay(starIconOn,
				eiffelTowerGeoPoint, "Eiffel Tower",
				"Eiffel tower was built as the entrance arch to the 1889 World's Fair");
		overlays.add(resultOverlay);
		mapView.setEnabled(false);

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