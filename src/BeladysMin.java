import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class BeladysMin {

	
	public static class Page{
		String id;
		Page next;
		int backFrequency;
		@Override
		public String toString() {
			return "Page [id=" + id + ", backFrequency=" + backFrequency + "]";
		}
		
		public int getFrequency() {return backFrequency;}
	}
	
	
	private int capacity;
	private HashMap<String, Page> LRU;
	private HashMap<String, Page> distinctPages;
	private LinkedList<Page> requestPages;
	
	
	public BeladysMin(int size) {
		this.capacity = size;
		this.LRU = new HashMap<String, BeladysMin.Page>();
		this.distinctPages = new HashMap<String, BeladysMin.Page>();
		this.requestPages = new LinkedList<BeladysMin.Page>();
		System.out.println("C = " + size);
	}
	
	
	public void buildFastWorkload(BufferedReader log) throws IOException {
		
		String logEntry = null;
		int count = 0;
		int approximateTotalSize = -1;
		boolean first = true;
		int percent = 0;
		String timeToFinish = null;
		
		long startTime =  System.nanoTime();

		while ((logEntry = log.readLine()) != null) { 
			
			count++;
			
			
			if (logEntry == null || logEntry.isEmpty()) {
				System.out.println("Read Error: " + count);
				continue;					
			}
			
			
			logEntry = logEntry.trim();

			String pageId = logEntry.split(",")[1];
			
			if(first) {
				first = false;
				approximateTotalSize =  Integer.parseInt(logEntry.split(",")[0]) ;
				System.out.println("Approximate N = " + approximateTotalSize+1 + " requests");
				System.out.println();
			}
			
			
			Page newPage = new Page();
			newPage.id = pageId;
			
			Page bufferedPage = distinctPages.get(pageId);
			
			if(bufferedPage != null) {
				newPage.next = bufferedPage;
				newPage.backFrequency = bufferedPage.backFrequency + 1;
			}
			
			distinctPages.put(pageId, newPage);
			requestPages.add(newPage);
			

			if(count % (approximateTotalSize/100) == 0) {
				percent++;

				long endTime =  System.nanoTime();
				//System.out.println(startTime + " " + endTime);
				double ms = (endTime - startTime);
				double minutes = ((((ms)/1000000)/1000)/60) * (1+approximateTotalSize-count)/(approximateTotalSize/100);
				timeToFinish = String.format("%.2f",minutes) + " minutes";	
				System.out.println(percent+"% ---> Finishing in " + timeToFinish);
				startTime =  System.nanoTime();
				
			}
			
			

		}
		
		System.out.println();
		System.out.println("Real N size: " + count);
		System.out.println("K size: " + distinctPages.size());
	}
	
	
	
	public void start() {
		boolean esp = false;
		
		
		int count = 0;
		Collections.reverse(requestPages);
		
		for (Page request : requestPages) {
			count++;
			
			Page p = LRU.get(request.id);
			
			if(p != null) {
				LRU.put(request.id, request);			
				//HIT?
			}else {
			
				if(LRU.size() >= capacity) {
					long startTime =  System.nanoTime();
				
					Page min = null;
					
					for (Page cPage : LRU.values()) {
						if(min == null)min = cPage;
						
						if(cPage.backFrequency < min.backFrequency)min = cPage;
						
						if(min.backFrequency == 0)break;
					}
					
					if(esp==false) {
						esp = true;
						long endTime =  System.nanoTime();
						
						double ms = (endTime - startTime);
						double minutes = ((((ms)/1000000)/1000)/60) * (requestPages.size()-capacity);
						String timeToFinish = String.format("%.2f",minutes) + " minutes";	
						System.out.println("Approximate finishing in " + timeToFinish);
					}
					
					LRU.remove(min.id);
				}
				
				LRU.put(request.id, request);			
			}
			
			
			
			
			
		}
		
		
	}
	
	
	public void save(){
		
		try {
			SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh-mm");
			String date = dt.format(new Date());
			PrintWriter logRequests = new PrintWriter(new FileWriter(new File(date + " baladys" + ".pages"), true));

			List<Page> result = new ArrayList<>(LRU.values());
			result.sort(Comparator.comparing(Page::getFrequency));
			
			for (int i = result.size()-1; i >= 0; i--) {
				Page r = result.get(i);
				
				logRequests.println(r.id+",R");
			}
			
			for (int i = result.size() - 1; i >= 0; i--) {


	
			}

			logRequests.flush();
			logRequests.close();
			
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	
	
	
	
}
