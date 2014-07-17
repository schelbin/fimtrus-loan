package com.fimtrus.loan;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.Toast;

import com.fimtrus.loan.fragment.ResultFragment;
import com.fimtrus.loan.fragment.SearchFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {

	private FragmentManager mFragmentManager;
	private Handler mBackHandler;
	protected boolean flag;
	private SearchFragment mSearchFragment;
	private ResultFragment mResultFragment;
	private AdView mAdMobView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initialize();
	}

	private void initialize() {

		initializeFragments();
		initializeFields();
		initializeListeners();
		initializeView();
	}

	private void initializeFields() {
		
		mAdMobView = (AdView) findViewById(R.id.adView);
		
		mBackHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					flag = false;
				}
			}
		};
	}

	private void initializeView() {
		// mSplashHandler.sendEmptyMessageDelayed(0, 2000);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdMobView.loadAd(adRequest);
	}

	private void initializeFragments() {
		// Utils.setPreference(Key.INTIALIZED_HELP, false);

		mFragmentManager = getFragmentManager();

		mSearchFragment = new SearchFragment();
		mResultFragment = new ResultFragment();
//		mSplashFragment = new SplashFragment();
//
//		mFragmentManager.beginTransaction()
//				.add(R.id.content_frame, mResultFragment, "result")
//				.commit();
		mFragmentManager.beginTransaction()
		.add(R.id.content_frame, mSearchFragment, "search")
		.commit();
		// mFragmentManager.beginTransaction().add(R.id.fragment_splash,
		// mSplashFragment, "splash").commit();
	}

	private void initializeListeners() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		
		if ( mFragmentManager.getBackStackEntryCount() > 0 ) {
			
			super.onBackPressed();
		} else {
			if (!flag) {
				Toast.makeText(this, R.string.exit_text, Toast.LENGTH_SHORT).show();
				flag = true;
				mBackHandler.sendEmptyMessageDelayed(0, 3000);
			} else {
				super.onBackPressed();
			}
		}
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (mAdMobView != null) {
			mAdMobView.pause();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (mAdMobView != null) {
			mAdMobView.resume();
		}
		super.onResume();
	}

}
