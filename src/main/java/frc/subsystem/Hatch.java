/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

/**
 * Add your docs here.
 */
public class Hatch extends GenericSubsystem{

    private Solenoid flipper;

    private Solenoid shooter;

    private boolean flipperState;

    private boolean flipperButtonState;

    private boolean shooterState;

    private boolean shooterButtonState;

    private HatchState state;

    private double time;

    public Hatch(){
        super("Hatch");
    }

    public enum HatchState{
        STANDBY,
        FLIPPER,
        SHOOT_AND_FLIPPER;
    }

    public void init(){
        flipper = new Solenoid(1);
        shooter = new Solenoid(0);
        flipperState = false;
        flipperButtonState = false;
        shooterState = false;
        shooterButtonState = false;
        state = HatchState.STANDBY;
        time = 0;
    }

    public void execute(){
       flipper.set(flipperState);
       switch(state){
            case STANDBY:
                break;
            case FLIPPER:
                if(flipperButtonState==true){
                    flipperState = !flipperState;
                    flipper.set(flipperState);
                }
                state = HatchState.STANDBY;
                break;
            case SHOOT_AND_FLIPPER:
                if(shooterButtonState==true){
                    shooterState = !shooterState;
                    shooter.set(shooterState);
                }
                if(shooterState==true){
                    if(System.currentTimeMillis() > time + 1000){
                        flipper.set(true);
                        state = HatchState.STANDBY;
                    }
                }
                break;
       }
    }

    public void flipperButton(boolean condition){ 
        flipperButtonState = condition;
        state = HatchState.FLIPPER;
    }

    public void shooterButton(boolean condition){
        shooterButtonState = condition;
        time = System.currentTimeMillis();
        state = HatchState.SHOOT_AND_FLIPPER;
    }

    public void debug(){

    }

    public boolean isDone(){
        return false;
    }

    public long sleepTime(){
        return 20;
    }

}
