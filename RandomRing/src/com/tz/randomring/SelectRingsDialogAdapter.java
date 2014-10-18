package com.tz.randomring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.tz.randomring.AllRingsDialogAdapter.ViewHolder;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class SelectRingsDialogAdapter extends BaseAdapter{
	private ArrayList<HashMap<String, Object>> data;
	private int uiId;
	private Context context;
	private String[] columnName;
	private int[] ctrlId;
	private LayoutInflater mInflater = null; 
	
	public SelectRingsDialogAdapter(Context context,
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
        public Button title;  
        public ImageButton btnPlaying;
        public ImageButton btnDel;  
    } 
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		final int index = pos;
		ViewHolder holder = null;
		if (convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(uiId, null);
			holder.title = (Button)convertView.findViewById(ctrlId[0]);
			holder.btnPlaying = (ImageButton)convertView.findViewById(ctrlId[1]);
			holder.btnDel = (ImageButton)convertView.findViewById(ctrlId[2]);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final ImageButton btnPlaying = holder.btnPlaying;
		HashMap<String, Object> map = data.get(index);
		holder.title.setText((String)map.get(columnName[0]));
		holder.title.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View view) {
				HashMap<String, Object> map = data.get(index);
				String path = (String)map.get("data");
				Log.i("tz", "onItemClick play ring " + path);
				MainActivity a = (MainActivity)context;
				a.playRing(path, index); 
				// MediaPlayerSingleton.playRing(path, view, btnPlaying);
			}
		});
		holder.btnDel.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Log.e("tz", "delete item");
				HashMap<String, Object> map = data.get(index);
				map.put("isSelected", Boolean.FALSE);
				// 2. 删除数据库; 
				MainActivity a = (MainActivity)context;
				a.updateDB(map);
				// 3. 更新界面表的数据; 
				a.refreshData();
			}
		});
		return convertView;
	}

	public void setData(ArrayList<HashMap<String, Object>> data) {
		this.data = data;
	}

}
