package com.ipinyou.data.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.util.ObjectUtils;

import com.ipinyou.data.exception.WebMailServiceException;

public class MailTaskJobRobot {

	private TimerTask task;
	private Runnable runnable;

	private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(7);

	private Map<Long, ScheduledFuture<?>> schedules = new HashMap<Long, ScheduledFuture<?>>();

	public void setTask(TimerTask task) {
		this.task = task;
	}
	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}

	public void once(long delay) {
		Timer timer = new Timer();
		timer.schedule(this.task, delay);
	}

	public Long poolOnce(long delay) {
		ScheduledFuture<?> schedule = executor.schedule(this.runnable, delay, TimeUnit.MILLISECONDS);
		long currentTimeMillis = System.currentTimeMillis();
		this.schedules.put(currentTimeMillis, schedule);
		this.removeTask();
		return currentTimeMillis;
	}

	public void removeTask() {
		executor.remove(this.runnable);
	}

	public void removeTask(Long index) {
		if (index <= 0l || this.schedules.isEmpty()) {
			return;
		}
		ScheduledFuture<?> scheduledFuture = this.schedules.get(index);
		boolean cancel = scheduledFuture.cancel(true);
		boolean remove = this.schedules.remove(index, scheduledFuture);
		if (cancel && remove && this.schedules.isEmpty()) {
			this.executor.shutdown();
		}
	}

	public void frequencyTask(String period) {
		int hour = 1000 * 60 * 60;
		BigDecimal bigDecimal = new BigDecimal(MailUtils.calculateEquation(period));
		int compareTo = bigDecimal.compareTo(new BigDecimal(0));
		long taskPeriod = compareTo < 0 ? hour * 1 : bigDecimal.multiply(new BigDecimal(hour)).longValue();
		Timer timer = new Timer();
	    timer.schedule(this.task, 500, taskPeriod);
	}

	public Long poolFrequencyTask(String period) {
		int hour = 1000 * 60 * 60;
		BigDecimal bigDecimal = new BigDecimal(MailUtils.calculateEquation(period));
		int compareTo = bigDecimal.compareTo(new BigDecimal(0));
		long taskPeriod = compareTo < 0 ? hour * 1 : bigDecimal.multiply(new BigDecimal(hour)).longValue();
		ScheduledFuture<?> scheduleAtFixedRate = executor.scheduleAtFixedRate(this.runnable, 500, taskPeriod, TimeUnit.MILLISECONDS);
		long currentTimeMillis = System.currentTimeMillis();
		this.schedules.put(currentTimeMillis, scheduleAtFixedRate);
		this.removeTask();
		return currentTimeMillis;
	}

	public void once(String date) {
		int[] dateStruct = this.analyseDate(date);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, dateStruct[0]);
		calendar.set(Calendar.MONTH, dateStruct[1]);
		calendar.set(Calendar.DAY_OF_MONTH, dateStruct[2]);
	    calendar.set(Calendar.HOUR_OF_DAY, dateStruct[3]);
	    calendar.set(Calendar.MINUTE, dateStruct[4]);
	    calendar.set(Calendar.SECOND, dateStruct[5]);
	    Date time = calendar.getTime();
	    Timer timer = new Timer();
	    timer.schedule(this.task, time);
	}

	public Long poolOnce(String date) {
		int[] dateStruct = this.analyseDate(date);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, dateStruct[0]);
		calendar.set(Calendar.MONTH, dateStruct[1]);
		calendar.set(Calendar.DAY_OF_MONTH, dateStruct[2]);
	    calendar.set(Calendar.HOUR_OF_DAY, dateStruct[3]);
	    calendar.set(Calendar.MINUTE, dateStruct[4]);
	    calendar.set(Calendar.SECOND, dateStruct[5]);
	    long millisecondsInterval = calendar.getTimeInMillis() - System.currentTimeMillis();
	    ScheduledFuture<?> schedule = executor.schedule(this.runnable, millisecondsInterval, TimeUnit.MILLISECONDS);
	    long currentTimeMillis = System.currentTimeMillis();
	    this.schedules.put(currentTimeMillis, schedule);
	    this.removeTask();
	    return currentTimeMillis;
	}

	public void dateTask(String date, String period) {
		int[] dateStruct = this.analyseDate(date);
		int day = 1000 * 60 * 60 * 24;
		BigDecimal bigDecimal = new BigDecimal(MailUtils.calculateEquation(period));
		int compareTo = bigDecimal.compareTo(new BigDecimal(0));
		long taskPeriod = compareTo < 0 ? day * 1 : bigDecimal.multiply(new BigDecimal(day)).longValue();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, dateStruct[0]);
		calendar.set(Calendar.MONTH, dateStruct[1]);
		calendar.set(Calendar.DAY_OF_MONTH, dateStruct[2]);
	    calendar.set(Calendar.HOUR_OF_DAY, dateStruct[3]);
	    calendar.set(Calendar.MINUTE, dateStruct[4]);
	    calendar.set(Calendar.SECOND, dateStruct[5]);
	    Date time = calendar.getTime();
	    Timer timer = new Timer();
	    timer.scheduleAtFixedRate(this.task, time, taskPeriod);
	}

	public Long poolDateTask(String date, String period) {
		int[] dateStruct = this.analyseDate(date);
		int day = 1000 * 60 * 60 * 24;
		BigDecimal bigDecimal = new BigDecimal(MailUtils.calculateEquation(period));
		int compareTo = bigDecimal.compareTo(new BigDecimal(0));
		long taskPeriod = compareTo < 0 ? day * 1 : bigDecimal.multiply(new BigDecimal(day)).longValue();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, dateStruct[0]);
		calendar.set(Calendar.MONTH, dateStruct[1]);
		calendar.set(Calendar.DAY_OF_MONTH, dateStruct[2]);
	    calendar.set(Calendar.HOUR_OF_DAY, dateStruct[3]);
	    calendar.set(Calendar.MINUTE, dateStruct[4]);
	    calendar.set(Calendar.SECOND, dateStruct[5]);
	    long millisencodsInterval = calendar.getTimeInMillis() - System.currentTimeMillis();
	    ScheduledFuture<?> scheduleAtFixedRate = executor.scheduleAtFixedRate(this.runnable, millisencodsInterval, taskPeriod, TimeUnit.MILLISECONDS);
	    long currentTimeMillis = System.currentTimeMillis();
	    this.schedules.put(currentTimeMillis, scheduleAtFixedRate);
	    this.removeTask();
	    return currentTimeMillis;
	}

	private int[] analyseDate(String date) {
		int[] dateStruct = new int[6];
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		try {
			Date parse = dateFormat.parse(date);
			if (parse.before(new Date())) {
				throw new WebMailServiceException("ERROR[0]:invalid param of date you input.It must after nowaday timstemp.'" + date + "'");
			}
			dateStruct[0] = Integer.valueOf(date.substring(6, 10));
			dateStruct[1] = Integer.valueOf(date.substring(0, 2)) - 1;
			dateStruct[2] = Integer.valueOf(date.substring(3, 5));
			dateStruct[3] = Integer.valueOf(date.substring(11, 13));
			dateStruct[4] = Integer.valueOf(date.substring(14, 16));
			dateStruct[5] = Integer.valueOf(date.substring(17, 19));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateStruct;
	}

	public void certification() {
		if (ObjectUtils.isEmpty(this.task)) {
			throw new WebMailServiceException("ERROR[0]:Please setting your task on 'com.ipinyou.data.utils.MailTaskJobRobot' tool.");
		}
	}

	public static void main(String[] args) {
		MailTaskJobRobot robot = new MailTaskJobRobot();
		robot.setRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("----任务一----");
			}
		});
		robot.poolDateTask("08/17/2017 20:07:30", "1/(60*60*24)");
		robot.setRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("====任务二====");
			}
		});
		robot.poolDateTask("08/17/2017 20:07:40", "2/(60*60*24)");
		robot.setRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("task1 killed...");
				robot.removeTask(0l);
			}
		});
		robot.poolOnce(60000);
		robot.setRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("task2 killed...");
				robot.removeTask(1l);
			}
		});
		robot.poolOnce(65000);
	}

}
