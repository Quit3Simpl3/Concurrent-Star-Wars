package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	private static void initialize(Input input) {
		// Create the Ewoks:
		Ewoks ewoks = Ewoks.getInstance();
		ewoks.createEwoks(input.getEwoks());
	}

	public static void main(String[] args) {
		Input input = new Input();
		initialize(input);
	}
}
