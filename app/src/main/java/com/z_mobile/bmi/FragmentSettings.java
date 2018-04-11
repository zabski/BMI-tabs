package com.z_mobile.bmi;

//import com.zabski.loancalc.R;

//import com.zabski.loancalc.R;

//import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
		import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
		import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

		import java.util.Calendar;
import java.util.Locale;

//import com.google.ads.*;
//import com.ironsource.mobilcore.MobileCore;
//import com.zmobile.foodtest.Registry.FavoriteRequest;

//import fatsecret.platform.FatSecretAPI;
//import fatsecret.platform.FatSecretException;


public class FragmentSettings extends Fragment implements OnClickListener,
		OnSeekBarChangeListener, OnCheckedChangeListener, OnItemSelectedListener {

	private static FragmentSettings instance = null;

	ActivityMain act;
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


    int minAge = 10;
	int maxAge = 60;
	int minHgt = 130;
	int maxHgt = 210;
	int minWgt = 30;	
	int maxWgt = 180;
	int minWgtLbs = 66;
	int maxWgtLbs = 500;

	int mAge;
	int mHeight;
	double mWeightKg;
	final double LbsRatio = 0.45359237;
	int mLbs;
	boolean mSex = false;//"Female";
	int mExercise = 0;
	double mBMR;
	double mBMI;

	int mFeet;
	int mInch;
	//int mWeight = 60;
	String mAuthToken = "";

	int clBlue = Color.rgb(100, 100, 255);
	int clGreen = Color.rgb(0, 200, 0);
	int clOrange = Color.rgb(255, 200, 0);
	int clRed = Color.rgb(255, 32, 32);
	boolean metric = true;

	static FragmentSettings getInstance(){
		if (instance == null){
			instance = new FragmentSettings();
			//return new FragmentSettings();
		}
		return instance;
	}

	static void dispose(){
		instance = null;
	}
	
	@Override
	public void onStart() {
	    super.onStart();
	    // The rest of your onStart() code.
	    //EasyTracker.getInstance(this).activityStart(this);  // Add this method.
		act.today = Calendar.getInstance().getTimeInMillis()/(1000*60*60*24);
	}

	@Override
	public void onStop() {
	    super.onStop();
	    // The rest of your onStop() code.
	    //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        //adView.pause();
        super.onPause();
        ThemeUtils.saveTheme(act.settings);
		act.saveData();
    }


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		// Inflate the Fragment, but don't attach it to the main
		// activity LinearLayout because attachToRoot is marked
		// as false

		// create ContextThemeWrapper from the original Activity Context with the custom theme
		//final Context contextThemeWrapper = new ContextThemeWrapper(getActivity().getApplicationContext(), ThemeUtils.getThemeId());//R.style.ThemeGreen);//
		final Context contextThemeWrapper = new ContextThemeWrapper(getActivity().getApplicationContext(), R.style.AppTheme);

		// clone the inflater using the ContextThemeWrapper
		LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

		//View view = inflater.inflate(R.layout.activity_settings, container, false);
		View view = localInflater.inflate(R.layout.activity_settings, container, false);

		act = (ActivityMain) getActivity();
		mAge = act.mAge;
		mHeight = act.mHeight;
		mWeightKg = act.mWeightKg;

		mLbs = act.mLbs;
		mSex = act.mSex;
		mExercise = act.mExercise;

		btSave = (Button) view.findViewById(R.id.btSave);
		btExit = (Button) view.findViewById(R.id.btExit);
		btChart = (Button) view.findViewById(R.id.btChart);
		btAgeMin = (ImageButton) view.findViewById(R.id.btAgeMin);
		btAgePlus = (ImageButton) view.findViewById(R.id.btAgePlus);
		btHgtMin = (ImageButton) view.findViewById(R.id.btHgtMin);
		btHgtPlus = (ImageButton) view.findViewById(R.id.btHgtPlus);
		btWgtMin = (ImageButton) view.findViewById(R.id.btWgtMin);
		btWgtPlus = (ImageButton) view.findViewById(R.id.btWgtPlus);
		//tbMetric = (Button) findViewById(R.id.tbMetric);
		tvYears = (TextView) view.findViewById(R.id.tvYears);
		tvFeet = (TextView) view.findViewById(R.id.tvFeet);
		tvBMR = (TextView) view.findViewById(R.id.tvBMR);
		tvBMI = (TextView) view.findViewById(R.id.tvBMI);
		tvBMIdesc = (TextView) view.findViewById(R.id.tvBMIdesc);
		tvMeters = (TextView) view.findViewById(R.id.tvCm);
		tvKilos = (TextView) view.findViewById(R.id.tvKgs);
		tvLbs = (TextView) view.findViewById(R.id.tvLbs);
		tvLang = (TextView) view.findViewById(R.id.tvLang);

		seekAge = (SeekBar) view.findViewById(R.id.seekAge); // make seekbar object
		seekAge.setOnSeekBarChangeListener(this); // set seekbar listener.
		seekAge.setMax(maxAge-minAge);
		seekHeight = (SeekBar) view.findViewById(R.id.seekHeight); // make seekbar object
		seekHeight.setMax(maxHgt-minHgt);
		seekHeight.setOnSeekBarChangeListener(this); // set seekbar listener.
		//seekWeight = (SeekBar)findViewById(R.id.seekWeight); // make seekbar object
		//seekWeight.setOnSeekBarChangeListener(this); // set seekbar listener.
		//seekWeight.setMax(maxWgt-minWgt);
		seekWeightLbs = (SeekBar) view.findViewById(R.id.seekWeightLbs); // make seekbar object
		seekWeightLbs.setMax(maxWgtLbs-minWgtLbs);
		seekWeightLbs.setOnSeekBarChangeListener(this); // set seekbar listener.


		btSave.setOnClickListener(this);
		btExit.setOnClickListener(this);
		btChart.setOnClickListener(this);
		btAgeMin.setOnClickListener(this);
		btAgePlus.setOnClickListener(this);
		btHgtMin.setOnClickListener(this);
		btHgtPlus.setOnClickListener(this);
		btWgtMin.setOnClickListener(this);
		btWgtPlus.setOnClickListener(this);
		rGroupSex = (RadioGroup) view.findViewById(R.id.radioGroupSex);
		rGroupSex.setOnCheckedChangeListener(this);
		rbMale = (RadioButton) view.findViewById(R.id.radioMale);
		rbFemale = (RadioButton) view.findViewById(R.id.radioFemale);
		//rGroupExc = (RadioGroup) findViewById(R.id.radioGroup2);
		//rGroupExc.setOnCheckedChangeListener(this);

		spinExe = (Spinner) view.findViewById(R.id.spinExcercise);
		// Create an ArrayAdapter using the string array and a default spinner layout
		//ArrayAdapter<CharSequence> adapterExc = ArrayAdapter.createFromResource(this,
		//        R.array.exercise, android.R.layout.simple_spinner_item);
		ArrayAdapter<CharSequence> adapterExc = ArrayAdapter.createFromResource(act,
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

		TextView privacyText = (TextView) view.findViewById(R.id.privacyPolicy);
		privacyText.setClickable(true);
		//privacyText.setMovementMethod(LinkMovementMethod.getInstance());
		//String text = "<a href='https://docs.google.com/document/d/1KU_5kCrjkvKUBFU790Az9KCPuqTEUPAc9J739mziMi8'> Privacy Policy </a>";
		//privacyText.setText(Html.fromHtml(text));
		privacyText.setOnClickListener(this);

		mAuthToken = act.settings.getString("token", "");
		String displang = Locale.getDefault().getDisplayLanguage();
		String lang = Locale.getDefault().getLanguage();
		tvLang.setText("Languages: "+displang+" : "+lang);
		displayValues();
		CountPublish();

		return view;
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
		//mWeightKg = act.nutrInfo.lbsToKg(mLbs);
		double bmi = (double)mWeightKg/ Math.pow((double) mHeight / 100, 2);
		int color = GetColor(bmi);
		String comment = act.nutrInfo.getBMIcomment(bmi);

		int colors[] = new int[2];
		colors[1] = color;
		colors[0] = ModifyColor(color,1,2);
		GradientDrawable backColor = new GradientDrawable(Orientation.TOP_BOTTOM, colors);
		tvBMIdesc.setBackgroundDrawable(backColor);
		//tvBMI.setBackgroundColor(color);
		int id = getResources().getIdentifier(comment, "string", act.getPackageName());
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

/*
	void SaveData(){
		SharedPreferences.Editor editor = act.settings.edit();
		editor.putInt("age", mAge);
		editor.putInt("height", mHeight);
		//editor.putInt("weight", mWeight);
		editor.putInt("lbs", mLbs);
		editor.putBoolean("male", mSex);
		editor.putInt("exercise", mExercise);
		editor.putInt("kcal", (int) Math.round(mBMR));
		// Commit the edits!
		editor.commit();
		//double weightKg = act.nutrInfo.lbsToKg(mLbs);
		try{
			//diary.updateWeight(weightKg, mHeight, weightKg-5, false);
		}catch(Exception e){
			String no_internet = "Turn on internet connection";
			Toast.makeText(act, no_internet, Toast.LENGTH_SHORT).show();

		}

		ListItem entry = new ListItem(mLbs, act.today);
		act.diary.addDiary(entry);
		String data_saved = getString(R.string.data_saved);
		Toast.makeText(act, data_saved, Toast.LENGTH_SHORT).show();
	}
	*/

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
			//SharedPreferences settings = getApplicationContext().getSharedPreferences("Settings", 0);
			SharedPreferences.Editor editor = act.settings.edit();
			editor.putInt("age", mAge);
			editor.putInt("height", mHeight);
			//editor.putInt("weight", mWeight);
			editor.putInt("lbs", mLbs);
			editor.putBoolean("male", mSex);
			editor.putInt("exercise", mExercise);
			editor.putInt("kcal", (int) Math.round(mBMR));
			// Commit the edits!
			editor.commit();
			//double weightKg = act.nutrInfo.lbsToKg(mLbs);
			try{
				//diary.updateWeight(weightKg, mHeight, weightKg-5, false);
			}catch(Exception e){
				String no_internet = "Turn on internet connection";
				Toast.makeText(act, no_internet, Toast.LENGTH_SHORT).show();

			}
            long today = Calendar.getInstance().getTimeInMillis()/(1000*60*60*24);
            ListItem entry = new ListItem(mLbs, today);
			act.diary.addDiary(entry);
            String data_saved = getString(R.string.data_saved);
            Toast.makeText(act, data_saved, Toast.LENGTH_SHORT).show();
			//finish();
		} else if (id == (R.id.btExit)) {
            //Intent intent = new Intent(this, GraphActivity.class);
            //startActivity(intent);
			act.finish();
		} else if (id == (R.id.btChart)) {
            //Intent intent = new Intent(this, GraphActivity.class);
            //startActivity(intent);

        }else if (id == R.id.privacyPolicy){
			String url = "https://docs.google.com/document/d/1KU_5kCrjkvKUBFU790Az9KCPuqTEUPAc9J739mziMi8";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
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
			//ListItem entry = new ListItem(mLbs, act.today);
			//act.diary.addDiary(entry);
			act.copy(mLbs, mHeight, mAge, mSex, mExercise, mBMI, mBMR);
			act.saveData();
		}
		/*
		mAge = seekAge.getProgress()+minAge;
		mHeight = seekHeight.getProgress()+minHgt;
		mWeight = seekWeight.getProgress()+minWgt;
		*/
		
		int feet = (int) (mHeight/(12*2.54));
		int inches = (int)(Math.round(mHeight % (feet * 12 * 2.54))/2.54);
		//int lbs = (int) Math.round(mWeightKg / 0.45359237);
		String years = getResources().getString(R.string.years);
		tvYears.setText(mAge+" "+years);
		String strFeet = " "+getString(R.string.feet)+" ";
		String strInches = " "+getString(R.string.inches)+" ";
		tvMeters.setText(mHeight+" cm = "+feet+strFeet+inches+strInches);
		//tvFeet.setText(feet+" feet "+inches+" inches");		
		//tvKilos.setText(mWeight+" kg = "+lbs+" lbs");
		String strKg = " "+getString(R.string.kg)+" = ";
		String strLb = " "+getString(R.string.lb)+" ";
		tvKilos.setText(String.format("%.1f", mWeightKg)+strKg+mLbs+strLb);
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
		RadioButton rb = (RadioButton) rGroup.findViewById(id);
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

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		menuHelp.handleOnItemSelected(this, item);
		return true;

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

    */
}