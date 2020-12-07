package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	private static void initialize(Input input) {
		// Create the Ewoks:
		Ewoks ewoks = Ewoks.getInstance();
		ewoks.createEwoks(input.getEwoks());
		// Create the diary:
		Diary diary = Diary.getInstance();
		// Create the attacks:
		Attack[] attacks = input.getAttacks();
		// TODO: Set attack sleep() times (millis):

		// TOOD: Create microservices

	}

	private static Input parseJson(String path) throws IOException {
		return JsonInputReader.getInputFromJson(path);
	}

	private static void generateDiaryOutput(String path) throws IOException {
		Gson gson = new Gson();
		// TODO: Verify this output!
		Diary diary = Diary.getInstance();
		gson.toJson(diary, new FileWriter(path));
	}

	public static void main(String[] args) {
		String inputPath = args[0];
		String outputPath = args[1];
		Input input = null;
		try {
			input = parseJson(inputPath);
			if (input == null) throw new IOException();
		}
		catch (IOException e) {
			System.out.println("Json parsing error. Check the file.");
			System.out.println(e.getMessage());
		}
		initialize(input);
		// TODO: Start the microservices:

		// Finally:
		try {
			generateDiaryOutput(outputPath);
		}
		catch (IOException e) {
			System.out.println("Json writing error. Check the file.");
			System.out.println(e.getMessage());
		}
	}
}
