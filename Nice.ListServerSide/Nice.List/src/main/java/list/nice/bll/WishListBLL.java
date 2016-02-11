package list.nice.bll;

import list.nice.dal.HibernateUtil;
import list.nice.dal.dto.User;
import list.nice.dal.dto.WishListItem;

import javax.persistence.EntityManager;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Calendar;

/**
 * Created by Jeremy on 1/5/2016.
 */
public class WishListBLL {

	public static WishListItem addWishListItem(WishListItem item, String selector, String validator) throws GeneralSecurityException, UnsupportedEncodingException {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		entityManager.getTransaction().begin();

		User cookieUser = UserBLL.getUser(selector, validator);
		item.setRequesterUserID(cookieUser.getUserID());
		item.setDateAdded(Calendar.getInstance().getTime());
		entityManager.persist(item);

		entityManager.getTransaction().commit();
		entityManager.close();

		return item;
	}

	public static WishListItem updateWishListItem(WishListItem item, String selector, String validator) throws GeneralSecurityException, UnsupportedEncodingException {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		entityManager.getTransaction().begin();

		User cookieUser = UserBLL.getUser(selector, validator);

		//WishListItem dbItem = (WishListItem) entityManager.createQuery("from WishListItem where wishListItemID = :id").setParameter("id", item.getWishListItemID()).getSingleResult();

		if(item.getRequesterUserID() == cookieUser.getUserID()){
			entityManager.persist(item);
		} else {
			throw new GeneralSecurityException();
		}

		entityManager.getTransaction().commit();
		entityManager.close();

		return item;
	}
}
