import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

	// Host or IP of Server
	private static final String HOST = "localhost";
	private static final int PORT = 2020;
	private static Registry registry;

	public static void main(String[] args) throws Exception {
		//File f = new File("InputFiles/cubeQueries.ini");
		File f2 = new File("InputFiles/cubeQueriesloan.ini");
		//File f3 = new File("InputFiles/cubeQueries2013_05_31.ini");

		//File f4 = new File("InputFiles/cubeQueriesorder.ini");

		// Search the registry in the specific Host, Port.

		registry = LocateRegistry.getRegistry(HOST, PORT);

		// LookUp for MainEngine on the registry

		IMainEngine service = (IMainEngine) registry.lookup(IMainEngine.class
				.getSimpleName());

		// Cube LOAN and queries
		service.initializeConnection("test", "CinecubesUser",
				"Cinecubes", "pkdd99", "loan");
		// service.initializeConnection("test", "CinecubesUser",
		//"Cinecubes", "pkdd99", "orders");
		//service.initializeConnection("adult_no_dublic", "CinecubesUser",
		//		"Cinecubes", "adult", "adult");

		// The first arg is audio; the second is for Word
		service.optionsChoice(false, true);

		// System.out.println(f2);
		service.answerCubeQueriesFromFile(f2);
		
		// System.out.println(f4);
		// service.AnswerCubeQueriesFromFile(f4);
		
		//service.AnswerCubeQueriesFromFile(f);
		
		System.out.println("Execution of client is complete");
	}

}
