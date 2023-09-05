package com.mixzing.musicobject.impl;

import java.sql.ResultSet;

import com.mixzing.musicobject.AndroidPackage;
import com.mixzing.musicobject.dto.impl.AndroidPackageDTOImpl;

public class AndroidPackageImpl extends AndroidPackageDTOImpl implements AndroidPackage {

	public AndroidPackageImpl(ResultSet rs) {
		super(rs);
	}
	
	public AndroidPackageImpl() {
		
	}

}
