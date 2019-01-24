
/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * Add your docs here.
 */
public class Vision extends GenericSubsystem {

    private directions direction;

    private DigitalInput leftIR;
    
    private DigitalInput centerIR;
    
    private DigitalInput rightIR;

    int firstHit;

    public Vision()
    {
        super("Vision");
    }

    public void init()
    {
        leftIR = new DigitalInput(0);
        centerIR = new DigitalInput(1);
        rightIR = new DigitalInput(2);
    }

    public enum directions
    {
        LEFT,
        STANDBY,
        RIGHT    
    }

    @Override
    public void execute() 
    {
        if(leftIR.get() && !centerIR.get() && !rightIR.get() && firstHit == -1)
            firstHit = 0;
        else if(rightIR.get() && !centerIR.get() && !leftIR.get() && firstHit == -1)
            firstHit = 2;
        else if(!rightIR.get() && !centerIR.get() && !leftIR.get())
            firstHit = -1;

        if(firstHit == 0)
            direction = directions.LEFT;
        else if(firstHit == 2)
            direction = directions.RIGHT;
        else
            direction = directions.STANDBY;
        System.out.println(direction);
    }

    @Override
    public void debug() {

    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public long sleepTime() {
        return 0;
    }

}
