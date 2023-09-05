package com.mixzing.message.messages.impl;


public class JSONMap {

	public static final String JSON_TYPE = "_t";
	public static  final int ClientMessageEnvelope = 10;
	public static  final int ServerMessageEnvelope = 11;
	
	public static  final int ClientDeleteRatings = 20;
	public static  final int ClientLibraryChanges = 21;	
	public static  final int ClientNewLibrary = 22;
	public static  final int ClientPing = 23;
	public static  final int ClientPlaylistChanges = 24;
	public static  final int ClientRatings = 25;
	public static  final int ClientRequestDefaultRecommendations = 26;
	public static  final int ClientRequestFile = 27;
	public static  final int ClientRequestRecommendations = 28;
	public static  final int ClientTagRequest = 29;
	public static  final int ClientTrackSignatures = 30;
	public static  final int ClientRefuseTrackEquivalence = 31;
	public static  final int ClientRequestMagicPlaylists = 32;
	public static  final int ClientRequestConfigParams = 33;
	
	public static  final int ServerFileResponse = 40;
	public static  final int ServerGenreBasisVectors = 41;
	public static  final int ServerNewLibraryResponse = 42;
	public static  final int ServerPingMe = 43;
	public static  final int ServerRecommendations = 44;
	public static  final int ServerRequestSignature = 45;
	public static  final int ServerResponseDelayed = 46;
	public static  final int ServerTagResponse = 47;
	public static  final int ServerTrackEquivalence = 48;
	public static  final int ServerTrackMapping = 49;
	public static  final int ServerMagicPlaylist = 50;
}
