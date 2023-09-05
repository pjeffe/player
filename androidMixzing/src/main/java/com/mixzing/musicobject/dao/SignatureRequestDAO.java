package com.mixzing.musicobject.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mixzing.musicobject.SignatureRequest;

public interface SignatureRequestDAO extends MusicObjectDAO<SignatureRequest>{

	public long insert(SignatureRequest gss);

	public ArrayList<SignatureRequest> readAll();
	
	public List<SignatureRequest> getRequested();
	
	public void signatureProcessed(long id, boolean isError);
	
	public List<SignatureRequest> findUnprocessedRequest(long lsid, int skip, int duration, int superWinMs);

}