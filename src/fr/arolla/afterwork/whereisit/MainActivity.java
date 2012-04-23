package fr.arolla.afterwork.whereisit;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.PhotoEntry;

import fr.arolla.afterwork.whereisit.db.WhereIsItDBAdapter;

public class MainActivity extends Activity {

	WhereIsItDBAdapter dbAdapter;

	String albumUrl = "https://picasaweb.google.com/data/feed/api/user/germain.soumier/albumid/5734132424019816081";

	
	private Iterator<PhotoEntry> photoIterator;

	private ImageView imageView;

	private PhotoOnClickListener photoOnClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		PicasawebService myService = new PicasawebService("Picasa-WhereIsIt");
		// try {
		// myService.setUserCredentials("liz@gmail.com", "mypassword");
		// } catch (AuthenticationException e) {
		// e.printStackTrace();
		// }
		try {
			URL feedUrl = new URL(albumUrl);
			AlbumFeed feed = myService.getFeed(feedUrl, AlbumFeed.class);
			List<PhotoEntry> photoEntries = feed.getPhotoEntries();
			photoIterator = photoEntries.iterator();
		} catch (Exception e) {

		}

		imageView = (ImageView) findViewById(R.id.image);
		photoOnClickListener = new PhotoOnClickListener();
		imageView.setOnClickListener(photoOnClickListener);
		
		if(photoIterator.hasNext()){
			PhotoEntry photoEntry = photoIterator.next();
			updateWithNextPhoto(photoEntry);
		}
		
		
	}

	private void updateWithNextPhoto(PhotoEntry photoEntry) {
		photoOnClickListener.setEntry(photoEntry);
		String photoUrl = photoEntry.getMediaThumbnails().get(0).getUrl();
		Drawable photoDrawable= null;
		try {
			URL urlO = new URL(photoUrl);
			URLConnection urlC = urlO.openConnection(); 
			InputStream is = urlC.getInputStream(); 
			photoDrawable = Drawable.createFromStream(is, photoUrl);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		imageView.setImageDrawable(photoDrawable);
	}
	
	class PhotoOnClickListener implements OnClickListener{
		
		PhotoEntry entry;
		
		public void setEntry(PhotoEntry entry) {
			this.entry = entry;
		}

		public void onClick(View v) {
			Intent intent = new Intent("WhereIsIt");
			if(entry==null)
				return;
			intent.putExtra("lat", entry.getGeoLocation().getLatitude());
			startActivity(intent);
		}
		
	}
}
