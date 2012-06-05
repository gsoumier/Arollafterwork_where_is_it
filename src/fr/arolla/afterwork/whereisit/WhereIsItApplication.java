package fr.arolla.afterwork.whereisit;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaAlbum;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaPhoto;
import fr.arolla.afterwork.whereisit.xml.parser.PicasaAlbumXmlHandler;

public class WhereIsItApplication extends Application {

	private static final String TAG = "WhereIsItApplication";

	private String albumUrl = "https://picasaweb.google.com/data/feed/api/user/germain.soumier/albumid/5734132424019816081";

	private Iterator<PicasaPhoto> photoIterator;

	@Override
	public void onCreate() {
		super.onCreate();
		AlbumDownloadTask albumDownloadTask = new AlbumDownloadTask();
		albumDownloadTask.execute(albumUrl);
	}

	class AlbumDownloadTask extends AsyncTask<String, Integer, PicasaAlbum> {

		@Override
		protected PicasaAlbum doInBackground(String... params) {
			PicasaAlbum picasaAlbum = null;
			try {
				URL feedUrl = new URL(params[0]);
				URLConnection urlC = feedUrl.openConnection();
				InputStream is = urlC.getInputStream();
				PicasaAlbumXmlHandler xmlHandler = new PicasaAlbumXmlHandler();
				picasaAlbum = xmlHandler.parse(is);
			} catch (Exception e) {
				Log.e(TAG,
						"Error occured while retriving picasa album from URL : "
								+ albumUrl, e);
			}
			return picasaAlbum;
		}

		@Override
		protected void onPostExecute(PicasaAlbum result) {
			initAlbumIterator(result);
		}
	}

	public PicasaPhoto getNextPhoto() {
		if (photoIterator.hasNext()) {
			return photoIterator.next();
		}

		return null;
	}

	public void initAlbumIterator(PicasaAlbum album) {
		photoIterator = album.iterator();
	}

}
