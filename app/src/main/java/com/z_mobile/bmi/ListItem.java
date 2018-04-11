package com.z_mobile.bmi;

import java.io.Serializable;

public class ListItem implements Serializable {

	private static int idCount;	

	int mId;
	int mAmount;
	double mPercent;
	int mTypeId;
	int mAmountId;
	String mLabel;
	String signature;
	long time;
	
	public ListItem(int mAmount, long time) {
		super();
		this.mAmount = mAmount;
		/*
		this.mPercent = mPercent;
		this.mTypeId = mTypeId;
		this.mAmountId = mAmountId;
		this.mLabel = mLabel;
		*/
        this.time = time;
		this.mId = idCount++;
		//this.signature = calcSignature(mTypeId, mAmountId, mPercent);
	}
	
	String calcSignature(int type, int amount, double percent){
		String res = "";
		int percentInt = (int)(percent*100);
		res = String.format("%d-%d-%d", type, amount, percentInt);
		return res;
	}
	
	String getSignature(){
		return signature;
	}
	
	String generateSignature(){
		String res = "";
		int percentInt = (int)(mPercent*100);
		res = String.format("%d-%d-%d", mTypeId, mAmountId, percentInt);
		this.signature = res;
		return res;
	}
	
	public double getPureAlcMl(){
		double res = mAmount*mPercent/100;
		return res;
	}
	
	public double getStandardDrinks(){
		double alcMl = mAmount*mPercent/100;
		double res = alcMl/12.7;
		return res;
	}	
	
	public double getflozUS(){
		double floz = (double)mAmount/28.4125;
		return floz;
	}
	
	public double getflozUK(){
		double floz = (double)mAmount/29.5735;
		return floz;
	}
	
	String getflozUSstr(){
		double floz = getflozUS();
		String res = String.format("%.1f fl oz", floz);
		return res;
	}
	
	String getflozUKstr(){
		double floz = getflozUK();
		String res = String.format("%.1f fl oz", floz);
		return res;
	}
	
	String getMlStr(){
		String res = String.format("%d ml", mAmount);
		return res;
	}	
	
	String getVolumeStr(int unitId){
		String res = "";
		switch(unitId){
			case 0:
				res = getMlStr();
				break;
			case 1:
				res = getflozUSstr();
				break;
			case 2:
				res = getflozUKstr();
				break;
		}
		return res;
	}		

	public int getId() {
		return mId;
	}

	public void setmId(int mId) {
		this.mId = mId;
	}

	public int getmAmount() {
		return mAmount;
	}

	public void setmAmount(int mAmount) {
		this.mAmount = mAmount;
	}

	public double getmPercent() {
		return mPercent;
	}

	public void setmPercent(int mPercent) {
		this.mPercent = mPercent;
	}

	public int getmTypeId() {
		return mTypeId;
	}

	public void setmTypeId(int mTypeId) {
		this.mTypeId = mTypeId;
	}

	public int getmAmountId() {
		return mAmountId;
	}

	public void setmAmountId(int mAmountId) {
		this.mAmountId = mAmountId;
	}
	
	public long getTime() {
		return time;
	}	

	public void setTime(long time) {
		this.time = time;
	}	
	
	public String getmLabel() {
		return mLabel;
	}

	public void setmLabel(String mLabel) {
		this.mLabel = mLabel;
	}
	
}

