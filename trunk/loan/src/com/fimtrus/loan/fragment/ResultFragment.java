package com.fimtrus.loan.fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fimtrus.loan.R;
import com.fimtrus.loan.Util;
import com.fimtrus.loan.model.Constant;
import com.fimtrus.loan.model.RepaymentResultModel;

/**
 * SearchFragment.java
 * 
 * 검색 화면
 * 
 * @auther jong-hyun.jeong
 * @date 2014. 7. 15.
 */
public class ResultFragment extends Fragment {

	private View mRootLayout;
	private FragmentManager mFragmentManager;
	private LayoutInflater mInflater;
	private LinearLayout mContentLayout;
	private Button mRetryButton;
	private EditText mLoansEditText;
	private EditText mInterestRateEditText;
	private EditText mTermEditText;
	private EditText mTotalInterestEditText;
	private ArrayList<RepaymentResultModel> mRepaymentResultList;
	private Integer mLoans;
	private Float mInterestRate;
	private Integer mTerm;
	private EditText mTotalAmountEditText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mRootLayout = inflater.inflate(R.layout.fragment_loan_result, container, false);
		
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
		mContentLayout = (LinearLayout) mRootLayout.findViewById(R.id.layout_content);
		mRetryButton = (Button) mRootLayout.findViewById(R.id.retry);
		mLoansEditText = (EditText) mRootLayout.findViewById(R.id.edittext_loans);
		mInterestRateEditText = (EditText) mRootLayout.findViewById(R.id.edittext_interest_rate);
		mTermEditText = (EditText) mRootLayout.findViewById(R.id.edittext_term);	
		mTotalInterestEditText = (EditText) mRootLayout.findViewById(R.id.edittext_total_interest);	
		mTotalAmountEditText = (EditText) mRootLayout.findViewById(R.id.edittext_total_amount);	
		
		
		mRepaymentResultList = new ArrayList<RepaymentResultModel>();
		
	}
	

	private void initializeView() {
		
		Intent intent = getActivity().getIntent();
		
		int selectRepayment = intent.getIntExtra(Constant.EXTRA_SELECTED_INDEX, -1);
		String loansText = intent.getStringExtra(Constant.EXTRA_LOANS);
		String interestRateText = intent.getStringExtra(Constant.EXTRA_INTEREST_RATE);
		String termText = intent.getStringExtra(Constant.EXTRA_TERM);
		
		mLoansEditText.setText( Util.toNumFormat(loansText) );
		mInterestRateEditText.setText( interestRateText );
		mTermEditText.setText( termText );
//		mTotalInterestEditText.setText( Util.toNumFormat(loansText) );
		
		mLoans = Integer.valueOf(loansText);
		mInterestRate = Float.valueOf(interestRateText);
		mTerm = Integer.valueOf(termText);
		
		calculateResult(selectRepayment);
		
		showResult();
	}

	/**
	 * 이자율 등을 계산...오브젝트에 담는다.
	 */
	private void calculateResult( int selectedRepayment ) {
		
		switch ( selectedRepayment ) {
		//원금 균등
		case 0 :
			calculateRepaymentLoans();
			break;
		//원리금 균등
		case 1 :
			calculateRepaymentLoansAndInterest();
			break;
		case 2 :
			calculateRepaymentLastRepayment();
			break;
		}
		
	}


	/**
	 * 0.원금
	 */
	private void calculateRepaymentLoans() {
		//월불입금
		//((1+B4)^B5*B4)/((1+B4)^B5-1)/12
		double repaymentResult = (double) mLoans / (double) mTerm;
//		double repaymentResult = mLoans * ( Math.pow( ( 1 + ( mInterestRate / 100 ) ) , mTerm / 12 ) * ( mInterestRate / 100 ) ) 
//				/ ( Math.pow( ( 1 + ( mInterestRate / 100 ) ) , mTerm / 12 ) - 1 ) / 12 ;
		
		Log.d("", "원금 : " + repaymentResult);
		
		double loansTemp; //원금 남은 금액 ..
		
		loansTemp = mLoans;
		RepaymentResultModel model;
		
		for ( int i = 0; (int) loansTemp > 0; i++ ) {
			
			if ( i == 0 ) {
				
				model = new RepaymentResultModel();
				model.setRemainingAmount( loansTemp );
				mRepaymentResultList.add(model);
			} else {

				//이자. 잔액 / 12 * 할부금리
				double interestResult = loansTemp / 12 * ( mInterestRate / 100 ) ;
				
				model = new RepaymentResultModel();
				model.setRepayments(  repaymentResult + interestResult ); //상환그,ㅁ
				model.setLoans(  repaymentResult ); //납입원금
				model.setInterest(  interestResult ); //이자
				//잔금
				model.setRemainingAmount(  mRepaymentResultList.get(mRepaymentResultList.size()- 1).getRemainingAmount() - model.getLoans() );
				//낸 원금
				model.setTotalLoans( model.getLoans() + mRepaymentResultList.get(mRepaymentResultList.size()- 1).getTotalLoans() );
				
				loansTemp = model.getRemainingAmount();
				
				mRepaymentResultList.add(model);
				
				Log.d("", "납입원금 : " + model.getLoans() + " 납입이자 : " + model.getInterest() + " 남은 원금 : " + model.getTotalLoans());
				
			}
		}
	}
	
	/**
	 * 1.원리금
	 */
	private void calculateRepaymentLoansAndInterest() {
		
		double loansTemp; //원금 남은 금액 ..
		
		
		loansTemp = mLoans;
		RepaymentResultModel model;
		
		//월불입금
		//월불입금 계산공식 : 대출원금 × 이자율 ÷ 12 × (1 + 이자율 ÷ 12)^기간 ÷((1 + 이자율 ÷ 12)^기간 -1)
		//대출원금 × 이자율 ÷ 12 × (1 + 이자율 ÷ 12)^기간 :  
		double temp = mLoans * ( mInterestRate / 100 ) / 12 * Math.pow( ( 1 + ( mInterestRate / 100 ) / 12 ) , mTerm );
		//((1 + 이자율 ÷ 12)^기간
		double temp2 = Math.pow( ( 1 + ( mInterestRate / 100 ) / 12 ), mTerm);
		double repaymentResult = temp / ( temp2 - 1); 
		
		for ( int i = 0; (int)loansTemp > 0; i++ ) {
			
			if ( i == 0 ) {
				
				model = new RepaymentResultModel();
				model.setRemainingAmount( loansTemp );
				mRepaymentResultList.add(model);
			} else {

				//이자. 잔액 / 12 * 할부금리
				double interestResult = loansTemp / 12 * ( mInterestRate / 100 ) ;
				
				model = new RepaymentResultModel();
				model.setRepayments(  repaymentResult  ); //상환그,ㅁ
				model.setLoans(  repaymentResult - interestResult ); //납입원금
				model.setInterest(  interestResult ); //이자
				//잔금
				model.setRemainingAmount(  mRepaymentResultList.get(mRepaymentResultList.size()- 1).getRemainingAmount() - model.getLoans() );
				//낸 원금
				model.setTotalLoans( model.getLoans() + mRepaymentResultList.get(mRepaymentResultList.size()- 1).getTotalLoans() );
				
				loansTemp = model.getRemainingAmount();
				
				mRepaymentResultList.add(model);
				
				Log.d("", "납입원금 : " + model.getLoans() + " 납입이자 : " + model.getInterest() + " 남은 원금 : " + model.getTotalLoans());
				
			}
		}
		
		
//		Log.d("", "RESULT : " + Util.toNumFormat((int) repaymentResult));
//		model.setReplayments(replayments);
	}
	
	/**
	 * 2.만기일시
	 */
	private void calculateRepaymentLastRepayment() {
		// 1000000 * 5.9 / 100 * 24 / 12 / 24
		//월불입금
		//((1+B4)^B5*B4)/((1+B4)^B5-1)/12
		double repaymentResult = 0;
//		double repaymentResult = mLoans * ( Math.pow( ( 1 + ( mInterestRate / 100 ) ) , mTerm / 12 ) * ( mInterestRate / 100 ) ) 
//				/ ( Math.pow( ( 1 + ( mInterestRate / 100 ) ) , mTerm / 12 ) - 1 ) / 12 ;
		
		
		double loansTemp; //원금 남은 금액 ..
		
		loansTemp = mLoans;
		RepaymentResultModel model;
		
		for ( int i = 0; i <=  mTerm; i++ ) {
			
			if ( i == 0 ) {
				
				model = new RepaymentResultModel();
				model.setRemainingAmount( loansTemp );
				mRepaymentResultList.add(model);
			} else if ( i == mTerm ) {
				
				//이자. 잔액 / 12 * 할부금리
				double interestResult = loansTemp * ( mInterestRate / 100 ) / 12;
				
				model = new RepaymentResultModel();
				model.setRepayments( mLoans + interestResult ); //상환그,ㅁ
				model.setLoans(  mLoans ); //납입원금
				model.setInterest(  interestResult ); //이자
				model.setRemainingAmount( repaymentResult );
				model.setTotalLoans( mLoans );
				
				mRepaymentResultList.add(model);
				
			} else {

				//1000000 * 5.9 / 100 * 24 / 12 / 24
				double interestResult = loansTemp * ( mInterestRate / 100 ) / 12;
				
				model = new RepaymentResultModel();
				model.setRepayments(  repaymentResult + interestResult ); //상환그,ㅁ
				model.setLoans(  repaymentResult ); //납입원금
				model.setInterest(  interestResult ); //이자
				model.setTotalLoans( repaymentResult );
				model.setRemainingAmount( mLoans );
				
				mRepaymentResultList.add(model);
				
			}
		}
	}


	/**
	 * 유형에 따라 다른로직을 적용한다.
	 * @param selectedRepayment
	 */
	private void showResult() {
		
		double totalInterest = 0;
		for ( int i = 0 ; i < mRepaymentResultList.size(); i++ ) {
			totalInterest += mRepaymentResultList.get(i).getInterest();
			
		}
		
		mTotalInterestEditText.setText( Util.toNumFormat((int) Math.round( totalInterest )) );
		mTotalAmountEditText.setText( Util.toNumFormat((int) Math.round( mLoans + totalInterest )) );
		
		
		//헤더.
		LinearLayout tableRow;
		tableRow = (LinearLayout) mInflater.inflate(R.layout.table_row, null);
		mContentLayout.addView(tableRow);
		
		
		for ( int i = 0; i < mRepaymentResultList.size(); i++ ) {
			
			RepaymentResultModel model = mRepaymentResultList.get(i);
			
			tableRow = (LinearLayout) mInflater.inflate(R.layout.table_row, null);
			((TextView) tableRow.findViewById(R.id.textview_no) ).setText(i + "");
			
			((TextView) tableRow.findViewById(R.id.textview_replayments) )
			.setText( Util.toNumFormat((int) Math.round(model.getRepayments() )) );
			
			((TextView) tableRow.findViewById(R.id.textview_principal) )
			.setText( Util.toNumFormat((int) Math.round(model.getLoans() )) );
			
			((TextView) tableRow.findViewById(R.id.textview_interest) )
			.setText( Util.toNumFormat((int) Math.round(model.getInterest() )) );
			
			((TextView) tableRow.findViewById(R.id.textview_remaining_amount) )
			.setText( Util.toNumFormat((int) Math.round(model.getRemainingAmount() )) );
			((TextView) tableRow.findViewById(R.id.textview_total_principal) )
			.setText( Util.toNumFormat((int) Math.round(model.getTotalLoans() )) );
			
			mContentLayout.addView(tableRow);
		}
	}

	private void initializeListeners() {
		mRetryButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ResultFragment.this.getActivity().onBackPressed();
			}
		});
	}
}
