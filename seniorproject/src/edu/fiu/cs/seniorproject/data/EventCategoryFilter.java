package edu.fiu.cs.seniorproject.data;

public enum EventCategoryFilter {
	
	Arts_Crafts(594),
	Business_Tech(595),
	Comedy(596),
	Community(597),
	Dance(598),
	Education_Campus(599),
	Fairs_Festivals(600),
	Food_Dining(601),
	Music(602),
	Other(603),
	Performing_Arts(604),
	Shopping(605),
	Sports_Outdoors(606),
	Visual_Arts(607),
	NONE(0);
	
	private final int _value;
	
	EventCategoryFilter(int value) {
		_value = value;
	}
	
	public int Value() {
		return _value;
	}
	
	public static EventCategoryFilter getValueAtIndex(int index) {
		return EventCategoryFilter.values()[ index ];
	}
}
/*
public final class EventCategoryFilter {
	public static final int Arts_Crafts=594;
	public static final int Business_Tech=595;
	public static final int Comedy=596;
	public static final int Community=597;
	public static final int Dance=598;
	public static final int Education_Campus=599;
	public static final int Fairs_Festivals=600;
	public static final int Food_Dining=601;
	public static final int Music=602;
	public static final int Other=603;
	public static final int Performing_Arts=604;
	public static final int Shopping=605;
	public static final int Sports_Outdoors=606;
	public static final int Visual_Arts=607;
}
*/