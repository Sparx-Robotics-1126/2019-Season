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
import frc.util.MotorGroup;

/**
 * Add your docs here.
 */
public class HAB extends GenericSubsystem{

    //----------------------------------------Motors/Sensors----------------------------------------

    private WPI_TalonSRX leadScrewMtr;
    private Encoder leadScrewEncRaw;
    private Encoder rightArmEnc;
    private Encoder leftArmEnc;
    private LeadScrewState state;
    private DigitalInput bottomSensor;

    //----------------------------------------Variables---------------------------------------------

    double actualDegree = 0;
    double leftSpeed;
    double rightSpeed;
    double armOffset;

    //----------------------------------------Constants---------------------------------------------

    final double wantedDegree = 0.0; 
    final double wantedSpeed = 10; //gav

    public HAB(){
        super("Hab");
    }

    @Override
    public void init(){
        leadScrewMtr = new WPI_TalonSRX(9);
        rightArmEnc = new Encoder(0, 0); //gav
        leftArmEnc = new Encoder(0, 0); //gav
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
                    leadScrewEncRaw.reset();
                    state = LeadScrewState.STANDBY;
                }
                break;
        }

    }

    public void ctrlDown(){
        state = LeadScrewState.DOWN;
    }

    public void ctrlUP(){
        state = LeadScrewState.UP;
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

    public void armsDown(MotorGroup rightMtrs, MotorGroup leftMtrs){
        while(wantedDegree < actualDegree){
            leftSpeed = leftArmEnc.getRate();
            rightSpeed = rightArmEnc.getRate();
            armOffset = Math.abs(rightArmEnc.getDistance() - leftArmEnc.getDistance());

            if(leftSpeed < wantedSpeed){
                leftMtrs.set(leftSpeed + 0.05); //gav
            }else if(leftSpeed > wantedSpeed){
                leftMtrs.set(leftSpeed - 0.05); //gav
            }else{
                leftMtrs.set(leftSpeed);
            }

            if(rightSpeed < wantedSpeed){
                leftMtrs.set(rightSpeed + 0.05); //gav
            }else if(rightSpeed > wantedSpeed){
                leftMtrs.set(rightSpeed - 0.05); //gav
            }else{
                leftMtrs.set(rightSpeed);
            }

            if(armOffset > 2.0){ //gav
                if(rightSpeed > leftSpeed){
                    rightMtrs.set(0.0);
                }else{ //if leftSpeed is faster than rightSpeed
                    leftMtrs.set(0.0);
                }
            }else{
                rightMtrs.set(rightSpeed);
                leftMtrs.set(leftSpeed);
            }
        }
    }
}