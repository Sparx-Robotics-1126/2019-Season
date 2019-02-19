
/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.IO;
import edu.wpi.first.wpilibj.SerialPort.Parity;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.SerialPort.StopBits;
import frc.util.Arduino;

/**
 * Add your docs here.
 */
public class Vision {

    private Arduino arduinoLeft;

    private Arduino arduinoRight;

    private double d1, d2, edgeDist, midDist;

    private boolean clockWise, firstIt, onRight;

    private final double DEADBAND;

    private DigitalInput irRM;

    private DigitalInput irRC;
  
    private DigitalInput irLM;
  
    private DigitalInput irLC;

    private VisionStateParalining vState;
    
    private VisionStateMixed vStateMixed;

    public MoveState DriveState;

    public MoveState dState;

    public Vision()
    {
        DEADBAND = .75;
        irRM = new DigitalInput(5);
        irRC = new DigitalInput(1);
        irLM = new DigitalInput(15);
        irLC = new DigitalInput(8);
        arduinoRight = new Arduino(115200, Port.kUSB, 8, Parity.kSpace, StopBits.kOne);
        vState = VisionStateParalining.SENSING;
        vStateMixed = VisionStateMixed.SENSING;
    }

    public void reset()
    {
        vState = VisionStateParalining.SENSING;
        firstIt = true;
    }

    public static enum VisionStateParalining
    {
        SENSING,
        PARALING,
        REALIGNING,
        QUARTERTURN,
        DRIVE,
        SCORE,
        NOTHING;
    }
    
    public static enum VisionStateMixed
    {
    	SENSING,
    	TURNING,
    	DRIVING,
    	SCORING,
    	FINISHED;
    }

    public static enum MoveState
    {
        DRIVER,
        SLOWRIGHT,
        RIGHT,
        SLIGHTRIGHT,
        SLOWLEFT,
        LEFT,
        SLIGHTLEFT,
        FORWARD,
        BACKWARD,
        SCORE,
        STANDBY;
    }

     public MoveState getDirection()
     {
         return DriveState;
     }
      
      public void test()
      {
        System.out.println("edge distance: " + edgeDist + "\nmid  distance: " + midDist);
          paraliningMethodOneSide();
      }

      public void mixedMethod()
      {
    	  switch(vStateMixed)
    	  {
    	  case SENSING:
    		  DriveState = MoveState.DRIVER;
    		  if(!irLM.get())
    		  {
    			  DriveState = MoveState.LEFT;
    			  vStateMixed = VisionStateMixed.TURNING;
    			  onRight = false;
    		  }
    		  else if(!irRM.get())
    		  {
    			  DriveState = MoveState.RIGHT;
    			  vStateMixed = VisionStateMixed.TURNING;
    			  onRight = true;
    		  }
    		  break;
    	  case TURNING:
    		  if(onRight)
    		  {
    			  if(!irLC.get())
    			  {
    				  DriveState = MoveState.LEFT;
    				  vStateMixed = VisionStateMixed.DRIVING;
    			  }
    		  }
    		  else
    		  {
    			  if(!irRC.get())
    			  {
    				  DriveState = MoveState.RIGHT;
    				  vStateMixed = VisionStateMixed.DRIVING;
    			  }
    		  }
    		  break;
    	  case DRIVING:
    		  if(!irLC.get())
    			  DriveState = MoveState.SLIGHTLEFT;
    		  else
    			  DriveState = MoveState.SLIGHTRIGHT;
    	  case SCORING:
    		  DriveState = MoveState.SCORE;
    	  }
      }
      
      public void paraliningMethodOneSide()
      {
        arduinoRight.updateDistances();
        onRightSetDistances();
        System.out.println("edge distance: " + edgeDist + "\nmid  distance: " + midDist);
    switch(vState)
      {
        case SENSING:
            if(firstIt)
                arduinoRight.reset();
            DriveState = MoveState.DRIVER;
            if(!irRM.get())
            {
                firstIt = true;
                vState = VisionStateParalining.PARALING;
                DriveState = MoveState.STANDBY;
            }
            firstIt = false;
            break;
        case PARALING:
            double distance;
            arduinoRight.updateDistances();
            onRightSetDistances();

            distance = edgeDist - midDist;

            if(edgeDist == Double.MAX_VALUE && midDist == Double.MAX_VALUE)
            {
                arduinoRight.reset();
                System.out.println("RESETING ARDUINO DURING PARALING");
            }
            if(Math.abs(distance) < DEADBAND && edgeDist != Double.MAX_VALUE)
            {
                 vState = VisionStateParalining.REALIGNING;
            }
            else
            {
                if(edgeDist > midDist)
                {
                    if(firstIt)
                    {
                        clockWise = true;
                        firstIt = false;
                    }
                    DriveState = MoveState.SLOWRIGHT;
                }
                else if(midDist > edgeDist)
                {
                    if(firstIt)
                    {
                        clockWise = false;
                        firstIt = false;
                    }
                    DriveState = MoveState.SLOWLEFT;
                }
            }
            break;
        case REALIGNING:
            if(clockWise)
                DriveState = MoveState.FORWARD;
            else
                DriveState = MoveState.BACKWARD;
            if(!irRM.get())
                {
                    DriveState = MoveState.STANDBY;
                    vState = VisionStateParalining.QUARTERTURN;
                }
            break;
        case QUARTERTURN:
                DriveState = MoveState.RIGHT;
                if(!irLC.get())
                    {
                        DriveState = MoveState.STANDBY;
                        vState = VisionStateParalining.DRIVE;
                    }
                break;
        case DRIVE:
                DriveState = MoveState.FORWARD;
                break;
        default:
                System.out.println("default");
        }
      }


    //   private void paraliningMethod()
    //   {

    //     switch(vState)
    //   {
    //     case SENSING:
    //         if(!irRM.get() || !irLM.get())
    //             if(!irRM.get())
    //                 onRight = true;
    //             vState = VisionState1.PARALING;
    //         break;
    //     case PARALING:
    //         double distance;

    //         if(onRight)
    //         {
    //             arduinoRight.updateDistances();
    //             onRightSetDistances();
    //         }
    //         else
    //         {
    //             arduinoLeft.updateDistances();
    //             onLeftSetDistances();
    //         }

    //         distance = d1 - d2;

    //         if(Math.abs(distance) < DEADBAND)
    //         {
    //             if(firstIt)
    //                 vState = VisionState1.QUARTERTURN;
    //             else
    //                 vState = VisionState1.REALIGNING;
    //             DriveState = MoveState.STANDBY;
    //         }
    //         else
    //         {
    //             if(d1 > d2)
    //             {
    //                 if(firstIt)
    //                     clockWise = true;
    //                 DriveState = MoveState.SLOWRIGHT;
    //             }
    //             else
    //             {
    //                 if(firstIt)
    //                     clockWise = false;
    //                 DriveState = MoveState.SLOWLEFT;
    //             }
    //         }
    //         firstIt = false;
    //         break;
    //     case REALIGNING:
    //         if((onRight && clockWise) || (!onRight && !clockWise))
    //             DriveState = MoveState.FORWARD;
    //         else
    //             DriveState = MoveState.BACKWARD;
    //         if(!irLM.get() || !irRM.get())
    //             {
    //                 DriveState = MoveState.STANDBY;
    //                 vState = VisionState1.QUARTERTURN;
    //             }
    //         break;
    //     case QUARTERTURN:
    //         if(onRight)
    //         {
    //             DriveState = MoveState.RIGHT;
    //             if(!irLC.get())
    //                 {
    //                     DriveState = MoveState.STANDBY;
    //                     vState = VisionState1.DRIVE;
    //                 }
    //         }
    //         else
    //         {
    //             DriveState = MoveState.LEFT;
    //             if(!irRC.get())
    //             {
    //                 DriveState = MoveState.STANDBY;
    //                 vState = VisionState1.DRIVE;
    //             }
    //         }
    //             break;
    //     case DRIVE:
    //             DriveState = MoveState.FORWARD;
    //             break;
    //     }
    //   }

      private void onRightSetDistances()
      {
        double distance;

        distance = arduinoRight.getEdgeDist();
        if(distance != -1.0)
            edgeDist = distance;

        distance = arduinoRight.getmidDist();
        if(distance != -1.0)
            midDist = distance;
      }

    //   private void onLeftSetDistances()
    //   {
    //     double distance;

    //     distance = arduinoLeft.getEdgeDist();
    //     if(distance != -1)
    //         d1 = distance;

    //     distance = arduinoLeft.getmidDist();
    //     if(distance != -1)
    //         d2 = distance;
    //   }
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


}
