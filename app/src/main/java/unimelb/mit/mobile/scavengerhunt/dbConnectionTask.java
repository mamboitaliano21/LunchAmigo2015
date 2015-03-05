package unimelb.mit.mobile.scavengerhunt;

import android.os.AsyncTask;

public class dbConnectionTask extends AsyncTask<String, Void, Boolean> {

	private UserDAO userDAO;
	
	@Override
	protected Boolean doInBackground(String... email) {
		// TODO Auto-generated method stub
		
		boolean temp = false;
		try{
		 
		if(email.length>0)
		{
		temp = userDAO.alreadyExistEmail(email[0]);
		}
		}catch(Exception e)
		{
			//Toast.makeText(this, "Error in Thread", Toast.LENGTH_LONG).Show() ;
			System.err.println("Error in thread");
		}
		return temp;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		
		try {
			userDAO = new UserDAO();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
