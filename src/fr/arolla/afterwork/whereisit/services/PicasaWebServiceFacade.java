package fr.arolla.afterwork.whereisit.services;

import com.google.gdata.client.photos.PicasawebService;

public class PicasaWebServiceFacade {

	private static PicasawebService myService;

	public static PicasawebService getService() {
		if (myService == null)
			myService = new PicasawebService("Arolla-WhereIsIt-1");
		return myService;
	}

}
