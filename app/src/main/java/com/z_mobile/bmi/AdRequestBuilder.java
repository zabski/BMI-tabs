package com.z_mobile.bmi;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;

import java.util.HashSet;
import java.util.Set;

public class AdRequestBuilder {
	AdRequest adRequest;
	
	String[] inputWords = {"contraception", "pregnancy", "fertility", "baby", "fashion", "diet", "fitness",
			"health", "weight loss", "love","relationship", "shopping", "shoes", "nutrition", "food", "exercise"};
	
	private static AdRequestBuilder instance = null;
	
	public static AdRequestBuilder getInstance(){
	      if(instance == null) {
	    	  instance = new AdRequestBuilder();
	      }		    	  		      
	      return instance;
	}
	
	private AdRequestBuilder(){
		Builder adBuilder = new Builder();
        adBuilder.setGender(AdRequest.GENDER_FEMALE);                
        
        // ----------------
        Set<String> keywords = new HashSet<String>();
		String key = "key";
		String suffix = "";
		int i = 1;
		for(String keyword : inputWords){
			
			suffix = key + i;
			//MMrequest.put(keyword, keyword);
			i++;
			keywords.add(keyword);
			adBuilder.addKeyword(keyword);
		}
		adBuilder.addTestDevice("D9OKCY134460");
		adBuilder.addTestDevice("R28F70CAAPY");
		adRequest = adBuilder.build();
	}

	AdRequest build(){
		return adRequest;
	}

}
