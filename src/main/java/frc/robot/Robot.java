/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
	private RobotSystem system;

	/**
	 * Changes:
	 * Swapped IO
	 * -Drives encoders (right encoder, channels swapped (10/11)
	 * -Drives PTO, Shifting (0/1)
	 * Gyro - not swapped, but first priority should be testing it when swapping during competition!
	 * Updated HAB level 2 height (-7.25 -> -7.75)
	 * Change level 2 method in TeleOP (do arms and hab at same time, dont do prearms)
	 * Changed level 2 delay time (3.2 -> 2)
	 * Changed level 3 delay time (2.35 -> 1.35)
	 * Changed pre-arms height (-2.5 -> -7.75)
	 */
	
	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {
		LiveWindow.disableAllTelemetry();
		system = new RobotSystem();
		system.init();
		system.start();
		// CameraServer.getInstance().startAutomaticCapture();
		System.out.println("***INIT ROBOT COMPLETE***");
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString line to get the
	 * auto name from the text box below the Gyro
	 *
	 * <p>
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the SendableChooser
	 * make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		system.resetVision();
		system.autonomous();
		System.out.println("Mode changed - Autonomous");
	}

	public void teleopInit() {
		system.resetVision();
		system.teleop();
		System.out.println("Mode changed - TeleOP");
	}

	/**
	 * This function is called every robot packet, no matter the mode. Use this for
	 * items like diagnostics that you want ran during disabled, autonomous,
	 * teleoperated and test.
	 *
	 * <p>
	 * This runs after the mode specific periodic functions, but before LiveWindow
	 * and SmartDashboard integrated updating.
	 */
	@Override
	public void robotPeriodic() {

	}
	
	@Override
	public void testInit() {
		LiveWindow.setEnabled(false);
		Shuffleboard.disableActuatorWidgets();
		System.out.println("Mode changed - Test");
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
