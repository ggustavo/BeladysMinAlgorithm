import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Main {
	
	
	
	public static void main(String[] args) throws IOException {
		
		System.out.println("Start Beladys MIN");
		
		BufferedReader log = backwardLog("log.requests");
		
		BeladysMin min = new BeladysMin(100000);
		
		System.out.println("Build Fast Workload ... O(N * log(K)), where K is the number of distinct pages");
		long startTime =  System.currentTimeMillis();
		
		min.buildFastWorkload(log);
		
		long endTime = System.currentTimeMillis();
		System.out.println("Finish Build Fast Workload " + (endTime - startTime) + " milliseconds");
		
		System.out.println("Process Beladys Min ... O(N * C)");
		startTime = System.currentTimeMillis();
		
		min.start();
		
		endTime = System.currentTimeMillis();
		System.out.println("Finish Process Beladys Min " + (endTime - startTime) + " milliseconds");
		
		
		System.out.println("Save Result ... O(N)");
		min.save();
		
		System.out.println("FINISH");
	}
	
	private static BufferedReader backwardLog(String logPath) throws FileNotFoundException {
		
		ReverseLineInputStream reverseLineInputStream = new ReverseLineInputStream(new File(logPath));

		return new BufferedReader(new InputStreamReader(reverseLineInputStream));	
	}
	
	
	private static BufferedReader forwardLog(String logPath) throws IOException {
		
		return Files.newBufferedReader(Paths.get(logPath));	
	}
	
}
