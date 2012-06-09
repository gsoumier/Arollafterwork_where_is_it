package fr.arolla.afterwork.whereisit.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import fr.arolla.afterwork.whereisit.R;
import fr.arolla.afterwork.whereisit.xml.elements.PicasaPhoto;

public class PhotoResultArrayAdapter extends ArrayAdapter<PicasaPhoto> {

	public PhotoResultArrayAdapter(Context context, int textViewResourceId, List<PicasaPhoto> photos) {
		super(context, textViewResourceId, photos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView != null)
			return convertView;
		LinearLayout newView = new LinearLayout(getContext());
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.photo_result, newView, true);
		return newView;
	}

}
