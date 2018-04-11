package com.z_mobile.bmi;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

//import com.zmobile.foodtest.FoodInfoActivity.GetFoodRequest;

/*
import fatsecret.platform.FatSecretAPI;
import fatsecret.platform.FatSecretAuth;
import fatsecret.platform.FatSecretException;
*/
public class Registry {

    Map<String, String> parameters;

    Context ctx;

    String consumerKey;
    String consumerSecret;

    //FatSecretAPI fatSecretApi;
    //FatSecretAuth fatSecretAuth;
    //Profile profile;

    String mFoodId;
    boolean sexMale;
    double weightKg;
    double legalAlc;
    int Lbs;
    final double LbsRatio = 0.45359237;

    //String favFileName = "weightDiary.list";
    String diaryFileName = "diaryItems.list";
    //String historyFileName = "history.list";

    //Map<Integer,FavoriteDrink> favList = new HashMap<Integer,FavoriteDrink>();

    //ArrayList<ListItem> favList = new ArrayList<ListItem>();
    ArrayList<ListItem> diaryList = new ArrayList<ListItem>();
    //ArrayList<ListItem> historyList = new ArrayList<ListItem>();

    public Registry(Context ctx) {

        this(ctx, true, 150, 0.08);
    }

    public Registry(Context ctx, boolean sexMale, int lbs, double legAlc) {
        this.ctx = ctx;
        this.sexMale = sexMale;
        this.Lbs = lbs;
        this.legalAlc = legAlc;
        this.weightKg = lbsToKg(lbs);

        getFullList();
        getDiaryList();

        //consumerKey = ctx.getString(R.string.consumerKey);
        //consumerSecret = ctx.getString(R.string.consumerSecret);
		
		/*
		 fatSecretApi = new FatSecretAPI(consumerKey,consumerSecret);
		 
		
		fatSecretApi = api;
		
		profile = new Profile(fatSecretApi);
		
		fatSecretAuth = loadAuth();		
		
		if (fatSecretAuth == null){
			try{
				profile.create();
				//fatSecretAuth = fatSecretApi.ProfileCreate();
				//Thread.sleep(10000);
				synchronized(profile){
					//Thread.currentThread().wait();
					//this.wait();
					profile.wait(5000);
				}
				fatSecretAuth = profile.getAuth();
				saveAuth(fatSecretAuth);				
				
			}catch(Exception e){
				String err = e.toString();
				int a = 1;
				
			}
		}*/
    }

	/*
	public Registry(FatSecretAPI api, FatSecretAuth auth)
	{	
		this.fatSecretApi = api;
		this.fatSecretAuth = auth;					
	}
	*/

    private ArrayList<ListItem> loadListFromFile(String filename, ArrayList<ListItem> list) {

        ArrayList<ListItem> masterlistrev = new ArrayList<ListItem>();
        try {
            FileInputStream fis = new FileInputStream(ctx.getFilesDir().getPath().toString() + "/" + filename);
            //openFileInput(serfilename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            masterlistrev = (ArrayList<ListItem>) ois.readObject();
        } catch (Exception e) {
            String err = e.toString();
            e.printStackTrace();
        }
        list = masterlistrev;
        return masterlistrev;
    }

    private void writeListToFile(ArrayList<ListItem> masterlistrev, String filename) {

        File myfile = new File(ctx.getFilesDir().getPath().toString() + "/" + filename);
        //getFileStreamPath(serfilename);
        try {
            if (myfile.exists() || myfile.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(myfile);//, Context.MODE_PRIVATE);
                //openFileOutput(serfilename, MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(masterlistrev);
            }
        } catch (Exception e) {
            String err = e.toString();
            e.printStackTrace();
        }
    }

    public List<ListItem> getFullList() {

        if (diaryList == null || diaryList.size() == 0)
            diaryList = loadListFromFile(diaryFileName, diaryList);
        return diaryList;
    }

    public List<ListItem> getDiaryList() {

        if (diaryList == null || diaryList.size() == 0)
            //diaryList = (ArrayList<DiaryItem>)
            diaryList = loadListFromFile(diaryFileName, diaryList);
        return diaryList;
    }

    public List<ListItem> getHistoryList() {

        if (diaryList == null || diaryList.size() == 0)
            //diaryList = (ArrayList<DiaryItem>)
            diaryList = loadListFromFile(diaryFileName, diaryList);
        return diaryList;
    }

    public List<ListItem> getHistoryforDates(long begin, long finish) {
        int start = -1;
        int end = -1;//diaryList.size();
        int id = 1;
        for (ListItem item : diaryList) {
            long time = item.getTime();
            id = diaryList.indexOf(item);
            if (start == -1 && time >= begin)
                start = id;
            if (time <= finish)
                end = id;
            //id++;
        }
        List<ListItem> subList = new ArrayList<ListItem>();
        if (start >= 0 && end >= 0) {
            //subList = diaryList.subList(start+1, end+1);
            subList = diaryList.subList(start, end+1);
        }
        return subList;
    }

    public void clearDiary() {
        getHistoryList();
        //historyList.addAll(diaryList);
        //saveHistory();
        diaryList.clear();
        saveDiary();
    }

    public void clearFavorites() {
        diaryList.clear();
        saveFavorites();
    }

    private void saveFavorites() {

        writeListToFile(diaryList, diaryFileName);

    }

    public void saveDiary() {

        writeListToFile(diaryList, diaryFileName);

    }

    private void saveHistory() {

        writeListToFile(diaryList, diaryFileName);

    }

    public void addFavorite(ListItem drink) {

        diaryList.add(drink);
        saveFavorites();

    }

    public void addDiary(ListItem drink) {
        List<ListItem> toDelete = new ArrayList<ListItem>();
        for(ListItem item: diaryList){
            if (item.getTime() == drink.getTime()) {
                toDelete.add(item);
            }
        }
        diaryList.removeAll(toDelete);
        diaryList.add(drink);
        Collections.sort(diaryList, new ItemComparator());
        //saveDiary();

    }

    public boolean delFavorite(String favor_sign) {

        for (ListItem drink : diaryList) {
			/*
			int id = drink.getId();
			if (id == favor_id){
				favList.remove(drink);
				saveFavorites();
				return true;
			}		*/
            String rowSign = drink.getSignature();
            if (rowSign == null || rowSign.equals(""))
                rowSign = drink.generateSignature();
            if (favor_sign.equals(rowSign)) {
                diaryList.remove(drink);
                saveFavorites();
                return true;
            }
        }
        return false;
    }

    public boolean delDiaryItem(String diary_sign) {

        for (ListItem drink : diaryList) {
            int id = drink.getId();
            String rowSign = drink.getSignature();
            if (rowSign == null || rowSign.equals(""))
                rowSign = drink.generateSignature();
            if (diary_sign.equals(rowSign)) {
                diaryList.remove(drink);
                saveDiary();
                return true;
            }
        }
        return false;
    }

    public long getLastDrinkTime() {
        long lastTime = 0;
        for (ListItem drink : diaryList) {
            if (drink.getTime() > lastTime)
                lastTime = drink.getTime();
        }
        return lastTime;
    }
	/*
	public ListItem getDrinkForId(String strId, String label, PersonInfo personInfo){
		
        //List<FavoriteDrink> favList = diary.getFullList();
        
        String[] vals = strId.split("-");
        int type = personInfo.strToInt(vals[0], 0);
        int amount = personInfo.strToInt(vals[1], 0);
        int percentInt = personInfo.strToInt(vals[2], 500);
        double percent = ((double)percentInt)/100;//personInfo.strToDbl(vals[2], 5);        
        
        ListItem drink = new ListItem(0, percent, type, amount, label, 0);
        
        return drink;	
	}*/

    public ListItem findFavorite(String sign, String label) {
        for (ListItem drink : diaryList) {
            if (sign.equals(drink.getSignature()) && label.equals(drink.getmLabel())) {
                return drink;
            }
        }
        return null;
    }

    public void addFoodDiary(String food_id, String serving_id, String entry_name) {


    }

    private double calcBAC(double Wt, double SD, double BW, double MR, double DP) {
        // Wt - weight in kg
        // SD - Standard Drink = 10g of pure alcohol
        // BW - body water constant (0.58 for men and 0.49 for women)
        // MR - metabolism constant (women 0.017, men 0.015)
        // DP - drinking period in hours
        double bac = 0;
        bac = 0.806 * SD * 1.2 / (BW * Wt) - (MR * DP);
        return Math.max(bac, 0);
    }

    public double calcTotalBacNow() {
        long nowSecs = Calendar.getInstance().getTimeInMillis() / (1000);
        return calcTotalBAC(nowSecs);
    }

    public double calcTotalBAC(long forTimeSecs) {
        double totalBAC = 0;
        //long now = Calendar.getInstance().getTimeInMillis()/(1000*60);
        double waterRatio = 0.58; // for males
        double MR = 0.015;
        if (sexMale == false) {
            waterRatio = 0.49;
            MR = 0.017;
        }
        long startTime = forTimeSecs;
        long lastTime = 0;

        for (ListItem drink : diaryList) {
            long time = drink.getTime();//("time");
            startTime = Math.min(startTime, time);
            lastTime = Math.max(lastTime, time);
            double amount = drink.getmAmount();//("ant");
            double percent = drink.getmPercent();//("perc");
            long drinkPeriodSecs = forTimeSecs - time;
            double drinkPeriodHours = (double) drinkPeriodSecs / (60 * 60);
            double standardDrinks = drink.getStandardDrinks();
            //amount*percent/1000;
            double bac = 0;
            double lastBAC = 0;
            if (forTimeSecs > time) {
                bac = calcBAC(waterRatio, standardDrinks, weightKg, MR, 0);
                lastBAC = calcBAC(waterRatio, standardDrinks, weightKg, MR, drinkPeriodHours);
            }
            totalBAC += bac;
        }
        long drinkPeriodSecs = forTimeSecs - startTime;
        double drinkPeriodHours = (double) drinkPeriodSecs / (60 * 60);
        totalBAC = totalBAC - (MR * drinkPeriodHours);
        return totalBAC;
    }

    public double calcLastBacNow() {
        long nowSecs = Calendar.getInstance().getTimeInMillis() / 1000;
        return calcLastBAC(nowSecs);
    }

    public double calcLastBAC(long forTimeSecs) {
        double totalBAC = 0;
        //long now = Calendar.getInstance().getTimeInMillis()/(1000*60);
        double waterRatio = 0.58; // for males
        double MR = 0.015;
        if (sexMale == false) {
            waterRatio = 0.49;
            MR = 0.017;
        }
        long startTime = Long.MAX_VALUE;
        long lastTime = 0;
        double bac = 0;
        double lastBAC = 0;

        for (ListItem drink : diaryList) {
            long time = drink.getTime();//("time");
            startTime = Math.min(startTime, time); // set drinking start time to time of first drink
            long startPeriodSecs = 0;
            if (time > startTime)                    //
                startPeriodSecs = time - startTime;
            double startPeriodHours = (double) startPeriodSecs / (60 * 60); //period from first to this drink
            double currentBAC = totalBAC - (MR * startPeriodHours); // currentBAC for this drinks time
            if (currentBAC <= 0) {    // if current BAC reached 0 reset start time to current drink and totaBAC to 0
                startTime = time;
                totalBAC = 0;
            }
			/*
			lastTime = Math.max(lastTime, time);
			long drinkPeriodSecs = forTimeSecs - time;
			double drinkPeriodHours = (double)drinkPeriodSecs/(60*60);
			*/
            double amount = drink.getmAmount();//("ant");
            double percent = drink.getmPercent();//("perc");
            double standardDrinks = amount * percent / 1000; // number of 10g alcohol units
            if (forTimeSecs > time) {                    // if drink time in past calulate BAC of this drink
                bac = calcBAC(waterRatio, standardDrinks, weightKg, MR, 0);
                //lastBAC = calcBAC(waterRatio, standardDrinks, weightKg, MR, drinkPeriodHours);
                totalBAC += bac;
            }
        }
        if (forTimeSecs > startTime) {
            long drinkPeriodSecs = forTimeSecs - startTime;
            double drinkPeriodHours = (double) drinkPeriodSecs / (60 * 60);
            totalBAC = totalBAC - (MR * drinkPeriodHours);
        } else {
            totalBAC = 0;
        }
        return Math.max(totalBAC, 0);
    }

    public double lbsToKg(int lbs) {

        double weightKg = (double) Math.round(lbs * LbsRatio * 2) / 2;
        return weightKg;
    }

    public class ItemComparator implements Comparator<ListItem> {
        @Override
        public int compare(ListItem item1, ListItem item2) {
            if (item1.getTime() < item2.getTime()) return -1;
            if (item1.getTime() > item2.getTime()) return 1;
            //if (item1.getTime() == item2.getTime())
            return 0;
        }

    }

}
