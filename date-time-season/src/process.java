import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.text.html.MinimalHTMLWriter;


public class process {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String  path="d:/doc.txt";
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
			String l = line;
			line = reader.readLine();
			
			switch(state)
			{
			case 0:
				if(l.toLowerCase().indexOf("day")<0 && l.toLowerCase().indexOf("year")<0)
				{
					state = 0;
					continue;
				}
				else
				{
					System.out.println(l);
					if(l.toLowerCase().indexOf("year")>=0)
						year = Integer.parseInt(l.split("=")[1]);
					else if(l.toLowerCase().indexOf("day")>=0)
						totalDays = Integer.parseInt(l.split("=")[1]);
					state = 1;
					break;
				}
					
			case 1:
				if(l.toLowerCase().indexOf("day")<0 && l.toLowerCase().indexOf("year")<0)
				 {
					state = 0;
					continue;
				 }
				else
				{
					
					if(l.toLowerCase().indexOf("year")>=0)
						year = Integer.parseInt(l.split("=")[1]);
					else if(l.toLowerCase().indexOf("day")>=0)
						totalDays = Integer.parseInt(l.split("=")[1]);
					else 
						continue;
					state = 2;
					break;
				}
			case 2:
				if(l.toLowerCase().indexOf("hours")<0)
				 {
					state = 0;
					continue;
				 }
				else
				{
					hour = Integer.parseInt(l.split("=")[1]);
					state = 3;
					break;
				}
			case 3:
				if(l.toLowerCase().indexOf("minutes")<0)
				 {
					state = 0;
					continue;
				 }
				else
				{
					minute = Integer.parseInt(l.split("[=]")[1]);
					state = 4;
					break;
				}
			case 4:
				if(l.toLowerCase().indexOf("seconds")<0)
				 {
					state = 0;
					continue;
				 }
				else
				{
					
					String s =l.split("=")[1];
					if(s.toLowerCase().indexOf(" ")<0)
						second = Integer.parseInt(s);
					else
					{
						second = Integer.parseInt(s.split(" ")[0]);
					}
					DetailTime t = new DetailTime(year, totalDays, hour, minute, second);
					System.out.println(year +" " + totalDays +" "+ t._month +" "+ t._day + " "+second );
					list.add(t);
					state = 0;
					break;
				}
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
