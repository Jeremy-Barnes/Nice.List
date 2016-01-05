package list.nice.bll;

import com.lambdaworks.crypto.SCrypt;
import list.nice.dal.HibernateUtil;
import list.nice.dal.dto.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;


/**
 * Created by Jeremy on 1/1/2016.
 */
public class UserBLL {

	public static String createUser(User user) throws GeneralSecurityException, UnsupportedEncodingException {
		Session dbSession = HibernateUtil.getSessionFactory().openSession();
		Transaction trans = dbSession.beginTransaction();

		hashAndSaltPassword(user);
		String selectorUnHashed =createSelectorAndHashValidator(user);

		dbSession.save(user);

		trans.commit();
		dbSession.close();
		return selectorUnHashed;
	}

	public static User getUser(String selector, String validator) throws GeneralSecurityException, UnsupportedEncodingException {
		Session dbSession = HibernateUtil.getSessionFactory().openSession();
		Transaction trans = dbSession.beginTransaction();

		Query q = dbSession.createQuery("from User where tokenSelector = ?").setParameter(0, selector);
		User user = (User) q.uniqueResult();
		dbSession.close();

		if(verifyValidator(validator, user)) {
			return user;
		} else {
			return null;
		}
	}

	public static User getUserLogin(String email, String password) throws GeneralSecurityException {
		Session dbSession = HibernateUtil.getSessionFactory().openSession();
		User user = (User) dbSession.createQuery("from User where emailAddress = " + email).uniqueResult();
		dbSession.close();
		if(checkLogin(user.getPassword(), password, user.getSalt())) {
			return user;
		} else {
			return null;
		}

	}

	private static void hashAndSaltPassword(User user) throws GeneralSecurityException {
		byte[] saltByte = new byte[16];
		SecureRandom.getInstance("SHA1PRNG").nextBytes(saltByte);
		byte[] hashByte = SCrypt.scrypt(user.getPassword().getBytes(), saltByte, 16384, 8, 1, 64);
		String hashStr = new String(hashByte);
		String saltStr = new String(saltByte);

		user.setPassword(hashStr);
		user.setSalt(saltStr);

	}

	private static boolean checkLogin(String passwordHash, String password, String salt) throws GeneralSecurityException {
		byte[] hashByte = SCrypt.scrypt(password.getBytes(), salt.getBytes(), 16384, 8, 1, 64);
		String hashStrConfirm = new String(hashByte);
		return hashStrConfirm.equals(passwordHash);
	}

	private static String createSelectorAndHashValidator(User user) throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] validByte = new byte[16];
		SecureRandom.getInstance("SHA1PRNG").nextBytes(validByte);
		byte[] hashByte = SCrypt.scrypt(validByte, validByte, 16384, 8, 1, 64);
		user.setTokenSelector(new String(SCrypt.scrypt(user.getEmailAddress().getBytes("UTF-8"), user.getEmailAddress().getBytes("UTF-8"), 16384, 8, 1, 64)));
		user.setTokenValidator(new String(hashByte));
		return new String(validByte); //TODO make this actually unique
	}

	private static boolean verifyValidator(String validator, User user) throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] hashByte = SCrypt.scrypt(validator.getBytes("UTF-8"), validator.getBytes("UTF-8"), 16384, 8, 1, 64);
		return new String(hashByte).equals(user.getTokenValidator());

	}

}
