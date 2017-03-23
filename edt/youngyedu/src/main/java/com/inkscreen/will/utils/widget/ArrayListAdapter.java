package com.inkscreen.will.utils.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public abstract class ArrayListAdapter<T> extends BaseAdapter{
	
	protected ArrayList<T> mList;
	public Context mContext;
	protected ListView mListView;
	
	public ArrayListAdapter(Context context){
		this.mContext = context;
	}

	@Override
	public int getCount() {
		if(mList != null)
			return mList.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	abstract public View getView(int position, View convertView, ViewGroup parent);
	
	public void setList(ArrayList<T> list){
//		if(this.mList!=null){
//			this.mList.clear();
//		}
		this.mList = list;
		notifyDataSetChanged();
	}
	
	public ArrayList<T> getList(){
		return mList;
	}
	
	public void setList(T[] list){
		ArrayList<T> arrayList = new ArrayList<T>(list.length);  
		for (T t : list) {  
			arrayList.add(t);  
		}  
		setList(arrayList);
	}
	
	public ListView getListView(){
		return mListView; 
	}
	
	public void setListView(ListView listView){
		mListView = listView;
	}
	
	@Override 
	public void unregisterDataSetObserver(DataSetObserver observer) {
	     if (observer != null) {
	         super.unregisterDataSetObserver(observer);
	     }
	 }

}
