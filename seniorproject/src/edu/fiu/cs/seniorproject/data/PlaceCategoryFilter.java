package edu.fiu.cs.seniorproject.data;

public enum PlaceCategoryFilter {
	RESTAURANT_BARS(361),
	HOTEL(360),
	BAKERY(447),
	DENTISTS(367),
	FOOD_SALES(383),
	GALLERY_ART(466),	
	LIQUOR_SALES(470),
	LOCKSMITH(504),
	PARKING_GARAGE(472),
	PARKING_LOT(401),
	PARKING_LOT_PROVISIONAL(445),
	PARKING_LOT_SELF_PARKING(394),
	PARKING_LOT_TEMPORARY(396),
	PARKING_LOT_UNDERUTILIZED(441),
	PARKING_LOT_VALET(516),
	PHARMACY(450);
	
	private final int _value;
	
	PlaceCategoryFilter(int value) {
		_value = value;
	}
	
	public int Value() {
		return _value;
	}
	
	public static PlaceCategoryFilter getValueAtIndex(int index) {
		return PlaceCategoryFilter.values()[ index ];
	}
}
/*
public class PlaceCategoryFilter {

	public static final int	BAKERY 						=	447;
	public static final int	DENTISTS 					=	367;
	public static final int	FOOD_SALES 					=	383;
	public static final int	GALLERY_ART 				=	466;
	public static final int	HOTEL 						=	360;
	public static final int	LIQUOR_SALES 				=	470;
	public static final int	LOCKSMITH 					=	504;
	public static final int	PARKING_GARAGE 				=	472;
	public static final int	PARKING_LOT 				=	401;
	public static final int	PARKING_LOT_PROVISIONAL     =	445;
	public static final int	PARKING_LOT_SELF_PARKING    =	394;
	public static final int	PARKING_LOT_TEMPORARY       =	396;
	public static final int	PARKING_LOT_UNDERUTILIZED   =	441;
	public static final int	PARKING_LOT_VALET           =	516;
	public static final int	PHARMACY 					=	450;
	public static final int	RESTAURANT_BARS 			=	361;

}*/
