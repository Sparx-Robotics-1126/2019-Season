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
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * Add your docs here.
 */
public class HAB extends GenericSubsystem{

    //----------------------------------------Motors/Sensors----------------------------------------

    private WPI_TalonSRX leadScrewMtr;
    private Encoder leadScrewEncRaw;
    private LeadScrewState state;
    private DigitalInput bottomSensor;

    public HAB(){
        super("Hab");
    }

    @Override
    public void init(){
        leadScrewMtr = new WPI_TalonSRX(9);
        leadScrewEncRaw = new Encoder(23, 22);
        leadScrewEncRaw.setDistancePerPulse(0.03103);
        leadScrewEncRaw.reset();
        bottomSensor = new DigitalInput(14);
        state = LeadScrewState.HOME;
    }
   
    public enum LeadScrewState{
        STANDBY,
        UP,
        DOWN,
        HOME;
    }

    @Override
    public void execute(){
        switch(state){
            case STANDBY:
                break;
            case UP:
                if(leadScrewEncRaw.getDistance() > 0){
                    leadScrewMtr.set(0.3);
                }else{
                    leadScrewMtr.set(0.0);
                }
                break;
            case DOWN:
                if(leadScrewEncRaw.getDistance()< 24){ //gav
                    leadScrewMtr.set(-0.3); //gav
                }else{
                    leadScrewMtr.set(0.0);
                }
                break;
            case HOME:
                if(bottomSensor.get()){
                    leadScrewMtr.set(0.3); 
                }else{
                    leadScrewMtr.set(0.0);
                    state = LeadScrewState.STANDBY;
                }
                break;
        }
        System.out.println("HAB Encoder Value:" + leadScrewEncRaw.getDistance());
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

}
