package unimelb.mit.mobile.scavengerhunt;

import android.os.AsyncTask;

public class dbCreateMessage extends AsyncTask<Message, Void, Boolean> {

	boolean isValid = false;
	@Override
	protected Boolean doInBackground(Message... parameters) {
		// TODO Auto-generated method stub
		Message m = parameters[0];
		
		
		
		isValid = false;
		try{
		//This can be refactored to use one single connection Object
		MessageDAO messageDAO = new MessageDAO();
		if(messageDAO.insertMessage(m))
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

}
