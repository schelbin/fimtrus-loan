package com.fimtrus.loan.fragment;

import java.util.Locale;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.fimtrus.loan.R;
import com.fimtrus.loan.Util;
import com.fimtrus.loan.model.Constant;

/**
 * SearchFragment.java
 * 
 * 검색 화면
 * 
 * @auther jong-hyun.jeong
 * @date 2014. 7. 15.
 */
public class SearchFragment extends Fragment implements View.OnClickListener,
		OnEditorActionListener {

	private View mRootLayout;
	private FragmentManager mFragmentManager;
	private Spinner mRepaymentSpinner;
	private EditText mLoansEditText;
	private EditText mInterestRateEditText;
	private EditText mTermEditText;
	private Button mCalculationButton;
	private TextView mHintTextView;
	private TextView mLoanTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRootLayout = inflater.inflate(R.layout.fragment_loan_search,
				container, false);

		initialize();
		return mRootLayout;
	}

	private void initialize() {

		initializeFields();
		initializeListeners();
		initializeView();
	}

	private void initializeFields() {

		mFragmentManager = getFragmentManager();
		mRepaymentSpinner = (Spinner) mRootLayout
				.findViewById(R.id.spinner_repayment);
		mLoansEditText = (EditText) mRootLayout
				.findViewById(R.id.edittext_loans);
		mInterestRateEditText = (EditText) mRootLayout
				.findViewById(R.id.edittext_interest_rate);
		mTermEditText = (EditText) mRootLayout.findViewById(R.id.edittext_term);
		mCalculationButton = (Button) mRootLayout
				.findViewById(R.id.calculation);
		mHintTextView = (TextView) mRootLayout.findViewById(R.id.textview_hint);
		
		mLoanTextView = (TextView) mRootLayout
				.findViewById(R.id.textview_loans_text);
	}

	private void initializeView() {
		mHintTextView.setText(Html.fromHtml(getResources().getString(
				R.string.hint_text)));
		
		Locale locale = Locale.getDefault();
		
		if ( !locale.getLanguage().contains("ko") ) {
			mLoanTextView.setVisibility(View.GONE);
		}
	}

	private void initializeListeners() {
		mCalculationButton.setOnClickListener(this);

		
		Locale locale = Locale.getDefault();
		
		if ( locale.getLanguage().contains("ko") ) {
			
			mLoansEditText.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before,
						int count) {
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					mLoanTextView.setText(Util.convertHangul(s.toString()) + "원");
				}
			});
		}
		

		mTermEditText.setOnEditorActionListener(this);
	}

	private void startResultFragment() {
		int selectRepayment = mRepaymentSpinner.getSelectedItemPosition();
		String loansText = mLoansEditText.getText().toString();
		String interestRateText = mInterestRateEditText.getText().toString();
		String termText = mTermEditText.getText().toString();

		if ( loansText == null || interestRateText == null || termText == null ||
				loansText.equals("") || interestRateText.equals("") || termText.equals("") ) {
			Toast.makeText(getActivity(), R.string.input_fields, Toast.LENGTH_SHORT).show();
			return;
		}
		
		
		Intent intent = getActivity().getIntent();

		intent.putExtra(Constant.EXTRA_SELECTED_INDEX, selectRepayment);
		intent.putExtra(Constant.EXTRA_LOANS, loansText);
		intent.putExtra(Constant.EXTRA_INTEREST_RATE, interestRateText);
		intent.putExtra(Constant.EXTRA_TERM, termText);

		mFragmentManager.beginTransaction().addToBackStack("result")
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.replace(R.id.content_frame, new ResultFragment(), "result")
				.commit();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.calculation:
			
			startResultFragment();
			break;
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v.getId() == R.id.edittext_term
				&& actionId == EditorInfo.IME_ACTION_DONE) { // 뷰의 id를 식별, 키보드의
																// 완료 키 입력 검출
			mCalculationButton.performClick();
		}
		return false;
	}
}
