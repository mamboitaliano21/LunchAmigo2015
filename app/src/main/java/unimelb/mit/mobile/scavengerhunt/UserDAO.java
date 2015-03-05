package unimelb.mit.mobile.scavengerhunt;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.lightcouch.CouchDbClientAndroid;
import org.lightcouch.CouchDbException;
import org.lightcouch.DocumentConflictException;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Response;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
import com.google.gson.Gson;
import com.google.gson.JsonObject;
*/

public class UserDAO {
	private CouchDbClientAndroid dbClient;

	public UserDAO(CouchDbClientAndroid dbClient) {
		this.dbClient = dbClient;
		connectDatabase();
	}

	public UserDAO() {
		connectDatabase();
	}
	
	protected void connectDatabase() {
		try{
		//dbClient = new CouchDbClientAndroid("localx.properties");
		dbClient = new CouchDbClientAndroid("raw/db/localx.properties");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public boolean insertUser(String email, String token) {
		Gson gson = new Gson();
		String json;
		Calendar c = Calendar.getInstance();
		Date myDate = c.getTime();
		try {
			Timestamp stamp = new Timestamp(myDate.getTime());
			User user = new User(email, "", token, "false", stamp);

			// Cast the new object to JSON file to be saved in the DB
			json = gson.toJson(user);

			JsonObject jsonObj = dbClient.getGson().fromJson(json,
					JsonObject.class);

			// Saving in DB
			Response responseCouch = dbClient.save(jsonObj);
			
			if (!responseCouch.getId().equals("")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return false;
		}
	}
	
	//Insert a Whole User
	public boolean insertUser(User u) {
		Gson gson = new Gson();
		String json;
		try {
			User user = u;

			// Cast the new object to JSON file to be saved in the DB
			json = gson.toJson(user);

			JsonObject jsonObj = dbClient.getGson().fromJson(json,
					JsonObject.class);

			// Saving in DB
			Response responseCouch = dbClient.save(jsonObj);
			
			if (!responseCouch.getId().equals("")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return false;
		}
	}

	public boolean alreadyExistEmail(String email) {
		try {
			//View needs to be created for lunch amigo!
			List<User> list = dbClient.view("userViews/getUserByEmail")
					.key(email)
					.includeDocs(true)
					.limit(1).query(User.class);

			if (list!= null && list.size()>0 && list.get(0) != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return false;
		}
	}

	public User getUser(String email)
	{
		try {
			//String token
			//View needs to be created for lunch amigo!
			List<User> list = dbClient.view("userViews/getUserByEmail")
					.includeDocs(true)
					.key(email)
					.limit(1).query(User.class);
			
			
			if (list.get(0) != null) {
				
				if(list.size()>1)
				{throw new Exception("Must Return just one Object");}
				else{
				return list.get(0);
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return null;
		}
		//return null;
	}

	public List<User> getMultipleUsers(List<String> emailList)
	{	
		try {
			//String token
			//View needs to be created for lunch amigo!
			List<String> minilist = emailList.subList(0, 10);
			List<User> list = dbClient.view("userViews/getUserByEmail")
					.includeDocs(true)
					.keys(emailList) //This is for multiple Keys
					.query(User.class);
			
			return list;
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return null;
		}
		//return null;
	}
	
	public List<User> getMultipleUsers2(List<String> emailList)
	{	
		try {
			//String token
			//View needs to be created for lunch amigo!
			List<User> list = dbClient.view("userViews/getUserByEmail")
					.includeDocs(true)
					.keys(emailList) //This is for multiple Keys
					.query(User.class);
			
			return list;
		} 
		catch(NoDocumentException e1){
			return null;
		}
		catch(DocumentConflictException e2){
			return null;
		}
		catch(CouchDbException e3){
			return null;
		}
		catch (Exception e) {			
			System.out.println(e.getStackTrace());
			return null;
		}
		//return null;
	}
	
	public boolean updateUser(User user) {
		
		Gson gson = new Gson();
		String json;
		try {
			// Cast the new object to JSON file to be saved in the DB
			json = gson.toJson(user);

			JsonObject jsonObj = dbClient.getGson().fromJson(json,
					JsonObject.class);

			// Saving in DB
			Response responseCouch = dbClient.update(jsonObj);
			
			if (!responseCouch.getId().equals("")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return false;
		}
	}
}
