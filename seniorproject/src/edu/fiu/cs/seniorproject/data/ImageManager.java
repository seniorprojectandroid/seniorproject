package edu.fiu.cs.seniorproject.data;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import edu.fiu.cs.seniorproject.R;

public class ImageManager extends Activity{
	
	private int listSize = 11;
	private ArrayList<Integer> hotdList =  new ArrayList<Integer>();
	private ArrayList<Integer> restdList =  new ArrayList<Integer>();
	
	private ArrayList<Bitmap> hotBmList =  new ArrayList<Bitmap>();
	private ArrayList<Bitmap> restBmList =  new ArrayList<Bitmap>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addBitmaps();
        
    }
	

	public void addBitmaps()
	{
		getHotelImages();
		getRestaurantImages();
		for(int i=0; i<listSize; i++)
		{
			Bitmap bm = (Bitmap)BitmapFactory.decodeResource(this.getResources(), hotdList.get(i));
			hotBmList.add(bm);	   
//			
//			Bitmap bm2 = (Bitmap)BitmapFactory.decodeResource(this.getResources(), restdList.get(i));
//			restBmList.add(bm2);	
		
		}
		
	}
	private void getHotelImages()
	{	
			hotdList.add(R.drawable.hot1);		
			hotdList.add(R.drawable.hot2);
			hotdList.add(R.drawable.hot3);
			hotdList.add(R.drawable.hot4);
			hotdList.add(R.drawable.hot5);
			hotdList.add(R.drawable.hot6);
			hotdList.add(R.drawable.hot7);
			hotdList.add(R.drawable.hot8);
			hotdList.add(R.drawable.hot9);
			hotdList.add(R.drawable.hot10);
			hotdList.add(R.drawable.hot11);
			hotdList.add(R.drawable.hot12);				
	}
	private void getRestaurantImages()
	{
		restdList.add(R.drawable.rest1);		
		restdList.add(R.drawable.rest2);
		restdList.add(R.drawable.rest3);
		restdList.add(R.drawable.rest4);
		restdList.add(R.drawable.rest5);
		restdList.add(R.drawable.rest6);
		restdList.add(R.drawable.rest7);
		restdList.add(R.drawable.rest8);
		restdList.add(R.drawable.rest9);
		//restdList.add(R.drawable.rest10);
		//restdList.add(R.drawable.rest11);
		//restdList.add(R.drawable.rest12);			
	}
	
	public ArrayList<Bitmap> getHotelBitmapList()
	{
		return hotBmList;
	}
	
	public ArrayList<Bitmap> getRestaurantBitmapList()
	{
		return restBmList;
	}
	

}
