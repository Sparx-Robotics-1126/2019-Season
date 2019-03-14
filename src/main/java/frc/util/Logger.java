package frc.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.StackWalker.Option;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

import frc.subsystem.GenericSubsystem;

public class Logger extends GenericSubsystem {

	private MillisTimer timer;

	private String logName;
	private final String COMPRESSION_MODE = "TAR.GZ";
//	private final String LOGS_DIRECTORY_LOCATION = "/home/lvuser/logs/"; //starting from the home directory
		private final String LOGS_DIRECTORY_LOCATION = "C:\\Sparx\\"; //starting from the home directory
	private boolean logReady;
	private final boolean LOG_TO_CONSOLE = true;
	private final static int MAXSAVEDFILES = 10;
	private String[] stackInfo;

	private PrintStream systemOut;

	private ConcurrentLinkedDeque<String> printToLog;
	private File logFile;

	private StackWalker stw;

	public Logger() {
		super("Logger", Thread.MIN_PRIORITY);
		logReady = false;
		if(!makeLogsDir()) { 
			return;			  
		}					

		stackInfo = new String[3];
		stw = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
		updateFiles();
		logName = LocalDateTime.now().getDayOfMonth() + "_" + LocalDateTime.now().getMonth() + "_" + LocalDateTime.now().getYear() + 
				"-" + LocalDateTime.now().getHour() + "_" + LocalDateTime.now().getMinute() + "_" + LocalDateTime.now().getSecond() + ".log";
		timer = new MillisTimer();
		timer.start();
		try {
			logFile = new File(LOGS_DIRECTORY_LOCATION + "0-" + logName);
			logFile.createNewFile();
			printToLog = new ConcurrentLinkedDeque<String>();
			log("LOGGER", "INFO", "Log created at " + logFile.getAbsolutePath());
		} catch (Exception e) {
			return;
		}
		logReady = true;
		printOverride();
	}

	public void printOverride() {
		if(logReady) {
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
						println("", 4);
					}

					@Override
					public void println(boolean x) {
						println(x + "", 4);
					}

					@Override
					public void println(char x) {
						println(x + "", 4);
					}

					@Override
					public void println(char[] x) {
						println(String.valueOf(x), 4);
					}

					@Override
					public void println(double x) {
						println(x + "", 4);
					}

					@Override
					public void println(float x) {
						println(x + "", 4);
					}

					@Override
					public void println(int x) {
						println(x + "", 4);
					}

					@Override
					public void println(long x) {
						println(x + "", 4);
					}

					@Override
					public void println(Object x) {
						println(String.valueOf(x), 4);
					}

					@Override
					public void println(String x) {
						systemOut.println(x);
						logOut(x + "\n", Tag.INFO);
					}

					public void println(String x, int stack) {
						systemOut.println(x);
						logOut(x + "\n", Tag.INFO, stack);
					}

					@Override
					public void print(boolean x) {
						print(x + "", 5);
					}

					@Override
					public void print(char x) {
						print(x + "", 5);				
					}

					@Override
					public void print(char[] x) {
						print(String.valueOf(x), 5);
					}

					@Override
					public void print(double x) {
						print(x + "", 5);				
					}

					@Override
					public void print(float x) {
						print(x + "", 5);
					}

					@Override
					public void print(int x) {
						print(x + "", 5);
					}

					@Override
					public void print(long x) {
						print(x + "", 5);
					}

					@Override
					public void print(Object x) {
						print(String.valueOf(x), 5);
					}

					@Override
					public void print(String x) {
						systemOut.print(x);
						logOut(x + "\n", Tag.INFO, 4);
					}

					public void print(String x, int stack) {
						systemOut.print(x);
						logOut(x + "\n", Tag.INFO, stack);
					}

				};/*
				PrintStream printStreamErr = new PrintStream(os) {

					@Override
					public void println() {
						println("", 4);
					}

					@Override
					public void println(boolean x) {
						println(x + "", 4);
					}

					@Override
					public void println(char x) {
						println(x + "", 4);
					}

					@Override
					public void println(char[] x) {
						println(String.valueOf(x), 4);
					}

					@Override
					public void println(double x) {
						println(x + "", 4);
					}

					@Override
					public void println(float x) {
						println(x + "", 4);
					}

					@Override
					public void println(int x) {
						println(x + "", 4);
					}

					@Override
					public void println(long x) {
						println(x + "", 4);
					}

					@Override
					public void println(Object x) {
						println(String.valueOf(x));
					}

					@Override
					public void println(String x) {
						logOut(x + "\n", Tag.ERROR);
					}

					public void println(String x, int stack) {
						logOut(x + "\n", Tag.ERROR, stack);
					}

					@Override
					public void print(boolean x) {
						print(x + "", 5);
					}

					@Override
					public void print(char x) {
						print(x + "", 5);				
					}

					@Override
					public void print(char[] x) {
						print(String.valueOf(x), 5);
					}

					@Override
					public void print(double x) {
						print(x + "", 5);				
					}

					@Override
					public void print(float x) {
						print(x + "", 5);
					}

					@Override
					public void print(int x) {
						print(x + "", 5);
					}

					@Override
					public void print(long x) {
						print(x + "", 5);
					}

					@Override
					public void print(Object x) {
						print(String.valueOf(x), 5);
					}

					@Override
					public void print(String x) {
						println(x, 4);
					}

					public void print(String x, int stack) {
						println(x, 5);
					}

				};*/
				systemOut = System.out;
				System.setOut(printStream);
				//				System.setErr(printStreamErr);
			}
		}


	}

	public synchronized void loggerNotify() {
		this.notify();
	}

	public void getStackInfo(boolean getClass, int stack){
		StackWalker.StackFrame frame = stw.walk(stream1 -> stream1.skip(stack).findFirst().orElse(null));
		if(getClass) {
			stackInfo[0] = frame.getClassName();
			stackInfo[2] = "" + frame.getLineNumber();
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

	public void logOut(String message, Tag type, int stack) {
		if(stw != null) {
			getStackInfo(true, stack);
		}
		String log = "[" + timerToHMS() + "][" + stackInfo[0] + "][" + stackInfo[1] + "][" + stackInfo[2] + "][" + type.toString() + "] " + message;
		printToLog.add(log);
		//		synchronized(printThread) {
		//			printThread.notify();
		//		}

	}

	public void logOut(String message, Tag type) {
		logOut(message, type, 4);
	}

	public void log(String subsystem, String method, Tag type, String message) {
		String log = "[" + timerToHMS() + "][" + subsystem.toUpperCase() + "][" + method.toUpperCase() + "][" + type.toString().toUpperCase() + "] " + message;
		if(logReady && LOG_TO_CONSOLE) {
			//			synchronized(dataToPrint) {
			printToLog.add(log);
			//			}
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
		log(stackInfo[0], stackInfo[1], Tag.INFO, message);
	}

	private String timerToHMS() {
		return timer.getHMS();
	}

	private boolean makeLogsDir() {
		File logsDirectory = new File(LOGS_DIRECTORY_LOCATION);
		logsDirectory.mkdirs();
		if(!logsDirectory.exists()) {
			return false;
		}
		return true;
	}

	private void updateFiles() {
		File logsDirectory = new File(LOGS_DIRECTORY_LOCATION);
		File[] files = logsDirectory.listFiles();
		ArrayList<String> logs = new ArrayList<String>();
		String name;
		int counter;
		for(File file: files) {
			name = file.getName();
			if(name.indexOf('-') != -1) {
				counter = Integer.parseInt(name.substring(0, name.indexOf('-')));
				System.out.println(counter);
				if(name.endsWith(".tar.gz") || name.endsWith(".log")) {
					if(counter > MAXSAVEDFILES) {
						file.delete();
					} else {
						file.renameTo(new File((counter + 1) + name.substring(name.indexOf('-'))));
					}
				}
				if(name.endsWith(".log")) {
					logs.add((counter + 1) + name.substring(name.indexOf('-'), name.lastIndexOf('.')));
				}
			}

		}
		if(COMPRESSION_MODE.equals("TAR.GZ")) {
			for(String str: logs) {
				File compressedFile = new File(LOGS_DIRECTORY_LOCATION + str + ".tar.gz");
				File originalFile = new File(LOGS_DIRECTORY_LOCATION + str + ".log");
				compressedFile.delete();
				try {
					Process ps = Runtime.getRuntime().exec("tar -zcf " + str + ".tar.gz " + str + ".log", null, compressedFile.getParentFile());
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
					Process ps = Runtime.getRuntime().exec("zip " + str + ".zip " + str + ".log", null, compressedFile.getParentFile());
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
				sleep(2000);
				if(logReady) {
					while(printToLog.size() > 0) {
						builder.append(printToLog.remove());
					}
					Files.write(logFile.toPath(), builder.toString().getBytes(), StandardOpenOption.APPEND, StandardOpenOption.DSYNC, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
					builder.setLength(0);
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
	public long sleepTime() {
		// TODO Auto-generated method stub
		return 0;
	}

}