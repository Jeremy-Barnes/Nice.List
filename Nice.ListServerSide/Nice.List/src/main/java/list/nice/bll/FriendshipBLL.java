package list.nice.bll;

import list.nice.dal.HibernateUtil;
import list.nice.dal.dto.Friendship;
import list.nice.dal.dto.User;

import javax.persistence.EntityManager;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

/**
 * Created by Jeremy on 1/5/2016.
 */
public class FriendshipBLL {

	public static void createFriendship(User requester, String requesteeEmail, String selector, String validator) throws GeneralSecurityException, UnsupportedEncodingException {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		entityManager.getTransaction().begin();

		User cookieUser = UserBLL.getUser(selector, validator);
		User requestedFriend = UserBLL.getUser(requesteeEmail);
		if(cookieUser.getEmailAddress().equalsIgnoreCase(requester.getEmailAddress())){
			Friendship request = new Friendship(cookieUser.getUserID(), requestedFriend.getUserID(),false);
			entityManager.persist(request);
		} else {
			throw new GeneralSecurityException("Invalid cookie supplied");
		}

		entityManager.getTransaction().commit();
		entityManager.close();
	}

}
