package unimelb.mit.mobile.scavengerhunt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
 
 
public class RegisterApp extends AsyncTask<Void, Void, String> {
 
 private static final String TAG = "GCMRelated";
 Context ctx;
 GoogleCloudMessaging gcm;
 String SENDER_ID = "18260074068";
 String regid = null; 
 private int appVersion;
 
 public RegisterApp(Context ctx, GoogleCloudMessaging gcm, int appVersion){
  this.ctx = ctx;
  this.gcm = gcm;
  this.appVersion = appVersion;
  
  
 }
  
  
 @Override
 protected void onPreExecute() {
  super.onPreExecute();
 }
 
 
 @Override
 protected String doInBackground(Void... arg0) {
  String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(ctx);
            }
            regid = gcm.register(SENDER_ID);
            msg = "Device registered, registration ID=" + regid;
 
            //sendRegistrationIdToBackend();

            storeRegistrationId(ctx, regid);
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
        }
        return msg;
 }
 
 private void storeRegistrationId(Context ctx, String regid) {
  final SharedPreferences prefs = ctx.getSharedPreferences(MainActivity.class.getSimpleName(),
             Context.MODE_PRIVATE);
     Log.i(TAG, "Saving regId on app version " + appVersion);
     SharedPreferences.Editor editor = prefs.edit();
     editor.putString("device_id", regid);
     editor.putInt("appVersion", appVersion);
     editor.commit();
   
 }
 
 
 private void sendRegistrationIdToBackend() {
  URI url = null;
  try {
   url = new URI("http://10.9.169.53/register.php?regId=" + regid);
  } catch (URISyntaxException e) {
   e.printStackTrace();
  } 
  HttpClient httpclient = new DefaultHttpClient();
  HttpGet request = new HttpGet();
  request.setURI(url);
  try {
   httpclient.execute(request);
  } catch (ClientProtocolException e) {
   e.printStackTrace();
  } catch (IOException e) {
   e.printStackTrace();
  }
 }
 
 
 @Override
 protected void onPostExecute(String result) {
  super.onPostExecute(result);
  //Toast.makeText(ctx, "Registration Completed. Now you can see the notifications", Toast.LENGTH_SHORT).show();
  //Log.v(TAG, result);
 }
}