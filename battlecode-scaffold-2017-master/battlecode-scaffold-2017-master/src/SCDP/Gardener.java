package SCDP;

import battlecode.common.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;

import static SCDP.RobotPlayer.*;
import static java.util.Arrays.sort;

public class Gardener {
    public static void runGardener(){
        //TODO code here, just dummy code is here
        System.out.println("I'm a gardener!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Listen for home archon's location
                int xPos = rc.readBroadcast(0);
                int yPos = rc.readBroadcast(1);
                MapLocation archonLoc = new MapLocation(xPos,yPos);
                
              Direction dir = randomDirection();
              if(rc.getTeamBullets()>75)
              {
            	  rc.donate(8);
              }
              
              
                if(rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .5)
                {
                	 rc.buildRobot(RobotType.SOLDIER, dir);
                }
              
                else
                {
                	  plantAll();
                	  waterOrShake();

                }
               

                // Generate a random direction
                //Direction dir = randomDirection();

                // Randomly attempt to build a soldier or lumberjack in this direction
               // if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
                 //   rc.buildRobot(RobotType.SOLDIER, dir);
                //} else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && rc.isBuildReady()) {
                //    rc.buildRobot(RobotType.LUMBERJACK, dir);
                //}



                // Move randomly
                //tryMove(randomDirection());

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

    private static void plantAll() throws GameActionException {
        /// Plant six trees around robot
        for(int i = 0; i <= 360; i++){
            Direction d = new Direction(i*60);
            if(rc.canPlantTree(d)){
                rc.plantTree(d);
            }
        }
    }

    private static void waterOrShake() throws GameActionException {
        TreeInfo [] trees = rc.senseNearbyTrees();
        sort(trees, Comparator.comparingDouble(TreeInfo::getHealth));
        for (TreeInfo t : trees
             ) {
            if(rc.canShake(t.ID) && t.containedBullets > 0){
                rc.shake(t.ID);
            }
            else if(rc.canWater(t.ID)){
                rc.water(t.ID);
            }
        }
    }
}
