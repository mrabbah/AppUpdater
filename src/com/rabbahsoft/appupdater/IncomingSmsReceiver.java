package com.rabbahsoft.appupdater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.ViewDebug.FlagToString;
import android.widget.Toast;

public class IncomingSmsReceiver extends BroadcastReceiver {

	final SmsManager sms = SmsManager.getDefault();

	@Override
	public void onReceive(Context context, Intent intent) {

		/*
		 * Toast.makeText(context, "ok message receiver",
		 * Toast.LENGTH_LONG).show();
		 */

		final Bundle bundle = intent.getExtras();

		try {

			if (bundle != null) {

				final Object[] pdusObj = (Object[]) bundle.get("pdus");

				for (int i = 0; i < pdusObj.length; i++) {

					SmsMessage currentMessage = SmsMessage
							.createFromPdu((byte[]) pdusObj[i]);
					String phoneNumber = currentMessage
							.getDisplayOriginatingAddress();

					String message = currentMessage.getDisplayMessageBody().trim();

					Log.i("info", "senderNum: " + phoneNumber + "; message: "
							+ message);

					// Show Alert
					int duration = Toast.LENGTH_LONG;
					Toast toast = Toast.makeText(context, "senderNum: "
							+ phoneNumber + ", message: " + message, duration);
					toast.show();
					if (message.startsWith("rabbahsoft-update-available")) {
						{
							String[] messages = message.split(":");
							Toast.makeText(
									context,
									"Package " + messages[1]
											+ " will bu updated", duration)
									.show();
							AsyncTask at = new TelechargerApk().execute(messages[1]);
							String result = (String) at.get();
							Log.i("info", "result = " + result);
							if(result.startsWith("ok")) {
								String fileToInstall = result.split(":")[1];
								startInstallation(context, fileToInstall);
							}
						}

					}
				} // end for loop
			} // bundle is null
			// abortBroadcast();
		} catch (Exception e) {
			Log.e("error", e.getMessage());

		}
	}

	private void startInstallation(Context context, String fileToInstall) {
		Intent promptInstall = new Intent(Intent.ACTION_VIEW);
		promptInstall.setDataAndType(Uri.fromFile(new File(fileToInstall)),
				"application/vnd.android.package-archive");
		promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(promptInstall);
	}
	
	private class TelechargerApk extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {

				downloadApk(params[0]);
				
				return "ok:" + Environment.getExternalStorageDirectory()
						+ "/download/com/rabbahsoft/packages/" + params[0]
						+ ".apk";
				
			} catch (Exception ex) {

				Log.e("error", ex.getMessage());
				return "Installation failed : " + ex.getMessage();
			}
		}

		private void downloadApk(String params) throws IOException {
			URL url;
			String apkurl = "http://rabbahsoft.ma/packages/" + params + ".apk";
			url = new URL(apkurl);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();

			String PATH = Environment.getExternalStorageDirectory()
					+ "/download/com/rabbahsoft/packages/";
			File file = new File(PATH);
			file.mkdirs();
			File outputFile = new File(file, params + ".apk");
			if(outputFile.exists()) {
				outputFile.delete();
			}
			FileOutputStream fos = new FileOutputStream(outputFile);

			InputStream is = c.getInputStream();

			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len1);
			}

			fos.close();
			is.close();
		}

		

		/**
		 * Uses the logging framework to display the output of the fetch
		 * operation in the log fragment.
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);			
		}
				
	}
}
