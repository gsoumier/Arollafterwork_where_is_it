package fr.arolla.afterwork.whereisit.overlays;

import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class UserResultOverlay extends ItIsHereOverlay {

	private boolean isEnabled = true;
	private Handler handler;

	public UserResultOverlay(Drawable marker) {
		super(marker, null);
	}

	/*
	 * Iter 3. etape 2)
	 */
	public UserResultOverlay(Drawable marker, Handler handler) {
		this(marker);
		this.handler = handler;
	}

	/*
	 * Iter 2. etape 3)
	 */
	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
		// START Iter 4. etape 2)
		if (!isEnabled) {
			return false;
		}
		// END Iter 4. etape 2)

		this.point = point;
		populate();
		mapView.refreshDrawableState();

		// START Iter. 3 etape 2)
		handler.sendEmptyMessage(0);
		// END Iter. 3 etape 2)
		return true;
	}

	// START Iter. 4 etape 2)
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	// END Iter. 4 etape 2)

}
