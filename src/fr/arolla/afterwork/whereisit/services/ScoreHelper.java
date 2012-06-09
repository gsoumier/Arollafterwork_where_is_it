package fr.arolla.afterwork.whereisit.services;

import android.content.res.Resources;

public class ScoreHelper {

	private static int starOffId;
	private static int starHalfId;
	private static int starOnId;

	static {
		Resources systemResources = Resources.getSystem();
		starOffId = systemResources.getIdentifier("rate_star_big_off_holo_dark", "drawable", "android");
		starHalfId = systemResources.getIdentifier("rate_star_big_half_holo_dark", "drawable", "android");
		starOnId = systemResources.getIdentifier("rate_star_big_on_holo_dark", "drawable", "android");
	}

	public static Integer[] getScoreStarIcons(int distance) {
		Integer[] result = new Integer[3];
		int score = getPointsOnSix(distance);
		switch (score) {
		case 0:
			result[0] = starOffId;
			result[1] = starOffId;
			result[2] = starOffId;
			break;
		case 1:
			result[0] = starHalfId;
			result[1] = starOffId;
			result[2] = starOffId;
			break;
		case 2:
			result[0] = starOnId;
			result[1] = starOffId;
			result[2] = starOffId;
			break;
		case 3:
			result[0] = starOnId;
			result[1] = starHalfId;
			result[2] = starOffId;
			break;
		case 4:
			result[0] = starOnId;
			result[1] = starOnId;
			result[2] = starOffId;
			break;
		case 5:
			result[0] = starOnId;
			result[1] = starOnId;
			result[2] = starHalfId;
			break;
		case 6:
			result[0] = starOnId;
			result[1] = starOnId;
			result[2] = starOnId;
			break;
		default:
			break;
		}
		return result;
	}

	public static int getPointsOnSix(int distance) {
		int maxDistanceForPoints = 10000;
		if (distance > maxDistanceForPoints)
			return 0;
		int result = 6 - (6 * distance / maxDistanceForPoints);
		return result;
	}

}
