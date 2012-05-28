package fr.arolla.afterwork.whereisit.xml.elements;

import java.util.ArrayList;
import java.util.List;

public class PicasaPhoto {

	private String id;

	private String title;

	private String description;

	private String link;

	private List<String> thumbnailUrlList = new ArrayList<String>();

	private Double latitude;

	private Double longitude;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public void addThumbnailUrl(String thumbnailUrl) {
		thumbnailUrlList.add(thumbnailUrl);
	}

	public void removeThumbnailUrl(String thumbnailUrl) {
		thumbnailUrlList.remove(thumbnailUrl);
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

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof PicasaPhoto)) {
			return false;
		}
		PicasaPhoto other = (PicasaPhoto) o;
		if (id != null) {
			return id.equals(other.id);
		}
		return this == o;
	}

	@Override
	public String toString() {
		return "PicasaPhoto{title=" + title + ",id=" + id + ",desc="
				+ description + "}";
	}

}