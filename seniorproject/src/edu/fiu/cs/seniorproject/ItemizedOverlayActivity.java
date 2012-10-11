package edu.fiu.cs.seniorproject;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class ItemizedOverlayActivity extends ItemizedOverlay{

private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	private Context mContext;
	
	//constructor
	public ItemizedOverlayActivity(Drawable defaultMarker) {
		  super(boundCenterBottom(defaultMarker));
		}
	
	public ItemizedOverlayActivity(Drawable defaultMarker, Context context) {
		  super(boundCenterBottom(defaultMarker));
		  mContext = context;
		}
	
	//method to add overlay objects to the mOverlays list
	//and populate it
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}
	
	@Override
	public int size() {
	  return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  return true;
	}
	
	
}
