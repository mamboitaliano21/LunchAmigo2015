package unimelb.mit.mobile.scavengerhunt;

import java.sql.Timestamp;
import java.util.List;

public class Message {

	//Information to persist
	private String _id;
	private String _rev;
	private String message;
	private String sender;
	private String receiver;
	//private String status;
	private MessageState status;
	private MessageNotificationState notificationState;
	private String location;
	private Timestamp timestamp;
	private List<String> hints;
	private Timestamp retrievedTime;
	private Timestamp discoveredTime;
	
	public Message() {
		// TODO Auto-generated constructor stub
	}
	
	
//--Constructors------------
//--Partial Basic constructor
	public Message(String message, String sender, String receiver,
			MessageState status, String location, Timestamp timestamp) {
		this.message = message;
		this.sender = sender;
		this.receiver = receiver;
		this.status = status;
		this.location = location;
		this.timestamp = timestamp;
		this.retrievedTime = null;
		this.discoveredTime = null;
	}
	
//--Complete Java constructor
	public Message(String message, String sender, String receiver,
			MessageState status, MessageNotificationState notificationState, String location, Timestamp timestamp, List<String> hints) {
		this.message = message;
		this.sender = sender;
		this.receiver = receiver;
		this.status = status;
		this.location = location;
		this.timestamp = timestamp;
		this.notificationState = notificationState;
		this.hints = hints;
		this.retrievedTime = null;
		this.discoveredTime = null;
	}

//--Constructor with fields for CouchDB
	public Message(String _id, String _rev, String message, String sender,
			String receiver, MessageState status, MessageNotificationState notificationState, String location, Timestamp timestamp, List<String> hints,
			Timestamp retrievedTime, Timestamp discoveredTime) {
		this._id = _id;
		this._rev = _rev;
		this.message = message;
		this.sender = sender;
		this.receiver = receiver;
		this.status = status;
		this.location = location;
		this.timestamp = timestamp;
		this.notificationState = notificationState;
		this.hints = hints;
		this.retrievedTime = retrievedTime;
		this.discoveredTime = discoveredTime;
		
	}

//------Getters and Setters----------
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String get_rev() {
		return _rev;
	}

	public void set_rev(String _rev) {
		this._rev = _rev;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public MessageState getStatus() {
		return status;
	}

	public void setStatus(MessageState status) {
		this.status = status;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public List<String> getHints() {
		return hints;
	}

	public void setHints(List<String> hints) {
		this.hints = hints;
	}


	public MessageNotificationState getNotificationState() {
		return notificationState;
	}


	public void setNotificationState(MessageNotificationState notificationState) {
		this.notificationState = notificationState;
	}


	public Timestamp getRetrievedTime() {
		return retrievedTime;
	}


	public void setRetrievedTime(Timestamp retrievedTime) {
		this.retrievedTime = retrievedTime;
	}


	public Timestamp getDiscoveredTime() {
		return discoveredTime;
	}


	public void setDiscoveredTime(Timestamp discoveredTime) {
		this.discoveredTime = discoveredTime;
	}
	
}
