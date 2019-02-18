package frc.controls;

import java.util.Vector;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.controls.Automation.AutoMethod;
import frc.subsystem.Drives;
import frc.subsystem.HAB;
import frc.subsystem.Hatch;

public class Autonomous implements Controls{
	
	private Autos selectedAuto;

	private Drives drives;
	private HAB hab;
	private Automation automation;
	
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
		HAB_ONE_TO_LEFT_HATCH_FRONT,
		HAB_ONE_TO_LEFT_HATCH_MIDDLE,
		HAB_ONE_TO_LEFT_HATCH_BACK,
		HAB_ONE_TO_LEFT_HATCH_FRONT_TO_PICKUP;
	}

	public Autonomous(Drives drives, Hatch hatch, HAB hab) {
		this.drives = drives;
		this.hatch = hatch;
		this.hab = hab;
		automation = new Automation(drives, hatch, hab);
		
		autoSelector = new SendableChooser<Autos>();
		autoSelector.setDefaultOption("Do Nothing", Autos.DO_NOTHING);
		autoSelector.addOption("Left side Hab 1 to front hatch", Autos.HAB_ONE_TO_LEFT_HATCH_FRONT);
		autoSelector.addOption("Left side Hab 1 to middle hatch", Autos.HAB_ONE_TO_LEFT_HATCH_MIDDLE);
		autoSelector.addOption("Left side Hab 1 to back hatch", Autos.HAB_ONE_TO_LEFT_HATCH_BACK);
		autoSelector.addOption("Left side Hab 1 to front hatch and to pickup station", Autos.HAB_ONE_TO_LEFT_HATCH_FRONT_TO_PICKUP);
		SmartDashboard.putData(autoSelector);
		
		selectedAuto = Autos.DO_NOTHING;
		firstRun = true;
	}

	public void reset() {
		firstRun = true;
		automation.reset();
	}

	public void setAuto(Autos auto) {
		firstRun = false;
		if(auto != selectedAuto) {
			switch(auto) {
			case HAB_ONE_TO_LEFT_HATCH_FRONT:
//				System.out.println("Auto set - HAB_ONE_TO_LEFT_HATCH_FRONT");
				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.5, 180); //-
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_FOLLOWLINE);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_DELAY, 2);
				automation.addStep(AutoMethod.HATCH_SHOOTFLIP);	
				automation.addStep(AutoMethod.AUTO_DELAY, 0.25);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 0.5, 30);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_STOP);
				break;
			case HAB_ONE_TO_LEFT_HATCH_MIDDLE:
				System.out.println("Auto set - HAB_ONE_TO_LEFT_HATCH_MIDDLE");
				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.5, 216);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_FOLLOWLINE);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.HATCH_SHOOTFLIP);
				automation.addStep(AutoMethod.AUTO_DELAY, 0.2);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 0.5, 30);
				automation.addStep(AutoMethod.AUTO_STOP);
				break;
			case HAB_ONE_TO_LEFT_HATCH_BACK:
				System.out.println("Auto set - HAB_ONE_TO_LEFT_HATCH_BACK");
				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.5, 252);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_FOLLOWLINE);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.HATCH_SHOOTFLIP);
				automation.addStep(AutoMethod.AUTO_DELAY, 0.2);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 0.5, 30);
				automation.addStep(AutoMethod.AUTO_STOP);
				break;
			case HAB_ONE_TO_LEFT_HATCH_FRONT_TO_PICKUP:
				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.75, 180); //-
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_FOLLOWLINE);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_DELAY, 2);
				automation.addStep(AutoMethod.HATCH_SHOOTFLIP);	
				automation.addStep(AutoMethod.AUTO_DELAY, 0.25);
				automation.addStep(AutoMethod.DRIVES_BACKWARD, 0.5, 30);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_TURNRIGHT, 0.5, 95);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.DRIVES_FORWARD, 0.75, 228);
				automation.addStep(AutoMethod.DRIVES_WAIT);
				automation.addStep(AutoMethod.AUTO_STOP);
			default: 
				break;
			}
		}
	}

	@Override
	public void execute() {
		if(DriverStation.getInstance().isEnabled() && DriverStation.getInstance().isAutonomous() && !firstRun) {
			automation.execute();
		} else {
			setAuto(autoSelector.getSelected());
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
