package list.nice.bll;

import com.lambdaworks.codec.Base64;
import com.lambdaworks.crypto.SCrypt;
import list.nice.dal.HibernateUtil;
import list.nice.dal.dto.User;
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

		User user = (User) dbSession.createQuery("from User where tokenSelector = :selector").setParameter("selector", selector).uniqueResult();
		dbSession.close();

		if(verifyValidator(validator, user)) {
			return user;
		} else {
			return null;
		}
	}

	public static User getUserLogin(String email, String password) throws GeneralSecurityException, UnsupportedEncodingException {
		Session dbSession = HibernateUtil.getSessionFactory().openSession();
		Transaction trans = dbSession.beginTransaction();

		User user = (User) dbSession.createQuery("from User where emailAddress = :email").setParameter("email", email).uniqueResult();

		String validator;
		if(checkLogin(user.getPassword(), password, user.getSalt())) {
			validator = createSelectorAndHashValidator(user);
			dbSession.flush();
			trans.commit();
			dbSession.close();
			user.setTokenValidator(validator);
		} else {
			user = null;
			trans.rollback();;
			dbSession.close();
		}

		return user;
	}

	public static User updateUser(User user, String selector, String validator) throws GeneralSecurityException, UnsupportedEncodingException {
		User confirmUser = getUser(selector, validator);
		if(confirmUser == null || confirmUser.getUserID() != user.getUserID()){
			throw new GeneralSecurityException("Invalid Cookie");
		}

		Session dbSession = HibernateUtil.getSessionFactory().openSession();
		Transaction trans = dbSession.beginTransaction();

		if(user.getPassword() != null && !user.getPassword().isEmpty()) {
			hashAndSaltPassword(user);
		}

		dbSession.update(user);
		trans.commit();
		dbSession.close();
		return user;
	}

	public static User wipeSensitiveFields(User user) {
		user.setSalt("");
		user.setPassword("");
		user.setTokenSelector("");
		user.setTokenValidator("");
		return user;
	}

	private static void hashAndSaltPassword(User user) throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] saltByte = new byte[16];
		SecureRandom.getInstance("SHA1PRNG").nextBytes(saltByte);
		String saltStr = new String(Base64.encode(saltByte));

		byte[] hashByte = SCrypt.scrypt(user.getPassword().getBytes("UTF-8"), saltStr.getBytes("UTF-8"), 16384, 8, 1, 64);
		String hashStr = new String(Base64.encode(hashByte));

		user.setPassword(hashStr);
		user.setSalt(saltStr);
	}

	private static boolean checkLogin(String dbPasswordHash, String suppliedPassword, String suppliedSalt) throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] hashByte = SCrypt.scrypt(suppliedPassword.getBytes("UTF-8"), suppliedSalt.getBytes("UTF-8"), 16384, 8, 1, 64);
		String hashStrConfirm = new String(Base64.encode(hashByte));
		return hashStrConfirm.equals(dbPasswordHash);
	}

	private static String createSelectorAndHashValidator(User user) throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] validatorByte = new byte[16];
		SecureRandom.getInstance("SHA1PRNG").nextBytes(validatorByte);
		String validatorStr = new String(Base64.encode(validatorByte)); //Get in UTF
		byte[] hashedValidatorByte = SCrypt.scrypt(validatorStr.getBytes("UTF-8"), validatorStr.getBytes("UTF-8"), 16384, 8, 1, 64);

		byte[] selectorByte = SCrypt.scrypt(user.getEmailAddress().getBytes("UTF-8"), user.getEmailAddress().getBytes("UTF-8"), 16384, 8, 1, 64);
		user.setTokenSelector(new String(Base64.encode(selectorByte)));
		user.setTokenValidator(new String(Base64.encode(hashedValidatorByte)));

		return validatorStr; //TODO make this actually unique
	}

	private static boolean verifyValidator(String suppliedValidator, User dbUser) throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] hashByte = SCrypt.scrypt(suppliedValidator.getBytes("UTF-8"), suppliedValidator.getBytes("UTF-8"), 16384, 8, 1, 64);
		String hashedCookieValidator = new String(Base64.encode(hashByte));
		return hashedCookieValidator.equals(dbUser.getTokenValidator());
	}

}
