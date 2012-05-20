package fr.arolla.afterwork.whereisit.xml.elements;

import java.util.ArrayList;
import java.util.List;

public class PicasaPhoto {

	private String name;

	private String link;

	private List<String> thumbnailUrlList = new ArrayList<String>();

	private Double latitude;

	private Double longitude;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public List<String> getThumbnailUrlList() {
		return thumbnailUrlList;
	}

	public void setThumbnailUrlList(List<String> thumbnailUrlList) {
		this.thumbnailUrlList = thumbnailUrlList;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

}