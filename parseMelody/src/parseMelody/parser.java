package parseMelody;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.List;
import java.util.Map;


public class parser {
	private final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {
		FileReader fr= new FileReader("./serverlist.txt");
		BufferedReader bufferedReader = new BufferedReader(fr);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			//System.out.println("reading");
			String[] words=line.split("\\s");
			//System.out.println(words[2]);
			if(words[1].equals("available")) {
				
				URL myURL = new URL("http://jcp.jioconnect.com/collectServer?application="+words[2]+"&format=json");
			    URLConnection c = myURL.openConnection();
			      FileWriter fw=new FileWriter("./logs/log"+words[2]+".json");
			      
			      
			      String authStr = Base64.getEncoder()
		                  .encodeToString("jcp:Jcp$1234".getBytes());
			      c.setRequestProperty("Authorization", "Basic " + authStr);
			      Map<String, List<String>> headers = c.getHeaderFields();
//			      System.out.println("-- Response headers --");
//			      headers.entrySet()
//			             .forEach(e -> System.out.printf("%s: %s%n", e.getKey(), e.getValue()));
//
//			      System.out.println("-- Response body --");
			      try (BufferedReader reader = new BufferedReader(
			              new InputStreamReader(c.getInputStream()))) {
			          reader.lines().forEach(t -> {
						try {
							//System.out.println(t);
							fw.write(t+"\n");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					});
			          fw.close();
			          System.out.println(words[2]+" data has been written");
			          Thread.sleep(500);
			          
			      } catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}
				
		}
		
	}
}
