/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import frc.sensors.EncoderData;
import frc.robot.IO;

/**
 * Add your docs here.
 */
public class HAB extends GenericSubsystem{

    //----------------------------------------Motors/Sensors----------------------------------------

    private Solenoid armsPTO;
    private WPI_TalonSRX leadScrewMtr;
    private Encoder leadScrewEnc;
    private WPI_TalonSRX leftArmWheelMtr;
    private WPI_TalonSRX rightArmWheelMtr;
    private Encoder leftArmWheelEnc;
    private Encoder rightArmWheelEnc;

    public HAB(){
        super("Hab");
    }

    @Override
    public void init(){
        armsPTO = new Solonoid(1);//gav
        armsPTO.set(false);
    }
   
    @Override
    public void execute(){
        ptoActivator();
        runDrives();
        leadScrewDown();
        armWheels();
    }

    @Override
    public void debug(){

    }
 
    @Override
    public boolean isDone(){

    }
  
    @Override
    public long sleepTime(){

    }
 
    public void ptoActivator(){
        armsPTO.set(true);
    }

    public void runDrives(){
        
    }

    public void leadScrewDown(){
        if(leadScrewEnc.getDistance()< 5){ //gav
            leadScrewMtr.set(1.0); //gav
        }else{
            leadScrewMtr.set(0.0);
        }
    }

    public void armWheels(){
        if(leftArmWheelEnc.getDistance()< 5){ //gav
            leftArmWheelMtr.set(1.0); //gav
        }else{
            leftArmWheelMtr.set(0.0);
        }
        if(rightArmWheelEnc.getDistance()< 5){ //gav
            rightArmWheelMtr.set(1.0); //gav
        }else{
            rightArmWheelMtr.set(0.0);
        }
    }
    
}
