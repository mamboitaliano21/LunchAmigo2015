package unimelb.mit.mobile.scavengerhunt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UnreadMessages extends Fragment {

	private int typeMessage;
	private ListviewMessagesAdapter adp;
	public UnreadMessages(int mtype){
		typeMessage=mtype;
	}
	public static final String AUTHPREFS = "authPrefs" ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	MessageDAO messageDAO = new MessageDAO();
    	List<Message> userMessages;
    	
    	//GetSender and Receiver from previous activity
    			// Restore preferences to get username global variable
	    SharedPreferences settings = this.getActivity().getSharedPreferences(AUTHPREFS, 0);
		
	    //Manage this error, what happens when there is not preference
	    //for this key and the default value is taken. This should not happen
		String receiver = settings.getString("userEmail", "abc@error.com");
    	
		if (typeMessage==0){
			userMessages=messageDAO.getMultipleMessagePerUser(receiver,MessageState.DISCOVERED);
		}
		else if (typeMessage==1){
			userMessages=messageDAO.getMultipleMessagePerUser(receiver,MessageState.UNDISCOVERED);
		}
		else {
			userMessages=messageDAO.getSentMessagesPerUser(receiver);
		}
    	
    	
    	ArrayList<Message> list = new ArrayList<Message>(userMessages);
    	
    	View rootView = inflater.inflate(R.layout.activity_unread_messages, container, false);

        ListView lv = (ListView)rootView.findViewById(R.id.listViewUnreadMessages);

        adp = new ListviewMessagesAdapter(getActivity(), list);
        
        lv.setAdapter(adp); 
        
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            	Message msg = (Message)adp.getItem(arg2);
            	String id=msg.get_id();
            	MessageDAO messageDAO = new MessageDAO();
            	msg=messageDAO.getMessagesById(id);
            	if (typeMessage==0){ 
              		Intent intent = new Intent(arg1.getContext(),ViewMessage.class);
              		intent.putExtra("type", "0");
              		intent.putExtra("sender", msg.getSender());
              		intent.putExtra("message", msg.getMessage());
              		intent.putExtra("date", msg.getDiscoveredTime().toString());              		
                  	startActivity(intent);
            		 
        		}
        		else if (typeMessage==1){
	        		Intent intent = new Intent(arg1.getContext(),SearchingMessage.class);
	           		intent.putExtra("message_id", msg.get_id());
	           		String hint1;
	           		String hint2;
	           		String hint3;
	           		
	           		try{
	           			hint1=msg.getHints().get(0);
	           		}	           		
	           		catch(Exception e){hint1="";}
	           		intent.putExtra("hint1", hint1);
	           		
	           		try{
	           			hint2=msg.getHints().get(2);	           			
	           		}
	           		catch(Exception e){hint2="";}
	           		intent.putExtra("hint2", hint2);
	           		
	           		try{
	           			hint3=msg.getHints().get(2);
	           		}
	           		catch(Exception e){hint3="";}
	           		intent.putExtra("hint3", hint3);
	           		
	           		intent.putExtra("messageFrom", msg.getSender());
	           		intent.putExtra("message", msg.getMessage());
	           		String location = msg.getLocation();
	           		String[] locationSplit = location.split(",");
	           		intent.putExtra("targetLatitude", locationSplit[0]);
	           		intent.putExtra("targetLongitude", locationSplit[1]);
	           		
	               	startActivity(intent);
        		}
        		else { 	
             		Intent intent = new Intent(arg1.getContext(),ViewMessage.class);
             		intent.putExtra("type", "1");
             		intent.putExtra("receiver", msg.getReceiver());
             		intent.putExtra("message", msg.getMessage());
             		intent.putExtra("date", msg.getTimestamp().toString());              		
                 	startActivity(intent);
        		}
            }
        });        return rootView;
    }
        
    public class ListviewMessagesAdapter extends BaseAdapter{
    	private ArrayList<Message> listMessages;

    	private LayoutInflater mInflater;

    	public ListviewMessagesAdapter(Context photosFragment, ArrayList<Message> results){
    	    listMessages= results;
    	    mInflater = LayoutInflater.from(photosFragment);
    	}

    	@Override
    	public int getCount() {
    	    // TODO Auto-generated method stub
    	    return listMessages.size();
    	}

    	@Override
    	public Object getItem(int arg0) {
    	    // TODO Auto-generated method stub
    	    return listMessages.get(arg0);
    	}

    	@Override
    	public long getItemId(int arg0) {
    	    // TODO Auto-generated method stub
    	    return arg0;
    	}


    	public View getView(int position, View convertView, ViewGroup parent) {
    	    // TODO Auto-generated method stub
    		ViewHolder holder;
    	    if(convertView == null){
    	        convertView = mInflater.inflate(R.layout.messages_custom_view, null);
    	        holder = new ViewHolder();
    	        holder.txtsender = (TextView) convertView.findViewById(R.id.txtSender);          
    	        holder.txtdetails = (TextView) convertView.findViewById(R.id.txtDetails);
    	        holder.image_icon = (ImageView) convertView.findViewById(R.id.imgIcon);
    	        holder.linearLayout = (LinearLayout)convertView.findViewById(R.id.linearLayoutMessage);
    	        convertView.setTag(holder);
    	    } else {
    	        holder = (ViewHolder) convertView.getTag();
    	    }
    	    
    	    Random rand = new Random();
    	    int randomNum = rand.nextInt(5);
    	    String uriString="myimages/";
    	    if (randomNum == 0){
    	    	uriString += "blue/";
    	    }
    	    else if (randomNum == 1){
    	    	uriString += "green/";
    	    }
			else if (randomNum == 2){
				uriString += "light_blue/";
			}
			else if (randomNum == 3){
				uriString += "light_orange/";	
			}
			else if (randomNum == 4){
				uriString += "light_red/";
			}
    	    char firstChar;
    	    if (typeMessage==2){
    	    	firstChar = listMessages.get(position).getReceiver().toLowerCase().charAt(0);
    	    }
    	    else{
    	    	firstChar = listMessages.get(position).getSender().toLowerCase().charAt(0);
    	    } 
    	    
    	    int pos = firstChar - 'a' + 1;
    	    
    	    uriString+="letters_s-"+pos+".png";
    	    try {
    	        InputStream is = getResources().getAssets().open(uriString);
    	        Bitmap bitmap = BitmapFactory.decodeStream(is);
    	        is.close();
    	        float ratio = Math.min(
    	                (float) 250 / bitmap.getWidth(),
    	                (float) 250 / bitmap.getHeight());
    	        int width = Math.round((float) ratio * bitmap.getWidth());
    	        int height = Math.round((float) ratio * bitmap.getHeight());

    	        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, width,
    	                height, true);
    	        
    	        holder.image_icon.setImageBitmap(newBitmap);
    	        // use the input stream as you want
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    	    if (typeMessage==2){
    	    	holder.txtsender.setText(listMessages.get(position).getReceiver());
    	    }
    	    else{
    	    	holder.txtsender.setText(listMessages.get(position).getSender());
    	    }    	    
    	    holder.txtdetails.setText("Message sent at: " + listMessages.get(position).getTimestamp());
    	    if (listMessages.get(position).getStatus()==MessageState.UNREAD && typeMessage != 2){
    	    	holder.txtsender.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
    	    	holder.linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
    	    }
    	    else
    	    {
    	    	holder.txtsender.setTypeface(Typeface.SANS_SERIF,Typeface.NORMAL);
    	    	holder.linearLayout.setBackgroundColor(Color.parseColor("#EEEEEE"));
    	    }

    	    return convertView;
    	}

    	public class ViewHolder{
    	    TextView txtsender, txtdetails;
    	    ImageView image_icon;
    	    LinearLayout linearLayout;
    	}
    	}

     
}