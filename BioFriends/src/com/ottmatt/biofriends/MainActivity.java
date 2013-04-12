package com.ottmatt.biofriends;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class MainActivity extends RoboFragmentActivity implements
		OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

	@InjectView(R.id.bio_pager)
	ViewPager bioPager;

	BioFragmentAdapter mBioFragmentAdapter;
	Cursor mContactsCursor;
	String mCurFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mBioFragmentAdapter = new BioFragmentAdapter(
				getSupportFragmentManager(), mContactsCursor);
		if (bioPager != null) {
			bioPager.setAdapter(mBioFragmentAdapter);
		}

		getSupportLoaderManager().initLoader(0, null, this);

	}

	// Create the actionbar menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_bar, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener(this);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// Called when the actionbar search text has changed.
		// Update the search filter, and restart the loader.
		mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
		getSupportLoaderManager().restartLoader(0, null, this);
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return true;
	}

	// Instantiate and return a new loader for a given id
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri;
		String[] projection, selectionArgs;
		String selection, sortOrder;
		if (mCurFilter != null)
			uri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI,
					Uri.encode(mCurFilter));
		else
			uri = Contacts.CONTENT_URI;
		projection = new String[] { Contacts.PHOTO_ID, Contacts.DISPLAY_NAME,
				Contacts._ID };
		selection = "((" + Contacts.DISPLAY_NAME + " NOT NULL) AND ("
				+ Contacts.HAS_PHONE_NUMBER + "=1) AND (" + Contacts.PHOTO_ID
				+ " NOT NULL) AND (" + Contacts.DISPLAY_NAME + " != '' ))";
		selectionArgs = null;
		sortOrder = Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

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

		// called when the cursor is changed
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Fragment getItem(int position) {
			position = position % getCount();
			mCursor.moveToPosition(position);
			final int photoIndex = mCursor.getColumnIndex(Contacts.PHOTO_ID);
			final int nameIndex = mCursor.getColumnIndex(Contacts.DISPLAY_NAME);
			final int contactIndex = mCursor.getColumnIndex(Contacts._ID);
			final String name = mCursor.getString(nameIndex);
			Log.d("getItem", "Name: " + name);
			final String phoneNumber = getPhoneNumber(mCursor
					.getLong(contactIndex));
			final byte[] photoBlob = getPhotoBlob(mCursor.getLong(photoIndex));
			return BioFragment.newInstance(photoBlob, name, phoneNumber);
		}

		public byte[] getPhotoBlob(long photoId) {
			final Uri uri = ContentUris.withAppendedId(Data.CONTENT_URI,
					photoId);
			final Cursor c = getContentResolver().query(uri,
					new String[] { Photo.PHOTO }, null, null, null);
			try {
				byte[] photoBlob = null;
				if (c.moveToFirst()) {
					photoBlob = c.getBlob(0);
				}
				return photoBlob;
			} finally {
				c.close();
			}
		}

		public String getPhoneNumber(long contactId) {
			Cursor c = getContentResolver().query(
					Data.CONTENT_URI,
					new String[] { Data._ID, Phone.NORMALIZED_NUMBER,
							Phone.TYPE, Phone.LABEL },
					Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
							+ Phone.CONTENT_ITEM_TYPE + "'",
					new String[] { String.valueOf(contactId) }, null);
			try {
				String phoneNumber = null;
				if (c.moveToFirst()) {
					phoneNumber = c.getString(1);
				}
				return phoneNumber;
			} finally {
				c.close();
			}
		}

		/**
		 * public byte[] getPhotoStream() { Uri displayPhotoUri =
		 * ContentUris.withAppendedId( DisplayPhoto.CONTENT_URI, photoId); try {
		 * AssetFileDescriptor fd = getContentResolver()
		 * .openAssetFileDescriptor(displayPhotoUri, "r"); InputStream inStream
		 * = fd.createInputStream(); return IOUtils.toByteArray(inStream); }
		 * catch (IOException e) { return null; } }
		 **/

		@Override
		public int getCount() {
			return mCursor != null ? mCursor.getCount() : 0;
		}

		public Cursor swapCursor(Cursor c) {
			notifyDataSetChanged();
			return mCursor = c;
		}

	}

}
