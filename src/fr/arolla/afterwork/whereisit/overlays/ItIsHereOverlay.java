package fr.arolla.afterwork.whereisit.overlays;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class ItIsHereOverlay extends ItemizedOverlay<OverlayItem> {

	protected GeoPoint point;

	public ItIsHereOverlay(Drawable marker, GeoPoint point) {
		super(boundCenter(marker));
		this.point = point;
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return new OverlayItem(point, null, null);
	}

	@Override
	public int size() {
		return point == null ? 0 : 1;
	}

	public GeoPoint getPoint() {
		return point;
	}

}
