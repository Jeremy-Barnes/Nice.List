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

	public static void acceptFriendRequest(Friendship request, String selector, String validator) throws GeneralSecurityException, UnsupportedEncodingException {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		entityManager.getTransaction().begin();

		User cookieUser = UserBLL.getUser(selector, validator);
		if(cookieUser.getUserID() == request.getRequestedUserID()) {
			Friendship dbReq = (Friendship) entityManager.createQuery("from Friendship where requesterUserID = :req and requestedUserID = :friend")
														 .setParameter("req", request.getRequesterUserID()).setParameter("friend", request.getRequestedUserID())
														 .getSingleResult();


			dbReq.setAccepted(true);
			entityManager.getTransaction().commit();
			entityManager.close();
			request.setFriendshipID(dbReq.getFriendshipID());

		} else {
			entityManager.close();
			throw new GeneralSecurityException("Invalid cookie supplied");
		}
	}

	public static void deleteFriendRequest(Friendship request, String selector, String validator) throws GeneralSecurityException, UnsupportedEncodingException {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		entityManager.getTransaction().begin();

		User cookieUser = UserBLL.getUser(selector, validator);
		if(cookieUser.getUserID() == request.getRequestedUserID()) {
			Friendship dbReq = (Friendship) entityManager.createQuery("from Friendship where requesterUserID = :req and requestedUserID = :friend")
														 .setParameter("req", request.getRequesterUserID()).setParameter("friend", request.getRequestedUserID())
														 .getSingleResult();

				entityManager.remove(dbReq);
				entityManager.getTransaction().commit();
				entityManager.close();
		} else {
			entityManager.close();
			throw new GeneralSecurityException("Invalid cookie supplied");
		}
	}
}
