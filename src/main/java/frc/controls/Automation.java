package frc.controls;

import java.util.Vector;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.subsystem.Drives;
import frc.subsystem.HAB;
import frc.subsystem.Hatch;

public class Automation {

	private Vector<AutoMethod> currentAuto;
	private Vector<double[]> currentAutoParams;
	private double[] currentStepData;
	private int currentStep;
	private double delayTimeStart;
	private double delayTime;

	private Drives drives;
	private HAB hab;
	private Hatch hatch;
	
	private double startTime;
	
	private boolean firstRun;
	private boolean isDone;
	
	private double time;

	private final double DISTANCE_MULITPLIER = 1;

	/**
	 * Autos
	 * -Goal 1 - Left side of hab level 1 -> go straight out, select first, second, or third hatch -> turn in, score (vision assistance), back up
	 *
	 */

	public enum AutoMethod {
		/**
		 * Moves forward with a given distance at a given speed.
		 * @param speed - speed to go (in inches/second)
		 * @param distance - distance to go (in inches)
		 */
		DRIVES_FORWARD(2),	
		/**
		 * Moves backward with a given distance at a given speed.
		 * @param speed - speed to go (in inches/second)
		 * @param distance - distance to go (in inches)
		 */
		DRIVES_BACKWARD(2),
		/**
		 * Moves forward for a period of time at a given speed.
		 * @param time - time to move (seconds)
		 * @param speed - speed to go (inches/second)
		 */
		DRIVES_TIMED(2),		
		/**
		 * Turns left x degrees at a given speed.
		 * @param speed - speed to go (inches/second)
		 * @param degree - degrees to go (degrees)
		 */
		DRIVES_TURNLEFT(2),
		/**
		 * Turns right x degrees at a given speed.
		 * @param speed - speed to go (inches/second)
		 * @param degree - degrees to go (degrees)
		 */
		DRIVES_TURNRIGHT(2),
		/**
		 * Starts vision following using line sensors.
		 */
		DRIVES_FOLLOWLINE(0),
		/**
		 * Waits until any autonomous drive functions have finished.
		 */
		DRIVES_WAIT(0),
		/**
		 * Stops drives.
		 */
		DRIVES_STOP(0),
		/**
		 * Returns the hatch to home.
		 */
		HATCH_HOME(0),
		/**
		 * Shoots and flips the hatch.
		 */
		HATCH_SHOOTFLIP(0),
		/**
		 * Flips the hatch.
		 */
		HATCH_FLIP(0),
		/**
		 * Moves arms down to latch onto the HAB.
		 */
		DRIVES_ARMS_DOWN(0),
		DRIVES_HIGHGEAR(0),
		DRIVES_LOWGEAR(0),
		/**
		 * Moves hab wheels forward (or backwards with -) at a given speed.
		 * @param speed - the speed at which to move the HAB wheels forward.
		 */
		HAB_WHEELS_FORWARD(1),
		/**
		 * Waits until the sensor on the robot detects that we are on the platform (safe to move)
		 */
		HAB_WAIT_PLATFORM(0),
		/**
		 * Moves the hab screw up completely into the robot.
		 */
		HAB_UP(0),
		/**
		 * Moves the hab screw down such that it extends around the height of the HAB.
		 */
		HAB_DOWN(0),
		/**
		 * Moves the hab screw just enough so that the arms can go safely onto the HAB.
		 */
		HAB_PREARMS(0),
		/**
		 * Waits until the hab screw has finished moving.
		 */
		HAB_WAIT(0),
		/**
		 * Pauses the auto for x seconds
		 * @param seconds - the number of seconds to pause the auto for.
		 */
		AUTO_DELAY(1),
		AUTO_STARTTIMER(1),
		AUTO_ENDTIMER(1),
		/**
		 * Records the auto time at the moment, storing it into SmartDashboard.
		 * @param 
		 */
		AUTO_RECORD(1),
		/**
		 * Kills the auto.
		 */
		AUTO_STOP(0),
		AUTO_KILL(0);

		private final int parameterCount;

		private AutoMethod(int parameterCount) {
			this.parameterCount = parameterCount;
		}

		private String printer(double[] parameters) {
			switch(this) {
			case DRIVES_FORWARD:
				return "DRIVES_FORWARD: move forward " + parameters[1] + " inches at a speed of " + parameters[0] + ".";
			case DRIVES_BACKWARD:
				return "DRIVES_BACKWARD: move backward " + parameters[1] + " inches at a speed of " + parameters[0] + ".";
			case DRIVES_TIMED:
				return "DRIVES_TIMED: move for " + parameters[1] + " seconds at a speed of " + parameters[0] + ".";
			case DRIVES_TURNLEFT:
				return "DRIVES_TURNLEFT: turn " + parameters[0] + " degrees left at a speed of " + parameters[1] + ".";
			case DRIVES_TURNRIGHT:
				return "DRIVES_TURNRIGHT: turn " + parameters[0] + " degrees right at a speed of " + parameters[1] + ".";
			case DRIVES_FOLLOWLINE:
				return "DRIVES_FOLLOWLINE: enable automatic vision tracking.";
			case DRIVES_WAIT:
				return "DRIVES_WAIT: wait until drives has finished.";
			case DRIVES_STOP:
				return "DRIVES_STOP: stop drives.";
			case HATCH_HOME:
				return "HATCH_HOME: return the hatch to the home position.";
			case HATCH_SHOOTFLIP:
				return "HATCH_SHOOTFLIP: shoot and flip the hatch.";
			case HATCH_FLIP:
				return "HATCH_FLIP: flip the hatch.";
			case AUTO_DELAY:
				return "AUTO_DELAY: pause auto for " + parameters[0] + " seconds.";
			case AUTO_STOP:
				return "AUTO_STOP: stop auto.";
			}
			return null;
		}

	}

	public Automation(Drives drives, Hatch hatch, HAB hab) {
		this.drives = drives;
		this.hatch = hatch;
		this.hab = hab;
		currentAuto = new Vector<AutoMethod>();
		currentAutoParams = new Vector<double[]>();

		firstRun = true;
		currentStepData = null;
		currentStep = 0;
		delayTime = -1;
		delayTimeStart = -1;
		isDone = false;
	}

	public void reset() {
		firstRun = true;
		currentAuto.clear();
		currentAutoParams.clear();
		clearData();
	}
	
	public void clearData() {
		currentStepData = null;
		currentStep = 0;
		delayTime = -1;
		delayTimeStart = -1;
		isDone = false;
	}

	public boolean addStep(AutoMethod autoMethod, double... parameters) {
		if(!firstRun) {
			System.out.println("Dirty run - call reset() on Automation before adding anything else!");
			return false;
		}
		if(parameters != null) {
			if(parameters.length != autoMethod.parameterCount) {
				System.out.println("Invalid number of parameters passed in - expected " + autoMethod.parameterCount + " but got " + parameters.length);
				return false;
			}
		} else {
			parameters = new double[0];
		}
		currentAuto.add(autoMethod);
		currentAutoParams.add(parameters);
		return true;
	}

	private boolean removeStep(int index) {
		if(!firstRun) {
			System.out.println("Dirty run - call reset() on Automation before changing anything else!");
			return false;
		}
		if(currentAuto.size() - 1 > index) {
			System.out.println("Selected index for auto does not exist.");
			return false;
		}
		currentAuto.remove(index);
		currentAutoParams.remove(index);
		//		System.out.println("Removed step " + );
		return true;
	}
	
	public boolean isDone() {
		return isDone;
	}

	public void execute() {
		if(firstRun) {
			startTime = Timer.getFPGATimestamp();
			clearData();
			firstRun = false;
		}
		if(delayTimeStart != -1) {
			if(delayTimeStart + delayTime > Timer.getFPGATimestamp()) {
				return;
			}
			delayTimeStart = -1;
			delayTime = -1;
			currentStep++;
		}
		System.out.println(currentStep);
		if(currentStep < currentAuto.size()) {
			currentStepData = currentAutoParams.get(currentStep);
			System.out.println(currentAuto.get(currentStep));
			switch(currentAuto.get(currentStep)) {
			case DRIVES_FORWARD:
				drives.move(currentStepData[0], currentStepData[1]*DISTANCE_MULITPLIER);
				currentStep++;
				break;
			case DRIVES_BACKWARD:
				drives.move(currentStepData[0], -currentStepData[1]*DISTANCE_MULITPLIER);
				currentStep++;
				break;
			case DRIVES_TIMED:
				currentStep++;
				break;
			case DRIVES_TURNLEFT:
				drives.turn(currentStepData[0], -currentStepData[1]);
				currentStep++;
				break;
			case DRIVES_TURNRIGHT:
				drives.turn(currentStepData[0], currentStepData[1]);
				currentStep++;
				break;
			case DRIVES_FOLLOWLINE:
				drives.findLine();
				currentStep++;
				break;
			case DRIVES_LOWGEAR:
				drives.lowShift();
				currentStep++;
				break;
			case DRIVES_HIGHGEAR:
				drives.highShift();
				currentStep++;
				break;
			case DRIVES_WAIT:
				if(drives.isDone()) {
					currentStep++;
				}
				break;
			case DRIVES_STOP:
				drives.stopMotors();
				currentStep++;
				break;
			case HATCH_HOME:
				hatch.homeButton();
				currentStep++;
				break;
			case HATCH_SHOOTFLIP:
				hatch.shooterButton();
				currentStep++;
				break;
			case HATCH_FLIP:
				hatch.flipperButton();
				currentStep++;
				break;
			case DRIVES_ARMS_DOWN:
				drives.toArms();
				currentStep++;
				break;
			case HAB_WHEELS_FORWARD:
				hab.setHabWheelsSpeed(currentStepData[0]);
				currentStep++;
				break;
			case HAB_DOWN:
				hab.ctrlDown();
				currentStep++;
				break;
			case HAB_PREARMS:
				hab.ctrlPreArms();
				currentStep++;
				break;
			case HAB_UP:
				hab.ctrlUP();
				currentStep++;
				break;
			case HAB_WAIT:
				if(hab.isDone()) {
					currentStep++;
				}
				break;
			case HAB_WAIT_PLATFORM:
				if(hab.onPlatform()) {
					hab.setHabWheelsSpeed(0);
					currentStep++;
				}
				break;
			case AUTO_DELAY:
				delayTimeStart = Timer.getFPGATimestamp();
				delayTime = currentStepData[0];
				break;
			case AUTO_STARTTIMER:
				time = Timer.getFPGATimestamp();
				return;
			case AUTO_STOP:
				SmartDashboard.putNumber("Auto time", Timer.getFPGATimestamp() - startTime);
				drives.stopMotors();
				currentStep = currentAuto.size() + 1;
				break;
			case AUTO_KILL:
				break;
			default:
				System.out.println("Invalid auto (" + currentAuto.get(currentStep) + ")");
				break;
			}
		} else {
			isDone = true;
		}
	}

}

