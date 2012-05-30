package fr.arolla.afterwork.whereisit.xml.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaAlbum;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaPhoto;

public class AlbumFeedParser {

	private static final String TAG = "AlbumFeedParser";

	private PicasaAlbum album = new PicasaAlbum();
	private PicasaPhoto photo;

	private static final String ATOM_NS = "http://www.w3.org/2005/Atom";
	private static final String MEDIA_NS = "http://search.yahoo.com/mrss/";
	private static final String GEO_RSS_NS = "http://www.georss.org/georss";
	private static final String GML_NS = "http://www.opengis.net/gml";

	public PicasaAlbum parse(String albumFeed) {
		// Get the XML
		URL url;
		try {
			url = new URL(albumFeed);

			URLConnection connection;
			connection = url.openConnection();

			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = httpConnection.getInputStream();

				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				Document dom = db.parse(in);
				Element docEle = dom.getDocumentElement();

				Element albumIdElement = (Element) docEle.getElementsByTagName(
						"title").item(0);
				album.setId(getElementValue(albumIdElement));

				Element albumTitleElement = (Element) docEle
						.getElementsByTagName("title").item(0);
				album.setTitle(getElementValue(albumTitleElement));

				// Get a list of each photo entry.
				NodeList nl = docEle.getElementsByTagName("entry");
				if (nl != null && nl.getLength() > 0) {
					for (int i = 0; i < nl.getLength(); i++) {
						Element photoElement = (Element) nl.item(i);

						photo = new PicasaPhoto();

						Element photoId = (Element) photoElement
								.getElementsByTagName("id").item(0);
						photo.setId(getElementValue(photoId));

						Element photoTitle = (Element) photoElement
								.getElementsByTagName("media:title").item(0);
						photo.setTitle(getElementValue(photoTitle));

						Element photoDescription = (Element) photoElement
								.getElementsByTagName("media:description")
								.item(0);
						photo.setDescription(getElementValue(photoDescription));

						Element photoContentElement = (Element) photoElement
								.getElementsByTagName("media:content").item(0);
						photo.setLink(getAttributeValue(photoContentElement,
								"url"));

						NodeList photoThumbnailElements = photoElement
								.getElementsByTagName("media:thumbnail");
						for (int j = 0; j < photoThumbnailElements.getLength(); j++) {
							Element thumbnailElement = (Element) photoThumbnailElements
									.item(j);
							photo.addThumbnailUrl(getAttributeValue(
									thumbnailElement, "url"));
						}

						// Element whereElement = (Element) photoElement
						// .getElementsByTagNameNS(GEO_RSS_NS, "where")
						// .item(0);
						// Element pointElement = (Element) whereElement
						// .getElementsByTagNameNS(GML_NS, "Point")
						// .item(0);
						Element posElement = (Element) photoElement
								.getElementsByTagName("gml:pos").item(0);
						if (posElement == null) {
							Log.i(TAG, "Photo '" + photo.getTitle()
									+ "' has no localization information");
							continue;
						}

						String point = getElementValue(posElement);
						String[] location = point.split(" ");
						photo.setLatitude(Double.parseDouble(location[0]));
						photo.setLongitude(Double.parseDouble(location[1]));

						album.addPhoto(photo);

					}
				}
			}
		} catch (MalformedURLException e) {
			Log.d(TAG, "MalformedURLException", e);
		} catch (IOException e) {
			Log.d(TAG, "IOException", e);
		} catch (ParserConfigurationException e) {
			Log.d(TAG, "Parser Configuration Exception", e);
		} catch (SAXException e) {
			Log.d(TAG, "SAX Exception", e);
		}

		return album;

	}

	private String getElementValue(Element element) {
		String result = element.getFirstChild().getNodeValue();
		return result;
	}

	private String getAttributeValue(Element element, String attributeName) {
		String result = element.getAttribute(attributeName);
		return result;
	}

}
