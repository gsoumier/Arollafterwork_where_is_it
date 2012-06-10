package fr.arolla.afterwork.whereisit;

import java.util.List;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import fr.arolla.afterwork.whereisit.services.ActionBarHelper;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaPhoto;

public class WhereIsItActivity extends MapActivity {

	private static final double FRANCE_LAT = 46.640568;
	private static final double FRANCE_LNG = 2.527843;

	private MapView mapView;
	private MapController mapController;
	private List<Overlay> overlays;

	private int photoIndex;
	private PicasaPhoto photo;
	private Location photoLocation;
	private GeoPoint photoGeoPoint;

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