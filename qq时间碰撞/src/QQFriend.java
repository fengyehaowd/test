import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QQFriend implements Comparable<QQFriend>{

	/**
	 * @param args
	 */
	String _QQNum;
	int _seconds;
	ArrayList<TimeInfo> _times;
	ArrayList<TimeInfo> _overlappedTimes;
	QQFriend(String path,ArrayList<TimeInfo> girl) throws IOException
	{
		Pattern pattern = Pattern.compile("\\d{5,12}");
		Matcher matcher = pattern.matcher(path);
		while(matcher.find())
			_QQNum = matcher.group();
		_seconds = 0;
		_times = new ArrayList<TimeInfo>();
		_overlappedTimes = new ArrayList<TimeInfo>();
		initTimes(path);
		computeOverlap(girl);
		
	//	for(TimeInfo t:_times)
	//		System.out.println("friend:"+t._start+" "+t._end);
		
	}
	public void initTimes(String path) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
		String line = reader.readLine();
		int flag = 0;
		int status = 0;
		String lineStart="";
		while(line != null)
		{
			//System.out.println(line);
			if(flag == 1)
			{
				if(status == 0)
				{
					if(line.indexOf("上线")!=-1)
					{
						lineStart = line;
						status = 1;
					}
				}
				if(status == 1)
				{
					if(line.indexOf("下线")!=-1)
					{
						_times.add(new TimeInfo(lineStart, line));
						status = 0;
					}
					else
					{
						lineStart = line;
					}
				}
				
			}
			if(line.indexOf("操作时间")!=-1)
			{	
				//System.out.println(line);
			    flag = 1;
			}
			line = reader.readLine();
		}
		reader.close();
		Collections.sort(_times);
	}
	
	public void computeOverlap(ArrayList<TimeInfo> girl)
	{
		for(TimeInfo t1:_times)
		{
			for(TimeInfo t2:girl)
			{
				if(!(t1._end.compareTo(t2._start)<=0 || t1._start.compareTo(t2._end)>=0))
				{
					String start;
					String end;
					String IP;
					String address;				
					start = (t1._start.compareTo(t2._start)>0)?t1._start:t2._start;
					end = (t1._end.compareTo(t2._end)>0)?t2._end:t1._end;
					IP = t1._IP;
					address = t1._address;
					_overlappedTimes.add(new TimeInfo(start, end, IP, address));				
					_seconds = _seconds+TimeInfo.Interval(start, end);
					//System.out.println("间隔:"+_seconds + " "+ t1._start+" "+t1._end+" "+t2._start+" "+t2._end);
				}	
			}			
		}
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	@Override
	public int compareTo(QQFriend arg0) {
		// TODO Auto-generated method stub
		return -_seconds+arg0._seconds;
	}

}
