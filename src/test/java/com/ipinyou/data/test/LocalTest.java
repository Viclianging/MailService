package com.ipinyou.data.test;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LocalTest {

	public static void main(String[] args) {
//		test1();
		
//		test2();
//		t1();
		String s = "asd,;ad;da;";
		System.out.println(s.replaceAll(";", "--"));
		System.out.println(s);
	}

	private static void t1() {
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
		MyTask myTask = new MyTask();
		executor.scheduleAtFixedRate(myTask, 2000, 1000, TimeUnit.MILLISECONDS);
		System.out.println("=============");
	}

	private static void test1() {
	    Timer timer = new Timer();
	    timer.schedule(new TimerTask() {
	      public void run() {
	        System.out.println("-------设定要指定任务--------");
	      }
	    }, 20000, 2000);// 设定指定的时间time,此处为2000毫秒
	    System.out.println("==========");
	}

	private static void test2() {
		Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.HOUR_OF_DAY, 9); // 控制时
	    calendar.set(Calendar.MINUTE, 55);    // 控制分
	    calendar.set(Calendar.SECOND, 0);    // 控制秒
	    Date time = calendar.getTime();     // 得出执行任务的时间,此处为今天的12：00：00
	    Timer timer = new Timer();
	    timer.scheduleAtFixedRate(new TimerTask() {
	      public void run() {
	        System.out.println("11-------设定要指定任务--------");
	      }
	    }, time, /*1000 * 60 * 60 * 24*/ 2000);// 这里设定将延时每天固定执行
	    System.out.println("=======");
	}

}

class MyTask implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("-------------task------------");
	}
	
}
