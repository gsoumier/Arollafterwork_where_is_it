package fr.arolla.afterwork.whereisit.overlays;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class UserResultOverlay extends ItIsHereOverlay {

	public UserResultOverlay(Drawable marker) {
		super(marker, null);
	}

	/*
	 * Iter 2. etape 3)
	 */
	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
		this.point = point;
		populate();
		mapView.refreshDrawableState();

		return true;
	}

}
