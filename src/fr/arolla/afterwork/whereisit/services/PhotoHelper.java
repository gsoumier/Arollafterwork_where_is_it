package fr.arolla.afterwork.whereisit.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaAlbum;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaPhoto;
import fr.arolla.afterwork.whereisit.xml.parser.PicasaAlbumXmlHandler;

public class PhotoHelper {

	private static final String TAG = "PhotoHelper";

	public static Bitmap downloadBitmap(String url) {
		Bitmap mBitmap = null;
		try {
			URLConnection urlC = new URL(url).openConnection();
			InputStream is = urlC.getInputStream();
			mBitmap = BitmapFactory.decodeStream(is);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "Error occured while creating drawable : " + url, e);
		}
		return mBitmap;
	}

	public static PicasaAlbum downloadAlbum(String url) {
		PicasaAlbum picasaAlbum = null;
		try {
			URL feedUrl = new URL(url);
			URLConnection urlC = feedUrl.openConnection();
			InputStream is = urlC.getInputStream();
			PicasaAlbumXmlHandler xmlHandler = new PicasaAlbumXmlHandler();
			picasaAlbum = xmlHandler.parse(is);
		} catch (Exception e) {
			Log.e(TAG, "Error occured while retriving picasa album from URL : "
					+ url, e);
		}

		addGeoInformation(picasaAlbum);
		return picasaAlbum;
	}

	/*
	 * FIXME : problème de parsing des infos géolocalisées
	 */
	static void addGeoInformation(PicasaAlbum picasaAlbum) {
		for (PicasaPhoto picasaPhoto : picasaAlbum) {
			String photoId = picasaPhoto.getId();
			if (photoId
					.equals("https://picasaweb.google.com/data/entry/api/user/114060422820973437445/albumid/5734132424019816081/photoid/5748107040104845138")) {
				picasaPhoto.setLatitude(48.865916);
				picasaPhoto.setLongitude(2.225677);
			} else if (photoId
					.equals("https://picasaweb.google.com/data/entry/api/user/114060422820973437445/albumid/5734132424019816081/photoid/5748477498962023090")) {
				picasaPhoto.setLatitude(48.612729);
				picasaPhoto.setLongitude(-1.505180);
			} else if (photoId
					.equals("https://picasaweb.google.com/data/entry/api/user/114060422820973437445/albumid/5734132424019816081/photoid/5748478389030221938")) {
				picasaPhoto.setLatitude(45.767500);
				picasaPhoto.setLongitude(4.828889);
			} else if (photoId
					.equals("https://picasaweb.google.com/data/entry/api/user/114060422820973437445/albumid/5734132424019816081/photoid/5748478868755550402")) {
				picasaPhoto.setLatitude(45.328611);
				picasaPhoto.setLongitude(3.708611);
			} else if (photoId
					.equals("https://picasaweb.google.com/data/entry/api/user/114060422820973437445/albumid/5734132424019816081/photoid/5748479441511849042")) {
				picasaPhoto.setLatitude(-22.947447);
				picasaPhoto.setLongitude(-43.1564);
			}
		}
	}

}
