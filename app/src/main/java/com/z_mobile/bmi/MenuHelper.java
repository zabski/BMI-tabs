package com.z_mobile.bmi;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;

class MenuHelper {
	
	DialogTheme dialogSymbol;
	//private FeedbackDialog feedBack;
		
	public MenuHelper(Context ctx) {
		// TODO Auto-generated constructor stub	
	
		dialogSymbol = new DialogTheme(ctx);
		//FeedbackSettings feedbackSettings = new FeedbackSettings();

		String opinion = ctx.getString(R.string.opinion);
		//feedbackSettings.setText(feedbackPrompt);
		//feedbackSettings.setIdeaLabel(opinion);
		//feedBack = new FeedbackDialog((Activity) ctx, "AF-24699C8C4270-A3", feedbackSettings); 
	}
	

	   public boolean handleOnItemSelected(Context ctx, MenuItem item) {
	          // do something..

		   Intent i;
		   String feedbackPrompt = ctx.getString(R.string.feedback_prompt);
		   int id = item.getItemId();
	        //switch (item.getItemId()) {
            //case A.action_theme:

		   if (id == R.id.action_theme) {
			   //dialogSymbol.show();
			   dialogSymbol.setAct((Updatable) ctx);
			   return true;
		   }else if (id == R.id.action_rate_app) {
			   //case A.action_rate_app:
			   Uri uri = Uri.parse("market://details?id=" + ctx.getPackageName());
			   Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
			   try {
				   ctx.startActivity(goToMarket);
			   } catch (ActivityNotFoundException e) {
				   ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + ctx.getPackageName())));
			   }
			   return true;
		   }else if (id == R.id.action_feedback) {

			   //case A.action_feedback:
			   //feedBack.show();
			   final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

			   emailIntent.setType("plain/text");

			   emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					   new String[]{"zmobapp@gmail.com"});

			   emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ctx.getString(R.string.app_name));

			   String prompt = ctx.getString(R.string.feedback_prompt) + "\n\n";
			   emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, prompt);

			   ctx.startActivity(Intent.createChooser(
					   emailIntent, feedbackPrompt));
			   return true;
		   }else{
	        //default:
	          //return super.onOptionsItemSelected(item);
	          return false;
	        }

	   }


	}