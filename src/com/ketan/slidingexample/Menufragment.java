package com.ketan.slidingexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class Menufragment extends SherlockFragment {
	ListView list;
	MenuClickInterFace mClick;

	interface MenuClickInterFace {
		void onListitemClick(String item);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mClick = (MenuClickInterFace) activity;
	}
/*
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		list = (ListView) getView().findViewById(R.layout.menulist);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String i=(String) arg0.getItemAtPosition(arg2);
				mClick.onListitemClick(i);
			}
		});
	}
*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.menulist, container, false);
		return v;
	}

}
