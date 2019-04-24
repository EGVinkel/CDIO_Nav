import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Waypoint;


public class MainServer extends Thread {
	public static final int port = 7361;
	private Socket client;
	private static boolean looping= true;
	private static ServerSocket server;
	private static EV3MediumRegulatedMotor A;
	private static EV3LargeRegulatedMotor B;
	private static EV3LargeRegulatedMotor C;
	private  MovePilot pilot;
	private DataInputStream dIn;
	Navigator nav;
	// private static EV3MediumRegulatedMotor D= new EV3MediumRegulatedMotor(MotorPort.D);
	public MainServer(Socket client){
		this.client= client;
		Button.ESCAPE.addKeyListener(new EscapeListener());
		
		 A = new EV3MediumRegulatedMotor(MotorPort.A);
		 B = new EV3LargeRegulatedMotor(MotorPort.B);
		 C = new EV3LargeRegulatedMotor(MotorPort.C);
		 	
		 double wheelDiameter = 3.7;
		 double trackWidth = 13;
		 
		
		 final Wheel leftWheel = WheeledChassis.modelWheel(B,wheelDiameter).offset(-12).invert(true);
		 final Wheel rightWheel = WheeledChassis.modelWheel(C,wheelDiameter).offset(12).invert(true);
		 
		 Chassis myChassis = new WheeledChassis(new Wheel[]{rightWheel,leftWheel} ,WheeledChassis.TYPE_DIFFERENTIAL);
		 pilot = new MovePilot(myChassis);
		 
		 
		 
		//pilot =  new DifferentialPilot(wheelDiameter, trackWidth, C, B, true);
		
	}
	public static void main(String[] args) throws IOException {
		server= new ServerSocket(port);
		while(looping){
			System.err.println("Awaiting client..");
			new MainServer(server.accept()).start();
		}
	}
	
	
	int lastcommand = 0;
	public void carAction(int command){
		System.err.println(command);
		System.err.println(command);
		
		if(command == lastcommand){
			return;
		}
		
		lastcommand = command;
		
		switch(command){
		case 1:
			pilot.forward();
			//B.backward();
			//C.backward();
			break;
		case -1:
			pilot.stop();
			
			//B.stop();
			//C.stop();
			break;
		case 2:
			pilot.backward();
			break;
		case -2: 
			pilot.stop();
			break;
		case 3:    //E
			A.forward();
			break;
		case -3:   //E
			A.stop();
			break;
		case 4:  
			pilot.rotateRight();
		break;
		case -4:  
			pilot.stop();
			break;
		case 5:  
			pilot.rotateLeft();
			break;
			case -5:  
			pilot.stop();
		
			break;
			case 7:
				nav = new Navigator(pilot);
				nav.clearPath();
				
			int x;
			try {
				x = dIn.readInt();
				int y = dIn.readInt();
				System.out.println(" X: " + x +" Y: " +y );
				//nav.goTo(x, y);
				nav.goTo(new Waypoint(x, y));
				Thread.sleep(5);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
				
		
				
				
				
					
						break;
				
		}
		
		
	}
	public void run(){
		System.out.println("Client Connect");
		try{
			InputStream in = client.getInputStream();
			dIn= new DataInputStream(in);
			
			while(client!=null){
				int command = dIn.readInt();
				System.out.println("REC" + command);
				
					carAction(command);
				}
			
			}catch(IOException e){
				e.printStackTrace();	
		}
	}
	
	private class EscapeListener implements KeyListener {

		@Override
		public void keyPressed(Key k) {
			looping= false;
			System.exit(0);
			
		}

		@Override
		public void keyReleased(Key k) {
			
		}
		
	}
	
	
	
}
