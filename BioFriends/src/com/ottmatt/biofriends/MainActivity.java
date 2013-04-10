package com.ottmatt.biofriends;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
	Cursor mCursor;

	private static final String STATE_CURRENT_VIEW = "state-current-view";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView.setText("YES");
		mBioFragmentAdapter = new BioFragmentAdapter(
				getSupportFragmentManager(), mCursor);
		if (bioPager != null) {
			bioPager.setAdapter(mBioFragmentAdapter);
		}
		getSupportLoaderManager().initLoader(0, null, this);

	}

	// Instantiate and return a new loader for a given id
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.Contacts.DISPLAY_NAME};
		String selection = "((" + ContactsContract.Contacts.DISPLAY_NAME
				+ " NOT NULL) AND ("
				+ ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
				+ ContactsContract.Contacts.DISPLAY_NAME + " != '' ))";
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";
		return new CursorLoader(this, uri, projection, selection, null,
				sortOrder);
	}

	// Called when a previously created loader finishes its load
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mBioFragmentAdapter.swapCursor(data);
		mCursor = data;
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
	public static class BioFragmentAdapter extends FragmentStatePagerAdapter {
		private Cursor mCursor;

		public BioFragmentAdapter(FragmentManager fm, Cursor c) {
			super(fm);
			mCursor = c;
		}

		@Override
		public Fragment getItem(int position) {
			position = position % getCount();
			mCursor.moveToPosition(position);
			final int nameIndex = mCursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			// final int numberIndex = mCursor.getColumnIndex(ContactsContract.PhoneLookup.NUMBER);
			final String name = mCursor.getString(nameIndex);
			final String number = "408-306-4285";//mCursor.getString(numberIndex);
			return BioFragment.newInstance(name, number);
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
