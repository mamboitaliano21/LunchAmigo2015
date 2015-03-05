package unimelb.mit.mobile.scavengerhunt;

import android.os.AsyncTask;

public class dbCreateUserTask extends AsyncTask<User, Void, Boolean> {

	boolean isValid = false;
	UserDAO userDAO = new UserDAO();
	@Override
	protected Boolean doInBackground(User... parameters) {
		// TODO Auto-generated method stub
		User amigoUser = parameters[0];
		
		
		
		isValid = false;
		try{
		//This can be refactored to use one single connection Object
		
		if(userDAO.insertUser(amigoUser))
		{
			isValid = true;
		}
		}catch(Exception e)
		{
			//Toast.makeText(this, "Error in Thread", Toast.LENGTH_LONG).Show() ;
			System.err.println("Error in thread");
			
		}
		return isValid;
	}
	
	protected void onPostExecute(Long result) {
        //showDialog("Downloaded " + result + " bytes");
        //Here I need to show a Message
    }

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}
}