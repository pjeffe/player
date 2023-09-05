package com.mixzing.musicobject.dao;

import com.mixzing.musicobject.EnumLibraryStatus;
import com.mixzing.musicobject.Library;

public interface LibraryDAO {

	public Library readLibrary();

	public void updateLibraryId(String server_id);

	public void updateResolvedCount(int count);
	
	public void updateLibraryStatus(EnumLibraryStatus status);
}