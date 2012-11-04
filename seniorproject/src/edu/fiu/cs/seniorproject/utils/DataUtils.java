package edu.fiu.cs.seniorproject.utils;

import android.location.Location;
import edu.fiu.cs.seniorproject.data.Event;

public class DataUtils {

	public static int max( int a , int b) {
		return a > b ? a : b;
	}
	
	public static int min( int a, int b) {
		return a < b ? a : b;
	}
	
	public static int LongestCommonSubsecuence(String a, String b) {
		int result = 0;
		
		if ( a != null && b != null ) {
			int m[][] = new int[ a.length() ][ b.length() ];
			
			for ( int i = 0; i < a.length(); i ++ )
				for ( int j = 0; j < b.length(); j++ ) {
					if ( i == 0 || j == 0) {
						m[i][j] = 0;
					} else if ( b.charAt(j) == a.charAt(i) ) {
						m[i][j] = m[i - 1][ j - 1] + 1;
					} else {
						m[i][j] = max(m[i][ j - 1], m[i - 1][ j ] );
					}
				}
			result = m[a.length() - 1][b.length() - 1];
		}		
		return result;
	}
	
	public static boolean isSameEvent( Event a, Event b ) {
		
		float[] distance = new float[3];
		Location.distanceBetween( Double.valueOf(a.getLocation().getLatitude()), Double.valueOf(a.getLocation().getLongitude()), Double.valueOf(b.getLocation().getLatitude()), Double.valueOf(b.getLocation().getLongitude()), distance);
		
		int lcs = LongestCommonSubsecuence(a.getName(), b.getName());
		int minLength = min(a.getName().length(), b.getName().length());
		
		//Logger.Debug("compare events nameA=" + a.getName() + " nameB=" + b.getName() + " lcs=" + lcs + " distance=" + distance[0] );
		return ( lcs / minLength >= 0.8 && distance[0] <= 50.0 && Math.abs( Long.valueOf( a.getTime() ) - Long.valueOf( b.getTime() ) ) <= 30 * 60 );
	}
}
