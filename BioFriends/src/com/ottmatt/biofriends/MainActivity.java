package com.ottmatt.biofriends;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

public class MainActivity extends RoboFragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	@InjectView(R.id.test)
	TextView textView;
	@InjectView(R.id.bio_pager)
	ViewPager bioPager;

	BioFragmentAdapter mBioFragmentAdapter;
	Cursor mContactsCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView.setText("YES");
		mBioFragmentAdapter = new BioFragmentAdapter(
				getSupportFragmentManager(), mContactsCursor);
		if (bioPager != null) {
			bioPager.setAdapter(mBioFragmentAdapter);
		}
		getSupportLoaderManager().initLoader(0, null, this);

	}

	// Instantiate and return a new loader for a given id
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri;
		String[] projection, selectionArgs;
		String selection, sortOrder;

		uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		projection = new String[] { Photo.PHOTO, Phone.DISPLAY_NAME,
				Phone.NUMBER };
		selection = "((" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
				+ " NOT NULL) AND ("
				+ ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER
				+ "=1) AND ("
				+ ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
				+ " != '' ))";
		selectionArgs = null;
		sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		return new CursorLoader(this, uri, projection, selection,
				selectionArgs, sortOrder);
	}

	// Called when a previously created loader finishes its load
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mBioFragmentAdapter.swapCursor(data);
		mContactsCursor = data;
		bioPager.setCurrentItem(0, false);
	}

	// Called when a previously created loader has been reset,
	// thus making its data unavailable
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mBioFragmentAdapter.swapCursor(null);
	}

	/**
	 * 
	 * @class retrieves fragments to be used inside the View Pager
	 * 
	 */
	class BioFragmentAdapter extends FragmentStatePagerAdapter {
		private Cursor mCursor;

		public BioFragmentAdapter(FragmentManager fm, Cursor contactsCursor) {
			super(fm);
			mCursor = contactsCursor;
		}

		@Override
		public Fragment getItem(int position) {
			position = position % getCount();
			mCursor.moveToPosition(position);
			final int photoIndex = mCursor.getColumnIndex(Photo.PHOTO);
			final int nameIndex = mCursor.getColumnIndex(Phone.DISPLAY_NAME);
			final int numberIndex = mCursor.getColumnIndex(Phone.NUMBER);
			final String photo = mCursor.getString(photoIndex);
			final String name = mCursor.getString(nameIndex);
			final String number = mCursor.getString(numberIndex);
			return BioFragment.newInstance(photo, name, number);
		}

		@Override
		public int getCount() {
			if (mCursor != null)
				return mCursor.getCount();
			else
				return 0;
		}

		public Cursor swapCursor(Cursor c) {
			return mCursor = c;
		}

	}
}
