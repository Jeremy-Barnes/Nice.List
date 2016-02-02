package list.nice.bll;

import list.nice.dal.HibernateUtil;
import list.nice.dal.dto.User;
import list.nice.dal.dto.WishListItem;

import javax.persistence.EntityManager;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

/**
 * Created by Jeremy on 1/5/2016.
 */
public class WishListBLL {

	public static void addWishListItem(WishListItem item, String selector, String validator) throws GeneralSecurityException, UnsupportedEncodingException {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		entityManager.getTransaction().begin();

		User cookieUser = UserBLL.getUser(selector, validator);

		//TODO

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
