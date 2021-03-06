package fr.arolla.afterwork.whereisit.xml.elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PicasaAlbum implements Iterable<PicasaPhoto> {

	private String id;

	public String title;

	private List<PicasaPhoto> photos = new ArrayList<PicasaPhoto>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<PicasaPhoto> getPhotos() {
		return photos;
	}

	public void setPhotos(List<PicasaPhoto> photos) {
		this.photos = photos;
	}

	public void addPhoto(PicasaPhoto photo) {
		photos.add(photo);
	}

	public void removePhoto(PicasaPhoto photo) {
		photos.remove(photo);
	}

	public PicasaPhoto getPhoto(int index) {
		return photos.get(index);
	}

	public int albumSize() {
		return photos.size();
	}

	public Iterator<PicasaPhoto> iterator() {
		return photos.iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof PicasaAlbum)) {
			return false;
		}
		PicasaAlbum other = (PicasaAlbum) o;
		if (id != null) {
			return id.equals(other.id);
		}
		return this == o;
	}

	@Override
	public String toString() {
		return "PicasaAlbum{title=" + title + ",id=" + id + "}";
	}

}
