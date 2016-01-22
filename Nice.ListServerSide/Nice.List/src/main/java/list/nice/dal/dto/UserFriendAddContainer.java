package list.nice.dal.dto;

import java.io.Serializable;

/**
 * Created by Jeremy on 1/22/2016.
 */
public class UserFriendAddContainer implements Serializable {
	private User user;
	private String requestedEmailAddress;

	public UserFriendAddContainer(User user, String requestedEmailAddress){ this.user = user; this.requestedEmailAddress = requestedEmailAddress;}
	public UserFriendAddContainer(){};

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRequestedEmailAddress() {
		return requestedEmailAddress;
	}

	public void setRequestedEmailAddress(String requestedEmailAddress) {
		this.requestedEmailAddress = requestedEmailAddress;
	}
}