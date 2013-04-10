package com.ottmatt.biofriends;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class MainActivity extends RoboActivity {
	@InjectView(R.id.search_bar) EditText searchBar;
	@InjectView(R.id.bio_pager) ViewPager bioPager;
	
	private static final String STATE_CURRENT_VIEW = "state-current-view";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (bioPager != null) {
			bioPager.setAdapter(new BioAdapter(bioPager));
			bioPager.setCurrentItem(savedInstanceState == null ? 0: savedInstanceState.getInt(STATE_CURRENT_VIEW, 0));
		}
		
	}
	
	class BioAdapter extends PagerAdapter {
		private View mBioPager;
		
		public BioAdapter(ViewPager parent) {
			final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			final View bioPager = inflater.inflate(R.id.bio_pager, parent, false);
			mBioPager = bioPager;
			
			// this is where you retrieve the information from the contact
			
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			final View page = mBioPager;
			container.addView(page);
			return page;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
	

}
