package other;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class KeywordProcessor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String file = "d:\\newkeywords.txt";
		FileReader fr;
		BufferedReader br;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			PrintWriter writer = new PrintWriter("d:\\keywords.txt", "UTF-8");
			
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				writer.print("\""+line+"\",");
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
