package com.ottmatt.biofriends;

import roboguice.fragment.RoboFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class BioFragment extends RoboFragment {

	public static BioFragment newInstance(byte[] photo, String name,
			String details) {
		BioFragment f = new BioFragment();

		Bundle args = new Bundle();
		args.putByteArray("photo", photo);
		args.putString("name", name);
		args.putString("details", details);
		f.setArguments(args);

		return f;
	}

	public String getShownName() {
		return getArguments().getString("name");
	}

	public String getShownDetails() {
		return getArguments().getString("details");
	}

	public Bitmap getShownPhoto() {
		byte[] photo = getArguments().getByteArray("photo");

		Bitmap decodedPhoto = null;
		if (photo != null) {
			decodedPhoto = BitmapFactory
					.decodeByteArray(photo, 0, photo.length);
			decodedPhoto = Bitmap.createScaledBitmap(decodedPhoto, 400, 400, false);
		}
		else
			decodedPhoto = null;
		return decodedPhoto;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	// Provides layout for the fragment
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// inflate the layout for this fragment
		final ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.bio_pager_fragment, container, false);
		ImageView imageView = ((ImageView) rootView.findViewById(R.id.photo));
		imageView.setImageBitmap(getShownPhoto());

		((TextView) rootView.findViewById(R.id.name)).setText(getShownName());
		((TextView) rootView.findViewById(R.id.details))
				.setText(getShownDetails());
		return rootView;
	}

}
