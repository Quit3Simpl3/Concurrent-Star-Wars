package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.CountDownLatch;

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

	private static void printOutput(int attacks, long timediff_finish, long timediff_terminate) {
		System.out.println("There are " + attacks + " attacks.");
		System.out.println("HanSolo and C3PO finished their tasks ~" + timediff_finish + " milliseconds one after the other.");
		System.out.println("All threads terminated ~" + timediff_terminate + " milliseconds later.");
	}

	private static Input parseJson(String path) throws IOException {
		return JsonInputReader.getInputFromJson(path);
	}

	private static void generateDiaryOutput(String path) throws IOException {
		Diary diary = Diary.getInstance();
		try (Writer writer = new FileWriter(path)) {
			Gson gson = new Gson();
			gson.toJson(diary, writer);
		}
	}

	public static void main(String[] args) {
		// Make sure args received:
		if (args == null) return;
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

		// Countdown latch preparation:
		CountDownLatch init = new CountDownLatch(4);

		// Create threads:
		Thread[] microservices = {
			new Thread(new HanSoloMicroservice(init),"HanSolo"),
			new Thread(new R2D2Microservice(input.getR2D2(), init), "R2D2"),
			new Thread(new LandoMicroservice(input.getLando(), init), "Lando"),
			new Thread(new C3POMicroservice(init), "C3PO"),
		};
		Thread leia = new Thread(new LeiaMicroservice(input.getAttacks()), "Leia");

		Thread OutputWriter = new Thread(
			() -> {
				try {
					// Wait until all threads are terminated:
					for (Thread thread : microservices) thread.join();
					long end_timestamp = System.currentTimeMillis();
					generateDiaryOutput(outputPath);

					// Print the output:
					Diary diary = Diary.getInstance();
					int attacks = diary.getTotalAttacks();
					long timediff_finish = Math.abs(diary.getHanSoloFinish() - diary.getC3POFinish());
					long last_finish = Math.max(diary.getHanSoloFinish(), diary.getC3POFinish());

					long last_termination = Math.max(diary.getHanSoloTerminate(), diary.getC3POTerminate());
					last_termination = Math.max(last_termination, diary.getR2D2Terminate());
					last_termination = Math.max(last_termination, diary.getLeiaTerminate());
					last_termination = Math.max(last_termination, diary.getLandoTerminate());
					long timediff_terminate = Math.abs(last_termination - last_finish);

					printOutput(attacks, timediff_finish, timediff_terminate);
				}
				catch (IOException e) {
					System.out.println("Json writing error.");
					System.out.println(e.getMessage());
				}
				catch (InterruptedException ex) {}
			},
			"OutputWriter"
		);

		// Start the microservice threads:
		for (Thread thread : microservices) thread.start();
		try {
			init.await();
		}
		catch (InterruptedException e) {}
		// Start Leia's thread:
		leia.start();
		// Write the output json:
		OutputWriter.start();
	}
}
