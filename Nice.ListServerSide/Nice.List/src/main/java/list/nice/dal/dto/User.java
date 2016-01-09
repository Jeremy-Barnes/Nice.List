package list.nice.dal.dto;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Jeremy on 12/30/2015.
 */
@Entity
@Table(name="users")
public class User {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private int userID;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String password;
	private String salt;
	private String city;
	private String state;
	private String country;
	private String postcode;
	private String tokenSelector;
	private String tokenValidator;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "friendships", joinColumns = @JoinColumn(name="requesteruserID"), inverseJoinColumns = @JoinColumn(name="requesteduserID"))
	@WhereJoinTable(clause = "accepted = 'TRUE'")
	private Set<User> friends = new HashSet<User>();

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "friendships", joinColumns = @JoinColumn(name="requesteduserID"), inverseJoinColumns = @JoinColumn(name="requesteruserID"))
	@WhereJoinTable(clause = "accepted = 'TRUE'")
	private Set<User> friendOf = new HashSet<User>();

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "friendships", joinColumns = @JoinColumn(name="requesteruserID"), inverseJoinColumns = @JoinColumn(name="requesteduserID"))
	@WhereJoinTable(clause = "accepted = 'FALSE'")
	private Set<User> pendingRequests = new HashSet<User>();

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "friendships", joinColumns = @JoinColumn(name="requesteduserID"), inverseJoinColumns = @JoinColumn(name="requesteruserID"))
	@WhereJoinTable(clause = "accepted = 'FALSE'")
	private Set<User> requestsToReview = new HashSet<User>();

	public User(){
	}

	public User(int userID, String firstName, String lastName, String emailAddress, String password, String salt, String city, String state,
				String country, String postcode, String tokenSelector, String tokenValidator) {
		this.userID = userID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.password = password;
		this.salt = salt;
		this.city = city;
		this.state = state;
		this.country = country;
		this.postcode = postcode;
		this.tokenSelector = tokenSelector;
		this.tokenValidator = tokenValidator;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getTokenSelector() {
		return tokenSelector;
	}

	public void setTokenSelector(String tokenSelector) {
		this.tokenSelector = tokenSelector;
	}

	public String getTokenValidator() {
		return tokenValidator;
	}

	public void setTokenValidator(String tokenValidator) {
		this.tokenValidator = tokenValidator;
	}

	public List<User> getMyFriends(){
		User[] myFriends = friends.toArray(new User[]{});
		User[] friendsOfArr = friendOf.toArray(new User[]{});
		List<User> allFriends = new ArrayList<User>();

		for(User u : myFriends){
			allFriends.add(u);
		}
		for(User u : friendsOfArr){
			allFriends.add(u);
		}
		return allFriends;
	}
}
