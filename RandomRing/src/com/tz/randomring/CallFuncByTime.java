package com.tz.randomring;

public class CallFuncByTime {
	private static long tick = 0;
	public static boolean check(long diff){
		if (tick == 0){
			tick = System.currentTimeMillis();
			return true;
		}
		if (System.currentTimeMillis() - tick > diff){
			tick = System.currentTimeMillis();
			return true;
		}
		return false;
	}
}
