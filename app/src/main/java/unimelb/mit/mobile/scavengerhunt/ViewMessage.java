package unimelb.mit.mobile.scavengerhunt;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewMessage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_message);
		
		TextView mSender = (TextView) findViewById(R.id.txtDetailsSender);
		TextView mMessage = (TextView) findViewById(R.id.txtDetailsMessage);
		TextView mDate = (TextView) findViewById(R.id.txtDetailsDateFound);
		TextView mDateLabel = (TextView) findViewById(R.id.txtLabelDate);
		TextView mDirectionLabel = (TextView) findViewById(R.id.txtLabelDirection);
		
		Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	if (extras.getString("type").equals("0")){
        		mSender.setText(extras.getString("sender"));
        		mDateLabel.setText("Discovered Time:");
        		mDirectionLabel.setText("Sender:");
        	}
        	else{
        		mSender.setText(extras.getString("receiver"));
        		mDateLabel.setText("Sent Time:");
        		mDirectionLabel.setText("Receiver:");
        	}
        	mMessage.setText(extras.getString("message"));
        	mMessage.setMaxLines(5);
        	mMessage.setHeight(25);
    		mDate.setText(extras.getString("date"));
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_message, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_view_message,
					container, false);
			return rootView;
		}
	}
}
