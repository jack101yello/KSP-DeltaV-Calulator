/*
 * To do:
 * - Add data on all boosters and tanks in KSP
 * - Add specific errors to throw everywhere because users are the worst and need to be told what they're doing wrong
 */

import java.util.Scanner; // Gets inputs

public class Main {
	
	private static Scanner scan = new Scanner(System.in);
	private static double g = 9.81; // Acceleration due to gravity on Earth. Change for other planets
	// Specific impulse, dry mass, fuel mass
	// Dev note: change these values to the ones used in the game
	private static SRB srb1 = new SRB(1, 1, 1);
	private static SRB srb2 = new SRB(2, 1, 1);
	// Fuel mass
	// Dev note: change these values to the ones used in the game
	private static LiquidFuel fuel1 = new LiquidFuel(3, 2);
	private static LiquidFuel fuel2 = new LiquidFuel(3, 1);
	// Specific impulse
	// Dev note: change these values to the ones used in the game
	private static LiquidFuelBooster LFB1 = new LiquidFuelBooster(1, 1);
	private static LiquidFuelBooster LFB2 = new LiquidFuelBooster(2, 1);
	static int[] deltaV; // Delta V of the current stage
	static int[] lFuel; // Liquid fuel mass of the current stage
	static int[] lIsp; // Specific impulse of all of the liquid fuel engines in the current stage
	static int[] lbMass; // Mass of the liquid fuel engines in the current stage
	static int payloadMass; // Mass of the payload
	static int[] totalStageMass; // Total mass of the current stage
	static int[] carryingMass; // Mass being held up by current stage
	static int decision;
	
	// Gets the parts for each stage from the user
	private static void getParts(int currentStage) {
		System.out.println("Stage " + currentStage); // Print current stage
		System.out.println("Would you like to add:\nSRB[1]\tLiquid fuel tank[2]\tLiquid fuel booster[3]\tDone with this stage[0]");
		switch(scan.nextInt()) { // Check input for which fuel tank was selected
		case 1:
			addSRB(currentStage);
			System.out.println("Would you like to add anything else to this stage? yes[1] no[0]");
			decision = scan.nextInt();
			if(decision == 1) { // Reruns the getParts() method to get another part
				getParts(currentStage);
			}
			else if(decision == 0) { // Compute the liquid fuel booster contribution to the delta v
				calcCarMass(currentStage);
				deltaV[currentStage] += lIsp[currentStage] * g * Math.log((lbMass[currentStage] + lFuel[currentStage] + carryingMass[currentStage]) / (lbMass[currentStage] + carryingMass[currentStage]));
			}
			else {
				throw new IllegalStateException("I cannot simultaneously add and not add parts.");
			}
			break;
		case 2:
			addFuel(currentStage);
			System.out.println("Would you like to add anything else to this stage? yes[1] no[0]");
			decision = scan.nextInt();
			if(decision == 1) {
				getParts(currentStage);
			}
			else if(decision == 0) {
				calcCarMass(currentStage);
				deltaV[currentStage] += lIsp[currentStage] * g * Math.log((lbMass[currentStage] + lFuel[currentStage] + carryingMass[currentStage]) / (lbMass[currentStage] + carryingMass[currentStage]));
			}
			else {
				throw new IllegalStateException("I cannot simultaneously add and not add parts.");
			}
		case 3:
			addLFB(currentStage);
			System.out.println("Would you like to add anything else to this stage? yes[1] no[0]");
			if(scan.nextInt() == 1) {
				getParts(currentStage);
			}
			else if(scan.nextInt() == 0) {
				calcCarMass(currentStage);
				deltaV[currentStage] += lIsp[currentStage] * g * Math.log((lbMass[currentStage] + lFuel[currentStage] + carryingMass[currentStage]) / (lbMass[currentStage] + carryingMass[currentStage]));
			}
			else {
				throw new IllegalStateException("I cannot simultaneously add and not add parts");
			}
		}
	}
	
	// Adds an SRB to the current stage
	private static void addSRB(int currentStage) {
		System.out.println("SRB 1[1]\tSRB2[2]");
		switch(scan.nextInt()) {
		case 1:
			calcCarMass(currentStage);
			deltaV[currentStage] += srb1.Isp * g * Math.log((srb1.dryMass + srb1.fuelMass + carryingMass[currentStage]) / (srb1.dryMass + carryingMass[currentStage]));
			totalStageMass[currentStage] += srb1.fuelMass + srb1.dryMass;
			break;
		case 2:
			calcCarMass(currentStage);
			deltaV[currentStage] += srb2.Isp * g * Math.log((srb2.dryMass + srb2.fuelMass + carryingMass[currentStage]) / (srb2.dryMass + carryingMass[currentStage]));
			totalStageMass[currentStage] += srb2.fuelMass + srb2.dryMass;
			break;
		default:
			throw new IllegalStateException("Invalid selection made");
		}
	}
	
	// Calculates the carried mass for the current stage
	private static void calcCarMass(int currentStage) {
		carryingMass[currentStage] = 0;
		for(int i = 0; i < currentStage; i++) {
			System.out.println("I'm here!");
			carryingMass[currentStage] += totalStageMass[i];
		}
		carryingMass[currentStage] += payloadMass;
		System.out.println("Calculated carrying mass!");
	}
	
	// Adds a liquid fuel tank to the current stage
	private static void addFuel(int currentStage) {
		System.out.println("Fuel 1[1]\tFuel2[2]");
		switch(scan.nextInt()) {
		case 1:
			lFuel[currentStage] += fuel1.fuel;
			totalStageMass[currentStage] += fuel1.fuel + fuel1.dryMass;
			break;
		case 2:
			lFuel[currentStage] += fuel2.fuel;
			totalStageMass[currentStage] += fuel2.fuel + fuel2.dryMass;
			break;
		default:
			throw new IllegalStateException("Invalid selection");
		}
	}
	
	// Adds a liquid fuel booster to the current stage
	private static void addLFB(int currentStage) {
		System.out.println("LFB 1[1]\tLFB 2[2]");
		switch(scan.nextInt()) {
		case 1:
			lIsp[currentStage] += LFB1.Isp;
			totalStageMass[currentStage] += LFB1.mass;
			break;
		case 2:
			lIsp[currentStage] += LFB2.Isp;
			totalStageMass[currentStage] += LFB2.mass;
			break;
		default:
			throw new IllegalStateException("Invalid selection");
		}
	}
	
	public static void main(String[] args) {
		System.out.println("/// DEV NOTE: If you get to a weird screen that you shouldn't be on\n"
				+ "/// or a request for an input where there shouldn't be any\n"
				+ "/// please input 0. I apologize for any inconvenience."); // Oops
		System.out.println("What is the mass of your payload? (Anything that isn't booster or fuel)");
		payloadMass = scan.nextInt(); // Get payload mass
		if(payloadMass < 0)
			throw new IllegalStateException("You done broke gravity");
		System.out.println("How many stages do you have?");
		int stageCount = scan.nextInt(); // Get the number of stages
		if(stageCount <= 0)
			throw new IllegalStateException("Please, I'm just a simple programmer. Don't make me deal with illegal inputs.");
		// Actually declare arrays because now we know how many stages there are
		deltaV = new int[stageCount];
		lFuel = new int[stageCount];
		lIsp = new int[stageCount];
		lbMass = new int[stageCount];
		totalStageMass = new int[stageCount];
		carryingMass = new int[stageCount];
		for(int i = 0; i < stageCount; i++) { // Get the parts for each stage, where i is the current stage
			getParts(i);
		}
		int sum = 0;
		for(int i = 0; i < stageCount; i++) { // Calculates the delta v remaining at each step in the process, in reverse order
			System.out.println("Stage" + i + " has " + deltaV[i] + "m/s of DeltaV.");
			sum += deltaV[i];
			System.out.println("The total deltaV remaining by now is " + sum + "m/s");
		}
	}

}
