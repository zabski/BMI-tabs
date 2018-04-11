package com.z_mobile.bmi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class ThemeUtils
{
	private static int sTheme;

	public final static int THEME_DEFAULT = 0;
	public final static int THEME_WHITE = 1;
	public final static int THEME_BLUE = 2;

	/**
	 * Set the theme of the Activity, and restart it by creating a new Activity
	 * of the same type.
	 */
	public static void changeToTheme(Activity activity, int theme)
	{
		sTheme = theme;
		activity.finish();

		activity.startActivity(new Intent(activity, activity.getClass()));
	}

	/** Set the theme of the activity, according to the configuration. */
	public static void onActivityCreateSetTheme(Activity activity)
	{
		switch (sTheme)
		{
        default:
		case 0:
			activity.setTheme(R.style.ThemeLight);
			break;
		case 1:
			activity.setTheme(R.style.ThemeDark);
			break;
		/*
        case 0:
            activity.setTheme(R.style.ThemeGreen);
            break;
		case 3:
			activity.setTheme(android.R.style.Theme_Black);
			break;
				*/
		case 4:
			activity.setTheme(android.R.style.Theme_Dialog);
			break;
		case 5:
			activity.setTheme(android.R.style.Theme_DeviceDefault);
			break;			
		}
	}
	
	public static int getTheme(){
		return sTheme;
	}

	public static int getThemeId(){

		switch (sTheme) {
			default:
			case 0:
				return R.style.ThemeLight;
			//break;
			case 1:
				return R.style.ThemeDark;
			//break;
			case 55:
				return R.style.ThemeGreen;
			//break;
			case 3:
		}
		return sTheme;
	}
	
	public static void setTheme(int theme){
		sTheme = theme;
	}
	
	public static void saveTheme(SharedPreferences settings){
		//SharedPreferences settings = getApplicationContext().getSharedPreferences("Settings", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("theme", ThemeUtils.getTheme());
		editor.commit();
	}
}
