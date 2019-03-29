package frc.controls;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.controls.Automation.AutoMethod;
import frc.subsystem.Drives;
import frc.subsystem.HAB;
import frc.subsystem.Hatch;

public class Autonomous implements Controls{
	
	private Autos selectedAuto;
	
	private Drives drives;
	@SuppressWarnings("unused")
	private HAB hab;
	@SuppressWarnings("unused")
	private Hatch hatch;
	private Automation automation;
	
	private boolean firstRun;
	private boolean isDone;

	private SendableChooser<Autos> autoSelector;
	
	private enum Autos{
		DO_NOTHING,
		HAB_TO_HATCH_FRONT,
		HAB_TO_HATCH_MIDDLE,
		HAB_TO_HATCH_BACK,
		LEFT_HAB_TO_HATCH_FRONT_TO_PICKUP,
		LEFT_HAB_TO_HATCH_FRONT_AND_PICKUP,
		HAB_TO_LEFT_ROCKET, HAB_TO_GROUND,
		AUTO_TEST, RIGHT_HAB_TO_HATCH_FRONT_TO_PICKUP, RIGHT_HAB_TO_HATCH_FRONT_AND_PICKUP;
	}

	public Autonomous(Drives drives, Hatch hatch, HAB hab) {
		this.drives = drives;
		this.hatch = hatch;
		this.hab = hab;
		automation = new Automation(drives, hatch, hab);
		
		autoSelector = new SendableChooser<Autos>();
		autoSelector.addOption("Do Nothing", Autos.DO_NOTHING);
		autoSelector.setDefaultOption("Hab to front hatch", Autos.HAB_TO_HATCH_FRONT);
		autoSelector.addOption("Hab to middle hatch", Autos.HAB_TO_HATCH_MIDDLE);
		autoSelector.addOption("Hab to back hatch", Autos.HAB_TO_HATCH_BACK);
		autoSelector.addOption("Left hab to front hatch and to pickup station", Autos.LEFT_HAB_TO_HATCH_FRONT_TO_PICKUP);
		autoSelector.addOption("Left hab to front hatch and to pickup station (and actually picks up!)", Autos.LEFT_HAB_TO_HATCH_FRONT_AND_PICKUP);
		autoSelector.addOption("Left hab to left rocket", Autos.HAB_TO_LEFT_ROCKET);
		autoSelector.addOption("Hab to ground", Autos.HAB_TO_GROUND);
		autoSelector.addOption("Right hab to front hatch and to pickup station", Autos.RIGHT_HAB_TO_HATCH_FRONT_TO_PICKUP);
		autoSelector.addOption("Right hab to front hatch and to pickup station (and actually picks up!)", Autos.RIGHT_HAB_TO_HATCH_FRONT_AND_PICKUP);
		autoSelector.addOption("haHAA (no touchy)", Autos.AUTO_TEST);
		SmartDashboard.putData("Auto selector", autoSelector);
		
		selectedAuto = Autos.DO_NOTHING;
		firstRun = true;
	}

	public void reset() {
		firstRun = true;
		isDone = false;
		automation.reset();
	}

	public void setAuto(Autos auto) {
		reset();
		firstRun = false;
		if(auto != selectedAuto || auto == null) {
			if(auto == null) {
				auto = Autos.HAB_TO_HATCH_FRONT;
			}
			switch(auto) {
			case HAB_TO_HATCH_FRONT:
				System.out.println("Auto set - HAB_ONE_TO_LEFT_HATCH_FRONT");
				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.7, 230); //186-
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_FOLLOWLINE);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_DELAY, 1);
				automation.addStep(AutoMethod.HATCH_SHOOTFLIP);	
				automation.addStep(AutoMethod.AUTO_DELAY, 0.25);
				automation.addStep(AutoMethod.DRIVES_RESETANGLE);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 0.5, 30);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_STOP);
				break;
			case HAB_TO_GROUND:
				System.out.println("Auto set - HAB_TO_GROUND");
				automation.addStep(AutoMethod.AUTO_DELAY, 5);
				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.7, 200); 
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_STOP);
				break;
			case HAB_TO_HATCH_MIDDLE:
				System.out.println("Auto set - HAB_ONE_TO_LEFT_HATCH_MIDDLE");
				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.5, 230); //216
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_FOLLOWLINE);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.HATCH_SHOOTFLIP);
				automation.addStep(AutoMethod.AUTO_DELAY, 0.2);
				automation.addStep(AutoMethod.DRIVES_RESETANGLE);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 0.5, 30);
				automation.addStep(AutoMethod.AUTO_STOP);
				break;
			case HAB_TO_HATCH_BACK:
				System.out.println("Auto set - HAB_ONE_TO_LEFT_HATCH_BACK");
				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.5, 230); //252
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_FOLLOWLINE);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.HATCH_SHOOTFLIP);
				automation.addStep(AutoMethod.AUTO_DELAY, 0.2);
				automation.addStep(AutoMethod.DRIVES_RESETANGLE);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 0.5, 30);
				automation.addStep(AutoMethod.AUTO_STOP);
				break;
			case LEFT_HAB_TO_HATCH_FRONT_TO_PICKUP:
				automation.addStep(AutoMethod.DRIVES_FORWARD, 1, 230); //- 180
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_FOLLOWLINE);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_DELAY, 1.35);
				automation.addStep(AutoMethod.HATCH_SHOOTFLIP);	
				automation.addStep(AutoMethod.AUTO_DELAY, 0.45);
				automation.addStep(AutoMethod.DRIVES_RESETANGLE);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 0.5, 30);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_TURNRIGHT, 0.65, 115);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.HATCH_HOME);
				automation.addStep(AutoMethod.DRIVES_FORWARD, 1, 204);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_STOP);
				break;
			case LEFT_HAB_TO_HATCH_FRONT_AND_PICKUP:
				automation.addStep(AutoMethod.DRIVES_FORWARD, 1, 230); //- 180
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_FOLLOWLINE);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_DELAY, 1.35);
				automation.addStep(AutoMethod.HATCH_SHOOTFLIP);	
				automation.addStep(AutoMethod.AUTO_DELAY, 0.45);
				automation.addStep(AutoMethod.DRIVES_RESETANGLE);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 0.5, 30);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_TURNRIGHT, 0.65, 105);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.HATCH_HOME);
				automation.addStep(AutoMethod.DRIVES_FORWARD, 1, 154);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.HATCH_FLIP);
				automation.addStep(AutoMethod.LIMENESS, 1.5);
				automation.addStep(AutoMethod.HATCH_HOME);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 0.4, 40);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_STOP);
				break;
			case RIGHT_HAB_TO_HATCH_FRONT_TO_PICKUP:
				automation.addStep(AutoMethod.DRIVES_FORWARD, 1, 230); //- 180
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_FOLLOWLINE);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_DELAY, 1.35);
				automation.addStep(AutoMethod.HATCH_SHOOTFLIP);	
				automation.addStep(AutoMethod.AUTO_DELAY, 0.45);
				automation.addStep(AutoMethod.DRIVES_RESETANGLE);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 0.5, 30);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_TURNLEFT, 0.65, 115);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.HATCH_HOME);
				automation.addStep(AutoMethod.DRIVES_FORWARD, 1, 204);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_STOP);
				break;
			case RIGHT_HAB_TO_HATCH_FRONT_AND_PICKUP:
				automation.addStep(AutoMethod.DRIVES_FORWARD, 1, 230); //- 180
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_FOLLOWLINE);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_DELAY, 1.35);
				automation.addStep(AutoMethod.HATCH_SHOOTFLIP);	
				automation.addStep(AutoMethod.AUTO_DELAY, 0.45);
				automation.addStep(AutoMethod.DRIVES_RESETANGLE);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 0.5, 30);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_TURNLEFT, 0.65, 105);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.HATCH_HOME);
				automation.addStep(AutoMethod.DRIVES_FORWARD, 1, 154);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.HATCH_FLIP);
				automation.addStep(AutoMethod.LIMENESS, 1.5);
				automation.addStep(AutoMethod.HATCH_HOME);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 0.4, 40);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_STOP);
				break;
			case HAB_TO_LEFT_ROCKET:
				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.75, 192);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_TURNLEFT, 0.5, 60);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.5, 66);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_FOLLOWLINE);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_DELAY, 1);
				automation.addStep(AutoMethod.HATCH_SHOOTFLIP);
				automation.addStep(AutoMethod.AUTO_DELAY, 1);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 0.5, 30);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.HATCH_HOME);
				automation.addStep(AutoMethod.AUTO_STOP);
				break;
			case AUTO_TEST:
				automation.addStep(AutoMethod.DRIVES_FORWARD, 1, 120);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_RECORD);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 1, 120);
				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.5, 30);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_TURNRIGHT, 0.5, 180);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.5, 30);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_TURNRIGHT, 0.5, 180);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_TURNRIGHT, 0.6, 65);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_TURNLEFT, 0.6, 65);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_TURNRIGHT, 0.6, 65);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_TURNLEFT, 0.6, 65);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.5, 30);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_TURNLEFT, 0.5, 180);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.5, 30);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_TURNLEFT, 0.5, 180);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.5, 30);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_TURNRIGHT, 0.5, 180);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.5, 30);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_TURNRIGHT, 0.5, 180);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
//				automation.addStep(AutoMethod.DRIVES_TURNRIGHT, 0.5, 90);
//				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_STOP);
				break;
			default: 
				break;
			}
		}
	}

	@Override
	public void execute() {
		if(DriverStation.getInstance().isEnabled() && DriverStation.getInstance().isAutonomous()) {
			if(firstRun) {
				setAuto(autoSelector.getSelected());
				drives.resetGyroAngle();
			} else {
				automation.execute();
				if(automation.isDone()) {
					isDone = true;
				}
			}
		}
	}

	public boolean isDone() {
		return isDone;
	}
	
	public void stopAuto() {
		isDone = true;
		automation.setDone(true);
		automation.stopAll();
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
