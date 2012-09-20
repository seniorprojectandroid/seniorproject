package edu.fiu.cs.seniorproject.data.provider;

import java.util.List;

public interface DataProvider {
	
	public  List<?> parseEvent();
	
	public  List<?> parsePlaces();

}
