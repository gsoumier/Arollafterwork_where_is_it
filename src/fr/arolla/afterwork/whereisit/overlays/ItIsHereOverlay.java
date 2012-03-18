package fr.arolla.afterwork.whereisit.overlays;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class ItIsHereOverlay extends ItemizedOverlay<OverlayItem> {

	private GeoPoint point;
	private final String title;
	private final String snippet;

	public ItIsHereOverlay(Drawable marker, GeoPoint point, String title,
			String snippet) {
		super(boundCenter(marker));
		this.point = point;
		this.title = title;
		this.snippet = snippet;
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return new OverlayItem(point, title, snippet);
	}

	@Override
	public int size() {
		return point == null ? 0 : 1;
	}

	protected void changePoint(GeoPoint point) {
		this.point = point;
		populate();
	}

}
