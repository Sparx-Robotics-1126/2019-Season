package frc.controls;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.subsystem.Drives;
import frc.subsystem.Hatch;

public class Autonomous implements Controls{

	private ArrayList<int[]> currentAuto;
	private int[] currentStepData;
	private int currentStep;
	private double delayTimeStart;
	private double delayTime;

	private Drives drives;
	private Hatch hatch;

	public boolean runAuto;

	private SendableChooser<Autos> autoSelector;

	/**
	 * Autos
	 * -Goal 1 - Left side of hab level 1 -> go straight out, select first, second, or third hatch -> turn in, score (vision assistance), back up
	 *
	 */

	private enum Autos{
		DO_NOTHING,
		HAB_ONE_TO_LEFT_HATCH_FRONT,
		HAB_ONE_TO_LEFT_HATCH_MIDDLE,
		HAB_ONE_TO_LEFT_HATCH_BACK;
	}

	private enum AutoMethod {
		/**
		 * @param speed - speed to go (in inches/second)
		 * @param distance - distance to go (in inches)
		 */
		DRIVES_FORWARD(0, 2),	
		/**
		 * @param speed - speed to go (in inches/second)
		 * @param distance - distance to go (in inches)
		 */
		DRIVES_BACKWARD(1, 2),
		DRIVES_TIMED(2, 3),		
		DRIVES_TURNLEFT(3, 2),
		DRIVES_TURNRIGHT(4, 2),
		DRIVES_FOLLOWLINE(5, 0),
		DRIVES_WAIT(6, 0),
		DRIVES_STOP(7, 0),
		HATCH_HOME(20, 0),
		HATCH_SHOOTFLIP(21, 0),
		HATCH_FLIP(22, 0),
		AUTO_DELAY(99, 1),
		AUTO_STOP(100, 0);

		private final int id;
		private final int parameterCount;

		private AutoMethod(int id, int parameterCount) {
			this.id = id;
			this.parameterCount = parameterCount;
		}

		private void printer(int[] parameters) {
			switch(this) {
			case DRIVES_FORWARD:
				break;
			case DRIVES_BACKWARD:
				break;
			case DRIVES_TIMED:
				break;
			case DRIVES_TURNLEFT:
				break;
			case DRIVES_TURNRIGHT:
				break;
			case DRIVES_FOLLOWLINE:
				break;
			case DRIVES_WAIT:
				break;
			case DRIVES_STOP:
				break;
			case HATCH_HOME:
				break;
			case HATCH_SHOOTFLIP:
				break;
			case HATCH_FLIP:
				break;
			}
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

	public Autonomous(Drives drives, Hatch hatch) {
		this.drives = drives;
		this.hatch = hatch;
		currentAuto = new ArrayList<int[]>();

		autoSelector = new SendableChooser<Autos>();
		autoSelector.setDefaultOption("Do Nothing", Autos.DO_NOTHING);
		autoSelector.addOption("Left side Hab 1 to front hatch", Autos.HAB_ONE_TO_LEFT_HATCH_FRONT);
		autoSelector.addOption("Left side Hab 1 to middle hatch", Autos.HAB_ONE_TO_LEFT_HATCH_MIDDLE);
		autoSelector.addOption("Left side Hab 1 to back hatch", Autos.HAB_ONE_TO_LEFT_HATCH_BACK);
		LiveWindow.add(autoSelector);
	}

	public void printCurrentAuto() {
	}

	public void reset() {
		currentStepData = null;
		currentAuto.clear();
		currentStep = 0;
		delayTime = -1;
		delayTimeStart = -1;
	}

	public void setRunAuto(boolean runAuto) {
		this.runAuto = runAuto;
	}

	private boolean addStep(AutoMethod autoMethod, int... parameters) {
		int[] autoMethodArray;
		if(parameters != null) {
			if(parameters.length != autoMethod.parameterCount) {
				System.out.println("Invalid number of parameters passed in - expected " + autoMethod.parameterCount + " but got " + parameters.length);
				return false;
			}
			autoMethodArray = new int[parameters.length + 1];
			for(int i = 1; i < autoMethodArray.length; i++) {
				autoMethodArray[i] = parameters[i - 1];
			}
		} else {
			autoMethodArray = new int[0];
		}
		autoMethodArray[0] = autoMethod.id;
		currentAuto.add(autoMethodArray);
		return true;
	}

	private boolean removeStep(int index) {
		if(currentAuto.size() - 1 > index) {
			System.out.println("Selected index for auto does not exist.");
			return false;
		}
		currentAuto.remove(index);
		//		System.out.println("Removed step " + );
		return true;
	}

	public void setAuto(Autos auto) {
		currentAuto.clear();
		switch(auto) {
		case HAB_ONE_TO_LEFT_HATCH_FRONT:
			System.out.println("Auto set - HAB_ONE_TO_LEFT_HATCH_FRONT");
			addStep(AutoMethod.DRIVES_FORWARD, 180, 50);
			addStep(AutoMethod.DRIVES_WAIT);
			addStep(AutoMethod.DRIVES_FOLLOWLINE);
			addStep(AutoMethod.DRIVES_WAIT);
			addStep(AutoMethod.HATCH_SHOOTFLIP);
			addStep(AutoMethod.AUTO_DELAY, 200);
			addStep(AutoMethod.DRIVES_BACKWARD, 30, 50);
			addStep(AutoMethod.AUTO_STOP);
			break;
		case HAB_ONE_TO_LEFT_HATCH_MIDDLE:
			System.out.println("Auto set - HAB_ONE_TO_LEFT_HATCH_MIDDLE");
			addStep(AutoMethod.DRIVES_FORWARD, 216, 50);
			addStep(AutoMethod.DRIVES_WAIT);
			addStep(AutoMethod.DRIVES_FOLLOWLINE);
			addStep(AutoMethod.DRIVES_WAIT);
			addStep(AutoMethod.HATCH_SHOOTFLIP);
			addStep(AutoMethod.AUTO_DELAY, 200);
			addStep(AutoMethod.DRIVES_BACKWARD, 30, 50);
			addStep(AutoMethod.AUTO_STOP);
			break;
		case HAB_ONE_TO_LEFT_HATCH_BACK:
			System.out.println("Auto set - HAB_ONE_TO_LEFT_HATCH_BACK");
			addStep(AutoMethod.DRIVES_FORWARD, 252, 50);
			addStep(AutoMethod.DRIVES_WAIT);
			addStep(AutoMethod.DRIVES_FOLLOWLINE);
			addStep(AutoMethod.DRIVES_WAIT);
			addStep(AutoMethod.HATCH_SHOOTFLIP);
			addStep(AutoMethod.AUTO_DELAY, 200);
			addStep(AutoMethod.DRIVES_BACKWARD, 30, 50);
			addStep(AutoMethod.AUTO_STOP);
			break;
		default: 
			break;
		}
	}

	@Override
	public void execute() {
		if(DriverStation.getInstance().isEnabled()) {
			runAuto();
		} else {
			setAuto(autoSelector.getSelected());
		}
	}

	private void runAuto() {
		if(delayTimeStart != -1) {
			if(delayTimeStart + delayTime < Timer.getFPGATimestamp()) {
				return;
			}
			delayTimeStart = -1;
			delayTime = -1;
			currentStep++;
		}
		if(currentStep < currentAuto.size()) {
			currentStepData = currentAuto.get(currentStep);
			switch(AutoMethod.toAutoMethod(currentStepData[0])) {
			case DRIVES_FORWARD:
				drives.move(currentStepData[1], currentStepData[2]);
				currentStep++;
				break;
			case DRIVES_BACKWARD:
				drives.move(currentStepData[1], -currentStepData[2]);
				currentStep++;
				break;
			case DRIVES_TIMED:
				currentStep++;
				break;
			case DRIVES_TURNLEFT:
				drives.turn(currentStepData[1], currentStepData[2]);
				currentStep++;
				break;
			case DRIVES_TURNRIGHT:
				drives.turn(currentStepData[1], -currentStepData[2]);
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
			case AUTO_DELAY:
				delayTimeStart = Timer.getFPGATimestamp();
				delayTime = currentAuto.get(currentStep)[1] / 1000.0;
				break;
			case AUTO_STOP:
				currentStep = currentAuto.size() + 1;
				break;
			default:
				System.out.println("Invalid auto (" + AutoMethod.toAutoMethod(currentStepData[0]) + ")");
				break;
			}
		}
	}

}
