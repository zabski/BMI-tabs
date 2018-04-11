package com.z_mobile.bmi;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Pair;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

//import com.appjolt.winback.Winback;
//import com.appjolt.sdk.Appjolt;
//import com.google.analytics.tracking.android.EasyTracker;
import com.zmobile.foodendpoint.customerApi.model.OfyCustomer;


public class ActivityMain extends AppCompatActivity implements ActionBar.TabListener, Updatable {

    //ActionBarActivity

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    Registry diary;

    NutritionInfo nutrInfo;
    MenuHelper menuHelp;
    SharedPreferences settings;
    public final static long millisPerDay = 1000*60*60*24;
    int mAge = 30;
    int mHeight = 160;
    double mWeightKg;
    final double LbsRatio = 0.45359237;
    int mLbs;
    boolean mSex = false;//"Female";
    int mExercise = 0;
    double mBMR;
    double mBMI;
    int theme;
    long today;
    boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_main);

        TextView privacyText = (TextView) findViewById(R.id.privacyPolicy);
        privacyText.setClickable(true);
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='https://docs.google.com/document/d/1KU_5kCrjkvKUBFU790Az9KCPuqTEUPAc9J739mziMi8'> Privacy Policy </a>";
        privacyText.setText(Html.fromHtml(text));

        // Appjolt - Init SDK
        //Winback.init(this);
        // Appjolt - Show EULA only in Google Play Installs (and Debug mode)
        /*if (Winback.isGooglePlayInstall(this))
        {
            Winback.showEULA(this);
        }
        if (Appjolt.isGooglePlayInstall(this))
        {
            Appjolt.showEULA(this);
        }*/

        //ThemeUtils.onActivityCreateSetTheme(this);
        settings = getApplicationContext().getSharedPreferences("Settings", 0);

        String googleMail = "";
        boolean userSaved = settings.getBoolean("userDataSaved", false);
        if (!userSaved)
            googleMail = saveCustomerData();
        theme = settings.getInt("theme", 0);
        mSex = settings.getBoolean("male", false);
        int BMR = settings.getInt("kcal", 2000);
        mHeight = settings.getInt("height", 160);
        mSex = settings.getBoolean("male", false);
        nutrInfo = new NutritionInfo(this, mSex, BMR);

        //fatSecretApi = new FatSecretAPI(consumerKey,consumerSecret);

        mAge = settings.getInt("age", 30);
        mHeight = settings.getInt("height", 160);
        //mWeight = settings.getInt("weight", 50);
        mLbs = settings.getInt("lbs", 100);
        mWeightKg = nutrInfo.lbsToKg(mLbs);

        mExercise = settings.getInt("exercise", 1);
        diary = new Registry(this);
        ThemeUtils.setTheme(theme);
        ThemeUtils.onActivityCreateSetTheme(this);
        today = Calendar.getInstance().getTimeInMillis()/(1000*60*60*24);

        //MobileCore.getSlider().setContentViewWithSlider(this, R.layout.activity_settings, R.raw.slider_1);
        menuHelp = new MenuHelper(this);

        // Set up the action bar.

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    public boolean onOptionsItemSelected(MenuItem item) {

        menuHelp.handleOnItemSelected(this, item);
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        //((FragmentSettings)mSectionsPagerAdapter.getItem(0)).SaveData();
                //setPrimaryItem().saveData();

        saveData();
        if (!firstTime) {
            FragmentGraph fragment = FragmentGraph.getInstance();
                    //((FragmentGraph) mSectionsPagerAdapter.getItem(1));
            fragment.getMonth(today);
            //fragment.changeUnit();
            //fragment.changeUnit();
        }
        firstTime = false;

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment = null;
            switch (position) {
                case 0:
                    //fragment = new FragmentSettings();//
                    fragment = FragmentSettings.getInstance();
                    break;
                case 1:
                    //fragment = new FragmentGraph();//
                    fragment = FragmentGraph.getInstance();//
                    break;
                default:
                    fragment = PlaceholderFragment.newInstance(position + 1);
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.settings).toUpperCase(l);
                case 1:
                    return getString(R.string.charts).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_activity_main, container, false);
            return rootView;
        }
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
*/
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

    public void onBackPressed() {
        /*
        if (interstitial.isLoaded()) {
            //interstitial.show();
        }
        */
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        //adView.pause();
        super.onPause();
        ThemeUtils.saveTheme(settings);
        diary.saveDiary();
        FragmentGraph.getInstance().dispose();
        FragmentSettings.getInstance().dispose();
    }

    @Override
    public void onStart() {
        super.onStart();
        // The rest of your onStart() code.
        //EasyTracker.getInstance(this).activityStart(this);  // Add this method.
        /*
        if (adView!=null) {
            if (ads > 2 && !handlerIAP.mHasRemovedAds)
                adView.loadAd(adRequest);
            else {
                adView.setVisibility(View.GONE);
            }
        }
        */
    }

    @Override
    public void onStop() {
        super.onStop();
        // The rest of your onStop() code.
        //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

    void copy(int lbs, int height, int age, boolean sex, int exercise, double bmi, double bmr){
        this.mLbs = lbs;
        this.mHeight = height;
        this.mAge = age;
        this.mSex = sex;
        this.mExercise = exercise;
        this.mBMI = bmi;
        this.mBMR = bmr;
    }

    void saveData(){
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
        //double weightKg = act.nutrInfo.lbsToKg(mLbs);
        try{
            //diary.updateWeight(weightKg, mHeight, weightKg-5, false);
        }catch(Exception e){
            String no_internet = "Turn on internet connection";
            Toast.makeText(this, no_internet, Toast.LENGTH_SHORT).show();

        }

        ListItem entry = new ListItem(mLbs, today);
        diary.addDiary(entry);
        String data_saved = getString(R.string.data_saved);

    }

    String saveCustomerData() {

        long todayMillis = Calendar.getInstance().getTimeInMillis();

        long today = todayMillis / (1000 * 60 * 60 * 24);

        String possibleEmail = "";
        OfyCustomer customer = new OfyCustomer();
        PackageManager pm = getPackageManager();
        int hasPerm = pm.checkPermission(Manifest.permission.GET_ACCOUNTS, getPackageName());
        if (hasPerm < PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23)
                //ActivityCompat.
                        requestPermissions(new String[]{android.Manifest.permission.GET_ACCOUNTS}, 0);
        }
            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
            Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
            //.get(context).getAccounts();
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    possibleEmail = account.name;
                    possibleEmail = encodeEmail(possibleEmail);
                    customer.setAddr(possibleEmail);
                    customer.setAppName("BMI");
                    customer.setProducer(android.os.Build.MANUFACTURER);
                    customer.setModel(android.os.Build.MODEL);
                    customer.setDevice(android.os.Build.DEVICE);
                    customer.setCountry(Locale.getDefault().getCountry());
                    customer.setLang(Locale.getDefault().getLanguage());
                    customer.setDate(today);
                    new AddCustomerAsyncTask().execute(new Pair<Context, OfyCustomer>(this, customer));
                }
            }

        return possibleEmail;
    }

    String encodeEmail(String email){
        String encoded = "";
        String encoded2 = "";
        for(int i=0; i<email.length(); i++){
            char c = email.charAt(i);
            char p = ++c;
            c--;
            char m = --c;
            encoded += p;
            encoded2 += m;
        }
        return encoded;
    }

}
