package com.z_mobile.bmi;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.*;
import com.jjoe64.graphview.*;
import com.jjoe64.graphview.GraphView.*;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.google.ads.AdRequest;
//import com.google.ads.InterstitialAd;
//import com.google.android.gms.ads.AdView;
//import com.ironsource.mobilcore.MobileCore;
//import com.zmobile.foodtest.Registry.UpdateWeightRequest;
/*
import fatsecret.platform.FatSecretAPI;
import fatsecret.platform.FatSecretAuth;
import fatsecret.platform.FatSecretException;
*/

public class GraphActivity extends Activity implements OnClickListener, Updatable {
	
	MenuHelper menuHelp;
    SharedPreferences settings;
	
	ImageButton bPrevMonth;
	ImageButton bNextMonth;
	TextView tvMonth;
	Button bUnit;
	
	//FatSecretAPI fatSecretApi;
	//FatSecretAuth fatSecretAuth;
	//Registry diary;
	NutritionInfo nutrInfo;	
	private InterstitialAd interstitial;	
	AdRequest adRequest;		
	AdView adView;
	
	String consumerKey;
	String consumerSecret;
	
	Map<String,String> parameters;
	List<Map<String,String>> parsedArray;
    List<ListItem> list;

    Registry diary;
	
	boolean lbsMode = true;
	long currentDate;
	long today;
	int mHeight;

	@Override
	public void onStart() {
	    super.onStart();
	    // The rest of your onStart() code.
	    //EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	    interstitial.loadAd(adRequest);
	    //adView.loadAd(adRequest);  
	}

	@Override
	public void onStop() {
	    super.onStop();
	    // The rest of your onStop() code.
	    //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        //adView.pause();
        super.onPause();
        ThemeUtils.saveTheme(settings);
    }

	
	public void onBackPressed() {
		  
		if (interstitial.isLoaded()) {
	        interstitial.show();
	    }
		super.onBackPressed();
	}		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
		setContentView(R.layout.activity_weight_chart);
        settings = getApplicationContext().getSharedPreferences("Settings", 0);
		//adView = (AdView) findViewById(R.id.adView5);
		//AdRequestBuilder AdBuilder = new AdRequestBuilder();
        AdRequestBuilder AdBuilder = AdRequestBuilder.getInstance();
		adRequest = AdBuilder.build();
        interstitial = new InterstitialAd(this);
        //interstitial.setAdUnitId("ca-app-pub-4402674240600002/3354423372");
		interstitial.setAdUnitId(getResources().getString(R.string.admob_inter_id));
		interstitial.setAdListener(getAdMobInterListener);
		//adRequest = new AdRequest();
		//interstitial = new InterstitialAd(this, "ca-app-pub-4402674240600002/3354423372");
		//MobileCore.getSlider().setContentViewWithSlider(this, R.layout.activity_graph_view, R.raw.slider_1);
		
        bPrevMonth = (ImageButton) findViewById(R.id.bPrevMonth);
        bNextMonth = (ImageButton) findViewById(R.id.bNextMonth);
        tvMonth = (TextView) findViewById(R.id.tvMonth);
        bUnit = (Button) findViewById(R.id.bUnit);
        
        bPrevMonth.setOnClickListener(this);
        bNextMonth.setOnClickListener(this);
        bUnit.setOnClickListener(this);

        diary = new Registry(this);
		
		//consumerKey = getString(R.string.consumerKey);
		//consumerSecret = getString(R.string.consumerSecret);
				

		today = Calendar.getInstance().getTimeInMillis()/(1000*60*60*24);
        currentDate = today;
		
		SharedPreferences settings = getApplicationContext().getSharedPreferences("Settings", 0);
		
		int BMR = settings.getInt("kcal", 2000);
		boolean sex = settings.getBoolean("male", false);
		
		nutrInfo = new NutritionInfo(this, sex, BMR);
		
		mHeight = settings.getInt("height", 160);
		
        //getWeight(0);
        //list = diary.getDiaryList();
        getMonth(currentDate);
		//drawGraph(list, 0);

		menuHelp = new MenuHelper(this);
	}
	
	void drawGraph(List<ListItem> weightMonth, long from_date_int){
		
		final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
		final SimpleDateFormat yearFormat = new SimpleDateFormat("MMM yyyy");
		CustomLabelFormatter formater = new CustomLabelFormatter() {
			@Override
			public String formatLabel(double value, boolean isValueX) {
				if (isValueX) {
					Date d = new Date((long) value);
					return dateFormat.format(d);
				}
				return null; // let graphview generate Y-axis label for us
			}
		};
		CustomLabelFormatter yearFormater = new CustomLabelFormatter() {
			@Override
			public String formatLabel(double value, boolean isValueX) {
				if (isValueX) {
					Date d = new Date((long) value);
					return yearFormat.format(d);
				}
				return null; // let graphview generate Y-axis label for us
			}
		}; 
		
		int num = weightMonth.size();
		
		if (num == 0){
			String no_data = getResources().getString(R.string.no_data);
			Toast.makeText(this, no_data, Toast.LENGTH_SHORT).show();
			if (currentDate > today)
				currentDate = today;
			return;
		}
		int min = 1000;
		int max = 0;
		int minB = 1000;
		int maxB = 0;
		int range = Math.min(num, 9);
		int labelsNum = Math.min(num, 7);
		int textSize = (int) getResources().getDimension(R.dimen.text_xs);
		GraphViewData[] data = new GraphViewData[num];
		GraphViewData[] bmi = new GraphViewData[num];
		int nr = 0;
		for(ListItem day: weightMonth){
			//int date = Integer.parseInt(day.get("date_int"));//-from_date_int;

			long date_long = day.getTime();

			long date = 1000*60*60*24*date_long;//-from_date_int;

            int weightLbs = day.getmAmount();
			double weight = weightLbs;
            double weightKg = nutrInfo.lbsToKg(weightLbs);
			if (lbsMode == false){
				weight = weightKg;//nutrInfo.lbsToKg(weightLbs);
			}
			data[nr] = new GraphViewData(date,weight);
			double valY = data[nr].getY(); 
			bmi[nr] = new GraphViewData(date, nutrInfo.calcBMI(mHeight,weightKg));//valY/Math.pow(mHeight,2));
			double valB = bmi[nr].getY(); 
			if (valY < min) min = (int)(Math.floor(valY));
			if (valY > max) max = (int)(Math.ceil(valY));
			if (valB < minB) minB = (int)(Math.floor(valB));
			if (valB > maxB) maxB = (int)(Math.ceil(valB));
			nr++;
		}
		if (min == max){
			min = (int) (Math.floor(min - 1));
			max = (int) (Math.ceil(max + 1));
		}
		if (minB == maxB){
			minB = (int) (Math.floor(minB - 1));
			maxB = (int) (Math.ceil(maxB + 1));
		}
		/*
		double v=0;
		for (int i=0; i<num; i++) {
			v += 0.2;
			data[i] = new GraphViewData(i, 70+10*Math.sin(Math.random()*v));			
			double valY = data[i].getY(); 
			bmi[i] = new GraphViewData(i, valY/Math.pow(1.75,2));
			double valB = bmi[i].getY(); 
			if (valY < min) min = (int)(Math.floor(valY/5))*5;
			if (valY > max) max = (int)(Math.ceil(valY/5))*5;
			if (valB < minB) minB = (int)(Math.floor(valB/5))*5;
			if (valB > maxB) maxB = (int)(Math.ceil(valB/5))*5;
		}
		/*
		 * date as label formatter
		 */
		//String month = yearFormater.formatLabel(data[0].getX(), true);
        long date_millis = from_date_int*1000*60*60*24;
        String month = yearFormater.formatLabel(date_millis, true);
		tvMonth.setText(month);
		String currentMonth = "("+month+")";
		String your_wgt = getResources().getString(R.string.weight);
		if (lbsMode == true)
			your_wgt = your_wgt.concat(" (lb)");
		else
			your_wgt = your_wgt.concat(" (kg)");
		GraphView graphView = new LineGraphView(this, your_wgt);
		((LineGraphView) graphView).setDrawDataPoints(true);
		((LineGraphView) graphView).setDataPointsRadius(5f);
		graphView.getGraphViewStyle().setVerticalLabelsWidth(50);
		graphView.getGraphViewStyle().setVerticalLabelsAlign(Align.CENTER);
		
		graphView.setCustomLabelFormatter(formater);
		// add data
		graphView.addSeries(new GraphViewSeries(data));
		// set view port, start=2, size=10
		//graphView.setViewPort(1, range);
		//graphView.setScalable(true);
		graphView.setScrollable(true);
		double minMaxRange = max-min;
        int verticalLabelsNum = 1+max-min;
        if (minMaxRange > 10){
            max = (int) Math.ceil((double) max / 5)*5;
            min = (int) Math.floor((double) min / 5)*5;
            verticalLabelsNum = 1+(int)((double)(max-min)/5);
        }
		graphView.getGraphViewStyle().setGridColor(Color.GRAY);
		graphView.getGraphViewStyle().setTextSize(textSize);
		graphView.getGraphViewStyle().setNumVerticalLabels(verticalLabelsNum);
		graphView.getGraphViewStyle().setNumHorizontalLabels(labelsNum);
		// set manual Y axis bounds
		graphView.setManualYAxisBounds(Math.ceil(max), Math.floor(min));
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.removeAllViews();
		layout.addView(graphView);
		/*
		layout = (LinearLayout) findViewById(R.id.graph2);
		layout.addView(graphView);
		*/
		String your_bmi = getResources().getString(R.string.your_bmi);
		GraphView bmiGraphView = new BarGraphView(this, your_bmi);
		GraphViewSeriesStyle seriesStyle = new GraphViewSeriesStyle();
		seriesStyle.setValueDependentColor(new ValueDependentColor() {
			@Override
			public int get(GraphViewDataInterface data) {
				// the higher the more red
				return GetColor(data.getY());
						//Color.rgb((int)(150+((data.getY()/3)*100)), (int)(150-((data.getY()/3)*150)), (int)(150-((data.getY()/3)*150)));
			}
		});
		
		// add data
		bmiGraphView.addSeries(new GraphViewSeries("aaa", seriesStyle, bmi));
		bmiGraphView.setCustomLabelFormatter(formater);
		// set view port, start=2, size=10
		//bmiGraphView.setViewPort(1, range);
		//bmiGraphView.setScalable(true);
		bmiGraphView.setScrollable(true);
		bmiGraphView.getGraphViewStyle().setGridColor(Color.GRAY);
		bmiGraphView.getGraphViewStyle().setTextSize(textSize);
		bmiGraphView.getGraphViewStyle().setNumVerticalLabels(1+maxB-minB);
		bmiGraphView.getGraphViewStyle().setNumHorizontalLabels(labelsNum);
		bmiGraphView.getGraphViewStyle().setVerticalLabelsWidth(50);
		bmiGraphView.getGraphViewStyle().setVerticalLabelsAlign(Align.CENTER);
		// set manual Y axis bounds
		bmiGraphView.setManualYAxisBounds(Math.ceil(maxB), Math.floor(minB));
		LinearLayout layout2 = (LinearLayout) findViewById(R.id.graph2);
		layout2.removeAllViews();
		layout2.addView(bmiGraphView);
		
		//adView.loadAd(adRequest);  
		//adView.setVisibility(View.VISIBLE);
	}

    void getMonth(long date){
        // current_weight_kg
        Map<String,String> params = new HashMap<String,String>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date*24*60*60*1000);

        try{

            //diary.getHistoryList();

            //todayHrs

            int min = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, min);
            long first = calendar.getTimeInMillis()/(1000*60*60*24);
            int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, max);
            long last = calendar.getTimeInMillis()/(1000*60*60*24);

            List<ListItem> month = diary.getHistoryforDates(first, last);

            drawGraph(month, last);

            //FoodSearchRequest request = new FoodSearchRequest();

            //new GetMonthRequest().execute(url, reqMethod, paramsSigned);

        }catch(Exception e){
            String err = e.toString();
            toastMsg(err);
        }
    }

	/*
	private class GetWeightRequest extends AsyncTask<String, String, String> {
		
		
	    protected void onPostExecute(String response) {
	        // TODO: check this.exception 
	        // TODO: do something with the feed
	    	List<String> rootPath = new ArrayList<String>();
	    	
	    	if (response.indexOf("error") > 0){
	    		if (response.indexOf("stamp") > 0){	    			
	    			String msg = getResources().getString(R.string.set_time);
	    			toastMsg(msg);
	    		}
	    		///sendEmail(response);	    		
	    	}
	    	
	    	rootPath.add("month");
	    	//rootPath.add("day");
	    	
	    	String[] itemNames = {"date_int", "weight_kg"};
	    	//{"brand_name",
	    	//<String, String, String>
	    	ParseJSON parser = new ParseJSON(response, rootPath, itemNames);
	    	
	    	parsedArray = parser.getArrayResponse("day");
	    	
	    	String[] dateItems = {"from_date_int", "to_date_int"};
	    	//{"brand_name",
	    	//<String, String, String>
	    	ParseJSON parser2 = new ParseJSON(response, rootPath, dateItems);
	    	
	    	//parsedArray = parser.getArrayResponse("serving");
	    	Map<String,String> dates = parser2.getResponse();
	    	
	    	currentDate = nutrInfo.strToLong(dates.get("from_date_int"), today);
	    	//Integer.valueOf(dates.get("from_date_int")); 
	    	
	    	if (parsedArray.size() > 0)
	    		drawGraph(parsedArray, currentDate);
	    	else{    	
		    	//String no_data = getResources().getString(R.string.no_data);
				//Toast.makeText(GraphViewActivity.this, no_data, Toast.LENGTH_SHORT).show();
				toastMsg(R.string.no_data);
				//finish();
	    	}

			
			
			//String retUrl = urlBase + "&" + result.getSignature();
	    }

		//protected String doInBackground(String url, String method, String paramsStr, Map<String,String> params) {
	    
	    protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			// If use POST, must use this
			String response = "";
			String urlStr = (String)params[0];	
			String requestMethod = (String)params[1];
			String paramStr = (String)params[2];
			//Map<String, String> header = params;//(Map<String, String>)params[3];
			//HttpURLConnection conn = (HttpURLConnection)params[0];				
			//String paramStr = (String)params[2];
			//StringBuffer sb = new StringBuffer();
			
			try {
				response = fatSecretApi.doHttpMethodReq(urlStr, requestMethod, paramStr, parameters);
			} catch (FatSecretException e) {
				// TODO Auto-generated catch block
				String err = e.toString();
				e.printStackTrace();
			}

			return response;
		}

	}
	*/
	int GetColor(double bmi) {
		float[] hsv = new float[3];
		int color = Color.GRAY;
		Color.colorToHSV(color, hsv);
		//hsv[parameter] = hsv[parameter]/factor;
		//hsv[0] = (float)(-bmi/10 + 7.5)/92*360+360;
		//hsv[0] = (float)(300/((bmi-0.5)/7)-23);
		hsv[0] = (float)(360-bmi*12);
		hsv[1] = 1;
		hsv[2] = 0.75f;
		int newCol = Color.HSVToColor(hsv);
		return newCol;
	}

	@Override
	public void onClick(View button) {
		// TODO Auto-generated method stub
		int id = button.getId();
		//switch(button.getId()){
		if (id == R.id.bPrevMonth) {
			//case R.id.bPrevMonth:
			currentDate -= 28;
			getMonth(currentDate);
			//break;
		}else if (id == R.id.bNextMonth) {
			//case R.id.bNextMonth:
			/*if (currentDate + 31 > today){
				Toast.makeText(this, "No data available for this month", Toast.LENGTH_SHORT).show();
			}else{*/
			currentDate += 31;
			getMonth(currentDate);
			//}
			//break;
		}else if (id == R.id.bUnit) {
			//case R.id.bUnit:
			if (lbsMode == true) {
				lbsMode = false;
				bUnit.setText("lb");
			} else {
				lbsMode = true;
				bUnit.setText("kg");
			}
			getMonth(currentDate);
			//drawGraph(list, currentDate);
			//break;
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		menuHelp.handleOnItemSelected(this, item);
		return true;
		/*
		Intent i;
        switch (item.getItemId()) {
        case R.id.action_search:
            i = new Intent(this, FoodSearch.class);
            startActivity(i);
            return true;        
        case R.id.action_favorites:
            i = new Intent(this, FavoritesActivity.class);
            startActivity(i);
            return true;        
        case R.id.action_diary:
            i = new Intent(this, GetTodayDiary.class);
            startActivity(i);
            return true;        
        case R.id.action_diary_chart:
            i = new Intent(this, DiaryGraphActivity.class);
            startActivity(i);
            return true;        
        case R.id.action_weight_chart:
            i = new Intent(this, GraphViewActivity.class);
            startActivity(i);
            return true;         
        case R.id.action_settings:
          i = new Intent(this, SettingsActivity.class);
          startActivity(i);
          return true;
        case R.id.action_info:
          i = new Intent(this, InfoActivity.class);
          startActivity(i);
          return true;
        default:
          return super.onOptionsItemSelected(item);
        } */
    } 
	
	private void toastMsg(int stringId){
		
		String msg = getResources().getString(stringId);
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}	

	public void toastMsg(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	private void sendEmail(String msg){
		nutrInfo.sendEmail(this, msg);
	}

    public void Update() {
        // TODO Auto-generated method stub
        int themeId = menuHelp.dialogSymbol.getThemeId();
        int selectionId = menuHelp.dialogSymbol.getSelectionId();
        //getApplication().setTheme(themeId);
        switch (selectionId)
        {
            case 0:
                ThemeUtils.changeToTheme(this, ThemeUtils.THEME_DEFAULT);
                break;
            case 1:
                ThemeUtils.changeToTheme(this, ThemeUtils.THEME_WHITE);
                break;
            case 2:
                ThemeUtils.changeToTheme(this, ThemeUtils.THEME_BLUE);
                break;
        }
    }

	com.google.android.gms.ads.AdListener getAdMobInterListener = new com.google.android.gms.ads.AdListener(){

		@Override
		public void onAdFailedToLoad(int errorCode){
			toastMsg("AdMob inter load error: "+errorCode, 3);
		}

		@Override
		public void onAdLoaded(){
			toastMsg("AdMob inter loaded", 3);
		}


	};

	public void toastMsg(String msg, int level){

		//String msg = context.getResources().getString(stringId);
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

}
