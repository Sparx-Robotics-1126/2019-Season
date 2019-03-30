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
	/**
	 * all devices with a port of -1 are used for debug
	 */
	
	DRIVES_RIGHTMOTOR_1(10, "Drives right motor (T1)"),
	DRIVES_RIGHTMOTOR_2(11, "Drives right motor (T2)"),
	DRIVES_RIGHTMOTOR_3(12, "Drives right motor (T3)"),
	DRIVES_RIGHTMOTOR_GROUP(-1, "Drives right motors"),
	DRIVES_LEFTMOTOR_1(1, "Drives left motor (T1)"),
	DRIVES_LEFTMOTOR_2(2, "Drives left motor (T2)"),
	DRIVES_LEFTMOTOR_3(3, "Drives left motor (T3)"),
	DRIVES_LEFTMOTOR_GROUP(-1, "Drives left motors"),
	
	DRIVES_RIGHTENCODER_CH1(10, "Drives right encoder (CH 1)"), //11
	DRIVES_RIGHTENCODER_CH2(11, "Drives right encoder (CH 2)"), //10
	DRIVES_RIGHTENCODER_GROUP(-1, "Drives right encoder"),
	DRIVES_LEFTENCODER_CH1(12, "Drives left encoder (CH 1)"),
	DRIVES_LEFTENCODER_CH2(13, "Drives left encoder (CH 2)"),
	DRIVES_LEFTENCODER_GROUP(-1, "Drives left encoder"),
	
	DRIVES_PTOSOLENOID(1, "Drives PTO (Arms)"), //0
	DRIVES_SHIFTINGSOLENOID(0, "Drives shifting"), //1
	DRIVES_UNSNAPPY(2, "Drives Unsnappy"),
	
	ARMS_LIMITSWITCH_LEFT(6, "Arms left limitswitch"),
	ARMS_LIMITSWITCH_RIGHT(5, "Arms right limitswitch"),
	
	ROBOT_COMPRESSOR(0, "Compressor"),
	
	HATCH_SOLENOID_FLIPPER(4, "Hatch flipper"),
	HATCH_SOLENOID_SHOOTER(5, "Hatch shooter"),
	HATCH_SOLENOID_HOLDER(3, "Hatch holder"),
	
	HAB_LEADSCREWENCODER_CH1(24, "HAB encoder (CH 1)"),
	HAB_LEADSCREWENCODER_CH2(25, "HAB encoder (CH 2)"),
	HAB_LEADSCREWENCODER_GROUP(-1, "HAB encoder"),
	HAB_LEADSCREWMOTOR(4, "HAB lead screw (T1)"),
	HAB_LEADSCREWSECONDMOTOR(9, "HAB lead screw (T2)"),
	HAB_LEADSCREWMOTOR_GROUP(-1, "HAB lead screw"),
	HAB_RIGHTMOTOR(7, "Right arm wheel"),
	HAB_LEFTMOTOR(6, "Left arm wheel"),
	
	VISION_LEFTFOLLOWINGSENSOR(14, "Vision left sensor"),
	VISION_CENTERLEFTFOLLOWINGSENSOR(15, "Vision center left sensor"),
	VISION_CENTERRIGHTFOLLOWINGSENSOR(16, "Vision center right sensor"),
	VISION_RIGHTFOLLOWINGSENSOR(17, "Vision right sensor");

	public final int port;
	public final String name;
	public final int uniqueID;
	
	private static class UniqueIDCounter {
		private static int uniqueID;
	}
	
	private IO(int port, String name) {
		this.port = port;
		this.name = name;
		uniqueID = UniqueIDCounter.uniqueID++;
	}
}
