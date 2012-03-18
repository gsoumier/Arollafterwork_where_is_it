package fr.arolla.afterwork.whereisit.overlays;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class ItIsHereListOverlay extends ItemizedOverlay<OverlayItem> {

	ArrayList<OverlayItem> items;

	public ItIsHereListOverlay(Drawable marker) {
		super(boundCenterBottom(marker));
		items = new ArrayList<OverlayItem>();
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return items.get(i);
	}

	@Override
	public int size() {
		return items.size();
	}

	public void addItem(GeoPoint here, String marker, String markerText) {
		items.add(new OverlayItem(here, marker, markerText));
		populate();
	}

	public void removeItem(int i) {
		items.remove(i);
		populate();
	}

}
