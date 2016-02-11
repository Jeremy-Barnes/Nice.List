package list.nice.dal.dto;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Jeremy on 1/6/2016.
 */
@Entity
@Table(name="friendships")
public class Friendship {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private int friendshipID;
	private int requesterUserID;
	private int requestedUserID;
	private boolean accepted;

	public Friendship() {}

	public Friendship(int friendshipID, int requesterUserID, int requestedUserID, boolean accepted) {
		this.friendshipID = friendshipID;
		this.requesterUserID = requesterUserID;
		this.requestedUserID = requestedUserID;
		this.accepted = accepted;
	}

	public Friendship( int requesterUserID, int requestedUserID, boolean accepted) {
		this.requesterUserID = requesterUserID;
		this.requestedUserID = requestedUserID;
		this.accepted = accepted;
	}

	public int getFriendshipID() {
		return friendshipID;
	}

	public void setFriendshipID(int friendshipID) {
		this.friendshipID = friendshipID;
	}

	public int getRequesterUserID() {
		return requesterUserID;
	}

	public void setRequesterUserID(int requesterID) {
		this.requesterUserID = requesterID;
	}

	public int getRequestedUserID() {
		return requestedUserID;
	}

	public void setRequestedUserID(int requestedID) {
		this.requestedUserID = requestedID;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
}
