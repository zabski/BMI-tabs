package com.z_mobile.bmi;

//import com.zabski.loancalc.R;

//import com.zabski.loancalc.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.analytics.tracking.android.EasyTracker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;
import java.util.Locale;

//import com.google.ads.*;
//import com.ironsource.mobilcore.MobileCore;
//import com.zmobile.foodtest.Registry.FavoriteRequest;

//import fatsecret.platform.FatSecretAPI;
//import fatsecret.platform.FatSecretException;


public class SettingsActivity extends Activity implements OnClickListener,
		OnSeekBarChangeListener, OnCheckedChangeListener, OnItemSelectedListener, Updatable {

	//MenuHelper menuHelp;
	EditText editWzrost;
	EditText editWaga;
	TextView textBMI;
	TextView tvMeters;
	TextView tvKilos;
	TextView tvYears;
	TextView tvFeet;
	TextView tvLbs;
	TextView tvHeight;
	TextView tvWeight;
	TextView tvBMR;
	TextView tvBMI;
	TextView tvBMIdesc;
    TextView tvLang;
	Button btSave;
	Button btExit;
    Button btChart;
	ImageButton tbMetric;
	ImageButton btAgeMin;
	ImageButton btAgePlus;
	ImageButton btHgtMin;
	ImageButton btHgtPlus;
	ImageButton btWgtMin;
	ImageButton btWgtPlus;
	TableRow tvLegend1;
	TableRow tvLegend2;
	TableRow tvLegend3;
	TableRow tvLegend4;
	SeekBar seekAge;
	SeekBar seekHeight;
	//SeekBar seekWeight;
	SeekBar seekWeightLbs;
	RadioGroup rGroupSex;
	RadioGroup rGroupExc;
    RadioButton rbMale;
    RadioButton rbFemale;
	Spinner spinExe;
	
	String consumerKey;
	String consumerSecret;
	
	//FatSecretAPI fatSecretApi;
	Registry diary;
	
	NutritionInfo nutrInfo;
    MenuHelper menuHelp;
    SharedPreferences settings;

    int minAge = 10;
	int maxAge = 60;
	int minHgt = 130;
	int maxHgt = 210;
	int minWgt = 30;	
	int maxWgt = 180;
	int minWgtLbs = 66;
	int maxWgtLbs = 500;
	int mAge = 30;
	int mHeight = 160;
	int mFeet;
	int mInch;
	//int mWeight = 60;
	double mWeightKg;
	final double LbsRatio = 0.45359237;
	int mLbs;
	boolean mSex = false;//"Female";
	int mExercise = 0;
	double mBMR;
	double mBMI;
	String mAuthToken = "";

	int clBlue = Color.rgb(100, 100, 255);
	int clGreen = Color.rgb(0, 200, 0);
	int clOrange = Color.rgb(255, 200, 0);
	int clRed = Color.rgb(255, 32, 32);
	boolean metric = true;

	AdView adView;
	
	@Override
	public void onStart() {
	    super.onStart();
	    // The rest of your onStart() code.
	    //EasyTracker.getInstance(this).activityStart(this);  // Add this method.
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        settings = getApplicationContext().getSharedPreferences("Settings", 0);
        int theme = settings.getInt("theme", 0);
        ThemeUtils.setTheme(theme);
        ThemeUtils.onActivityCreateSetTheme(this);
		setContentView(R.layout.activity_settings);
		//MobileCore.getSlider().setContentViewWithSlider(this, R.layout.activity_settings, R.raw.slider_1);
        menuHelp = new MenuHelper(this);
		
		//consumerKey = getString(R.string.consumerKey);
		//consumerSecret = getString(R.string.consumerSecret);
		
		btSave = (Button) findViewById(R.id.btSave);
		btExit = (Button) findViewById(R.id.btExit);
        btChart = (Button) findViewById(R.id.btChart);
		btAgeMin = (ImageButton) findViewById(R.id.btAgeMin);
		btAgePlus = (ImageButton) findViewById(R.id.btAgePlus);
		btHgtMin = (ImageButton) findViewById(R.id.btHgtMin);
		btHgtPlus = (ImageButton) findViewById(R.id.btHgtPlus);
		btWgtMin = (ImageButton) findViewById(R.id.btWgtMin);
		btWgtPlus = (ImageButton) findViewById(R.id.btWgtPlus);
		//tbMetric = (Button) findViewById(R.id.tbMetric);
		tvYears = (TextView) findViewById(R.id.tvYears);
		tvFeet = (TextView) findViewById(R.id.tvFeet);
		tvBMR = (TextView) findViewById(R.id.tvBMR);
		tvBMI = (TextView) findViewById(R.id.tvBMI);
		tvBMIdesc = (TextView) findViewById(R.id.tvBMIdesc);
		tvMeters = (TextView) findViewById(R.id.tvCm);
		tvKilos = (TextView) findViewById(R.id.tvKgs);
		tvLbs = (TextView) findViewById(R.id.tvLbs);
        tvLang = (TextView) findViewById(R.id.tvLang);
		adView = (AdView) findViewById(R.id.adView);
		
		// Get from the SharedPreferences
		//SharedPreferences
		
		//mSex = settings.getString("sex", "Female");
        mSex = settings.getBoolean("male", false);
		int BMR = settings.getInt("kcal", 2000);		
		
		nutrInfo = new NutritionInfo(this, mSex, BMR);
		mWeightKg = nutrInfo.lbsToKg(mLbs);
		
        seekAge = (SeekBar)findViewById(R.id.seekAge); // make seekbar object
        seekAge.setOnSeekBarChangeListener(this); // set seekbar listener.
        seekAge.setMax(maxAge-minAge);
        seekHeight = (SeekBar)findViewById(R.id.seekHeight); // make seekbar object
        seekHeight.setMax(maxHgt-minHgt);
        seekHeight.setOnSeekBarChangeListener(this); // set seekbar listener.
        //seekWeight = (SeekBar)findViewById(R.id.seekWeight); // make seekbar object
        //seekWeight.setOnSeekBarChangeListener(this); // set seekbar listener.
        //seekWeight.setMax(maxWgt-minWgt);
        seekWeightLbs = (SeekBar)findViewById(R.id.seekWeightLbs); // make seekbar object
        seekWeightLbs.setOnSeekBarChangeListener(this); // set seekbar listener.
        seekWeightLbs.setMax(maxWgtLbs-minWgtLbs);
		
		//fatSecretApi = new FatSecretAPI(consumerKey,consumerSecret);
		
		diary = new Registry(this);
		
		mAge = settings.getInt("age", 30);
		mHeight = settings.getInt("height", 160);
		//mWeight = settings.getInt("weight", 50);
		mLbs = settings.getInt("lbs", 100);
		
		mExercise = settings.getInt("exercise", 1);
		mAuthToken = settings.getString("token", ""); 
		
		// Read stored data		

		/*
		editWzrost = (EditText) findViewById(R.id.editWzrost);
		editWaga = (EditText) findViewById(R.id.editWaga);
		*/


		/*
		tvLegend1 = (TableRow) findViewById(R.id.tableRow5);
		tvLegend2 = (TableRow) findViewById(R.id.tableRow6);
		tvLegend3 = (TableRow) findViewById(R.id.tableRow7);
		tvLegend4 = (TableRow) findViewById(R.id.tableRow8);
		*/

		btSave.setOnClickListener(this);
		btExit.setOnClickListener(this);
        btChart.setOnClickListener(this);
		btAgeMin.setOnClickListener(this);
		btAgePlus.setOnClickListener(this);
		btHgtMin.setOnClickListener(this);
		btHgtPlus.setOnClickListener(this);
		btWgtMin.setOnClickListener(this);
		btWgtPlus.setOnClickListener(this);
		rGroupSex = (RadioGroup) findViewById(R.id.radioGroupSex);
		rGroupSex.setOnCheckedChangeListener(this);
        rbMale = (RadioButton) findViewById(R.id.radioMale);
        rbFemale = (RadioButton) findViewById(R.id.radioFemale);
		//rGroupExc = (RadioGroup) findViewById(R.id.radioGroup2);
		//rGroupExc.setOnCheckedChangeListener(this);
		
		spinExe = (Spinner) findViewById(R.id.spinExcercise);
		// Create an ArrayAdapter using the string array and a default spinner layout
		//ArrayAdapter<CharSequence> adapterExc = ArrayAdapter.createFromResource(this,
		//        R.array.exercise, android.R.layout.simple_spinner_item);
		ArrayAdapter<CharSequence> adapterExc = ArrayAdapter.createFromResource(this,
				R.array.exercise, R.layout.spinner_textview);
                //R.layout.spinner_type_item);
                //android.R.layout.simple_selectable_list_item);
                        //simple_spinner_item);
		        //.simple_selectable_list_item);
		// Specify the layout to use when the list of choices appears
		//adapterExc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinExe.setAdapter(adapterExc);
		
		//spinExe.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner, spinnerItems));
		
		spinExe.setOnItemSelectedListener(this);
		
		/*tbMetric.setOnClickListener(this);
		tvLegend1.setBackgroundColor(clBlue);
		tvLegend2.setBackgroundColor(clGreen);
		tvLegend3.setBackgroundColor(clOrange);
		tvLegend4.setBackgroundColor(clRed);
		NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker1);
		
		np.setMinValue(140);
	    np.setMaxValue(200);
	    np.setWrapSelectorWheel(true);
	    //np.setDisplayedValues(nums);
	    np.setValue(160);
	    */		
		//menuHelp = new MenuHelper(this);
        String displang = Locale.getDefault().getDisplayLanguage();
        String lang = Locale.getDefault().getLanguage();
        tvLang.setText("Languages: "+displang+" : "+lang);
		displayValues();
		CountPublish();

		AdRequestBuilder AdBuilder = AdRequestBuilder.getInstance();
		AdRequest adRequest = AdBuilder.build();
		adView.setAdListener(getAdMobBannerListener);
		adView.loadAd(adRequest);

	}
	
	// Calculates BMI and Calories and display it
	private void CountPublish()
	{
		if (mSex){
			mBMR = 88.362 + (13.397*mWeightKg) + (4.799*mHeight) - (5.677 * mAge);
		}else{		
			mBMR = 447.593 + (9.247*mWeightKg) + (3.098*mHeight) - (4.330 * mAge);						
		}
		double factor = 1;
		switch (mExercise){
			case 0: factor = 1.2;
				break;
			case 1: factor =  1.375;
				break;
			case 2: factor =  1.55;
				break;
			case 3: factor = 1.725;
				break;			
			case 4: factor = 1.9;
				break;					
		}
		mBMR = (int) Math.round(mBMR * factor);
		mWeightKg = nutrInfo.lbsToKg(mLbs);
		double bmi = (double)mWeightKg/ Math.pow((double) mHeight / 100, 2);
		int color = GetColor(bmi);
		String comment = nutrInfo.getBMIcomment(bmi);

		int colors[] = new int[2];
		colors[1] = color;
		colors[0] = ModifyColor(color,1,2);
		GradientDrawable backColor = new GradientDrawable(Orientation.TOP_BOTTOM, colors);
		tvBMIdesc.setBackgroundDrawable(backColor);
		//tvBMI.setBackgroundColor(color);
		int id = getResources().getIdentifier(comment, "string", getPackageName());
		comment = getResources().getString(id);
		mBMI = bmi;
        int bmr = (int) mBMR;
        String kcal = " "+getResources().getString(R.string.kcal);
		tvBMR.setText(bmr+kcal);
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
        tvBMI.setText(String.format("%.1f", mBMI));//+ hsv[0]);
		tvBMIdesc.setText(comment);//+ hsv[0]);
		return;
	}

	// Display all input values
	void displayValues()
	{
		seekAge.setProgress(mAge-minAge);
		seekHeight.setProgress(mHeight-minHgt);
		//seekWeight.setProgress(mWeight-minWgt);
		seekWeightLbs.setProgress(mLbs-minWgtLbs);
		spinExe.setSelection(mExercise);		
		if (mSex)
			rGroupSex.check(R.id.radioMale);
		else
			rGroupSex.check(R.id.radioFemale);
	}
	
	
	/// Buttons click
	@Override
	public void onClick(View arg0) {
		

/*
Men	BMR = 88.362 + (13.397 x weight in kg) + (4.799 x height in cm) - (5.677 x age in years)
Women	BMR = 447.593 + (9.247 x weight in kg) + (3.098 x height in cm) - (4.330 x age in years)

Little to no exercise	Daily kilocalories needed = BMR x 1.2
Light exercise (1-3 days per week)	Daily kilocalories needed = BMR x 1.375
Moderate exercise (3-5 days per week)	Daily kilocalories needed = BMR x 1.55
Heavy exercise (6-7 days per week)	Daily kilocalories needed = BMR x 1.725
Very heavy exercise (twice per day, extra heavy workouts)	Daily kilocalories needed = BMR x 1.9
*/
		int id = arg0.getId();
		final int ageMin = R.id.btAgeMin;
		
		if (id == (R.id.btAgeMin)) {
			//case (ageMin):
			if (mAge>minAge){ 
				mAge--;
				seekAge.setProgress(mAge-minAge);
			}
		} else if (id == (R.id.btAgePlus)) {
			if (mAge<maxAge){ 
				mAge++;
				seekAge.setProgress(mAge-minAge);
			}
		} else if (id == (R.id.btHgtMin)) {
			if (mHeight>minHgt){ 
				mHeight--;
				seekHeight.setProgress(mHeight-minHgt);
			}
		} else if (id == (R.id.btHgtPlus)) {
			if (mHeight<maxHgt){ 
				mHeight++;
				seekHeight.setProgress(mHeight-minHgt);
			}
		} else if (id == (R.id.btWgtMin)) {
			/*
			if (mWeight>minWgt){ 
				mWeight--;
				seekWeight.setProgress(mWeight-minWgt);
			}*/
			// Lbs
			if (mLbs>minWgtLbs){ 
				mLbs--;
				seekWeightLbs.setProgress(mLbs-minWgtLbs);
			}
		} else if (id == (R.id.btWgtPlus)) {
			/*
			if (mWeight<maxWgt){ 
				mWeight++;
				seekWeight.setProgress(mWeight-minWgt);
			}*/
			// Lbs
			if (mLbs<maxWgtLbs){ 
				mLbs++;
				seekWeightLbs.setProgress(mLbs-minWgtLbs);
			}
		} else if (id == (R.id.btSave)) {
			SharedPreferences settings = getApplicationContext().getSharedPreferences("Settings", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("age", mAge);
			editor.putInt("height", mHeight);
			//editor.putInt("weight", mWeight);
			editor.putInt("lbs", mLbs);
			editor.putBoolean("male", mSex);
			editor.putInt("exercise", mExercise);
			editor.putInt("kcal", (int) Math.round(mBMR));
			// Commit the edits!
			editor.commit();
			double weightKg = nutrInfo.lbsToKg(mLbs);
			try{
				//diary.updateWeight(weightKg, mHeight, weightKg-5, false);
			}catch(Exception e){
				String no_internet = "Turn on internet connection";
				Toast.makeText(SettingsActivity.this, no_internet, Toast.LENGTH_SHORT).show();

			}
            long today = Calendar.getInstance().getTimeInMillis()/(1000*60*60*24);
            ListItem entry = new ListItem(mLbs, today);
			diary.addDiary(entry);
            String data_saved = getString(R.string.data_saved);
            Toast.makeText(SettingsActivity.this, data_saved, Toast.LENGTH_SHORT).show();
			//finish();
		} else if (id == (R.id.btExit)) {
            //Intent intent = new Intent(this, GraphActivity.class);
            //startActivity(intent);
			finish();
		} else if (id == (R.id.btChart)) {
            Intent intent = new Intent(this, GraphActivity.class);
            startActivity(intent);
            //finish();
        }
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		
		int id = seekBar.getId();
		
		if (id == R.id.seekAge){
			mAge = progress+minAge;
		}
		else if (id == R.id.seekHeight){
			mHeight = progress+minHgt;
			
			/*
		case (R.id.seekWeight):
			mWeight = progress+minWgt;
			mLbs = (int)Math.round(mWeight/LbsRatio);		    
			break;*/		
		}
		else if (id == R.id.seekWeightLbs){
			mLbs = progress+minWgtLbs;			
			//mWeight = (int)Math.round(mLbs*LbsRatio);
			mWeightKg = (double) Math.round(mLbs * LbsRatio * 2)/2;
			
		}
		/*
		mAge = seekAge.getProgress()+minAge;
		mHeight = seekHeight.getProgress()+minHgt;
		mWeight = seekWeight.getProgress()+minWgt;
		*/
		
		int feet = (int) (mHeight/(12*2.54));
		int inches = (int)(Math.round(mHeight % (feet * 12 * 2.54))/2.54);
		int lbs = (int) Math.round(mWeightKg / 0.45359237);
		String years = getResources().getString(R.string.years);
		tvYears.setText(mAge+" "+years);
		String strFeet = " "+getResources().getString(R.string.feet)+" ";
		String strInch = " "+getResources().getString(R.string.inches);
		tvMeters.setText(mHeight+" cm = "+feet+strFeet+inches+strInch);
		//tvFeet.setText(feet+" feet "+inches+" inches");		
		//tvKilos.setText(mWeight+" kg = "+lbs+" lbs");
		String kg = " "+getResources().getString(R.string.kg)+" = ";
		String lb = " "+getResources().getString(R.string.lb);
		tvKilos.setText(String.format("%.1f", mWeightKg)+kg+mLbs+lb);
		//tvLbs.setText(lbs+" lbs");
		CountPublish();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCheckedChanged(RadioGroup rGroup, int id) {
		// TODO Auto-generated method stub
		Drawable btn = getResources().getDrawable(R.layout.menu_item);
		
		rGroup.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
		rGroup.getChildAt(1).setBackgroundColor(Color.TRANSPARENT);

        if (id == R.id.radioMale)
            mSex = true;
        if (id == R.id.radioFemale)
            mSex = false;

		/*
		rGroup.getChildAt(0).setBackgroundDrawable(btn);
		rGroup.getChildAt(1).setBackgroundDrawable(btn);
		*/
		RadioButton rb = (RadioButton) findViewById(id);
		/*
		if (rGroup == rGroupExc){
			mExercise = rGroup.indexOfChild(rb);
			rGroup.getChildAt(2).setBackgroundColor(Color.WHITE);
			rGroup.getChildAt(3).setBackgroundColor(Color.WHITE);
		}*/
		if (rGroupSex == rGroup){						
			//mSex = (String) rb.getText();
		}
			Drawable gold = getResources().getDrawable(R.layout.gold_gradient);
            //Drawable btn_default = getResources().getDrawable(android.R.drawable.btn_radio);
                    //btn_dialog);
                    //style.Holo_Light_ButtonBar);
                    //drawable.btn_plus);
                    //btn_default);
			//rb.setBackground(btn_default);
			rb.setBackgroundDrawable(btn);
		//}

        displayValues();
		CountPublish();
	}
	
	// Spinner select
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// TODO Auto-generated method stub
		/*
		switch (view.getId()) {
		case R.id.ddownKapital:*/
			switch(pos){
			case 0:
				mExercise = 0;
				//compoundsPerYear = 365;
				break;
			case 1:
				mExercise = 1;
				//compoundsPerYear = 12;
				break;
			case 2:
				mExercise = 2;
				//compoundsPerYear = 1;
				break;				
			case 3:
				mExercise = 3;
				//compoundsPerYear = 1;
				break;								
			case 4:
				mExercise = 4;
				//compoundsPerYear = 1;
				break;					
			}			
			CountPublish();
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		mExercise = 0;
		CountPublish();
	}
	
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
	
	int GetColorCal(double cal) {
		float[] hsv = new float[3];
		int color = Color.GRAY;
		Color.colorToHSV(color, hsv);
		//hsv[parameter] = hsv[parameter]/factor;
		//hsv[0] = (float)(-bmi/10 + 7.5)/92*360+360;
		//hsv[0] = (float)(300/((bmi-0.5)/7)-23);
		hsv[0] = (float)(360-cal*12);
		hsv[1] = 1;
		hsv[2] = 0.75f;
		int newCol = Color.HSVToColor(hsv);
		return newCol;
	}
	
	int ModifyColor(int color, int parameter, float factor) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[parameter] = hsv[parameter]/factor;
		int newCol = Color.HSVToColor(hsv);
		return newCol;
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

	com.google.android.gms.ads.AdListener getAdMobBannerListener = new com.google.android.gms.ads.AdListener(){


		@Override
		public void onAdFailedToLoad(int errorCode){
			toastMsg("AdMob banner load error: "+errorCode, 3);


		}

		@Override
		public void onAdLoaded(){
			toastMsg("AdMob banner loaded", 3);
		}

		/*
		//@Override
		public void onError(Ad ad, AdError adError) {
			toastMsg("AdMob inter load error: "+adError.getErrorMessage(), 3);

		}

		//@Override
		public void onAdLoaded(Ad ad) {
			toastMsg("AdMob inter loaded", 3);

		}

		//@Override
		public void onAdClicked(Ad ad) {

		}
		*/
	};

	public void toastMsg(String msg, int level){

		//String msg = context.getResources().getString(stringId);

			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
}