package fr.arolla.afterwork.whereisit.xml.parser;

import org.w3c.dom.Document;

public class GeoInformationParser {

	public void parseGeoInfo(String url) {
		Document xml = new javaxt.http.Request(url).getResponse().getXML();
	}

}
