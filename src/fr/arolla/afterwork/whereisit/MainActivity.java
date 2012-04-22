package fr.arolla.afterwork.whereisit;

import java.net.URL;

import android.app.Activity;
import android.content.Intent;
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

	String url = "https://picasaweb.google.com/114060422820973437445/TestWhereIsIt";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		dbAdapter = new WhereIsItDBAdapter(this);
		dbAdapter.open();

		PicasawebService myService = new PicasawebService(
				"Picasa access for WhereIsIt");
		// try {
		// myService.setUserCredentials("liz@gmail.com", "mypassword");
		// } catch (AuthenticationException e) {
		// e.printStackTrace();
		// }
		String photoUrl = null;
		try {
			URL feedUrl = new URL(
					"https://picasaweb.google.com/data/feed/api/user/germain.soumier/albumid/5734132424019816081");
			AlbumFeed feed = myService.getFeed(feedUrl, AlbumFeed.class);

			for (PhotoEntry photo : feed.getPhotoEntries()) {
				photoUrl = photo.getMediaContents().get(0).getUrl();
			}
		} catch (Exception e) {

		}

		ImageView image = (ImageView) findViewById(R.id.image);
		image.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent("WhereIsIt");
				startActivity(intent);
			}
		});
	}
}
