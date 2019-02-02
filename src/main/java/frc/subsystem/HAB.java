/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import frc.robot.IO;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Encoder;

/**
 * Add your docs here.
 */
public class HAB extends GenericSubsystem{

    //----------------------------------------Motors/Sensors----------------------------------------

    private WPI_TalonSRX leadScrewMtr;
    private Encoder leadScrewEncRaw;
    // private WPI_TalonSRX leftArmWheelMtr;
    // private WPI_TalonSRX rightArmWheelMtr;
    // private Encoder leftArmWheelEnc;
    // private Encoder rightArmWheelEnc;

    public HAB(){
        super("Hab");
    }

    @Override
    public void init(){
        leadScrewMtr = new WPI_TalonSRX(9);
        leadScrewEncRaw = new Encoder(22, 23);
        leadScrewEncRaw.setDistancePerPulse(0.03103);
        leadScrewEncRaw.reset();
    }
   
    @Override
    public void execute(){
        leadScrewDown();
        System.out.println("HAB Encoder Value:" + leadScrewEncRaw.getDistance());
//        armWheels();
    }

    @Override
    public void debug(){

    }
 
    @Override
    public boolean isDone(){
        return false;
    }
  
    @Override
    public long sleepTime(){
        return 20;
    }

    public void leadScrewDown(){
        if(leadScrewEncRaw.getDistance()< 24){ //gav
            leadScrewMtr.set(0.2); //gav
        }else{
            leadScrewMtr.set(0.0);
        }
    }

    // public void armWheels(){
    //     if(leftArmWheelEnc.getDistance()< 5){ //gav
    //         leftArmWheelMtr.set(1.0); //gav
    //     }else{
    //         leftArmWheelMtr.set(0.0);
    //     }
    //     if(rightArmWheelEnc.getDistance()< 5){ //gav
    //         rightArmWheelMtr.set(1.0); //gav
    //     }else{
    //         rightArmWheelMtr.set(0.0);
    //     }
    // }
    
}
