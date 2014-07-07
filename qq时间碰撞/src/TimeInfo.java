
public class TimeInfo implements Comparable<TimeInfo>{

	/**
	 * @param args
	 */
	String _start;
	String _end;
	String _IP;
	String _address;
	
	public TimeInfo(String line1,String line2)
	{
		String[] eles1 = line1.split("\t{1,2}");
		String[] eles2 = line2.split("\t{1,2}");
		
		_start = Time2String(eles1[0]);
		_end = Time2String(eles2[0]);
		_IP = eles1[1];
		_address = eles1[3];
	}
	public TimeInfo(String line)
	{
		String[] eles = line.split("\t");
		int interval = String2Seconds(eles[1]);
		_IP = eles[2];
		_address = eles[3];
		_start = Time2String(eles[4]);
		_end = EndTimeFromStartAndInterval(_start, interval);	
	}
	
	public TimeInfo(String start,String end,String IP,String address)
	{
		_start = start;
		_end = end;
		_IP = IP;
		_address = address;
	}
	
	public String Time2String(String time)
	{
		return time.replace(" ","").replace("-", "").replace(":", "");
	}
	
	public static int Interval(String t1,String t2)
	{
		return Math.abs(String2Num(t2)-String2Num(t1));
	}
	
	public static int String2Num(String t1)
	{
		int month1 = Integer.parseInt(t1.substring(4,6));
		int day1 = Integer.parseInt(t1.substring(6,8));
		int hour1 = Integer.parseInt(t1.substring(8,10));
		int min1 = Integer.parseInt(t1.substring(10,12));
		int sec1 = Integer.parseInt(t1.substring(12,14));
		
		return (month1*30+day1)*86400+hour1*3600+min1*60+sec1;
	}
	
	public static String Num2String(int time)
	{
		int month = time/(30*86400);
		int residue = time - month*30*86400;
		int day = residue / 86400;
		residue = residue - day * 86400;
		int hour = residue / 3600;
		residue = residue - hour * 3600;
		int minute = residue/60;
		int second = residue - minute* 60;
		
		return "2013"+n2s(month)+n2s(day)+n2s(hour)+n2s(minute)+n2s(second);
	}
	
	public static String n2s(int num)
	{
		String result = String.valueOf(num);
		if(result.length() == 1)
			result = "0"+result;
		return result;
	}
	
	public String EndTimeFromStartAndInterval(String t1,int interval)
	{
		return  Num2String(String2Num(t1)+interval);
	}
	
	public int String2Seconds(String s)
	{
		//System.out.println(s);
		int hourIndex = s.indexOf("小时");
		int minIndex = s.indexOf("分钟");
		int secIndex = s.indexOf("秒");
		
		int hour=0;
		int minute=0;
		int second=0;
		try
		{
			hour = Integer.parseInt(s.substring(0,hourIndex));
			minute = Integer.parseInt(s.substring(hourIndex+2,minIndex));
			second = Integer.parseInt(s.substring(minIndex+2,secIndex));
		}
		catch(Exception e)
		{
			return 0;
		}
		
		return hour*3600+minute*60+second;
	}
	
	
	
	//public static 
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TimeInfo t = new TimeInfo("2013-09-02 18:03:56	113.118.24.59	����		�㶫ʡ������ ");
		System.out.println(t.Time2String("2013-09-02 18:03:56"));
		System.out.println(Interval(t.Time2String("2013-09-02 18:03:56"),t.Time2String("2013-09-02 19:01:56")));

	}
	@Override
	public int compareTo(TimeInfo arg0) {
		// TODO Auto-generated method stub
		return _start.compareTo(arg0._start);
	}

}
