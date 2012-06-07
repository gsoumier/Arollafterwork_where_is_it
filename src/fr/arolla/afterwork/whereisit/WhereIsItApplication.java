package fr.arolla.afterwork.whereisit;

import android.app.Application;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaAlbum;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaPhoto;

public class WhereIsItApplication extends Application {

	private static WhereIsItApplication singleton;

	public static WhereIsItApplication getInstance() {
		return singleton;
	}

	private PicasaAlbum album;

	@Override
	public final void onCreate() {
		super.onCreate();
		singleton = this;
	}

	public void setAlbum(PicasaAlbum album) {
		this.album = album;
	}

	public PicasaPhoto getPhoto(int index) {
		return album.getPhoto(index);
	}

}
