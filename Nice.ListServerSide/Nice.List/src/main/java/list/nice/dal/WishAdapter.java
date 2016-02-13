//package list.nice.dal;
//
//import list.nice.dal.dto.WishListItem;
//import org.hibernate.collection.internal.PersistentSet;
//
//import javax.xml.bind.annotation.adapters.XmlAdapter;
//import java.util.*;
//
///**
// * Created by Jeremy on 2/11/2016.
// */
//public class WishAdapter extends XmlAdapter<WishListItem[], Set> {
//
//	@Override
//	public Set unmarshal(WishListItem[] v) throws Exception {
//		HashSet hs = new HashSet();
//		for(WishListItem i : v){
//			hs.add(i);
//		}
//		return hs;
//
//	}
//
//	@Override
//	public WishListItem[] marshal(Set v) throws Exception {
//		PersistentSet p = (PersistentSet) v;
//		if(p.empty()) {
//			return null;
//		}
//		WishListItem[] z = (WishListItem[])v.toArray(new WishListItem[0]);
//		List y = Arrays.asList(z);
//		ArrayList x = new ArrayList<WishListItem>(y);
//		return z;
//	}
//}

package list.nice.dal;

		import list.nice.dal.dto.WishListItem;
		import org.hibernate.collection.internal.PersistentSet;

		import javax.xml.bind.annotation.adapters.XmlAdapter;
		import java.util.*;

/**
 * Created by Jeremy on 2/11/2016.
 */
public class WishAdapter extends XmlAdapter<List<WishListItem>, Set> {

	@Override
	public Set unmarshal(List<WishListItem> v) throws Exception {
		return new HashSet<WishListItem>(v);

	}

	@Override
	public List<WishListItem> marshal(Set v) throws Exception {
		PersistentSet p = (PersistentSet) v;
		if(p.empty()) {
			return null;
		}
		WishListItem[] z = (WishListItem[])v.toArray(new WishListItem[0]);
		List y = Arrays.asList(z);
		ArrayList x = new ArrayList<WishListItem>(y);
		return x;
	}
}