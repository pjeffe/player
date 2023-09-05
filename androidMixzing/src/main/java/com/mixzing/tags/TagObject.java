package com.mixzing.tags;

public class TagObject {
	
	public String artist;
	
	public String title;
	
	public String album;
	
	public String genre;
	
	public String comment;
	
	public int track;
	
	public int year;
	
	public String toString() {
		String ret = "\n";
		ret += " Artist :"  + artist  + ":\n";
		ret += " Album  :"  + album   + ":\n";
		ret += " Title  :"  + title   + ":\n";
		ret += " Genre  :"  + genre   + ":\n";
		ret += " Comment:"  + comment + ":\n";
		ret += " Year   :"  + year    + ":\n";
		ret += " Track  :"  + track   + ":\n";
		
		return ret;
	}

}
