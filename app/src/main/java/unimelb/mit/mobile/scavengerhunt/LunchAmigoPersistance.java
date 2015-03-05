package unimelb.mit.mobile.scavengerhunt;

public class LunchAmigoPersistance {

	public LunchAmigoPersistance() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//CouchDbClient dbClient = new CouchDbClient();
		User lunchAmigoUser;  // = new User("ivan@lunchamigo.com", "codebabes", "1234", "confirmed", t);
		//UserDAO lunchAmigoUserDao = new UserDAO(dbClient);
		UserDAO lunchAmigoUserDao2 = new UserDAO();
		lunchAmigoUserDao2.insertUser("dennis@lunchamigo.com", "1234");

	}
}
