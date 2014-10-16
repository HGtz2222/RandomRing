package com.tz.randomring;

import java.util.ArrayList;
import java.util.Random;

import android.util.Log;

public class RandomInt {
	private static Random r;  
	private static ArrayList<Integer> oldNum = new ArrayList<Integer>();
	
	public static int roll(int beg, int end){
		if (end <= beg){
			return 0;
		}
		if (r == null){
			r = new Random(System.currentTimeMillis());
		}
		int rs = beg + r.nextInt(end - beg);
		Log.e("tz", "random: " + rs);		
		return rs;
	}
	
	public static int rollUnique(int beg, int end){
		if (end <= beg){
			return 0;
		}
		if (r == null){
			r = new Random(System.currentTimeMillis());
		}
		int rs = 0;
		int loopCnt = 0;
		while(true){
			// ·ÀËÀÑ­»·»úÖÆ; 
			if (loopCnt > 1000){
				loopCnt = 0;
				oldNum.clear();
			}
			++loopCnt;
			rs = beg + r.nextInt(end - beg);
			if (oldNum.indexOf(rs) == -1){
				oldNum.add(rs);
				return rs;
			}
		}
	}
}
