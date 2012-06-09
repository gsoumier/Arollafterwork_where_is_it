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

		return picasaAlbum;
	}

}
