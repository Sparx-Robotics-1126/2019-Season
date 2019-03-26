package frc.controls;

import frc.subsystem.Drives;
import frc.subsystem.HAB;
import frc.subsystem.Hatch;

public class Debug implements Controls{
	
	private Automation automation;
	
	public Debug(Drives drives, Hatch hatch, HAB hab, TeleOP teleop) {
		automation = new Automation(drives, hatch, hab);
		
	}

	@Override
	public void execute() {
		
	}

}
