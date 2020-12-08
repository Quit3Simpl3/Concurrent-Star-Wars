package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;



import java.io.FileWriter;
import java.io.IOException;
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
		Diary diary = Diary.getInstance();
		// Create the attacks:
		Attack[] attacks = input.getAttacks();


		// TODO: Set attack sleep() times (millis):


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
		//String outputPath = args[1];
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
		HanSoloMicroservice HanSolo = new HanSoloMicroservice(init);
		C3POMicroservice C3P0 = new C3POMicroservice(init);
		R2D2Microservice R2D2 = new R2D2Microservice(input.getR2D2(),init);
		LandoMicroservice Lando = new LandoMicroservice(input.getLando(),init);
		LeiaMicroservice Leia = new LeiaMicroservice(input.getAttacks());



		Thread hanSolo = new Thread(HanSolo);
		Thread leia = new Thread(Leia);
		Thread r2d2 = new Thread(R2D2);
		Thread lando = new Thread(Lando);
		Thread c3po = new Thread(C3P0);

		// run:
		hanSolo.start();
		c3po.start();
		r2d2.start();
		lando.start();
		System.out.println("all the 4 threads runing");  //TODO: delete this after finish debug
	//	while (init.getCount()!=0) {   //TODO : not sure if we need loop
			try {
				init.await();
			} catch (InterruptedException e) {}
	//	}
		leia.start();
		System.out.println("liea is runing");  //TODO: delete this after finish debug
		// Finally:
	//	try {
	//		generateDiaryOutput(outputPath);
	//	}
	//	catch (IOException e) {
	//		System.out.println("Json writing error. Check the file.");
	//		System.out.println(e.getMessage());
	//	}
	}
}
