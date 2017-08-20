package com.rabbahsoft.appupdater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PackageChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		try {
			String action = intent.getAction();
			String packagename = intent.getData().getSchemeSpecificPart();
			
			if(packagename.startsWith("com.rabbahsoft.appslocker")) {
				
				TelephonyManager tMgr = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				String phoneId = tMgr.getDeviceId();
				SmsManager smsManager = SmsManager.getDefault();
				
				if(action.equals("android.intent.action.PACKAGE_ADDED")) {
					smsManager.sendTextMessage("0668858905", null, "Téléphone " + phoneId + " a installé l'application " + packagename, null, null);
				} else if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
					smsManager.sendTextMessage("0668858905", null, "Téléphone " + phoneId + " a désinstallé l'applicatioin " + packagename, null, null);
				} else if (action.equals("android.intent.action.PACKAGE_REPLACED")) {
					smsManager.sendTextMessage("0668858905", null, "Téléphone " + phoneId + " a remplacé l'application " + packagename, null, null);
				} else if (action.equals("android.intent.action.PACKAGE_RESTARTED")) {
					Toast.makeText(context, "Merci de l'aisser les applications métiers sans modification",
				            Toast.LENGTH_LONG).show();
					smsManager.sendTextMessage("0668858905", null, "Téléphone " + phoneId + " a forcé l'arret application :" + packagename, null, null);
				} else if (action.equals("android.intent.action.PACKAGE_CHANGED")) {
					Toast.makeText(context, "Merci de l'aisser les applications métiers sans modification",
				            Toast.LENGTH_SHORT).show();
					smsManager.sendTextMessage("0668858905", null, "Téléphone " + phoneId + " a activé/désactivé " + packagename, null, null);
				} else if (action.equals("android.intent.action.PACKAGE_REPLACED")) {				
					smsManager.sendTextMessage("0668858905", null, "Téléphone " + phoneId + " a remplacé l'application : " + packagename, null, null);
				}
				
			} 
			
		} catch(Exception ex) {
			Log.e("error", ex.getMessage());
		}
		

	}

}
