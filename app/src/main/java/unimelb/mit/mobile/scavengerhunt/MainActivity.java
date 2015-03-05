package unimelb.mit.mobile.scavengerhunt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import unimelb.mit.mobile.scavengerhunt.R;

// abstract this out to a different package
// todo
// what to do on Pause or back button ?
// todo
// todo
// use regex
/*
 * PUSH NOTIFICATIONS
 * ADDED: 29/09/2014
*/

/*
 * END NOTIFICATIONS.
 * */

public class MainActivity extends Activity {
	/* login */
	
	private String mEmail;
	private String mPassword;
	
	private EditText PasswordView;
	private EditText EmailView;	
	private Boolean isLoggedIn;
	private Boolean isValid;
	
	//For DB Management
	private MessageDAO userDAO;
	private User amigoUser;
	
	//Shared preferences is useful to persist application information
	//while the app is alive, Key Value way.
	SharedPreferences sharedpreferences;
	//TODO make this private I guess.
	public static final String AUTHPREFS = "authPrefs" ;
	public static final String EMAIL = "emailKey"; 

	//Global Asynch task
	AsyncTask<String, Void, Boolean> connectDB;
	AsyncTask<User, Void, Boolean> loginDB;
	AsyncTask<User, Void, Boolean> updateUserToDB;	
	
	/*
	 * PUSH NOTIFICATIONS
	 * ADDED: 29/09/2014
	*/
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "device_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String TAG = "GCMRelated_1";
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	String regid; //Device ID for push notifications
	/*
	 * END CODE.
	 * */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		isLoggedIn = false;
		isValid = false;
		EmailView = (EditText) findViewById(R.id.editEmailAddress);		
		PasswordView = (EditText) findViewById(R.id.editPassword);
	    
	    //For network permissions
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		
	    sharedpreferences = getSharedPreferences(AUTHPREFS, Context.MODE_PRIVATE);
	   // userDAO = new UserDAO();

	    appRegistration();
	    preLoadAsynchTask();
    
	}
	
	private void preLoadAsynchTask()
	{
		//Here we call the db and check if the user exists
		connectDB = new dbConnectionTask();
		loginDB = new dbLogin();
		updateUserToDB = new dbUpdateUserTask();
		
	}

	/**
	 * 
	 */
	public void appRegistration() {
		/****************************************************/
	    if (checkPlayServices() && isLoggedIn) {
	        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
	              regid = getRegistrationId(getApplicationContext());
	               
	              if (regid.isEmpty()) {
	            	  new RegisterApp(getApplicationContext(), gcm, getAppVersion(getApplicationContext())).execute();
	              }
	       } else {
	              Log.i(TAG, "No valid Google Play Services APK found.");
	       }
	    /****************************************************/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	// Login Button
	public void tryLogin(View view) {

		mEmail = EmailView.getText().toString();
		mPassword = PasswordView.getText().toString();
				
		attemptLogin();
		
		// start another activity if the person succeed to login
		if (isLoggedIn == true) {
			storeCredentials();
			Intent intent = new Intent(this, Inbox.class);
			startActivity(intent);
		}
	}
	

	
	public void attemptLogin() {
		// Please abstract the string to R.strings
		// todo

		//Match Email with regular Expressions
		
		//shows error in a toast
		// todo improve this to be similar to LoginActivity android template
		// or just use toast?
		if (TextUtils.isEmpty(mEmail)) {
			Context context = getApplicationContext();
			CharSequence text = "Please Insert Email";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} else if (!mEmail.contains("@")) {
			Context context = getApplicationContext();
			CharSequence text = "Error Invalid Email!";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} else {
			
			Boolean emailExists;
			Boolean userLoginOK;
			try {
				connectDB = new dbConnectionTask();
				emailExists = connectDB.execute(mEmail).get();
		
				if(emailExists)
				{
					Calendar c = Calendar.getInstance();
					c.setTime(new Date()); // sets calendar time/date
					c.add(Calendar.HOUR_OF_DAY, 1);
					Date myDate = c.getTime();  // adds one hour
					Timestamp stamp = new Timestamp(myDate.getTime());				
					
					
					amigoUser = new User(mEmail, mPassword, "1234", "yes", stamp, "0.0,0.0", true, myDate);
					
					/*
					userDAO.insertUser(amigoUser);
					isValid = true;
					*/
					loginDB = new dbLogin();
					userLoginOK = loginDB.execute(amigoUser).get();
					
					if(userLoginOK)
					{
						Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
					
					// todo use shared preference as a bool to say you are logged in instead of a instance variable
					isLoggedIn = true;
					
					//Save username email to be persistent in the whole application
					SharedPreferences.Editor editor = sharedpreferences.edit();
					editor.putString("userEmail", mEmail);
					editor.commit();
					
					persistDeviceRegistrationIDInUser();
					
					}else{
						Toast.makeText(this, "Login Failed, Wrong Credentials!!!", Toast.LENGTH_LONG).show();
					isLoggedIn = false;
					}
				}else{
					isValid = false;
					Toast.makeText(this, "User doesn't Exist!!!", Toast.LENGTH_LONG).show();
				}	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// todo what if user is not registered ? add another elif	

	}

	private boolean persistDeviceRegistrationIDInUser() {
		
		//Call to make sure that registration ID is not null
		appRegistration();
		
		//Do I need to refactor this and use an AsynchTask
		//Now persist registration ID
		boolean userUpdateOK = false;
		UserDAO userDAO = new UserDAO();
		User tempUser =	userDAO.getUser(amigoUser.getEmail());
		
		String deviceId = tempUser.getDeviceId();
		
		if(tempUser!=null && deviceId == "")
			{
				String registrationID = getRegistrationId(this);
				tempUser.setDeviceId(registrationID );
				// send the changes to the server
				updateUserToDB = new dbUpdateUserTask();
				try {
					userUpdateOK = updateUserToDB.execute(tempUser).get();
					
					if(!userUpdateOK)
					{
						Toast.makeText(this, "Error persisting Registration ID!!!", Toast.LENGTH_SHORT).show();
					}
						
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return userUpdateOK;
	}
	
	public void tryRegister(View view) {

		mEmail = EmailView.getText().toString();
		mPassword = PasswordView.getText().toString();
				
		attemptRegister();
		/* todo register the person here */
		if (isValid == true) {
			Context context = getApplicationContext();
			CharSequence text = "You are now registered";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			
		}
		
		// start another activity if the person succeed to login

	}
	
	public void attemptRegister() {
		if (TextUtils.isEmpty(mEmail)) {
			Context context = getApplicationContext();
			CharSequence text = "Please Insert Email";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} else if (!mEmail.contains("@")) //check agains regex 
		{
			Context context = getApplicationContext();
			CharSequence text = "Error Invalid Email!";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();		

			
		} else {
			
			//Our User
			//amigoUser = new User
			
			//Here we call the db and check if the user exists
			AsyncTask<String, Void, Boolean> connectDB = new dbConnectionTask();
			AsyncTask<User, Void, Boolean> saveUserToDB = new dbCreateUserTask();
			
			Boolean emailExists;
			Boolean userRegistrationOK;
			try {
				emailExists = connectDB.execute(mEmail).get();
		
				if(!emailExists)
				{
					Calendar c = Calendar.getInstance();
					c.setTime(new Date()); // sets calendar time/date
					c.add(Calendar.HOUR_OF_DAY, 1);
					Date myDate = c.getTime();  // adds one hour
					Timestamp stamp = new Timestamp(myDate.getTime());				
					
					
					amigoUser = new User(mEmail, mPassword, "1234", "yes", stamp, "0.0,0.0", true, myDate);
					
					/*
					userDAO.insertUser(amigoUser);
					isValid = true;
					*/
					userRegistrationOK = saveUserToDB.execute(amigoUser).get();
					
					if(userRegistrationOK)
					{
					Toast.makeText(this, "Now you are Part of Scavenger Hunt!!!", Toast.LENGTH_SHORT).show();
					
					}else{
						Toast.makeText(this, "Registration Failed, try later please!!!", Toast.LENGTH_SHORT).show();
					}
				}else{
				isValid = false;
				Toast.makeText(this, "User Already Exists!", Toast.LENGTH_LONG).show();
				}	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void storeCredentials() {
		Editor editor = sharedpreferences.edit();
	    editor.putString(EMAIL, mEmail);
	    editor.commit();
	}
	
	/**
	  * Check the device to make sure it has the Google Play Services APK. If
	  * it doesn't, display a dialog that allows users to download the APK from
	  * the Google Play Store or enable it in the device's system settings.
	  */
	  
	 private boolean checkPlayServices() {
	     int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	     if (resultCode != ConnectionResult.SUCCESS) {
	         if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	             GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                     PLAY_SERVICES_RESOLUTION_REQUEST).show();
	         } else {
	             Log.i(TAG, "This device is not supported.");
	             finish();
	         }
	         return false;
	     }
	     return true;
	 }
	  
	 /**
	  * Gets the current registration ID for application on GCM service.
	  * <p>
	  * If result is empty, the app needs to register.
	  *
	  * @return registration ID, or empty string if there is no existing
	  *         registration ID.
	  */
	 private String getRegistrationId(Context context) {
	     final SharedPreferences prefs = getGCMPreferences(context);
	     String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	     if (registrationId.isEmpty()) {
	         Log.i(TAG, "Registration not found.");
	         return "";
	     }
	     // Check if app was updated; if so, it must clear the registration ID
	     // since the existing regID is not guaranteed to work with the new
	     // app version.
	     int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	     int currentVersion = getAppVersion(getApplicationContext());
	     if (registeredVersion != currentVersion) {
	         Log.i(TAG, "App version changed.");
	         return "";
	     }
	     return registrationId;
	 }
	  
	 /**
	  * @return Application's {@code SharedPreferences}.
	  */
	 private SharedPreferences getGCMPreferences(Context context) {
	  // This sample app persists the registration ID in shared preferences, but
	     // how you store the regID in your app is up to you.
	     return getSharedPreferences(MainActivity.class.getSimpleName(),
	             Context.MODE_PRIVATE);
	 }
	  
	 /**
	  * @return Application's version code from the {@code PackageManager}.
	  */
	 private static int getAppVersion(Context context) {
	     try {
	         PackageInfo packageInfo = context.getPackageManager()
	                 .getPackageInfo(context.getPackageName(), 0);
	         return packageInfo.versionCode;
	     } catch (NameNotFoundException e) {
	         // should never happen
	         throw new RuntimeException("Could not get package name: " + e);
	     }
	 }
}
