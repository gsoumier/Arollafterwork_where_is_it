package fr.arolla.afterwork.whereisit.services;

import android.app.ActionBar;
import fr.arolla.afterwork.whereisit.WhereIsItApplication;

public class ActionBarHelper {

	public static void fillActionBarProperties(ActionBar actionBar, int photoIndex, String photoDescription) {
		actionBar.setTitle(photoIndex + 1 + "/" + WhereIsItApplication.getInstance().album.albumSize() + " "
				+ WhereIsItApplication.getInstance().album.title);
		actionBar.setSubtitle(photoDescription);
	}
}
