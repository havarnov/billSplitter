package com.billSplitter;

import org.w3c.dom.Text;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.MailTo;
import android.os.Bundle;
import android.provider.Settings.System;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

// Create an anonymous implementation of OnClickListener




public class billSplitter extends Activity {

	Button submit;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(myClickHandler);
	}


	//this method is called when the button is clicked
	View.OnClickListener myClickHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String[] nameArrayTEMP = null;
			String[] nameArray = null;
			int[] payArrayNULL = null;
			float[] payArray = null;
			float[] payArrayTEMP = null;
			if (submit.getId() == ((Button)v).getId() ) {
				TableLayout content = (TableLayout) findViewById(R.id.contentGroup);
				nameArrayTEMP = new String[content.getChildCount()];
				payArrayTEMP = new float[content.getChildCount()];
				payArrayNULL = new int[content.getChildCount()];
				int j = 0;
				int k = 0;
				for (int i=0;i<content.getChildCount();i++) {
					EditText name = (EditText) content.getChildAt(i).findViewById(R.id.name);
					EditText pay = (EditText) content.getChildAt(i).findViewById(R.id.pay);
					if (name != null || pay != null) {
						if (name != null) {
							String nameTEMP = name.getText().toString();
							if (TextUtils.isEmpty(nameTEMP)) {
								nameArrayTEMP[j] = null;
							}
							else {
								nameArrayTEMP[j] = nameTEMP;
							}
							j += 1;
						}
						if (pay != null) {
							String payTEMP = pay.getText().toString();
							if (TextUtils.isEmpty(payTEMP)) {
								payArrayNULL[k] = 1; 
							}
							else {
								payArrayTEMP[k] = Float.parseFloat(payTEMP);
							}
							k += 1;
						}
					}

					name = null;
					pay = null;
				}

				//make nameArray the right size, just the values that are not null
				int nc = 0;
				for (int i = 0;i < nameArrayTEMP.length;i++) {
					if (nameArrayTEMP[i] != null) {
						nc += 1;
					}	
				}
				nameArray = new String[nc];
				nc = 0;
				for (int i = 0;i < nameArrayTEMP.length;i++) {
					if (nameArrayTEMP[i] != null) {
						nameArray[nc] = nameArrayTEMP[i];
						nc += 1;
					}
				}
				//make payArrat the right size
				nc = 0;
				for (int i = 0;i < payArrayNULL.length;i++) {
					if (payArrayNULL[i] == 1) {
						nc += 1;
					}
				}
				payArray = new float[payArrayTEMP.length - nc];
				nc = 0;
				for (int i = 0;i < payArrayTEMP.length;i++) {
					if (payArrayNULL[i] != 1) {
						payArray[nc] = payArrayTEMP[i];
						nc += 1;
					}
				}



				//Do calculations to who pay whom if nameArray and payArray is both of equal size
				if (nameArray.length == payArray.length) {

					float avgPay = avgPay(payArray);
					float[] deptArray = dept(payArray,avgPay);
					float[][] wPw = whoPayWho(nameArray,payArray,deptArray);


					Intent intent = new Intent(billSplitter.this, results.class);
					intent.putExtra("nameArray", nameArray);
					intent.putExtra("payArray", payArray);
					//				intent.putExtra("payArrayTest", payArrayTest);
					intent.putExtra("deptArray", deptArray);
					intent.putExtra("avgPay", avgPay);
					for (int i = 0;i < wPw.length;i++) {
						float[] wPwTEMP = wPw[i].clone();
						String name = "wPw"+i;
						intent.putExtra(name, wPwTEMP);
					}
					intent.putExtra("wPwLength", wPw.length);


					startActivityForResult(intent, 0);
				}
				else {
					AlertDialog alertDialog = new AlertDialog.Builder(billSplitter.this).create();
					alertDialog.setTitle("Error");
					alertDialog.setMessage("Mismatch between name and payed");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					});
					alertDialog.show();
				}
			}		
		}
	};

	//function to round at the second decimal place
	// taken from: http://www.roseindia.net/java/beginners/RoundTwoDecimalPlaces.shtml
	public static float Round(float Rval, int Rpl) {
		float p = (float)Math.pow(10,Rpl);
		Rval = Rval * p;
		float tmp = Math.round(Rval);
		return (float)tmp/p;
	}

	//Functions for calculation the payback
	public float avgPay(float[] array) {
		float totSum = 0f;
		for (int i = 0;i<array.length;i++) {
			totSum += array[i];
		}
		return totSum / array.length;
	}

	public float[] dept(float[] payArray,float avgPay) {
		float[] dept = new float[payArray.length];
		for (int i = 0;i<payArray.length;i++) {
			dept[i] = avgPay - payArray[i];
		}
		return dept;
	}

	public float[][] whoPayWho(String[] nameArray,float[] pay,float[] dept) {
		float[][] wPw = new float[nameArray.length][];
		float[] deptTEMP = dept.clone();
		for (int i = 0;i<nameArray.length;i++) {
			wPw[i] = new float[nameArray.length];
			for (int j = 0;j<nameArray.length;j++) {
				if (nameArray[i] == nameArray[j] || deptTEMP[j] >= 0) {
					wPw[i][j] = 0;
				}
				else if (deptTEMP[i]*-1 <= deptTEMP[j]) {
					float payBack = Round(deptTEMP[j]*-1,2);
					wPw[i][j] = payBack;
					deptTEMP[i] -= payBack;
					deptTEMP[j] += payBack;
				}
			}
		}

		for (int i = 0;i<nameArray.length;i++) {
			if (deptTEMP[i] > 0) {
				for (int j = 0;j<nameArray.length;j++) {
					if (nameArray[i] == nameArray[j] || deptTEMP[j] > 0) {
						wPw[i][j] = 0;
					}
					else if (deptTEMP[i]*-1 >= deptTEMP[j]) {
						float payBack = Round(deptTEMP[i],2);
						wPw[i][j] = payBack;
						deptTEMP[i] += payBack;
						deptTEMP[j] -= payBack;
					}
				}
			}
		}

		return wPw;
	}


};
