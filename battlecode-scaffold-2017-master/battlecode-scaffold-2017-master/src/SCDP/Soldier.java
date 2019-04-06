package SCDP;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

import static SCDP.RobotPlayer.*;

import com.sun.tools.classfile.TypeAnnotation.Position;



public class Soldier {
    static int leash = 10000;
	 static RobotInfo target = null;
	 static MapLocation oldlocation = null;
	 static MapLocation newlocation = null;
	 static int cooldown = 10;
	
	
	public static void runSoldier()
	{
    	
    	boolean dontShoot = false;
        //TODO code here, just random code presented
        System.out.println("I'm an soldier!");
        Team enemy = rc.getTeam().opponent();
        Team friends = rc.getTeam();
        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                
            	//Collect Info
            	MapLocation myLocation = rc.getLocation();
                //Nearby enemy robots
            	RobotInfo[] robots = rc.senseNearbyRobots(10.0f, enemy);
                //Nearby friendly robots
            	RobotInfo[] friendlyrobots = rc.senseNearbyRobots(10.0f,friends );
                //Nearby neutral trees
            	TreeInfo[] trees = rc.senseNearbyTrees();
                
            	boolean moved = false;
            	 int xPos = rc.readBroadcast(0);
                 int yPos = rc.readBroadcast(1);
                 MapLocation OpparchonLoc = new MapLocation(xPos,yPos);
                 
                 
                 //Direction dir = myLocation.directionTo(OpparchonLoc);
                 // Move in the opponent archon direction
                 Direction d=myLocation.directionTo(OpparchonLoc);
            	//If possible avoid incoming bullets
              moved = AvoidBullets();
              if(rc.canMove(d))
              {
              	tryMove(d);
              }
              else
              {
              	tryMove(randomDirection());
              }
              /*
              if(!moved)
              {
            	  moved =  GoToHelp(leash);
            	  if(!moved)
            	  {
            		  if(trees.length > 0 && Math.random() < .01)
                  	{
                  		if(rc.canShake(trees[0].getLocation()))
                  		rc.shake(trees[0].getLocation());
                  		
                  		tryMove(myLocation.directionTo( trees[0].getLocation()));
                  	}
                  	else
                  	tryMove(randomDirection());
                  }
            }
              */
              
             
            
             
                
                //Fight
              
                
                // If there are some...
                if (robots.length > 0)
                {
                	
                	AskForReinforcement();                	             	
                	/* if(target == null)
                     {
                     	target  = robots[0];
                     	oldlocation = target.getLocation();
                     }*/
                    target = robots[0];
                    
                    // 	newlocation = target.getLocation();
                    // MapLocation aim = new MapLocation(oldlocation.x - newlocation.x, oldlocation.y - newlocation.y);
                	
                    if (rc.canFireSingleShot())
                    {
                    	Direction aimdir = rc.getLocation().directionTo(target.location);
                      
    
                      /*       	//Avoid friendly-fire
                    	for(int i=0;i<friendlyrobots.length;i++)
                    	{
                    			
                    			Direction friendDir = rc.getLocation().directionTo(friendlyrobots[i].getLocation());

                    			if(friendDir.equals(aimdir, 0.314f))
                        		{
                        			dontShoot = true;
                        		}             
                    		
                    	}
                    	
                    	if(!dontShoot)
                    	{
                    		rc.fireSingleShot(aimdir);
                    	}*/
                    		
                    	rc.fireSingleShot(aimdir);
                     }
                    oldlocation = newlocation;
               
                
                }
                else {
                 	if( (rc.readBroadcast(5) == rc.getID() && rc.readBroadcastBoolean(7) ))
                	{
                		rc.broadcastBoolean(7, false);
                	}
                }
             
              

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }
}
