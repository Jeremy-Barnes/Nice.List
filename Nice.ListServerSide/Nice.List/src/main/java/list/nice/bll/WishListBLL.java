package list.nice.bll;

import list.nice.dal.HibernateUtil;
import list.nice.dal.dto.User;
import list.nice.dal.dto.WishListItem;

import javax.persistence.EntityManager;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * Created by Jeremy on 1/5/2016.
 */
public class WishListBLL {

	public static WishListItem addWishListItem(WishListItem item, User user) throws GeneralSecurityException, UnsupportedEncodingException {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		entityManager.getTransaction().begin();

		correctURLs(item);
		item.setRequesterUserID(user.getUserID());
		item.setDateAdded(Calendar.getInstance().getTime());
		entityManager.persist(item);

		entityManager.getTransaction().commit();
		entityManager.close();

		return item;
	}

	public static WishListItem updateWishListItem(WishListItem item, User user) throws GeneralSecurityException, UnsupportedEncodingException {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		entityManager.getTransaction().begin();

		correctURLs(item);
		if(item.getPurchaserUserID() != null && item.getPurchaserUserID() == user.getUserID()){
			entityManager.merge(item);
		} else if(item.getRequesterUserID() == user.getUserID()) {
			WishListItem dbItem = (WishListItem) entityManager.createQuery("from WishListItem where wishListItemID = :id").setParameter("id", item.getWishListItemID()).getSingleResult();
			item.setPurchaserUserID(dbItem.getPurchaserUserID());
			item.setIsBought(dbItem.getIsBought());
			entityManager.merge(item);
		} else {
			throw new GeneralSecurityException();
		}

		entityManager.getTransaction().commit();
		entityManager.close();

		return item;
	}

	public static List<WishListItem> getFriendsWishList(int activeUserID, int wishListUserID, User user) throws GeneralSecurityException, UnsupportedEncodingException {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		entityManager.getTransaction().begin();

		List<WishListItem> dbItems;
		if(activeUserID == user.getUserID()) {
			dbItems = (List<WishListItem>) entityManager.createQuery("from WishListItem where requesterUserID = :id and isBought = false").setParameter("id", wishListUserID).getResultList();
		} else {
			throw new GeneralSecurityException();
		}

		entityManager.getTransaction().commit();
		entityManager.close();
		return dbItems;
	}

	public static Set<WishListItem> removeSensitivePresentData(Set<WishListItem> presents){
		for(WishListItem present : presents){
			present.setIsBought(false);
			present.setPurchaserUserID(-1);
		}
		return presents;
	}

	private static void correctURLs(WishListItem item){
		String correctedURL = item.getURL();

		if(!(correctedURL == null || correctedURL.isEmpty() || correctedURL.toUpperCase().contains("HTTP://") || correctedURL.toUpperCase().contains("HTTPS://"))) {
			correctedURL = "http://" + correctedURL;
		}
		item.setURL(correctedURL);

		String correctedImgURL = item.getImageURL();

		if(!(correctedImgURL == null || correctedImgURL.isEmpty() || correctedImgURL.toUpperCase().contains("HTTP://") || correctedImgURL.toUpperCase().contains("HTTPS://"))) {
			correctedImgURL = "http://" + correctedImgURL;
		}
		item.setImageURL(correctedImgURL);

	}
}
