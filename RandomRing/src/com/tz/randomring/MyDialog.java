package com.tz.randomring;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

public class MyDialog {
	@SuppressWarnings("deprecation")
	private static Dialog makeDialog(Context context, int id){
		// 1. 创建对话框;
		Dialog dialog = new Dialog(context, R.style.dialog);
		dialog.setContentView(id);
		// 2. 根据当前屏幕大小, 设置对话框大小; 			
		WindowManager m = ((Activity)context).getWindowManager(); 
		Display d = m.getDefaultDisplay();
		Window dialogWindow = dialog.getWindow();
	    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
	    lp.height = (int)(d.getHeight() * 0.8);
	    lp.width = (int)(d.getWidth() * 0.9);
	    //lp.alpha = 0.95f;
	    //Log.e("tz", "h, w" + lp.height + ", " + lp.width);
	    dialogWindow.setAttributes(lp);
	    // 3. 禁止后退; 
	    // dialog.setCancelable(false);
	    return dialog;
	}
	
	public static void selectRings(Context context){
		final Dialog dialog = makeDialog(context, R.layout.select_ring_dialog);
		final MainActivity a = (MainActivity)context;
		// 1. 填充对话框中的数据; 
		ListView list = (ListView)dialog.findViewById(R.id.list_all_rings);
		final ArrayList<HashMap<String, Object>> data = a.getAllData();
		final AllRingsDialogAdapter sa = new AllRingsDialogAdapter(context, data, R.layout.vlist_all_rings, 
				new String[]{"title", "isSelected"}, 
				new int[]{R.id.tv_title_in_all, R.id.btn_select_ring});
		list.setAdapter(sa);
		// 2. 绑定列表框的事件; 
		list.setOnItemClickListener(new AllRingsOnItemClickListener());
		// 3. 绑定按钮; 
		Button btnSure = (Button)dialog.findViewById(R.id.btn_sure_dialog);
		btnSure.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		Button btnSelectAll = (Button)dialog.findViewById(R.id.btn_select_all_dialog);
		btnSelectAll.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Log.e("tz", "select all start");
				// 1. 通过ringInfo, 把所有的数据写到数据库; 
				a.setAllData();
				// 2. 调用刷新函数, 重置界面; 
				refreshDataDialog();
				Log.e("tz", "select all finish");
			}

			private void refreshDataDialog() {
				// 1. 从数据库中查询数据;
				ArrayList<HashMap<String, Object>> data = a.getData();
				// 2. 把数据表设置到adapter中;
				sa.setData(data);
				// 3. 通知adapter刷新界面; 
				sa.notifyDataSetChanged();
			}
		});
		dialog.setOnDismissListener(new Dialog.OnDismissListener(){
			@Override
			public void onDismiss(DialogInterface arg0) {
				Log.e("tz", "refresh Data");
				a.refreshData();
			}
		});
		// 4. 设置标题; 
		//dialog.setTitle(R.string.select_ring_title);
		dialog.show();
	}
}
