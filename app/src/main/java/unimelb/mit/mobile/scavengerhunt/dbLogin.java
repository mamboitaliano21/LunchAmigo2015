package unimelb.mit.mobile.scavengerhunt;

import android.os.AsyncTask;


public class dbLogin extends AsyncTask<User, Void, Boolean> {

	public dbLogin() {
		// TODO Auto-generated constructor stub
	}

	UserDAO userDAO = new UserDAO();
	boolean isValidCredentials = false;
	
	
	@Override
	protected Boolean doInBackground(User... parameters) {
		// TODO Auto-generated method stub
		User amigoUser = parameters[0];
		
		isValidCredentials = false;
		try{
		//This can be refactored to use one single connection Object
		
		User tempUser =	userDAO.getUser(amigoUser.getEmail());
		if(tempUser!=null && tempUser.getEmail().equals(amigoUser.getEmail())&& 
				tempUser.getPassword().equals(amigoUser.getPassword()))
		{
			isValidCredentials = true;
		}
		}catch(Exception e)
		{
			//Toast.makeText(this, "Error in Thread", Toast.LENGTH_LONG).Show() ;
			System.err.println("Error in thread");
			
		}
		return isValidCredentials;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		
		try {
			UserDAO userDAO = new UserDAO();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
