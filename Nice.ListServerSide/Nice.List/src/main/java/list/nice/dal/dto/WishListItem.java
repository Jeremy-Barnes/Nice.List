package list.nice.dal.dto;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

/**
 * Created by Jeremy on 1/1/2016.
 */
@Entity
@Table(name="wishListItems")
public class WishListItem {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private int wishListItemID;

	private int requesterUserID;
	@XmlElement(nillable=true)
	private Integer purchaserUserID = null;
	@XmlElement(nillable=true)
	private String URL;
	@XmlElement(nillable=true)
	private String imageURL;
	@XmlElement(nillable=true)
	private String itemName;
	@XmlElement(nillable=true)
	private String comment;
	private boolean isBought = false;
	@Temporal(TemporalType.DATE)
	private Date dateAdded;
	private double price;
	private int want = 0;


	public WishListItem(){}

	public WishListItem(int wishListItemID, int requesterUserID, String URL, String imageURL, String itemName, String comment, boolean isBought, Integer purchaserUserID, Date dateAdded, double price, int want) {
		this.wishListItemID = wishListItemID;
		this.requesterUserID = requesterUserID;
		this.URL = URL;
		this.imageURL = imageURL;
		this.itemName = itemName;
		this.comment = comment;
		this.isBought = isBought;
		this.purchaserUserID = purchaserUserID;
		this.dateAdded = dateAdded;
		this.price = price;
		this.want = want;
	}

	public int getWishListItemID() {
		return wishListItemID;
	}

	public void setWishListItemID(int wishListItemID) {
		this.wishListItemID = wishListItemID;
	}

	public int getRequesterUserID() {
		return requesterUserID;
	}

	public void setRequesterUserID(int requesterUserID) {
		this.requesterUserID = requesterUserID;
	}

	public Integer getPurchaserUserID() {
		return purchaserUserID;
	}

	public void setPurchaserUserID(Integer purchaserUserID) {
		this.purchaserUserID = purchaserUserID;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean getIsBought() {
		return isBought;
	}

	public void setIsBought(boolean isBought) {
		this.isBought = isBought;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getWant() {
		return want;
	}

	public void setWant(int want) {
		this.want = want;
	}
}
