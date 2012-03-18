package fr.arolla.afterwork.whereisit.overlays;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class UserResultsOverlay extends ItIsHereOverlay {

	private GeoPoint point;

	public UserResultsOverlay(Drawable marker) {
		super(marker, null, "Here ?", "Are you sure ?");
	}

	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
		if (!mapView.isEnabled()) {
			return false;
		}

		this.point = point;
		changePoint(point);
		return true;
	}

	public GeoPoint getPoint() {
		return point;
	}

}
