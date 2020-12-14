package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
		// Create the diary:
		Diary.getInstance();
	}

	// TODO: DELETE BEFORE SUBMITTING!!!
	private static void println(String str) {
		System.out.println(str);
	}

	private static void generateTestResults(String path, long start_timestamp, long end_timestamp) {
		println("\n**** TEST RESULTS ****");
		Diary diary = Diary.getInstance();

		long timediff_finish_attackers = Math.abs(diary.getC3POFinish() - diary.getHanSoloFinish());
		println("First attacker finished after start: " + (Math.min(diary.getC3POFinish(), diary.getHanSoloFinish()) - start_timestamp));
		println("Last attacker finished after start: " + (Math.max(diary.getC3POFinish(), diary.getHanSoloFinish()) - start_timestamp));
		println("Time diff attack finish: " + timediff_finish_attackers);

		long first_termination = Math.min(diary.getHanSoloTerminate(), diary.getC3POTerminate());
		first_termination = Math.min(first_termination, diary.getR2D2Terminate());
		first_termination = Math.min(first_termination, diary.getLeiaTerminate());
		first_termination = Math.min(first_termination, diary.getLandoTerminate());
		println("First termination after start: " + (first_termination - start_timestamp));

		long last_termination = Math.max(diary.getHanSoloTerminate(), diary.getC3POTerminate());
		last_termination = Math.max(last_termination, diary.getR2D2Terminate());
		last_termination = Math.max(last_termination, diary.getLeiaTerminate());
		last_termination = Math.max(last_termination, diary.getLandoTerminate());
		println("Last termination after start: " + (last_termination - start_timestamp));

		println("Program end after start: " + (end_timestamp - start_timestamp));

		println("**** END ****");
	}
	// TODO: DELETE BEFORE SUBMITTING!!!

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
		// TODO: DELETE BEFORE SUBMITTING!!!
		long start_timestamp = System.currentTimeMillis();
		// TODO: DELETE BEFORE SUBMITTING!!!

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
		CountDownLatch init = new CountDownLatch(4);
		// Create threads:
		Thread[] microservices = {
			new Thread(new HanSoloMicroservice(init),"HanSolo"),
			new Thread(new R2D2Microservice(input.getR2D2(), init), "R2D2"),
			new Thread(new LandoMicroservice(input.getLando(), init), "Lando"),
			new Thread(new C3POMicroservice(init), "C3PO"),
		};
		/*Thread hanSolo = new Thread(new HanSoloMicroservice(init),"HanSolo");
		Thread r2d2 = new Thread(new R2D2Microservice(input.getR2D2(),init), "R2D2");
		Thread lando = new Thread(new LandoMicroservice(input.getLando(),init), "Lando");
		Thread c3po = new Thread(new C3POMicroservice(init), "C3PO");*/
		Thread leia = new Thread(new LeiaMicroservice(input.getAttacks()), "Leia");

		Thread OutputWriter = new Thread(
			() -> {
				try {
					/*hanSolo.join();
					leia.join();
					r2d2.join();
					c3po.join();
					lando.join();*/
					for (Thread thread : microservices) thread.join();
					long end_timestamp = System.currentTimeMillis();
					generateDiaryOutput(outputPath);

					// TODO: DELETE BEFORE SUBMITTING!!!
					generateTestResults("test_results.json", start_timestamp, end_timestamp);
					// TODO: DELETE BEFORE SUBMITTING!!!
				}
				catch (IOException e) {
					System.out.println("Json writing error. Check the file.");
					System.out.println(e.getMessage());
				}
				catch (InterruptedException ex) {}
			},
			"OutputWriter"
		);

		// Start threads:
		/*hanSolo.start();
		c3po.start();
		r2d2.start();
		lando.start();*/
		for (Thread thread : microservices) thread.start();
	//	while (init.getCount()!=0) {   //TODO : not sure if we need loop
			try {
				init.await();
			} catch (InterruptedException e) {}
	//	}
		leia.start();
		// Finally:
		OutputWriter.start();
	}
}
