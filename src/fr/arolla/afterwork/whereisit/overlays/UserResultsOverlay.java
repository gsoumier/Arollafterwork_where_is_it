package fr.arolla.afterwork.whereisit.overlays;

import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class UserResultsOverlay extends ItIsHereOverlay {

	private GeoPoint point;
	private boolean isEnabled = true;
	private final Handler handler;

	public UserResultsOverlay(Drawable marker, Handler handler) {
		super(marker, null, "Here ?", "Are you sure ?");
		this.handler = handler;

	}

	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
		if (!isEnabled) {
			return false;
		}

		this.point = point;
		changePoint(point);
		mapView.refreshDrawableState();
		handler.sendEmptyMessage(23);
		return true;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public GeoPoint getPoint() {
		return point;
	}

}
