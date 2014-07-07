
public class DetailTime {
	int _year;
	//int _totalDay;
	int _month;
	int _day;
	int _hour;
	int _minute;
	int _second;
	
	int[] totalday2Month(int totaldays)
	{
		int[] m={31,28,31,31,30,31,30,31,31,30,31,30,31};
		int[] months = {0,0,0,0,0,0,0,0,0,0,0,0,0};
		int[] result={1,1};
		for(int i=1;i<=12;i++)
			months[i] = months[i-1] + m[i-1];
		int month = 0;
		int day;
		//System.out.println(months[12]);
		for(int i=0;i<13;i++)
		{
			if(totaldays > months[i])
				continue;
			result[0] = i;
			result[1] = totaldays - months[i-1];
			break;
		}
		return result;
		
	}
	public DetailTime(int year,int totalDay,int hour,int minute,int second) {
		// TODO Auto-generated constructor stub
		_year = year;
	int	_totalDay = totalDay;
		_hour = hour;
		_minute = minute;
		_second = second;
	int[] tmp = totalday2Month(_totalDay);
	 	_month = tmp[0];
	 	_day = tmp[1];
	}
	
	public static void main(String[] args)
	{
		DetailTime d = new DetailTime(2014,361, 12,12,12);
		System.out.println(d._month+" "+d._day);
	}
}

