package com.billSplitter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class results extends Activity {

		@Override
		public void onCreate(Bundle savedInstanceState) {          

			super.onCreate(savedInstanceState);
//			setContentView(R.layout.results);
			Bundle extras = getIntent().getExtras();
//			String v = extras.getString("sumIt");
//			String valueText = Integer.toString(v);
//			int test = extras.getInt("test");
			String[] nameArray = extras.getStringArray("nameArray");
			float[] payArray = extras.getFloatArray("payArray");
			float[] deptArray = extras.getFloatArray("deptArray");
			float avgPay = extras.getFloat("avgPay");
			int wPwLength = extras.getInt("wPwLength");
			float[][] wPw = new float[wPwLength][];
			for (int i = 0;i < wPwLength;i++) {
				String name = "wPw" + i;
				float[] wPwTEMP = extras.getFloatArray(name);
				wPw[i] = wPwTEMP.clone();
			}
//			String[] payArrayTest = extras.getStringArray("payArrayTest");
//			TextView result = (TextView) findViewById(R.id.result);
//			if (payArrayTest[0] != null) {
////				result.setText(Float.toString(deptArray[1]));'
//				result.setText(Float.toString(avgPay));
//			}
//			else {
//				result.setText("testtest");
//			}
			ScrollView sv = new ScrollView(this);
			LinearLayout ll = new LinearLayout(this);
			ll.setOrientation(LinearLayout.VERTICAL);
			sv.addView(ll);
			for (int i = 0;i < deptArray.length; i++) {
				TextView tv = new TextView(this);
				tv.setText(Float.toString(deptArray[i]));
				ll.addView(tv);
			}
			for (int i = 0;i < wPw.length;i++) {
				TextView tv = new TextView(this);
				tv.setText(nameArray[i]+":");
				ll.addView(tv);
				for (int j = 0;j < wPw[i].length;j++) {
					TextView tv2 = new TextView(this);
					tv2.setText(nameArray[j]+":");
					ll.addView(tv2);
					TextView tv3 = new TextView(this);
					tv3.setText(Float.toString(wPw[i][j]));
					ll.addView(tv3);
					
				}
			}
			
			this.setContentView(sv);

		}

	
	
}
