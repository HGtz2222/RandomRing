package com.tz.randomring;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class AllRingsDialogAdapter extends BaseAdapter{
	private ArrayList<HashMap<String, Object>> data;
	private int uiId;
	private Context context;
	private String[] columnName;
	private int[] ctrlId;
	private LayoutInflater mInflater = null; 
	
	public AllRingsDialogAdapter(Context context,
			ArrayList<HashMap<String, Object>> data, int vlistAllRings,
			String[] strings, int[] is) {
		this.data = data;
		this.uiId = vlistAllRings;
		this.context = context;
		this.columnName = strings;
		this.ctrlId = is;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	static class ViewHolder  
    {  
        public TextView title;  
        public CheckBox isSelected;  
    }  
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		final int index = pos;
		ViewHolder holder = null;
		if (convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(uiId, null);
			// TODO ����д��JB, , , 
			holder.title = (TextView)convertView.findViewById(ctrlId[0]);
			holder.isSelected = (CheckBox)convertView.findViewById(ctrlId[1]);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		HashMap<String, Object> map = data.get(index);
		holder.title.setText((String)map.get(columnName[0]));
		holder.isSelected.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				Log.e("tz", "onCheckedChanged " + isChecked);
				HashMap<String, Object> map = data.get(index);
				map.put(columnName[1], isChecked);
				data.set(index, map);
			}
		});
		return convertView;
	}

}
