package list.nice.bll;

import list.nice.dal.HibernateUtil;
import list.nice.dal.dto.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Created by Jeremy on 1/1/2016.
 */
public class UserBLL {

	public static void createUser(User user){
		Session dbSession = HibernateUtil.getSessionFactory().openSession();
		Transaction trans = dbSession.beginTransaction();

		user.setSalt(createPasswordSalt());
		user.setPassword(hashPass(user.getPassword() + user.getSalt()));
		dbSession.save(user);

		trans.commit();
		dbSession.close();
	}

	public static User getUser(int userID) {
		return null; //TODO
	}

	public static User[] getAllUsers() {
		Session dbSession = HibernateUtil.getSessionFactory().openSession();

		User[] allUsers = (User[]) dbSession.createQuery("from User").list().toArray(new User[0]);

		dbSession.close();
		return allUsers;
	}

	private static String createPasswordSalt(){
		return ""; //TODO
	}

	private static String hashPass(String pwdAndSalt) {
		return pwdAndSalt; //TODO
	}
}
