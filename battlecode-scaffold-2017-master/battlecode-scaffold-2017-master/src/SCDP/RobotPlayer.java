package SCDP;
import static SCDP.RobotPlayer.randomDirection;
import static SCDP.RobotPlayer.rc;
import static SCDP.RobotPlayer.tryMove;

import battlecode.common.*;import scala.reflect.runtime.ThreadLocalStorage.MyThreadLocalStorage;

public strictfp class RobotPlayer {
    static RobotController rc;
    static int numofArchon = 0;
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;
        Team var=rc.getTeam();
        Team opp=var==Team.A?Team.B:Team.A;
        MapLocation []OppArchon=rc.getInitialArchonLocations(opp);
      //   numofArchon =  OppArchon.length; 
        // if(numofArchon!=0)
         {
        	// for i
        	 rc.broadcast(0,(int)OppArchon[0].x);
             rc.broadcast(1,(int)OppArchon[0].y);
         }
        //Broadcasting opponent archon location
        
        
        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
        switch (rc.getType()) {
            case ARCHON:
                runArchon();
                break;
            case GARDENER:
                runGardener();
                break;
            case SOLDIER:
                runSoldier();
                break;
            case LUMBERJACK:
                runLumberjack();
                break;
            case SCOUT:
                runScout();
                break;
            case TANK:
                runTank();
                break;
        }
	}

    static void runArchon() throws GameActionException {
            Archon.runAnchor();
    }

	static void runGardener() throws GameActionException {
      Gardener.runGardener();
    }

    static void runSoldier() throws GameActionException {
            Soldier.runSoldier();
    }

    static void runLumberjack() throws GameActionException {
        Lumberjack.runLumberjack();
    }

    static void runScout(){
        Scout.runScout();
    }

    static void runTank(){
        Tank.runTank();
    }

    /**
     * Returns a random Direction
     * @return a random Direction
     */
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }
    
    
    static boolean GoToHelp(int leash)
    {
    	try {
    	MapLocation myLocation = rc.getLocation();
    	//channel 7 says if some robots need help
    	//channel 5 gives the ID of the asking robot
    	
        if(rc.readBroadcast(5) != rc.getID() && rc.readBroadcastBoolean(7))
        {
           //Channels 3 and 4 stores the info about x and y location of the robot that is asking for help 
            MapLocation requestlocation =  new MapLocation(rc.readBroadcast(3), rc.readBroadcast(4));
            if(requestlocation.distanceTo(myLocation) < leash )
            {
            	return tryMove(myLocation.directionTo(requestlocation));            	
            }
        }
    	}
    	 catch (Exception e) {
             System.out.println("Soldier Exception");
             e.printStackTrace();
         }
		return false;
    }
    
    
    static void AskForReinforcement()
    {
    	
    	
    	 try {
    	if(rc.readBroadcast(6) != rc.getID())
    	{
    		 rc.broadcast(3,(int)rc.getLocation().x);
             rc.broadcast(4,(int)rc.getLocation().y);
             rc.broadcast(5, rc.getID());
             rc.broadcastBoolean(7, true);
        } 
    	 }
    	 catch (Exception e) {
            System.out.println("Soldier Exception");
            e.printStackTrace();
        }
    	 
    }
    
    
    static boolean AvoidBullets()
    {
    	 try {
    	BulletInfo[] incomingbullets = rc.senseNearbyBullets();
    	for(BulletInfo bi : incomingbullets)
    	{
    		if(willCollideWithMe(bi))
    		{
    			return tryMove((new Direction(0)));
    		}
    	}
    
    	 }
    	 catch (Exception e) {
             System.out.println("Soldier Exception");
             e.printStackTrace();
         }
		return false;
    		
    }
    
    /**
     * A slightly more complicated example function, this returns true if the given bullet is on a collision
     * course with the current robot. Doesn't take into account objects between the bullet and this robot.
     *
     * @param bullet The bullet in question
     * @return True if the line of the bullet's path intersects with this robot's current position.
     */
    static boolean willCollideWithMe(BulletInfo bullet) {
        MapLocation myLocation = rc.getLocation();

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }
}
