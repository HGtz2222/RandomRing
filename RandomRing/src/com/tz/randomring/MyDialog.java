package com.tz.randomring;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
		// 1. �����Ի���;
		Dialog dialog = new Dialog(context);
		dialog.setContentView(id);
		// 2. ���ݵ�ǰ��Ļ��С, ���öԻ����С; 			
		WindowManager m = ((Activity)context).getWindowManager(); 
		Display d = m.getDefaultDisplay();
		Window dialogWindow = dialog.getWindow();
	    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
	    lp.height = (int)(d.getHeight() * 0.8);
	    lp.width = (int)(d.getWidth() * 0.9);
	    //Log.e("tz", "h, w" + lp.height + ", " + lp.width);
	    dialogWindow.setAttributes(lp);
	    // 3. ��ֹ����; 
	    // dialog.setCancelable(false);
	    return dialog;
	}
	
	public static void selectRings(Context context){
		final Dialog dialog = makeDialog(context, R.layout.select_ring_dialog);
		final MainActivity a = (MainActivity)context;
		// 1. ���ñ���; 
		dialog.setTitle(R.string.select_ring_title);
		// 2. ���Ի����е�����; 
		ListView list = (ListView)dialog.findViewById(R.id.list_all_rings);
		final ArrayList<HashMap<String, Object>> data = a.getAllData();
		AllRingsDialogAdapter sa = new AllRingsDialogAdapter(context, data, R.layout.vlist_all_rings, 
				new String[]{"title", "isSelected"}, 
				new int[]{R.id.tv_title_in_all, R.id.btn_select_ring});
		list.setAdapter(sa);
		// 3. ���б����¼�; 
		list.setOnItemClickListener(new AllRingsOnItemClickListener());
		// 4. �󶨰�ť; 
		Button btnSure = (Button)dialog.findViewById(R.id.btn_sure_dialog);
		btnSure.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				a.refreshData();
			}
		});
		Button btnCancel = (Button)dialog.findViewById(R.id.btn_cancel_dialog);
		btnCancel.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				a.refreshData();
			}
		});
		dialog.show();
	}
}
