import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;


public class process {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ArrayList<TimeInfo> girl = new ArrayList<TimeInfo>();
		String path = "d:/data/girl.txt";
		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
		String line = reader.readLine();
		while(line != null)
		{
			girl.add(new TimeInfo(line));
			line = reader.readLine();
		}
		//for(TimeInfo t:girl)
		//	System.out.println(t._start+" "+t._end);
		reader.close();
		Collections.sort(girl);
		ArrayList<QQFriend> friends = new ArrayList<QQFriend>();
		String path2="d:/data/friends/";
		File directory = new File(path2);
		for(File f:directory.listFiles())
		{
			//System.out.println(f.getName());
			friends.add(new QQFriend(f.getPath(), girl));
		}
		Collections.sort(friends);
		
		for(QQFriend f:friends)
			System.out.println(f._QQNum+" "+f._seconds);
		BufferedWriter log = new BufferedWriter(new FileWriter(new File("d:/qq分析.xls")));
		log.write("qq号码\t在线重叠总时间(分钟)\t时间列表\n");
		log.flush();
		
		for(QQFriend f:friends)
		{
			String timelist="";
			for(TimeInfo t:f._overlappedTimes)
			{
				timelist = timelist+"\t"+t._start+"-"+t._end+"-"+t._IP+"-"+t._address;
			}
			log.write(f._QQNum+"\t");
			log.write(f._seconds/60+"\t");
			log.write(timelist+"\n");
			log.flush();
		}
		
		log.close();
	}

}
