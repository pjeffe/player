package com.mixzing.message.messageobject.impl;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title: MixMoxie Java Client</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: MixMoxie</p>
 *
 * @author G.Miller S Mathur.
 * @version 1.0
 */
public class TrackEquivalence  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	long wishlist_gsid;
	
	List<TrackMapping> local_matches;
    
    int match_level;
    //@XmlElement(name = "local_matches")
    public List getLocal_matches() {
        return local_matches;
    }

    public void setLocal_matches(List local_matches) {
        this.local_matches = local_matches;
    }

    public void setWishlist_gsid(long wishlist_gsid) {
        this.wishlist_gsid = wishlist_gsid;
    }

    public void setMatch_level(int match_level) {
        this.match_level = match_level;
    }
    //@XmlAttribute(name = "match_level")
    public int getMatch_level() {
        return match_level;
    }
    
    //@XmlAttribute (name = "wishlist_gsid")
    public long getWishlist_gsid() {
        return wishlist_gsid;
    }
}
