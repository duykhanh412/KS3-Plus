package pw;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class APIExtractor {
	ArrayList<String> APIs;
	Boolean[][] API_API;
	
	public HashMap<String, ArrayList<String>> API_API_Strings = new HashMap<String, ArrayList<String>>();
	public HashMap<Integer, ArrayList<Integer>> API_API_Integers = new HashMap<Integer, ArrayList<Integer>>();;
	
	public APIExtractor() {
		APIs = new ArrayList<String>();
		API_API = new Boolean[1496][1496];
		for(int i=0; i<API_API.length; ++i) {
			for(int j=0; j<API_API[i].length; ++j) {
				API_API[i][j] = false;
			}
		}
		try {
			readMashups();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readMashups() throws IOException {
		
		String line = null;
		String[][] mashup_id_name = new String[6295][2];
		Integer index = 0;
		String[] parts;
		String[] mashupID;
		String[] mashupName;
		
		String[][] all_mashup_api = new String[13185][3]; //[mashup_id][mashup_name][api_name]
		HashMap<String, Integer> uiqueAPIs = new HashMap<String, Integer>();
		HashMap<String, Integer> uniqueAPIs = new HashMap<String, Integer>();

		
		File fin;
		BufferedReader br;
		PrintWriter writer;
		
//		//read mashups from
//		fin = new File("d:\\mashup_summary.sql");
//		br = new BufferedReader(new FileReader(fin));
//		while((line = br.readLine()) != null) {
//			parts = line.split("VALUES");
//			//System.out.println(parts[1]);
//		
//			parts = parts[1].split(",");
//			//System.out.println(parts[1]); //+ " - " + parts[1]);
//			
//			mashupID = parts[0].split("'");
//			mashup_id_name[index][0] = mashupID[1];
//			
//			mashupName = parts[1].split("'");
//			mashup_id_name[index][1] = mashupName[1];
//						
//			++index;
//			
//		}
//		br.close();
//		
//		//write mashups to file mashups_new.txt
//		PrintWriter writer = new PrintWriter("d:\\mashups_new.txt", "UTF-8");
//		for(int i=0; i<mashup_id_name.length; ++i) {
//			writer.println(mashup_id_name[i][0]+"#"+mashup_id_name[i][1]);
//			System.out.println(mashup_id_name[i][0] + " - " + mashup_id_name[i][1]);
//		}
//		writer.close();
		
		//read mashups-apis
		fin = new File("d:\\mashup_api_related.sql");
		br = new BufferedReader(new FileReader(fin));
		
		while((line = br.readLine()) != null) {
			parts = line.split("VALUES");
			//System.out.println(parts[1]);
			
			parts = parts[1].split(",");
			parts[0] = parts[0].split("'")[1];
			parts[1] = parts[1].split("'")[1];
			parts[2] = parts[2].split("'")[1];
			//System.out.println(parts[0] + " - " + parts[1] + " - " + parts[2]);
			all_mashup_api[index][0] = parts[0];
			all_mashup_api[index][1] = parts[1];
			all_mashup_api[index][2] = parts[2];
			++index;
		}
		//output all_mashup_api[][]
/*		for(int i=0; i<all_mashup_api.length; ++i) {
			System.out.println(all_mashup_api[i][0] + " - " + all_mashup_api[i][1] + " - " + all_mashup_api[i][2]);
		}*/
		
		//link multiple apis to corresponding mashups based on all_mashup_api[][]
		HashMap<Integer, ArrayList<String>> hm_mashups_apis = new HashMap<Integer, ArrayList<String>>();
		ArrayList<String> apiList;
		for(int i=0; i<all_mashup_api.length; ++i) {
			apiList = hm_mashups_apis.get(Integer.parseInt(all_mashup_api[i][0]));
			if(apiList != null) {
				apiList.add(all_mashup_api[i][2]);
//				System.out.println("Adding " + all_mashup_api[i][2] + " to API #" + all_mashup_api[i][0]);
				hm_mashups_apis.put(Integer.parseInt(all_mashup_api[i][0]), apiList);
			} else {
				apiList = new ArrayList<String>();
				apiList.add(all_mashup_api[i][2]);
//				System.out.println("Adding " + all_mashup_api[i][2] + " to API #" + all_mashup_api[i][0]);
				hm_mashups_apis.put(Integer.parseInt(all_mashup_api[i][0]), apiList);
			}
		}
		//output links between mashups and apis
/*		Set<HashMap.Entry<Integer, ArrayList<String>>> entrySet = hm_mashups_apis.entrySet();
		for (Entry<Integer, ArrayList<String>> entry : entrySet) {
			System.out.print("Mashup #" + entry.getKey() + ": ");
			for(int i=0; i<entry.getValue().size(); ++i) {
				System.out.print(entry.getValue().get(i) + ", ");
			}
			System.out.println("");
		}*/
		//write hm_mashups_apis to file mashups_apis.txt
/*		writer = new PrintWriter("d:\\mashups_apis.txt", "UTF-8");
		for(Integer key : hm_mashups_apis.keySet()) {
			writer.print(key + "#");
			for(int i=0; i<hm_mashups_apis.get(key).size(); ++i) {
				writer.print(hm_mashups_apis.get(key).get(i));
				if(i != hm_mashups_apis.get(key).size()-1) {
					writer.print("#");
				}
			}
			writer.println("");
		}
		writer.close();*/
		
		//extract all unique apis from hm_mashups_apis
		uniqueAPIs = new HashMap<String, Integer>();
		Set<HashMap.Entry<Integer, ArrayList<String>>> mashup_APIs_Set = hm_mashups_apis.entrySet();
		Integer apiIndex = 0;
		for (Entry<Integer, ArrayList<String>> entry : mashup_APIs_Set) {
			for(int i=0; i<entry.getValue().size(); ++i) {
				if(uniqueAPIs.get(entry.getValue().get(i)) == null) {
					uniqueAPIs.put(entry.getValue().get(i), apiIndex);
					++apiIndex;
				}
			}
		}
		//output all unique apis in uniqueAPIs
//		Set<HashMap.Entry<String, Integer>> apiSet = uniqueAPIs.entrySet();
//		for(Entry<String, Integer> entry : apiSet) {
//			System.out.println("API #" + entry.getValue() + ":\t" + entry.getKey());
//		}
		
		//insert all unique APIs into the ArrayList named APIs
		String[] APIArray = new String[uniqueAPIs.size()];
		Set<HashMap.Entry<String, Integer>> APISet = uniqueAPIs.entrySet();
		//into an array first
		for (Entry<String, Integer> entry : APISet) {
			APIArray[entry.getValue()] = entry.getKey();
		}
		//then into the ArrayList named APIs
		for(int i=0; i<APIArray.length; ++i) {
			this.APIs.add(APIArray[i]);
		}
		//output the ArrayList named APIs
/*		for(int i=0; i<this.APIs.size(); ++i) {
			System.out.println("API #" + i + ": " + this.APIs.get(i));
		}*/
		//write APIs to file APIs.txt
/*		writer = new PrintWriter("d:\\APIs.txt", "UTF-8");
		for(int i=0; i<this.APIs.size(); ++i) {
			writer.println("API #" + i + ": " + this.APIs.get(i));
		}
		writer.close();*/
		
		//link API to API based on mashup_APIs_Set extracted from HashMap<Integer, ArrayList<String>> hm_mashups_apis
		String[] apiArray;
		for (Entry<Integer, ArrayList<String>> entry : mashup_APIs_Set) {
			if(entry.getValue().size() > 1) {	//only process mashups that use more than 1 API
				apiArray = entry.getValue().toArray(new String[entry.getValue().size()]);
//				for(int i=0; i<apiArray.length; ++i) {System.out.print(apiArray[i] + ", ");} System.out.println("");
				for(int i=0; i<entry.getValue().size()-1; ++i) {
					String service1 = entry.getValue().get(i);
					String service2 = entry.getValue().get(i+1);
					
					Integer service1Index = uniqueAPIs.get(service1);
					Integer service2Index = uniqueAPIs.get(service2);
					
					//add to API_API
					this.API_API[service1Index][service2Index] = true;
					this.API_API[service2Index][service1Index] = true;
					
					//add to API_API_Integers
					if(this.API_API_Integers.get(service1Index) == null) {
						ArrayList<Integer> al = new ArrayList<Integer>();
						al.add(service2Index);
						this.API_API_Integers.put(service1Index, al);
//						System.out.println(service1Index);
					} else {
						if(this.API_API_Integers.get(service1Index).contains(service2Index)) {
							
						} else {
							this.API_API_Integers.get(service1Index).add(service2Index);
						}
					}
					
					//add to API_API_Strings
					if(this.API_API_Strings.get(service1) == null) {
						ArrayList<String> al = new ArrayList<String>();
						al.add(service2);
						this.API_API_Strings.put(service1, al);
//						System.out.println(service1Index);
					} else {
						if(this.API_API_Strings.get(service1).contains(service2)) {
							
						} else {
							this.API_API_Strings.get(service1).add(service2);
						}
					}
				}
			}
		}
		//output API_API
/*		for(int i=0; i<this.API_API.length; ++i) {
			for(int j=0; j<this.API_API[i].length; ++j) {
				if(this.API_API[i][j] == true) {
					System.out.print("1");
				} else {
					System.out.print("0");
				}
			}
			System.out.println("");
		}*/
		//output API_API_Integers
/*		Set<HashMap.Entry<Integer, ArrayList<Integer>>> entrySet = this.API_API_Integers.entrySet();
		for (Entry<Integer, ArrayList<Integer>> entry : entrySet) {
			System.out.print("API #" + entry.getKey() + ": ");
			for(int i=0; i<entry.getValue().size(); ++i) {
				System.out.print(entry.getValue().get(i));
				if(i != entry.getValue().size() - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("");
		}*/
		//output API_API_Strings
		Set<HashMap.Entry<String, ArrayList<String>>> entrySet = this.API_API_Strings.entrySet();
		for (Entry<String, ArrayList<String>> entry : entrySet) {
			System.out.print("API " + entry.getKey() + ": ");
			for(int i=0; i<entry.getValue().size(); ++i) {
				System.out.print(entry.getValue().get(i));
				if(i != entry.getValue().size() - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("");
		}
	}
}


