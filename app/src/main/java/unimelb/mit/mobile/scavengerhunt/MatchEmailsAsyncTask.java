package unimelb.mit.mobile.scavengerhunt;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class MatchEmailsAsyncTask extends AsyncTaskLoader<List<String>>{
	
	List<String> emails;
	
	public MatchEmailsAsyncTask(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<String> loadInBackground() {
		// TODO Auto-generated method stub
		List<String> listOfMatchingEmails = matchEmails();
		return listOfMatchingEmails;
	}
	
	public List<String> matchEmails()
	{
		boolean amigosRetrieved = false;
		boolean amigosOnFile = false;
		
		UserDAO userDAO = new UserDAO();
		List<String> amigos = getNameEmailDetails();
		List<String> trueAmigos = new ArrayList<String>();
		
		String amigosListFilename = "amigosListFile";
		
		try{
		File file = new File(amigosListFilename);
		//If file exists
		if(file.exists()){
			//A context is needed to open the files
			FileInputStream fis = getContext().getApplicationContext().openFileInput(amigosListFilename);
			byte[] bytes = null;
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(bais);
			while (fis.available() > 0) {
			    String element = in.readUTF();
			    //System.out.println(element);
			    trueAmigos.add(element);
			}
		}      
		//Do something if file does not exists
		else{
			
			//userDAO = new UserDAO();
			List<User> contactsAlreadyInPlatform = userDAO.getMultipleUsers(amigos);
			//contactsAlreadyInPlatform = userDAO.getMultipleUsers2(amigos);
			
			// if s get user availability or timestamp
			//Time Now
			Date dNow = new Date( ); // Instantiate a Date object
			Calendar cal = Calendar.getInstance();
			cal.setTime(dNow);
			dNow = cal.getTime();	
			
			//timestamp arimethic
			// if the time now is before the available until, it's valid !
			
			for(User u: contactsAlreadyInPlatform)
			{
					trueAmigos.add(u.getEmail());
			}

			//to write in file
			// write to byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			for (String element : trueAmigos) {
			    out.writeUTF(element);
			}
			byte[] bytes = baos.toByteArray();
			
			FileOutputStream fos = getContext().openFileOutput(amigosListFilename, Context.MODE_PRIVATE);
			fos.write(bytes);
			fos.close();
		}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		// Do something else.
			
		//If file does not exist
		
		//If file is not there create file with email list Otherwise do nothing or update it
		/*
		if(!amigosOnFile)
		{
		File file = new File(this.getFilesDir(), "lunchAmigoList");
		
		FileOutputStream outputStream;

		try {
		  outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
		  outputStream.write(string.getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
		}else{
			
		}*/
		return trueAmigos;
	}
	
	//For retrieving data from DB
	public ArrayList<String> getNameEmailDetails() {
	    ArrayList<String> emlRecs = new ArrayList<String>();
	    HashSet<String> emlRecsHS = new HashSet<String>();
	    Context context = getContext();
	    ContentResolver cr = context.getContentResolver();
	    String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID, 
	            ContactsContract.Contacts.DISPLAY_NAME,
	            ContactsContract.Contacts.PHOTO_ID,
	            ContactsContract.CommonDataKinds.Email.DATA, 
	            ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
	    String order = "CASE WHEN " 
	            + ContactsContract.Contacts.DISPLAY_NAME 
	            + " NOT LIKE '%@%' THEN 1 ELSE 2 END, " 
	            + ContactsContract.Contacts.DISPLAY_NAME 
	            + ", " 
	            + ContactsContract.CommonDataKinds.Email.DATA
	            + " COLLATE NOCASE";
	    String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
	    Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
	    if (cur.moveToFirst()) {
	        do {
	            // names comes in hand sometimes
	            String name = cur.getString(1);
	            String emlAddr = cur.getString(3);

	            // keep unique only
	            if (emlRecsHS.add(emlAddr.toLowerCase())) {
	                emlRecs.add(emlAddr);
	            }
	        } while (cur.moveToNext());
	    }

	    cur.close();
	    return emlRecs;
	}

}
