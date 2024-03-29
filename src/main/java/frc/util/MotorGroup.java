/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.util;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * Add your docs here.
 */
public class MotorGroup extends SpeedControllerGroup{

    public SpeedController[] speedControllers;

    /**
	 * MotorGroup extends speedControllerGroup, so it implements sendable and can control multiple motors at once
	 * @param arg0 - the first motor in this group
	 * @param arg1 - the second motor in this group
	 * @param arg2 - the third motor in this group
	 */
	public MotorGroup(SpeedController arg0, SpeedController arg1, SpeedController arg2) {
		super(arg0, arg1, arg2);
		speedControllers = new SpeedController[]{arg0, arg1, arg2};	
    }
    
    public MotorGroup(SpeedController arg0, SpeedController arg1) {
		super(arg0, arg1);
		speedControllers = new SpeedController[]{arg0, arg1};	
	}
	
	//public double getPower()
	//{

	//}
    /**
	 * Gets the speed controller at motorNum, if motorNum is bigger than the number of 
	 		motors in this motorGroup return null. Motor numbering starts at 0.
	 * @param motorNum - the number of the motor in this group
	 * @return - the motorNumth motor in this group
	 */
	public SpeedController getSpeedController(int motorNum) {
		if(motorNum > speedControllers.length)
			return null;
		return speedControllers[motorNum];
    }
    
    /**
	 * Get the size of this motor group
	 * @return - the number of motors in this group
	 */
	public int getMtrCount() {
		return speedControllers.length;
    }
	
	public double getCurrent() {
		double avg = 0;
		for(SpeedController spd: speedControllers) {
			avg += ((WPI_TalonSRX)spd).getOutputCurrent();
		}
		return avg / speedControllers.length;
	}
	
	public double getVoltage() {
		double avg = 0;
		for(SpeedController spd: speedControllers) {
			avg += ((WPI_TalonSRX)spd).getMotorOutputVoltage();
		}
		return avg / speedControllers.length;
	}
    
    /**
	 * Sets the mode of operation during neutral throttle output for each speedController that is a WPI_TalonSRX 
	 * @param neutralMode - The desired mode of operation when the Controller output throttle is neutral (ie brake/coast)
	 */
	public void setNeutralMode(NeutralMode neutralMode) {
        for(int i = 0; i < speedControllers.length; i++){
            ((WPI_TalonSRX)speedControllers[i]).setNeutralMode(neutralMode);
        }
    }

    /**
	 * gets the specific motor
	 * @param motorNum - the motor number
	 * @return - the motor
	 */
	public SpeedController getMotor(int motorNum) {
		if(motorNum >= speedControllers.length) {
			return null;
		}
		return speedControllers[motorNum];
	}
    
    /**
	 * inverts each individual motor when called
	 * @param arg0 - the desired state of the motor group
	 */
	public void setInverted(boolean arg0) {
        super.setInverted(arg0);
        for(int i = 0; i < speedControllers.length; i++){
            speedControllers[i].setInverted(arg0);
        }
    }
    
    /**
	 * sets the motor speeds
	 * @param arg0 - the speed we want to set the motor to
	 */
	public void set(double arg0) {
        for(int i = 0; i < speedControllers.length; i++){
            speedControllers[i].set(arg0);
        }
	}
	
	public void stopMotors(){
		for(int i = 0; i < speedControllers.length; i++){
            speedControllers[i].set(0);
        }
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("Motor Group");
		builder.setSafeState(this::stopMotors);
		builder.addDoubleProperty("Value", this::get, this::set);
	}


}
