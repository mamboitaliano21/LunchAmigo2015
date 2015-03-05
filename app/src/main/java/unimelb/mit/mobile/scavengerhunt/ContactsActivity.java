package unimelb.mit.mobile.scavengerhunt;

import android.app.ActionBar.LayoutParams;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.List;


public class ContactsActivity extends ListActivity
	implements LoaderManager.LoaderCallbacks<Cursor> {

	// This is the Adapter being used to display the list's data
	SimpleCursorAdapter mAdapter;
	
	// These are the Contacts rows that we will retrieve
	static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
	    ContactsContract.Data.DISPLAY_NAME,
	    ContactsContract.Data.DATA1};
	
	// This is the select criteria
	/*
	 *Previous selection
	static final String SELECTION = "((" + 
	    ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
	    ContactsContract.Data.DISPLAY_NAME + " != '' ))";
	*/
	
	// This is the select criteria
	static final String SELECTION = "((" + 
	    ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
	    ContactsContract.Data.DISPLAY_NAME + " != '' ) AND (" + 
	    ContactsContract.Data.DATA1 + "= ?))";
	
	
	//For access to CouchDB
	private UserDAO userDAO;
	private List<String> friendList;
	private User amigoUser;
	
	//for geolocation
	private static final String AUTHPREFS = "authPrefs" ;
	private static final String GEOLOCATIONKEY = "geoKey"; 
	
	//To pass recipient email in message to next activity (message activity).
	private String recipientEmail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//If contact file does not exist
		
		//Match Names before displaying
		//This does the same several times, always on create!!! FIX THIS!
		
		//Instead of the normal function I will try to use an AsyncLoaderTask
		//friendList = matchEmails();
		friendList = null; 
		AsyncTaskLoader<List<String>> loadEmailsAndMatchThem = new MatchEmailsAsyncTask(this);
		friendList = loadEmailsAndMatchThem.loadInBackground();
		
		
		if(friendList==null || friendList.size()<1)
		{
			Toast.makeText(this, "=( No friends to show", Toast.LENGTH_LONG).show();
			finish();
		}
		else{
		
				// Create a progress bar to display while the list loads
			ProgressBar progressBar = new ProgressBar(this);
			progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
			        LayoutParams.WRAP_CONTENT, Gravity.CENTER));
			progressBar.setIndeterminate(true);
			getListView().setEmptyView(progressBar);
			
			// Must add the progress bar to the root of the layout
			ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
			root.addView(progressBar);
			
			// For the cursor adapter, specify which columns go into which views
			String[] fromColumns = {ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data.DATA1};
			int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1
			
			// Create an empty adapter we will use to display the loaded data.
			// We pass null for the cursor, then update it in onLoadFinished()
			mAdapter = new SimpleCursorAdapter(this, 
			        android.R.layout.simple_list_item_1, null,
			        fromColumns, toViews, 0);
			setListAdapter(mAdapter);
			
			// Prepare the loader.  Either re-connect with an existing one,
			// or start a new one.
			getLoaderManager().initLoader(0, null, this);
		}
	}
	
	// Called when a new Loader needs to be created
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		//Previous cursor
		/*return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
		        PROJECTION, SELECTION, null, null);*/
		
		
		//The selection should be an expression and selectionArgs should have as many elements as there are ? literal placeholders in selection.
		
		   //final String[] SELECTION_ARGS = new String[friendList.size()];
		final String[] SELECTION_ARGS = new String[friendList.size()];
		  friendList.toArray(SELECTION_ARGS);
		
		   
		  // Loader<Cursor> cursor = new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
		//	        PROJECTION, SELECTION,SELECTION_ARGS , null);
		
		// This is the select criteria
		String SELECTION2 = "((" + 
		    ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
		    ContactsContract.Data.DISPLAY_NAME + " != '' ) AND (" + 
		    ContactsContract.Data.DATA1 + " IN ("+ makePlaceholders(friendList.size()) +") ))";

		   return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
		        PROJECTION, SELECTION2,SELECTION_ARGS , null);
		        
	}
	
	//This method helps to show the contacts in the loader 
	//Basically transforms a little bit the data so it can be shown in contact list in query (selection criteria).
	private String makePlaceholders(int len) {
	    if (len < 1) {
	        // It will lead to an invalid query anyway ..
	        throw new RuntimeException("No placeholders");
	    } else {
	        StringBuilder sb = new StringBuilder(len * 2 - 1);
	        sb.append("?");
	        for (int i = 1; i < len; i++) {
	            sb.append(",?");
	        }
	        return sb.toString();
	    }
	}
	
	// Called when a previously created loader has finished loading
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in.  (The framework will take care of closing the
		// old cursor once we return.)
		mAdapter.swapCursor(data);
	}
	
	// Called when a previously created loader is reset, making the data unavailable
	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed.  We need to make sure we are no
		// longer using it.
		mAdapter.swapCursor(null);
	}
	
	//This is used when you click on a contact, need to send info to next activity (send message)/
	//Info required is basically User Id and Recipient ID
	@Override 
	public void onListItemClick(ListView l, View v, int position, long id) {
	// Do something when a list item is clicked
        
		super.onListItemClick(l, v, position, id);

        Cursor cursor = (Cursor) mAdapter.getItem(position);
        String selection = cursor.getString(1);
        String recepientEmail = cursor.getString(2);
        Toast.makeText(this, "You selected: " + selection,Toast.LENGTH_LONG).show(); 
        Toast.makeText(this, "Email: " + recepientEmail,Toast.LENGTH_LONG).show(); 
		
		Object contactItem = getListView().getItemIdAtPosition(position);
		//String item = (String) getListAdapter().getItem(position);
		//Object o = getListAdapter().getItem(position);
		Intent intent = new Intent(this, CreateMessage.class);
		intent.putExtra("recipientEmail", recepientEmail );
		startActivity(intent);
	}
	
	//Someone Elses code - Document this
	//Probably code from Dennis, not sure if it works!
	private double haversine(
	        double lat1, double lng1, double lat2, double lng2) {
	    int r = 6371; // average radius of the earth in km
	    double dLat = Math.toRadians(lat2 - lat1);
	    double dLon = Math.toRadians(lng2 - lng1);
	    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	       Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) 
	      * Math.sin(dLon / 2) * Math.sin(dLon / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double d = r * c;
	    return d;
	}
}