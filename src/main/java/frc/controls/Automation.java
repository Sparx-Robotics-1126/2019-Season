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
	
	private Autos selectedAuto;

	private Drives drives;
	private HAB hab;
	
	private boolean firstRun;
	private Hatch hatch;

	public boolean runAuto;

	private SendableChooser<Autos> autoSelector;
	
	private final double DISTANCE_MULITPLIER = 1;

	/**
	 * Autos
	 * -Goal 1 - Left side of hab level 1 -> go straight out, select first, second, or third hatch -> turn in, score (vision assistance), back up
	 *
	 */

	private enum Autos{
		DO_NOTHING,
		AUTO,
		CLIMBING;
	}

	private enum AutoMethod {
		/**
		 * Moves forward with a given distance at a given speed.
		 * @param speed - speed to go (in inches/second)
		 * @param distance - distance to go (in inches)
		 */
		DRIVES_FORWARD(0, 2),	
		/**
		 * Moves backward with a given distance at a given speed.
		 * @param speed - speed to go (in inches/second)
		 * @param distance - distance to go (in inches)
		 */
		DRIVES_BACKWARD(1, 2),
		/**
		 * Moves forward for a period of time at a given speed.
		 * @param time - time to move (seconds)
		 * @param speed - speed to go (inches/second)
		 */
		DRIVES_TIMED(2, 2),		
		/**
		 * Turns left x degrees at a given speed.
		 * @param degree - degrees to go (degrees)
		 * @param speed - speed to go (inches/second)
		 */
		DRIVES_TURNLEFT(3, 2),
		/**
		 * Turns right x degrees at a given speed.
		 * @param degree - degrees to go (degrees)
		 * @param speed - speed to go (inches/second)
		 */
		DRIVES_TURNRIGHT(4, 2),
		/**
		 * Starts vision following using line sensors.
		 */
		DRIVES_FOLLOWLINE(5, 0),
		/**
		 * Waits until any autonomous drive functions have finished.
		 */
		DRIVES_WAIT(6, 0),
		/**
		 * Stops drives.
		 */
		DRIVES_STOP(7, 0),
		/**
		 * Returns the hatch to home.
		 */
		HATCH_HOME(20, 0),
		/**
		 * Shoots and flips the hatch.
		 */
		HATCH_SHOOTFLIP(21, 0),
		/**
		 * Flips the hatch.
		 */
		HATCH_FLIP(22, 0),
		DRIVES_ARMS_DOWN(40, 0),
		HAB_WHEELS_FORWARD(41, 1),
		HAB_WAIT_PLATFORM(42, 0),
		HAB_UP(60, 0),
		HAB_DOWN(61, 0),
		HAB_PREARMS(62, 0),
		HAB_WAIT(63, 0),
		
		/**
		 * Pauses the auto for x seconds
		 * @param seconds - the number of seconds to pause the auto for.
		 */
		AUTO_DELAY(99, 1),
		/**
		 * Kills the auto.
		 */
		AUTO_STOP(100, 0),
		AUTO_EXIT(101, 0);

		private final int id;
		private final int parameterCount;

		private AutoMethod(int id, int parameterCount) {
			this.id = id;
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

		private static AutoMethod toAutoMethod(int id) {
			for(AutoMethod auto: AutoMethod.values()) {
				if(auto.id == id) {
					return auto;
				}
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
		
		selectedAuto = Autos.DO_NOTHING;
		firstRun = true;
	}

	public void printCurrentAuto() {
	}

	public void reset() {
		firstRun = true;
		currentStepData = null;
		currentAuto.clear();
		currentAutoParams.clear();
		currentStep = 0;
		delayTime = -1;
		delayTimeStart = -1;
	}

	public void setRunAuto(boolean runAuto) {
		this.runAuto = runAuto;
	}

	private boolean addStep(AutoMethod autoMethod, double... parameters) {
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
		if(currentAuto.size() - 1 > index) {
			System.out.println("Selected index for auto does not exist.");
			return false;
		}
		currentAuto.remove(index);
		currentAutoParams.remove(index);
		//		System.out.println("Removed step " + );
		return true;
	}

	public void setAuto(Autos auto) {
		firstRun = false;
		if(auto != selectedAuto) {
			currentAuto.clear();
			switch(auto) {
			case CLIMBING:
				addStep(AutoMethod.HAB_PREARMS); //-
				addStep(AutoMethod.HAB_WAIT);
				addStep(AutoMethod.DRIVES_ARMS_DOWN);
				addStep(AutoMethod.DRIVES_WAIT);
				addStep(AutoMethod.HAB_DOWN);
				addStep(AutoMethod.HAB_WAIT);	
				addStep(AutoMethod.HAB_WHEELS_FORWARD, 0.5);
				addStep(AutoMethod.HAB_WAIT_PLATFORM); //UNFINISHED, NEEDS SENSORS -> DO NOT USE
				addStep(AutoMethod.HAB_UP);
				addStep(AutoMethod.HAB_WAIT);
				addStep(AutoMethod.AUTO_STOP);
				break;
			default: 
				System.out.println("haHAA its not working");
				break;
			}
		}
	}

	public void execute() {
		if(DriverStation.getInstance().isEnabled() && DriverStation.getInstance().isAutonomous() && !firstRun) {
			runAuto();
			System.out.println(currentAuto.toString());
		} else {
			setAuto(autoSelector.getSelected());
		}
	}

	private void runAuto() {
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
				drives.turn(currentStepData[0], currentStepData[1]);
				currentStep++;
				break;
			case DRIVES_TURNRIGHT:
				drives.turn(currentStepData[0], -currentStepData[1]);
				currentStep++;
				break;
			case DRIVES_FOLLOWLINE:
				drives.findLine();
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
			case AUTO_STOP:
//				drives.stopMotors();
				currentStep = currentAuto.size() + 1;
				break;
			default:
				System.out.println("Invalid auto (" + currentAuto.get(currentStep) + ")");
				break;
			}
		}
	}
/*
	public class AutoSendable extends SendableBase {
		public String[] displayAuto() {
			String[] str = new String[currentAuto.size()];
			for(int i = 0; i < currentAuto.size(); i++) {
				str[i] = currentAuto.get(i).printer(currentAutoParams.get(i));
			}
			return str;
		}
		
		public double[] getAuto() {
			return null;
		}
		
		@Override
		public void initSendable(SendableBuilder builder) {
			builder.addStringArrayProperty("Autonomous", this::displayAuto, null);
//			builder.addDoubleArrayProperty("Autonomous parameters", getter, setter);
		}
		
	}*/
	
}

