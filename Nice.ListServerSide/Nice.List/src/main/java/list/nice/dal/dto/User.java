package list.nice.dal.dto;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.WhereJoinTable;
import org.hibernate.collection.internal.PersistentSet;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Map;
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
	private String pictureURL;


	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "friendships", joinColumns = @JoinColumn(name="requesteruserID"), inverseJoinColumns = @JoinColumn(name="requesteduserID"))
	@WhereJoinTable(clause = "accepted = 'TRUE'")
	private Set<User> friends = new HashSet<User>();
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "friendships", joinColumns = @JoinColumn(name="requesteduserID"), inverseJoinColumns = @JoinColumn(name="requesteruserID"))
	@WhereJoinTable(clause = "accepted = 'TRUE'")
	private Set<User> friendsOf = new HashSet<User>();


	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "friendships", joinColumns = @JoinColumn(name="requesteruserID"), inverseJoinColumns = @JoinColumn(name="requesteduserID"))
	@WhereJoinTable(clause = "accepted = 'FALSE'")
	private Set<User> pendingRequests = new HashSet<User>();

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "friendships", joinColumns = @JoinColumn(name="requesteduserID"), inverseJoinColumns = @JoinColumn(name="requesteruserID"))
	@WhereJoinTable(clause = "accepted = 'FALSE'")
	private Set<User> requestsToReview = new HashSet<User>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "requesterUserID")
	private Set<WishListItem> wishList; //a sacrificial offering to the Hibernate demons

	@Transient
	private Set<WishListItem> wishListSnapshot = new HashSet<WishListItem>();
	@Transient
	private Set<User> friendsSnapshot = new HashSet<User>();
	@Transient
	private Set<User> friendsOfSnapshot = new HashSet<User>();
	@Transient
	private Set<User> requestsToReviewSnapshot = new HashSet<User>();
	@Transient
	private Set<User> pendingRequestsSnapshot = new HashSet<User>();

	@Transient
	private boolean wishListInitialized = false;
	@Transient
	private boolean friendsInitialized = false;
	@Transient
	private boolean friendRequestsInitialized = false;

	public User(){}

	public User(int userID, String firstName, String lastName, String emailAddress, String password, String salt, String city, String state,
				String country, String postcode, String tokenSelector, String tokenValidator, String pictureURL) {
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
		this.pictureURL = pictureURL;
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

	public String getPictureURL() {
		return tokenValidator;
	}

	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}

	public Set<User> getFriends() {
		Set friendsList = new HashSet<User>();
		friendsList.addAll(friendsSnapshot);
		friendsList.addAll(friendsOfSnapshot);
		return friendsList;
	}

	public void setFriends(Set<User> friends) {
		this.friends = friends;
		this.friendsInitialized = false;
		this.friendsSnapshot = new HashSet<User>();
		this.requestsToReviewSnapshot = new HashSet<User>();
		this.friendsOfSnapshot = new HashSet<User>();
	}

	public void setFriendsOf(Set<User> friendsOf) {
		this.friendsOf = friendsOf;
		this.friendsInitialized = false;
		this.friendsOfSnapshot = new HashSet<User>();
		this.friendsSnapshot = new HashSet<User>();
		this.requestsToReviewSnapshot = new HashSet<User>();
	}

	public Set<User> getPendingRequests() {
		return pendingRequestsSnapshot;
	}

	public void setPendingRequest(Set<User> pendingRequests) {
		this.pendingRequests = pendingRequests;
		this.friendRequestsInitialized = false;
		this.pendingRequestsSnapshot = new HashSet<User>();
	}

	public Set<User> getRequestsToReview() {
		return requestsToReviewSnapshot;
	}

	public void setRequestsToReview(Set<User> requestsToReview) {
		this.requestsToReview = requestsToReview;
		this.friendsInitialized = false;
		this.friendsSnapshot = new HashSet<User>();
		this.friendsOfSnapshot = new HashSet<User>();
		this.requestsToReviewSnapshot = new HashSet<User>();
	}

	public void setWishList(Set<WishListItem> wishList) {
		if(wishList instanceof PersistentSet) {
			this.wishListInitialized = false;
			this.wishListSnapshot = new HashSet<WishListItem>();
			this.wishList = wishList;
		} else {
			this.wishListInitialized = true;
			this.wishListSnapshot = wishList;
			this.wishList = wishList;
		}
	}

	public Set<WishListItem> getWishList() {
		return wishListSnapshot;
	}

	public void initWishList(){
		((PersistentSet)wishList).forceInitialization();
		this.wishListSnapshot = ((Map<WishListItem, ?>)((PersistentSet) wishList).getStoredSnapshot()).keySet();
		this.wishListInitialized = true;
	}

	public void initFriendsList(){
		((PersistentSet)friends).forceInitialization();
		((PersistentSet)friendsOf).forceInitialization();
		((PersistentSet)requestsToReview).forceInitialization();
		this.friendsSnapshot = ((Map<User, ?>)((PersistentSet) friends).getStoredSnapshot()).keySet();
		this.friendsOfSnapshot = ((Map<User, ?>)((PersistentSet) friendsOf).getStoredSnapshot()).keySet();
		this.requestsToReviewSnapshot = ((Map<User, ?>)((PersistentSet) requestsToReview).getStoredSnapshot()).keySet();
	}

	public void initSentFriendRequests() {
		((PersistentSet)pendingRequests).forceInitialization();
		this.pendingRequestsSnapshot = ((Map<User, ?>)((PersistentSet) pendingRequests).getStoredSnapshot()).keySet();
	}
}
