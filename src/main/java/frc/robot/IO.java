/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * Add your docs here.
 */
public enum IO {
	
	DRIVES_RIGHTMOTOR_1(10, "Drives right motor"),
	DRIVES_RIGHTMOTOR_2(11, "Drives right motor"),
	DRIVES_RIGHTMOTOR_3(12, "Drives right motor"),
	DRIVES_LEFTMOTOR_1(1, "Drives left motor"),
	DRIVES_LEFTMOTOR_2(2, "Drives left motor"),
	DRIVES_LEFTMOTOR_3(3, "Drives left motor"),
	
	DRIVES_RIGHTENCODER_CH1(10, "Drives right encoder"), //11
	DRIVES_RIGHTENCODER_CH2(11, "Drives right encoder"), //10
	DRIVES_LEFTENCODER_CH1(12, "Drives left encoder"),
	DRIVES_LEFTENCODER_CH2(13, "Drives left encoder"),
	
	DRIVES_PTOSOLENOID(1, "Arms PTO"),					//0
	DRIVES_SHIFTINGSOLENOID(0, "Drives shifter"),		//1
	DRIVES_UNSNAPPY(2, "Unsnappy"),
	
	ARMS_LIMITSWITCH_LEFT(6, "Arms left limit switch"),
	ARMS_LIMITSWITCH_RIGHT(5, "Arms right limit switch"),
	
	ROBOT_COMPRESSOR(0, "Robot compressor"),
	
	HATCH_SOLENOID_FLIPPER(4, "Hatch flipper"),
	HATCH_SOLENOID_SHOOTER(5, "Hatch shooter"),
	HATCH_SOLENOID_HOLDER(3, "Hatch holder"),
	
	HAB_LEADSCREWENCODER_CH1(24, "HAB lead screw encoder"),
	HAB_LEADSCREWENCODER_CH2(25, "HAB lead screw encoder"),
	HAB_LEADSCREWMOTOR(4, "HAB lead screw motor"),
	HAB_RIGHTMOTOR(6, "HAB right motor"),
	HAB_LEFTMOTOR(7, "HAB left motor"),
	
	VISION_CENTERLEFTFOLLOWINGSENSOR(14, "Vision center left sensor"),
	VISION_LEFTFOLLOWINGSENSOR(15, "Vision left sensor"),
	VISION_RIGHTFOLLOWINGSENSOR(16, "Vision right sensor"),
	VISION_CENTERRIGHTFOLLOWINGSENSOR(17, "Vision center right sensor");
	
	private final int port;
	private final String name;
	private final int uniqueID;
	
	private static final class IDCounter {
		private static int uniqueIDCounter = 0;
	}

	private IO(int port, String name) {
		this.port = port;
		this.name = name;
		uniqueID = IDCounter.uniqueIDCounter++;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getUniqueID() {
		return uniqueID;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return port + "";
	}
	
//    public static final int DRIVES_RIGHTMOTOR_1 =							10;
//    public static final int DRIVES_RIGHTMOTOR_2 =							11;
//    public static final int DRIVES_RIGHTMOTOR_3 =							12;
//    public static final int DRIVES_LEFTMOTOR_1 =							1;
//    public static final int DRIVES_LEFTMOTOR_2 =							2;
//    public static final int DRIVES_LEFTMOTOR_3 =							3;
//    
//    public static final int DRIVES_RIGHTENCODER_CH1 =						10; //10
//    public static final int DRIVES_RIGHTENCODER_CH2 =						11; //11
//    public static final int DRIVES_LEFTENCODER_CH1 =						12;
//    public static final int DRIVES_LEFTENCODER_CH2 =						13;
//    
//    public static final int DRIVES_PTOSOLENOID =							1; //0
//    public static final int DRIVES_SHIFTINGSOLENOID =						0; //1
//    public static final int DRIVES_UNSNAPPY = 								2;
//    
//    public static final int ARMS_LIMITSWITCH_LEFT =							6;
//    public static final int ARMS_LIMITSWITCH_RIGHT = 						5;
//    
//    public static final int ROBOT_COMPRESSOR =								0;
//    
//    public static final int HATCH_SOLENOID_FLIPPER =						4;
//    public static final int HATCH_SOLENOID_SHOOTER =						5;
//    public static final int HATCH_SOLENOID_HOLDER = 						3; //NEW
//  
//  //HAB
//    public static final int HAB_LEADSCREWENCODER_CH1 =						24;
//    public static final int HAB_LEADSCREWENCODER_CH2 =						25;
//    public static final int HAB_LEADSCREWMOTOR =							4;
//    public static final int HAB_RIGHTMOTOR =								6;
//    public static final int HAB_LEFTMOTOR =									7;
//   
//    public static final int VISION_LEFTFOLLOWINGSENSOR =					14;
//    public static final int VISION_CENTERLEFTFOLLOWINGSENSOR =				15;
//    public static final int VISION_CENTERRIGHTFOLLOWINGSENSOR =				16;
//    public static final int VISION_RIGHTFOLLOWINGSENSOR =					17;
//    
//    public static final int NOISEEEE_SOLENOID = 							3;
}
