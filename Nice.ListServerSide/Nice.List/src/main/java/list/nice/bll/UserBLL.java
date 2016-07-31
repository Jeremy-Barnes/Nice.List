package list.nice.bll;

import com.lambdaworks.codec.Base64;
import com.lambdaworks.crypto.SCrypt;
import list.nice.dal.HibernateUtil;
import list.nice.dal.dto.User;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.swing.filechooser.FileSystemView;
import javax.ws.rs.core.Context;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.UUID;


/**
 * Created by Jeremy on 1/1/2016.
 */
public class UserBLL {

	@Context

	public static String createUserReturnUnHashedValidator(User user) throws GeneralSecurityException, UnsupportedEncodingException {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		entityManager.getTransaction().begin();

		hashAndSaltPassword(user);
		String validatorUnHashed = createSelectorAndHashValidator(user);

		entityManager.persist(user);

		entityManager.getTransaction().commit();
		entityManager.close();
		return validatorUnHashed;
	}

	protected static User getUser(String emailAddress) {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();

		User user = (User) entityManager.createQuery("from User where UPPER(emailAddress) = :emailAddress").setParameter("emailAddress", emailAddress.toUpperCase()).getSingleResult();
		user.initWishList();//fight Hibernate's stupid lazy loading
		entityManager.close();
		return wipeSensitiveFields(user); //email is not a secure getter - don't give passwords hashes out to friends.
	}

	public static User getUser(String selector, String validator) throws GeneralSecurityException, UnsupportedEncodingException {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();

		User user = (User) entityManager.createQuery("from User where tokenSelector = :selector").setParameter("selector", selector).getSingleResult();

		if(verifyValidator(validator, user)) {
			user.initWishList();//fight Hibernate's stupid lazy loading
			user.initFriendsList();
			entityManager.close();

			return user;
		} else {
			entityManager.close();
			return null;
		}
	}

	public static User getUserLogin(String email, String password) throws GeneralSecurityException, UnsupportedEncodingException {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		entityManager.getTransaction().begin();

		User user = (User) entityManager.createQuery("from User where emailAddress = :email").setParameter("email", email).getSingleResult();

		String validator = null;
		if(checkLogin(user.getPassword(), password, user.getSalt())) {
			validator = createSelectorAndHashValidator(user);
			user.initWishList(); //fight Hibernate's stupid lazy loading
			user.initFriendsList();
			entityManager.getTransaction().commit();
			entityManager.close();
		} else {
			entityManager.getTransaction().rollback();
			entityManager.close();
			user = null;
		}
		if(validator != null) user.setTokenValidator(validator);

		return user;
	}

	public static User updateUser(User changeUser, User sessionUser) throws GeneralSecurityException, UnsupportedEncodingException {

		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		entityManager.getTransaction().begin();

		if(changeUser.getPassword() != null && !changeUser.getPassword().isEmpty()) {
			hashAndSaltPassword(changeUser);
			sessionUser.setPassword(changeUser.getPassword());
			sessionUser.setSalt(changeUser.getSalt());
		}
		sessionUser.setFirstName(changeUser.getFirstName());
		sessionUser.setLastName(changeUser.getLastName());
		sessionUser.setPostcode(changeUser.getPostcode());
		sessionUser.setEmailAddress(changeUser.getEmailAddress());
		sessionUser.setEmailAddress(changeUser.getEmailAddress());
		sessionUser.setCity(changeUser.getCity());
		sessionUser.setState(changeUser.getState());
		sessionUser.setCountry(changeUser.getCountry());

		entityManager.merge(sessionUser);
		entityManager.getTransaction().commit();
		entityManager.close();
		return changeUser;
	}

	public static User wipeSensitiveFields(User user) {
		user.setWishList(WishListBLL.removeSensitivePresentData(user.getWishList()));
		user.setSalt("");
		user.setPassword("");
		user.setTokenSelector("");
		user.setTokenValidator("");
		for(User friend : user.getFriends()){
			friend.setSalt("");
			friend.setPassword("");
			friend.setTokenSelector("");
			friend.setTokenValidator("");
		}
		return user;
	}

	public static String saveUserImage(BufferedImage img) throws IOException {
		double aspectRatio = (img.getWidth(null) * 1.0) / img.getHeight(null);

		int newWidth = 0;
		int newHeight = 0;

		if(aspectRatio > 1) { //wider than it is tall
			newHeight = 540; //270 (default profile picture size) * 2
			newWidth = (int) (newHeight/ aspectRatio);
		} else { //taller than it is wide
			newWidth = 540;
			newHeight = (int) (newWidth / aspectRatio);
		}

		img.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
		String path = "\\UserContent\\" + System.currentTimeMillis() + ".png";

		ImageIO.write(img, "png", new File(FileSystemView.getFileSystemView().getHomeDirectory() + path));
		return path;
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

		user.setTokenSelector(UUID.randomUUID().toString());
		user.setTokenValidator(new String(Base64.encode(hashedValidatorByte)));
		String uuid = UUID.randomUUID().toString();
		return validatorStr;
	}

	protected static boolean verifyValidator(String suppliedValidator, User dbUser) throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] hashByte = SCrypt.scrypt(suppliedValidator.getBytes("UTF-8"), suppliedValidator.getBytes("UTF-8"), 16384, 8, 1, 64);
		String hashedCookieValidator = new String(Base64.encode(hashByte));
		return hashedCookieValidator.equals(dbUser.getTokenValidator());
	}


}
