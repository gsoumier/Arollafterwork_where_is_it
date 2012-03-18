package fr.arolla.afterwork.whereisit;

public class WereIsItPhoto {

	private String label;
	private String description;
	private String tips;
	private double lat;
	private double lng;

	public WereIsItPhoto(String label, String description, String tips,
			double lat, double lng) {
		super();
		this.label = label;
		this.description = description;
		this.tips = tips;
		this.lat = lat;
		this.lng = lng;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

}
