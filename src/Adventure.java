/*
 * File: Adventure.java
 * --------------------
 * This program plays the Adventure game from Assignment #4.
 */

import java.io.*;
import java.util.*;

/* Class: Adventure */
/**
 * This class is the main program class for the Adventure game.
 */

public class Adventure /* extends AdventureStub */{

	// Use this scanner for any console input
	private static Scanner scan = new Scanner(System.in);

	/**
	 * This method is used only to test the program
	 */
	public static void setScanner(Scanner theScanner) {
		scan = theScanner;
		// Delete the following line when done
		// AdventureStub.setScanner(theScanner);
	}

	/**
	 * Runs the adventure program
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) {
		// AdventureStub.main(args); // Replace with your code
		System.out.print("What will be your adventure today? ");
		String adventureName = scan.next(); // crowther, small, or tiny
		try {
			Adventure game = createGame(adventureName);
			game.run();
		} catch (IOException e) {
			System.out.println("The file requested is not found");
		}
	}

	/**
	 * This method will check if the fileName given is valid to be run or not.
	 * 
	 * @param fileName
	 * @return game.
	 */
	public static Adventure createGame(String adventureName) throws IOException {
		Adventure game = new Adventure();
		// For tiny only, because tiny only has rooms
		if (adventureName.equals("tiny")) {

			// read the rooms
			Scanner scan = new Scanner(new File(adventureName + "Rooms.txt"));
			while (scan.hasNextInt()) {
				AdvRoom room = AdvRoom.readFromFile(scan);
				game.rooms.put(room.getRoomNumber(), room);
			}
			scan.close();

		} else { // for crowther and small

			// read the rooms
			Scanner scan = new Scanner(new File(adventureName + "Rooms.txt"));
			while (scan.hasNextInt()) {
				AdvRoom room = AdvRoom.readFromFile(scan);
				game.rooms.put(room.getRoomNumber(), room);
			}
			scan.close();

			// read the objects
			scan = new Scanner(new File(adventureName + "Objects.txt"));
			while (scan.hasNext()) {
				// Be careful with blank lines
				AdvObject object = AdvObject.readFromFile(scan);
				game.objects.put(object.getName(), object);
				// place the object in its corresponding room
				AdvRoom room = game.rooms.get(object.getInitialLocation());
				room.addObject(object);
			}
			scan.close();

			// read the synonyms command
			scan = new Scanner(new File(adventureName + "Synonyms.txt"));
			while (scan.hasNext()) {
				String line = scan.nextLine().trim();
				String[] tokens = line.split("=");
				game.synonyms.put(tokens[0], tokens[1]);
			}
			scan.close();
		}
		return game;
	}

	/**
	 * Setting the currentRoom into the new currentRoom accessing the HashMap
	 * rooms.
	 * 
	 * @param roomNumber
	 */
	public void setCurrentRoom(int roomNumber) {
		if (!quit) {
			System.out.println("Current Room: " + roomNumber);
			currentRoom = rooms.get(roomNumber);
			currentRoom.setVisited(true);
			for (String description : currentRoom.getDescription()) {
				System.out.println(description);
			}
			if (currentRoom.getMotionTable()[0].equals("FORCED")) {
				executeMotionCommand("FORCED");
			}
		}

	}

	/**
	 * Creating initial room of the room after reading all the text file
	 */
	public void run() {
		// initial room is set to 1
		this.setCurrentRoom(1);
		while (!quit) {
			String command = scan.nextLine().trim();
			executeMotionCommand(command);
		}

	}

	/* Method: executeMotionCommand(direction) */
	/**
	 * Executes a motion command. This method is called from the
	 * AdvMotionCommand class to move to a new room.
	 * 
	 * @param direction
	 *            The string indicating the direction of motion
	 */
	public void executeMotionCommand(String direction) {
		// super.executeMotionCommand(direction); // Replace with your cod
		AdvMotionTableEntry[] entries = this.currentRoom.getMotionTable();
		direction = direction.toUpperCase();
		// Check whether the synonyms can be used
		if (synonyms.containsKey(direction)) {
			// direction is set to the equivalent synonym
			direction = synonyms.get(direction);
		}

		for (AdvMotionTableEntry entry : entries) {

			if (direction.equals(entry.getDirection())) {
				if (entry.getKeyName() == null) {
					setCurrentRoom(entry.getDestinationRoom());
					if (entry.getDirection().equals("FORCED")) {
						setCurrentRoom(entry.getDestinationRoom());
					}
					return;
				} else if (inventory.contains(entry.getKeyName())) {
					setCurrentRoom(entry.getDestinationRoom());
					if (entry.getDirection().equals("FORCED")) {
						setCurrentRoom(entry.getDestinationRoom());
					}
					return;
				}
			}
		}

		String[] tokens = direction.split("\\s");
		AdvObject object = null;
		if (tokens.length > 1) {
			object = objects.get(tokens[1]);
		}
		direction = tokens[0];

		// read the command from input
		if (direction.equals("QUIT")) {
			executeQuitCommand();
		} else if (direction.equals("HELP")) {
			executeHelpCommand();
		} else if (direction.equals("LOOK")) {
			executeLookCommand();
		} else if (direction.equals("INVENTORY")) {
			executeInventoryCommand();
		} else if (direction.equals("TAKE")) {
			executeTakeCommand(object);
		} else if (direction.equals("DROP")) {
			executeDropCommand(object);
		}
		// else {
		// System.out.println("Unavailable Command");
		// }

		String command = scan.nextLine();
		this.executeMotionCommand(command);
	}

	/* Method: executeQuitCommand() */
	/**
	 * Implements the QUIT command. This command should ask the user to confirm
	 * the quit request and, if so, should exit from the play method. If not,
	 * the program should continue as usual.
	 */
	public void executeQuitCommand() {
		// super.executeQuitCommand(); // Replace with your code
		System.out.print("Are you sure? Y/N ");
		String r = scan.next().toUpperCase();
		if (r.equals("Y")) {
			quit = true;
		}
	}

	/* Method: executeHelpCommand() */
	/**
	 * Implements the HELP command. Your code must include some help text for
	 * the user.
	 */
	public void executeHelpCommand() {
		// super.executeHelpCommand(); // Replace with your code
		List<String> commands = new ArrayList<String>();
		String[] otherCommands = { "QUIT", "LOOK", "DROP", "TAKE", "HELP",
				"INVENTORY" };
		for (String command : otherCommands) {
			commands.add(command);
		}
		AdvMotionTableEntry[] entries = this.currentRoom.getMotionTable();
		for (AdvMotionTableEntry entry : entries) {
			commands.add(entry.getDirection());
		}
		System.out.println("Available commands:");
		for (String command : commands) {
			System.out.println(command);
		}
	}

	/* Method: executeLookCommand() */
	/**
	 * Implements the LOOK command. This method should give the full description
	 * of the room and its contents.
	 */
	public void executeLookCommand() {
		// super.executeLookCommand(); // Replace with your code
		for (String description : this.currentRoom.getDescription()) {
			System.out.println(description);
		}
	}

	/* Method: executeInventoryCommand() */
	/**
	 * Implements the INVENTORY command. This method should display a list of
	 * what the user is carrying.
	 */
	public void executeInventoryCommand() {
		// super.executeInventoryCommand(); // Replace with your code
		if (inventory.isEmpty()) {
			System.out.println("Your inventory is empty");
		}
		for (AdvObject object : inventory) {
			System.out.println("You have " + object.getName().toLowerCase()
					+ ": " + object.getDescription());
		}
	}

	/* Method: executeTakeCommand(obj) */
	/**
	 * Implements the TAKE command. This method should check that the object is
	 * in the room and deliver a suitable message if not.
	 * 
	 * @param obj
	 *            The AdvObject you want to take
	 */
	public void executeTakeCommand(AdvObject obj) {
		// super.executeTakeCommand(obj); // Replace with your code
		if (currentRoom.containsObject(obj)) {
			inventory.add(obj);
			currentRoom.removeObject(obj);
			System.out.println(obj.getName().toLowerCase() + " Taken");
		} else {
			System.out.println("No Such object found!");
		}
	}

	/* Method: executeDropCommand(obj) */
	/**
	 * Implements the DROP command. This method should check that the user is
	 * carrying the object and deliver a suitable message if not.
	 * 
	 * @param obj
	 *            The AdvObject you want to drop
	 */
	public void executeDropCommand(AdvObject obj) {
		// super.executeDropCommand(obj); // Replace with your code
		if (inventory.contains(obj)) {
			inventory.remove(obj);
			currentRoom.addObject(obj);
			System.out.println(obj.getName().toLowerCase() + " Dropped");
		} else {
			System.out.println("You do not have such object!");
		}
	}

	/* Private instance variables */
	// Add your own instance variables here
	private AdvRoom currentRoom;
	// the room in the game
	private Map<Integer, AdvRoom> rooms = new HashMap<Integer, AdvRoom>();
	// List that contain the object
	private Map<String, AdvObject> objects = new HashMap<String, AdvObject>();
	// Store the Synonyms
	private Map<String, String> synonyms = new HashMap<String, String>();
	// to store the inventory of the player
	private List<AdvObject> inventory = new ArrayList<AdvObject>();
	// quit boolean
	private boolean quit;

}
