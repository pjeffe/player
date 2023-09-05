package com.mixzing.log;

/**
 * 
 * The enumeration name is returned by getMsg as a cryptic message if Logger.IS_CRYPTIC_MESSAGES_DISABLED is false
 * Otherwise the plain English text is returned.
 *
 */
public enum Cryptics {
	// note when adding messages, reuse any holes in the pattern that develop because messages were removed
	FC1, FC2, FC3, FC4, FC5, FC6, FC7, FC8, FC9, FC10, FC11, FC12,
	A,	B,	C,	D,	E,	F,	G,	H,	I,	J,	K,	L,	M,	N,	O,	P,	Q,	R,	S,	T,	U,	V,	W,	X,	Y,	Z,
	AA,	AB,	AC,	AD,	AE,	AF,	AG,	AH,	AI,	AJ,	AK,	AL,	AM,	AN,	AO,	AP,	AQ,	AR,	AS,	AT,	AU,	AV,	AW, AX, AY, AZ,
	BA,	BB,	BC,	BD,	BE,	BF,	BG,	BH,	BI,	BJ,	BK,	BL,	BM,	BN,	BO,	BP,	BQ,	BR,	BS,	BT,	BU,	BV,	BW,	BX,	BY,	BZ,
	CA, CB, CC, CD, CE, CF, CG, CH, CI, CJ, CK, CL, CM, CN, CO, CP, CQ, CR, CS, CT, CU, CV, CW, CX, CY, CZ,
	DA, DB, DC, DD, DE, DF, DG, DH, DI, DJ, DK, DL, DM, DN, DO, DP, DQ, DR, DS, DT, DU, DV, DW, DX, DY, DZ,
	EA, EB, EC, ED, EE, EF, EG, EH, EI, EJ, EK, EL, EM, EN, EO, EP, EQ, ER, ES, ET, EU, EV;

	private static final Logger log = Logger.getRootLogger();
	private static boolean initialized = false;
	private String msg = null;;
	
	public String getMsg () {
		if (!initialized) {
			init();
		}
		StringBuffer ret = new StringBuffer("{");
		ret.append(name());
		ret.append("} ");
		if (msg != null) {
			ret.append(msg);
		}
		return ret.toString();
	}
	
	private void init () {
		initialized = true;
		if(Logger.IS_CRYPTIC_MESSAGES_DISABLED) {
			FC1.msg = "License check canceled";
			FC2.msg = "Invalid license response code";
			FC3.msg = "License valid";
			FC4.msg = "License invalid";
			FC5.msg = "Old licence still valid";
			FC6.msg = "License not market managed";
			FC7.msg = "License server failure";
			FC8.msg = "License server over quota";
			FC9.msg = "Error contacting license server";
			FC10.msg = "Invalid package name";
			FC11.msg = "Non matching UID";
			FC12.msg = "License check timed out";
			
			A.msg = "License.init: License = ";
			B.msg = "allFeatures = ";
			C.msg = "License.isLicensed: License = ";
			D.msg = "License.isLicensed: Bits = ";
			E.msg = "License.checkUpgrade: Type = ";
			F.msg = "License.checkUpgrade: Invalid upgrade state";
			G.msg = "License.getCachedUpgradePackageType: Recorded package type = ";
			H.msg = "License.getCachedUpgradePackageType: No package type currently recorded";
			I.msg = "License.getCachedUpgradePackageType: Exception: ";
			J.msg = "License.getUpgradePackageType: Returned package type = ";
			K.msg = "License: Forcing non market management because SDK < 3";
			L.msg = "License: Android market not installed, using non market management";
			M.msg = "License.checkUpgradePackageType: Exception: ";
			N.msg = "License.setUpgradePackageType: type = ";
			O.msg = "License.setLicenseCode: license = ";
			P.msg = "License.setLicenseCode: licensedFeatures = ";
			Q.msg = "License.setLicenseCode: license unchanged = ";
			R.msg = "License.getLicenseCode: validation failed";
			S.msg = "License.getLicenseCode: invalid value";
			T.msg = "License.nonMarketLicenseCheck: status = ";
			U.msg = "License.getMarketUpgradeStatus: Check expiration at ";
			V.msg = "License.getMarketUpgradeStatus: License expired at ";
			W.msg = "License.getMarketUpgradeStatus: Non market license confirmed but expired";
			X.msg = "License.getMarketUpgradeStatus: Non market license confirmed and valid";
			Y.msg = "License.timeRemaining: Called with no prior start for key ";
			Z.msg = "License.graceRemind: Last reminder done at = ";

			AA.msg = "License.graceRemind: Called with no last reminder";
			AB.msg = "License.getNonMarketUpgradeStatus: Validation failed";
			AC.msg = "License.getNonMarketUpgradeStatus: Invalid value";
			AD.msg = "License.getUpgradeStatus exception: ";
			AE.msg = "License.getNonMarketUpgradeStatus: status = ";
			AF.msg = "License.fromServerState: Invalid state ";
			AG.msg = "License.setUpgradeStatusFromServer: State = ";
			AH.msg = "License.setNonMarketUpgradeStatus: Status = ";
			AI.msg = "License.getLicense: Key = ";
			AJ.msg = "license = ";
			AK.msg = "License.getLicense IOException: ";
			AL.msg = "License.getLicense Exception: ";
			AM.msg = "License.shouldDoNonMarketLicenseCheck: error checking SET_LICENSE_KEY obfuscated pref value";
			AN.msg = "License.unbindLicenseService: Unable to unbind (already unbound)";
			AO.msg = "License.cancelLicenseCheck: Reason = ";
			AP.msg = "License.performMarketLicenseCheck: Binding to market licensing service.";
			AQ.msg = "License.performMarketLicenseCheck: Unable to bind to market licensing service.";
			AR.msg = "License.performMarketLicenseCheck: Missing permission to access license service.";
			AS.msg = " - Reverting to non market licensing";
			AT.msg = "License.init started";
			AU.msg = "License: Setting expiration time at: ";
			AV.msg = "License: Setting valid expiration time to: ";
			AW.msg = "Rerstarting license check from upgrade app";
			AX.msg = "License.restartBackgroundCheckUpgrade: start";
			AY.msg = " - Valid response unexpected for test package - hack alert";
			AZ.msg = "License.restartBackgroundCheckUpgrade: end";

			BA.msg = " - Signature not valid";
			BB.msg = "License.doMarketLicenseCheck - license check still in progress";
			BC.msg = "License: Got expected bad package result for test package. Starting real license check.";
			BD.msg = "License.restartBackgroundCheckUpgrade: package state changed, rechecking upgrade status";
			BE.msg = "License.getLongValue: Key = ";
			BF.msg = "License.getLongValue: No value set for key: ";
			BG.msg = "License.getLongValue: Validation error for key: ";
			BH.msg = "License.getLongValue: Invalid value for key: ";
			BI.msg = "License.processStoredMarketStatus: Check expiration at: ";
			BJ.msg = "License.processStoredMarketStatus: License expired at: ";
			BK.msg = "License.processStoredMarketStatus: License valid but expired";
			BL.msg = "License.processStoredMarketStatus: License valid";
			BM.msg = "License.processStoredMarketStatus: License invalid";
			BN.msg = "License.processStoredMarketStatus: Status value invalid: ";
			BO.msg = "License.processStoredMarketStatus: Check unconfirmed expiration at: ";
			BP.msg = "License.processStoredMarketStatus: Check system error expiration at: ";
			BQ.msg = "License.processStoredMarketStatus: License error or unconfirmed and expired";
			BR.msg = "License.processStoredMarketStatus: License error or unconfirmed but still valid";
			BS.msg = "License.processStoredMarketStatus: System error on last check";
			BT.msg = "License.processStoredMarketStatus: Market not signed in";
			BU.msg = "License.getMarketUpgradeStatus: Status = ";
			BV.msg = "License.getMarketUpgradeStatus: Status not set";
			BW.msg = "License.getMarketUpgradeStatus: Validation error";
			BX.msg = "License.getMarketUpgradeStatus: Invalid value";
			BY.msg = "License.setMarketUpgradeStatus: status = ";
			BZ.msg = "License.doMarketLicenseCheck: checkLicense: pkg = ";

			CA.msg = "License server response timeout monitor";
			CB.msg = "License.doMarketLicenseCheck: RemoteException in checkLicense call.";
			CC.msg = "License.LicenseServiceConnection: Service unexpectedly disconnected.";
			CD.msg = "License.generatePublicKey: Invalid decoder algorithm";
			CE.msg = "License.generatePublicKey: Could not decode from Base64.";
			CF.msg = "License.generatePublicKey: Invalid key specification.";
			CG.msg = "License.verifyLiucense: Debugging delay is set";
			CH.msg = "Licence.verifyLicense processing";
			CI.msg = "License server response received after timeout period or cancel";
			CJ.msg = "License.TrialFeatureSet: start init";
			CK.msg = "License.TrialFeatureSet: end init";
			CL.msg = "License.LicensedFeatureSet: start init";
			CM.msg = "License.LicensedFeatureSet: end init";
			CN.msg = "License.ExpiredFeatureSet: start init";
			CO.msg = "License.ExpiredFeatureSet: end init";
			CP.msg = "License.init finished";
			CQ.msg = "License.setLicenseCode: Unmatched codes when setting license, all features reset";
			CR.msg = "Licens.isLicensed: Unmatched codes when checking license, all features reset";
			CS.msg = "no key";
			CT.msg = "no code";
			CU.msg = "bad state ";
			CV.msg = "no state";
			CW.msg = "License.NonMarketLicenseResult: invalid result: ";
			CX.msg = "state = ";
			CY.msg = "Invalid key exception";
			CZ.msg = "Signature exception";
			
			DA.msg = "Base 64 decoder exception";
			DB.msg = "Illegal argument exception";
			DC.msg = "Response codes do not match";
			DD.msg = "Nonce does not match";
			DE.msg = "Package name does not match";
			DF.msg = "User identifier is empty";
			DG.msg = "License.getInitialLicense: Obfuscated license code not found";
			DH.msg = "Check upgrade";
			DI.msg = "Recheck upgrade";
			DJ.msg = "License.checkUpgrade: start";
			DK.msg = "License.checkUpgrade: end";
			DL.msg = ".checkUpgradePrompt: action = ";
			DM.msg = "MixzingApp: Starting background license check timer";
			DN.msg = "License.getCheckUpgradeResult: null handler";
			DO.msg = "License.notifyCompletion: action = ";
			DP.msg = "LicensePrompt.setLicense: key = ";
			DQ.msg = "License.getCheckUpgradeResult: handler is null";
			DR.msg =  "License.verifyLicense: handler is null";
			DS.msg = "License.getCheckUpgradeResult: returning existing result";
			DT.msg = "License.getInitialLicense: obfuscated license setting = ";
			DU.msg = "License.performPeriodicMarketLicenseCheck: check if it is time for a market lookup";
			DV.msg = "License.performPeriodicMarketLicenseCheck: launching market lookup";
			DW.msg = "License.getCheckUpgradeResult: queued handler to receive result";
			DX.msg = "License.restartBackgroundCheckUpgrade: package state unchanged, noaction";
			DY.msg = "License.checkUpgradePackageType returning: ";
			DZ.msg = "License.restartBackgroundCheckUpgrade: non market state is UPGRADED returning LICENSE_PROMPT";
			
			EA.msg = "License.setUpgradeStatusFromServer: change package type from NO_UPGRADE to NON_MARKET";
			EB.msg = "License.setPackageTypeFromServer: marketmanaged = ";
			EC.msg = "License.getUpgradePackageType: package type has changed";
			ED.msg = "License: Got bad package result becuase upgrade app was uninstalled";
			EE.msg = "License.checkUpgrade: market upgrade was uninstalled";
			EF.msg = "License.setPremiumState: changed launch from basic to premium";
			EG.msg = "License.hideUpgrade: disabled upgrader launch";
			EH.msg = "License.restartLauncher: attempt to restart home package - ";
			EI.msg = "MarketLicenseService.unbindLicenseService: Unable to unbind (already unbound)";
			EJ.msg = "MarketLicenseService.checkMarketLicense RemoteException in checkLicense call.";
			EK.msg = "MarketLicenseService.LicenseServiceConnection: Service unexpectedly disconnected.";
			EL.msg = "MarketLicenseService.onBind.";
			EM.msg = "MarketLicenseService.performMarketLicenseCheck: Binding to licensing service.";
			EN.msg = "MarketLicenseService.checkMarketLicense license check requested callback";
			EO.msg = "MarketLicenseService.checkMarketLicense params: ";
			EP.msg = "License.checkMarketEnv: license proxy service found";
			EQ.msg = "License.checkMarketEnv: market license service found";
			ER.msg = "LicenseService.setBasicState: changed launch from premium to basic";
			ES.msg = "MarketLicenseService.checkMarketEnv: service is disabled, treat as Non Market";
			ET.msg = "License.init: trialfeatures.codeEquals(expiredFeatures) = ";
			EU.msg = "License.checkMarketEnv: successful bind";
			EV.msg = "License.onFriendEvent: ";
			
			for (Cryptics c : values()) {
				if (c.msg == null || c.msg.length() == 0) {
					if (Logger.IS_DEBUG_ENABLED) {
						log.debug ("Cryptics: Empty message for: " + c.name());
					}
				}
			}
		}
	}
}
