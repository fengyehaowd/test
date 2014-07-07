import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class process2 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String  path="d:/0322.txt";
		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
		ArrayList<DetailTime> list = new ArrayList<DetailTime>();
		String line;
		line = reader.readLine();
		int state = 0;
		int year = 0;
		int totalDays = 0;
		int hour = 0;
		int minute = 0;
		int second = 0;
		while(line!= null)
		{
			try{
			String l = line;
			line = reader.readLine();
			String[] eles = line.split("Year=");
			String tmp = eles[1].split(" ")[0];
			year= Integer.parseInt(tmp);
			
			eles = line.split("Day=");
			tmp = eles[1].split(" ")[0];
			totalDays= Integer.parseInt(tmp);
			
			eles = line.split("Hours=");
			tmp = eles[1].split(" ")[0];
			hour= Integer.parseInt(tmp);
			
			eles = line.split("Minutes=");
			tmp = eles[1].split(" ")[0];
			minute= Integer.parseInt(tmp);
			
			eles = line.split("Seconds=");
			tmp = eles[1].split(" ")[0];
			second= Integer.parseInt(tmp);
			System.out.println(year+" "+totalDays);
			list.add(new DetailTime(year, totalDays, hour, minute, second));
			}
			catch(Exception e)
			{
				System.out.println("some error occured!");
			}
		}
		
		BufferedWriter log = new BufferedWriter(new FileWriter(new File("d:/result.txt")));
		for(DetailTime t : list)
		{
			String l1 = t._year +"Äê"+t._month+"ÔÂ"+t._day+"ÈÕ "+t._hour+":"+t._minute+":"+t._second;
			log.write(l1+"\r\n");
		}
		log.close();
		reader.close();

	}

}
