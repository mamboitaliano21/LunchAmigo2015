package unimelb.mit.mobile.scavengerhunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SearchMessage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_message);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.search_message, menu);
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
	
	public void startTracking(View view){
		//Intent intent = new Intent(this, TrackPosition.class);
		Intent intent = new Intent(this, SearchingMessage.class);
		intent.putExtra("hint1", "Close to Union House.");
		intent.putExtra("hint2", "Every day you go there.");
		intent.putExtra("hint3", "Good for health.");
		intent.putExtra("messageFrom", "Julio");
		intent.putExtra("message", "Let's go to the gym.");
		intent.putExtra("targetLatitude", "-37.789697");
		intent.putExtra("targetLongitude", "144.958870");
		
		startActivity(intent);
	}
}
