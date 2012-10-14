package edu.fiu.cs.seniorproject.data;

public enum DateFilter {
	NONE(0),
	TODAY(24 * 60 * 60),
	THIS_WEEK( 7 * 24 * 60 * 60),
	THIS_WEEKEND(1),
	NEXT_WEEKEND(2),
	NEXT_30_DAYS(30 * 24 * 60 * 60);
	
	private final int _value;
	
	DateFilter(int value) {
		_value = value;
	}
	
	public int Value() {
		return _value;
	}
	
	public static DateFilter getValueAtIndex(int index) {
		return DateFilter.values()[ index ];
	}
}

/*
public final class DateFilter {
	public static final int NOME = 0;
	public static final int TODAY = 24 * 60 * 60;
	public static final int THIS_WEEK = 7 * 24 * 60 * 60;
	public static final int THIS_WEEKEND= 1;
	public static final int NEXT_WEEKEND= 2;
	public static final int NEXT_30_DAYS= ;
	
	private static final int[] list = new int[] { TODAY, THIS_WEEK, THIS_WEEKEND, NEXT_WEEKEND, NEXT_30_DAYS, NOME };
	
	public static int getValueAtIndex(int index) {
		return index < list.length ? list[index] : NOME;
	}
}
*/