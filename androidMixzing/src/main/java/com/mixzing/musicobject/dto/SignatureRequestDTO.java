package com.mixzing.musicobject.dto;

import com.mixzing.musicobject.EnumSignatureProcessingStatus;

public interface SignatureRequestDTO {

	public long getId();

	public void setId(long id);

	public boolean isPriority();

	public void setPriority(boolean isPriority);

	public EnumSignatureProcessingStatus getProcessingStatus();

	public void setProcessingStatus(EnumSignatureProcessingStatus processingStatus);

	public long getLsid();

	public void setLsid(long lsid);

	public int getSkip();

	public void setSkip(int skip);

	public int getDuration();

	public void setDuration(int skip);

	public int getSuperWindowMs();

	public void setSuperWindowMs(int skip);	
}