package fr.arolla.afterwork.whereisit.xml.parser;

import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Log;
import android.util.Xml;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaAlbum;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaPhoto;

public class PicasaAlbumXmlHandler extends DefaultHandler {

	private static final String TAG = "PicasaAlbumXmlHandler";

	private PicasaAlbum album = new PicasaAlbum();
	private PicasaPhoto photo;

	private static final String ATOM_NS = "http://www.w3.org/2005/Atom";
	private static final String MEDIA_NS = "http://search.yahoo.com/mrss/";
	private static final String GEO_RSS_NS = "http://www.georss.org/georss";
	private static final String GML_NS = "http://www.opengis.net/gml";

	public PicasaAlbum parse(InputStream is) {
		RootElement root = new RootElement(ATOM_NS, "feed");
		Element albumIdElement = root.getChild(ATOM_NS, "id");
		Element albumTitleElement = root.getChild(ATOM_NS, "title");

		Element photoElement = root.getChild(ATOM_NS, "entry");
		Element photoId = photoElement.getChild(ATOM_NS, "id");

		Element mediaGroupElement = photoElement.getChild(MEDIA_NS, "group");
		Element photoTitle = mediaGroupElement.getChild(MEDIA_NS, "title");
		Element photoDescription = mediaGroupElement.getChild(MEDIA_NS,
				"description");
		Element photoContentElement = mediaGroupElement.getChild(MEDIA_NS,
				"content");
		Element photoThumbnailElement = mediaGroupElement.getChild(MEDIA_NS,
				"thumbnail");

		Element geoRssElement = photoElement.getChild(GEO_RSS_NS, "where");
		Element gmlPointElement = geoRssElement.getChild(GML_NS, "Point");
		Element gmlPosElement = gmlPointElement.getChild(GML_NS, "pos");

		albumIdElement.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				album.setId(body);
			}
		});

		albumTitleElement
				.setEndTextElementListener(new EndTextElementListener() {
					public void end(String body) {
						album.setTitle(body);
					}
				});

		photoElement.setStartElementListener(new StartElementListener() {
			public void start(Attributes attributes) {
				photo = new PicasaPhoto();
			}
		});

		photoElement.setEndElementListener(new EndElementListener() {
			public void end() {
				album.addPhoto(photo);
			}
		});

		photoId.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				photo.setId(body);
			}
		});

		photoTitle.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				photo.setTitle(body);
			}
		});

		photoDescription
				.setEndTextElementListener(new EndTextElementListener() {
					public void end(String body) {
						photo.setDescription(body);
					}
				});

		photoContentElement.setStartElementListener(new StartElementListener() {
			public void start(Attributes attributes) {
				String contentUrl = attributes.getValue("url");
				photo.setLink(contentUrl);
			}
		});

		photoThumbnailElement
				.setStartElementListener(new StartElementListener() {
					public void start(Attributes attributes) {
						String thumbnailUrl = attributes.getValue("url");
						photo.addThumbnailUrl(thumbnailUrl);
					}
				});

		gmlPosElement.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				String[] split = body.split(" ");
				Double lat = Double.parseDouble(split[0]);
				photo.setLatitude(lat);
				Double lng = Double.parseDouble(split[1]);
				photo.setLongitude(lng);
			}
		});

		try {
			Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
			return album;
		} catch (Exception e) {
			Log.e(TAG, "An error occured while parsing xml for album feed", e);
		}
		return null;
	}

}
