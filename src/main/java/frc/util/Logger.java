package frc.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.StackWalker.Option;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import edu.wpi.first.wpilibj.Timer;
import frc.subsystem.GenericSubsystem;

public class Logger extends GenericSubsystem {

	private Timer timer;

	private String logName;
	private final String COMPRESSION_MODE = "TAR.GZ";
	private final String COUNTER_FILE = "counter.txt";
	private final String LOGS_DIRECTORY_LOCATION = "/home/lvuser/logs/"; //starting from the home directory
	//	private final String LOGS_DIRECTORY_LOCATION = "C:\\Users\\Spenser\\Documents\\Miscellaneous\\Sparx\\"; //starting from the home directory
	private boolean logReady;
	private final boolean LOG_TO_CONSOLE = true;
	private String[] stackInfo;

	private PrintStream systemOut;
	private Thread printThread;
	private DecimalFormat df;
	private DecimalFormat df2;

	private ArrayDeque<String> dataToPrint;
	private ArrayDeque<String> printToLog;
	private File logFile;

	private StackWalker stw;

	public Logger() {
		super("Logger", Thread.MIN_PRIORITY);
		logReady = false;
		if(!makeLogsDir()) { 
			return;			  
		}					
		long counterNumber = readCounter();
		if(counterNumber == -1) {
			return;
		}
		dataToPrint = new ArrayDeque<String>();
		df = new DecimalFormat();
		df.setGroupingUsed(false);
		df.setMinimumFractionDigits(3);
		df.setMaximumFractionDigits(3);
		df.setMinimumIntegerDigits(2);
		df2 = new DecimalFormat();
		df2.setGroupingUsed(false);
		df2.setMinimumIntegerDigits(2);
		stackInfo = new String[2];
		stw = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
		compression();
		logName = LocalDateTime.now().getDayOfMonth() + "_" + LocalDateTime.now().getMonth() + "_" + LocalDateTime.now().getYear() + 
				"-" + LocalDateTime.now().getHour() + "_" + LocalDateTime.now().getMinute() + "_" + LocalDateTime.now().getSecond() + ".log";
		timer = new Timer();
		timer.start();
		try {
			logFile = new File(LOGS_DIRECTORY_LOCATION + counterNumber + "-" + logName);
			logFile.createNewFile();
			printToLog = new ArrayDeque<String>();
			log("LOGGER", "INFO", "Log created at " + logName);
		} catch (Exception e) {
			return;
		}
		logReady = true;
		printOverride();
	}

	public void printOverride() {
		if(printThread == null) {
			printThread = new Thread(new Runnable() {

				@Override
				public void run() {
					String str = null;
					while(!isInterrupted()) {
						if(!dataToPrint.isEmpty()) {
							synchronized(dataToPrint) {
								str = "[" + timerToHMS() + "]" + dataToPrint.remove();
								systemOut.print(str);
							}
						}
						if(str != null) {
							synchronized(printToLog) {
								printToLog.push(str);
								loggerNotify();
								str = null;
							}
						}
						try {
							synchronized(printThread) {
								printThread.wait();
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			printThread.setPriority(Thread.MIN_PRIORITY);
		}
		if(!printThread.isAlive()) {
			printThread.start();
		}
		if(systemOut == null) {
			OutputStream os = new OutputStream() {
				@Override
				public void write(int b) throws IOException {
					return;
				}
			};
			PrintStream printStream = new PrintStream(os) {

				@Override
				public void println() {
					println("");
				}

				@Override
				public void println(boolean x) {
					println(x + "");
				}

				@Override
				public void println(char x) {
					println(x + "");
				}

				@Override
				public void println(char[] x) {
					println(String.valueOf(x));
				}

				@Override
				public void println(double x) {
					println(x + "");
				}

				@Override
				public void println(float x) {
					println(x + "");
				}

				@Override
				public void println(int x) {
					println(x + "");
				}

				@Override
				public void println(long x) {
					println(x + "");
				}

				@Override
				public void println(Object x) {
					println(String.valueOf(x));
				}

				@Override
				public void println(String x) {
					logOut(x + "\n", Tag.INFO);
				}

				@Override
				public void print(boolean x) {
					print(x + "");
				}

				@Override
				public void print(char x) {
					print(x + "");				
				}

				@Override
				public void print(char[] x) {
					print(String.valueOf(x));
				}

				@Override
				public void print(double x) {
					print(x + "");				
				}

				@Override
				public void print(float x) {
					print(x + "");
				}

				@Override
				public void print(int x) {
					print(x + "");
				}

				@Override
				public void print(long x) {
					print(x + "");
				}

				@Override
				public void print(Object x) {
					print(String.valueOf(x));
				}

				@Override
				public void print(String x) {
					println(x);
				}

			};
			PrintStream printStreamErr = new PrintStream(os) {

				@Override
				public void println() {
					println("");
				}

				@Override
				public void println(boolean x) {
					println(x + "");
				}

				@Override
				public void println(char x) {
					println(x + "");
				}

				@Override
				public void println(char[] x) {
					println(String.valueOf(x));
				}

				@Override
				public void println(double x) {
					println(x + "");
				}

				@Override
				public void println(float x) {
					println(x + "");
				}

				@Override
				public void println(int x) {
					println(x + "");
				}

				@Override
				public void println(long x) {
					println(x + "");
				}

				@Override
				public void println(Object x) {
					println(String.valueOf(x));
				}

				@Override
				public void println(String x) {
					logOut(x, Tag.ERROR);
				}

				@Override
				public void print(boolean x) {
					print(x + "");
				}

				@Override
				public void print(char x) {
					print(x + "");				
				}

				@Override
				public void print(char[] x) {
					print(String.valueOf(x));
				}

				@Override
				public void print(double x) {
					print(x + "");				
				}

				@Override
				public void print(float x) {
					print(x + "");
				}

				@Override
				public void print(int x) {
					print(x + "");
				}

				@Override
				public void print(long x) {
					print(x + "");
				}

				@Override
				public void print(Object x) {
					print(String.valueOf(x));
				}

				@Override
				public void print(String x) {
					println(x);
				}

			};
			systemOut = System.out;
			System.setOut(printStream);
			System.setErr(printStreamErr);
		}
		
	}
	
	public synchronized void loggerNotify() {
		this.notify();
	}

	public void getStackInfo(boolean getClass, int stack){
		StackWalker.StackFrame frame = stw.walk(stream1 -> stream1.skip(stack).findFirst().orElse(null));
		if(getClass) {
			stackInfo[0] = frame.getClassName();
			
			if(stackInfo[0].indexOf('.') != -1) {
				stackInfo[0] = stackInfo[0].substring(stackInfo[0].lastIndexOf('.') + 1);
			}
			if(stackInfo[0].indexOf('$') != -1) {
				stackInfo[0] = stackInfo[0].substring(0, stackInfo[0].indexOf('$'));
			}
		}
		stackInfo[1] = frame.getMethodName();
	}

	public void getStackInfo(boolean getClass){
		getStackInfo(getClass, 2);
	}

	public enum Tag{
		INFO,			//Used for software-related logs, default
		ERROR,			//Used for errors
		CRITICAL,		//Used for Important problems that are not yet errors
		WARNING,		//Used for Lower level problems
		START,			//Used at the start of commands
		END,			//Used at the end of commands
		STATUS,			//Used for logging the state of hardware
		INTERRUPTED;	//Used when commands have been interrupted
	}

	public void logOut(String message, Tag type) {
		if(stw != null) {
			getStackInfo(true, 3);
		}
		String log = "[" + stackInfo[0].toUpperCase() + "][" + stackInfo[1].toUpperCase() + "][" + type.toString() + "] " + message;
		synchronized(dataToPrint) {
			dataToPrint.add(log);
		}
		synchronized(printThread) {
			printThread.notify();
		}
		
	}

	public void log(String subsystem, String method, Tag type, String message) {
		String log = "[" + timerToHMS() + "][" + subsystem.toUpperCase() + "][" + method.toUpperCase() + "][" + type.toString().toUpperCase() + "] " + message;
		if(logReady && LOG_TO_CONSOLE) {
			synchronized(dataToPrint) {
				dataToPrint.add(log);
			}
			synchronized(printThread) {
				printThread.notify();
			}
		} else {
			if(!logReady) {
				System.out.println(log);
			}
		}
	}

	public void log(String subsystem, Tag type, String message) {
		if(stw != null) {
			getStackInfo(false);
		}
		log(subsystem, stackInfo[1], type, message);
	}

	public void log(String subsystem, String method, String message) {
		log(subsystem, method, Tag.INFO, message);
	}

	public void log(String subsystem, String message) {
		log(subsystem, Tag.INFO, message);
	}

	public void log(String message) {
		if(stw != null) {
			getStackInfo(true);
		}
		log(stackInfo[0].toUpperCase(), stackInfo[1], Tag.INFO, message);
	}

	private String timerToHMS() {
		double timeDouble = timer.get();
		return (df2.format((int) (timeDouble / 3600))) + ":" + df2.format(((int) ((timeDouble / 60) % 60))) + ":" + df.format(timeDouble % 60);
	}

	private boolean makeLogsDir() {
		File logsDirectory = new File(LOGS_DIRECTORY_LOCATION);
		logsDirectory.mkdirs();
		if(!logsDirectory.exists()) {
			return false;
		}
		return true;
	}

	private long readCounter() {//we probably don't need a long but w/e "just in case"
		try {
			File counter = new File(LOGS_DIRECTORY_LOCATION + COUNTER_FILE);
			if(counter.exists()) {
				Scanner sc = new Scanner(new FileReader(counter));
				long ln = sc.nextLong();
				sc.close();
				Files.write(counter.toPath(), ((ln + 1) + "").getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.DSYNC, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
				return ln;
			} else {
				counter.createNewFile();
				Files.write(counter.toPath(), "2".getBytes());
				return 1;
			}
		} catch(Exception e) {
		}
		return -1;
	}

	private void compression() {
		File logsDirectory = new File(LOGS_DIRECTORY_LOCATION);
		File[] files = logsDirectory.listFiles();
		ArrayList<String> logs = new ArrayList<String>();
		for(File file: files) {
			if(file.getName().endsWith(".log")) {
				logs.add(file.getName().substring(0, file.getName().lastIndexOf('.')));
			}
		}
		if(COMPRESSION_MODE.equals("TAR.GZ")) {
			for(String str: logs) {
				File compressedFile = new File(LOGS_DIRECTORY_LOCATION + str + ".tar.gz");
				File originalFile = new File(LOGS_DIRECTORY_LOCATION + str + ".log");
				compressedFile.delete();
				try {
					Process ps = Runtime.getRuntime().exec("tar -zcf " + str + ".tar.gz " + str + ".log");
					ps.waitFor();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(compressedFile.exists()) {
					originalFile.delete();
				}
			}
		} else if(COMPRESSION_MODE.equals("ZIP")) {
			for(String str: logs) {
				File compressedFile = new File(LOGS_DIRECTORY_LOCATION + str + ".zip");
				File originalFile = new File(LOGS_DIRECTORY_LOCATION + str + ".log");
				compressedFile.delete();
				try {
					Process ps = Runtime.getRuntime().exec("zip " + str + ".zip " + str + ".log");
					ps.waitFor();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(compressedFile.exists()) {
					originalFile.delete();
				}
			}
		} else {
			System.err.println("[LOGGER][ERROR] Unsupported compression mode - " + COMPRESSION_MODE);
		}

	}


	@Override
	public void init() {
	}

	@Override
	public void run() {
		StringBuilder builder = new StringBuilder();
		while(!isInterrupted()) {
			try {
				sleep(1000);
				if(logReady) {
					while(printToLog.size() > 0) {
						synchronized (printToLog) {
							builder.append(printToLog.poll() + "\n");
						}
						Files.write(logFile.toPath(), builder.toString().getBytes(), StandardOpenOption.APPEND, StandardOpenOption.DSYNC, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
						builder.setLength(0);
					}
				}
				synchronized(this) {
					wait();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	@Override
	public void execute() {
		
	}

	@Override
	public void debug() {
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public long sleepTime() {
		// TODO Auto-generated method stub
		return 0;
	}
}