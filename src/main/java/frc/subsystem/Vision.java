
/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.IO;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Parity;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.SerialPort.StopBits;
import frc.util.Arduino;

/**
 * Add your docs here.
 */
public class Vision extends GenericSubsystem {

    private Arduino arduinoLE;

    private Arduino arduinoLM;

    private Arduino arduinoRE;

    private Arduino arduinoRM;

    private double d1, d2;

    private boolean clockWise, firstIt, onRight;

    private final double DEADBAND;

    private DigitalInput irRM;

    private DigitalInput irRC;
  
    private DigitalInput irLM;
  
    private DigitalInput irLC;

    private VisionState1 vState;

    public MoveState DriveState;

    public MoveState dState;

    public Vision()
    {
        super("Vision");
        DEADBAND = 0.1;
    }

    public void reset()
    {

    }

    /*
    private static enum VisionState
    {
      SENSING,
      FIRSTTURN,
      SECONDTURN,
      CORRECTING,
      LINEUP,
      DRIVE;
    }
    */

    private static enum VisionState1
    {
        SENSING,
        PARALING,
        REALIGNING,
        QUARTERTURN,
        DRIVE,
        SCORE;
    }

    public static enum MoveState
    {
        SLOWRIGHT,
        RIGHT,
        SLOWLEFT,
        LEFT,
        FORWARD,
        BACKWARD,
        SCORE,
        STANDBY;
    }

    public void init()
    {
        arduinoLM = new Arduino(115200, Port.kUSB1, 8, Parity.kSpace, StopBits.kOne);
        arduinoLE = new Arduino(115200, Port.kUSB2, 8, Parity.kSpace, StopBits.kOne);
        arduinoRM = new Arduino(115200, Port.kUSB1, 8, Parity.kSpace, StopBits.kOne);
        arduinoRE = new Arduino(115200, Port.kUSB2, 8, Parity.kSpace, StopBits.kOne);
        vState = VisionState1.SENSING;
        firstIt = true;
        DriveState = MoveState.STANDBY;
        irLC = new DigitalInput(IO.centerLeftFollowingSensor);
        irRC = new DigitalInput(IO.centerRightFollowingSensor);
        irLM = new DigitalInput(IO.leftFollowingSensor);
        irRM = new DigitalInput(IO.rightFollowingSensor);
        /*
        count = 0;
        minDist = Double.MAX_VALUE;
        irLM = new DigitalInput(IO.leftFollowingSensor);
        irLE = new DigitalInput(IO.centerLeftFollowingSensor);
        irRE = new DigitalInput(IO.centerRightFollowingSensor);
        irRM = new DigitalInput(IO.rightFollowingSensor);
        vState = VisionState.SENSING;
        dState = MoveState.STANDBY;
        try
        {
            arduino = new SerialPort(115200, Port.kUSB, 8, Parity.kSpace, StopBits.kOne);
        }
        catch(Exception e){}
        */

     }

     public MoveState getDirection()
     {
         return DriveState;
     }

    @Override
    public void execute() 
    {
        // double num, num2;
        // num = arduinoLE.getDistance();
        // if(num != -1.0 && num != Double.MAX_VALUE)
        //     System.out.println("Arduino 1: " + num);
        // num2 = arduinoLM.getDistance();
        // if(num2 != -1.0 && num2 != Double.MAX_VALUE)
        // System.out.println("Arduino 2: " + num);

        switch(vState)
      {
        case SENSING:
            if(!irRM.get() || !irLM.get())
                if(!irRM.get())
                    onRight = true;
                vState = VisionState1.PARALING;
            break;
        case PARALING:
            double distance;

            if(onRight)
            {
                onRightSetDistances();
            }
            else
            {
                onLeftSetDistances();
            }

            distance = d1 - d2;

            if(Math.abs(distance) < DEADBAND)
            {
                if(firstIt)
                    vState = VisionState1.QUARTERTURN;
                else
                    vState = VisionState1.REALIGNING;
                DriveState = MoveState.STANDBY;
            }
            else
            {
                if(d1 > d2)
                {
                    if(firstIt)
                        clockWise = true;
                    DriveState = MoveState.SLOWRIGHT;
                }
                else
                {
                    if(firstIt)
                        clockWise = false;
                    DriveState = MoveState.SLOWLEFT;
                }
            }
            firstIt = false;
            break;
        case REALIGNING:
            if((onRight && clockWise) || (!onRight && !clockWise))
                DriveState = MoveState.FORWARD;
            else
                DriveState = MoveState.BACKWARD;
            if(!irLM.get() || !irRM.get())
                {
                    DriveState = MoveState.STANDBY;
                    vState = VisionState1.QUARTERTURN;
                }
            break;
        case QUARTERTURN:
            if(onRight)
            {
                DriveState = MoveState.RIGHT;
                if(!irLC.get())
                    {
                        DriveState = MoveState.STANDBY;
                        vState = VisionState1.DRIVE;
                    }
            }
            else
            {
                DriveState = MoveState.LEFT;
                if(!irRC.get())
                {
                    DriveState = MoveState.STANDBY;
                    vState = VisionState1.DRIVE;
                }
            }
                break;
        case DRIVE:
                DriveState = MoveState.FORWARD;
                break;
        }

               
      }

      private void onRightSetDistances()
      {
        double distance;

        distance = arduinoRE.getDistance();
        if(distance != -1)
            d1 = distance;

        distance = arduinoRM.getDistance();
        if(distance != -1)
            d2 = distance;
      }

      private void onLeftSetDistances()
      {
        double distance;

        distance = arduinoLE.getDistance();
        if(distance != -1)
            d1 = distance;

        distance = arduinoLM.getDistance();
        if(distance != -1)
            d2 = distance;
      }
        /*
    if(arduino == null)
    {
        System.err.println("WARNING: arduino not initialized");
        return;
    }
      switch(vState)
      {
        case SENSING:
          if(!irRM.get() || !irLM.get())
            vState = VisionState.FIRSTTURN;
          break;
        case FIRSTTURN:
          //turn
          if(!irLE.get() || !irRE.get())
          {
          //zero gyro
          vState = VisionState.SECONDTURN;
          }
          break;
        case SECONDTURN:
          double dist = getDistance();
          if(dist < minDist)
          {
            minDist = dist;
            //angle = gyro angle;
          }
          else if(count > 2)
            vState = VisionState.CORRECTING;
          else
            count++;
          break;
        case CORRECTING:
          //if(gyroAngle > angle)
          //turn back
          break;
        case LINEUP:
          //do stuff
          break;
        case DRIVE:
          //do stuff
      }
      
    }*/

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
