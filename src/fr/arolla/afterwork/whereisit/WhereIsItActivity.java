package fr.arolla.afterwork.whereisit;

import java.util.List;

import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import fr.arolla.afterwork.whereisit.overlays.UserResultOverlay;
import fr.arolla.afterwork.whereisit.services.ActionBarHelper;
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
	// END Iter. 3 etape 2)

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
		return true;
	}

	// END Iter. 3 etape 2)

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// START Iter. 3 etape 3)
		case R.id.action_validate:
			Toast.makeText(this, "click sur activer", 1000).show();
			return true;
			// END Iter. 3 etape 3)
		default:
			return super.onOptionsItemSelected(item);
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