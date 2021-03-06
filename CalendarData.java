import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CalendarData
{

	private int days;
	private int chosen;
	private HashMap<String, ArrayList<Event>> map  = new HashMap<>();
	private ArrayList<ChangeListener> lts = new ArrayList<>();
	private GregorianCalendar cal = new GregorianCalendar();
	private boolean differentMonth = false;
	
	private static class Event implements Serializable
	{
		private String title;
		private String date;
		private String start;
		private String end;

		private Event(String title, String date, String start, String end) 
		{
			this.title = title;
			this.date = date;
			this.start = start;
			this.end = end;
		}

		public String toString() 
		{
			if (end.equals("")) 
			{
				return start + ": " + title;
			}
			return start + " - " + end + ": " + title;
		}
	}
	public CalendarData() 
	{
		days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		chosen = cal.get(Calendar.DATE);
		load();
	}

	public void attach(ChangeListener cl) 
	{
		lts.add(cl);
	}

	public void update() 
	{
		for (ChangeListener l : lts) 
		{
			l.stateChanged(new ChangeEvent(this));
		}
	}

	public void setChosenDate(int day) 
	{
		chosen = day;
	}

	public int getDay (int i) 
	{
		cal.set(Calendar.DAY_OF_MONTH, i);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public int getDaysNumMax() 
	{
		return days; // max days a certain month can have 
	}

	public int getChosen() 
	{
		return chosen;
	}

	public int getYear() 
	{
		return cal.get(Calendar.YEAR);
	}

	public int getMonth() 
	{
		return cal.get(Calendar.MONTH);
	}

	public void monthFollowing() 
	{
		cal.add(Calendar.MONTH, 1);
		days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		differentMonth = true;
		update();
	}

	public void monthBefore() 
	{
		cal.add(Calendar.MONTH, -1);
		days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		differentMonth = true;
		update();
	}

	public void followingDay() 
	{
		chosen++;
		if (chosen > cal.getActualMaximum(Calendar.DAY_OF_MONTH)) 
		{
			monthFollowing();
			chosen = 1;
		}
		update();
	}

	public void dayPrior() 
	{
		chosen--;
		if (chosen < 1) 
		{
			monthBefore();
			chosen = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		update();
	}

	public boolean hasdifferentMonth() 
	{
		return differentMonth;
	}

	public void restartNewMonth() 
	{
		differentMonth = false;
	}

	public void createEvent(String title, String start, String end) 
	{
		ArrayList<Event> event = new ArrayList<>();
		String date = (cal.get(Calendar.MONTH) + 1) + "/" + chosen + "/" + cal.get(Calendar.YEAR);
		Event e = new Event(title, date, start, end);
		if (isEvent(e.date)) 
		{
			event = map.get(date);
		}
		map.put(date, event);
		event.add(e);
	}

	public Boolean isEvent(String date) 
	{
		return map.containsKey(date);
	}

	public Boolean doEventsClash(String start, String end) 
	{
		boolean no = false;
		boolean yes = true;
		String date = (getMonth() + 1) + "/" + chosen + "/" + getYear();
		if (!isEvent(date)) 
		{
			return no;
		}
		int minutesStart = convert(start);
		int minutesEnd = convert(end);
		ArrayList<Event> event = map.get(date);
		Collections.sort(event, timeCompare());
		for (Event e : event) 
		{
			int startEvent = convert(e.start);
			int endEvent = convert(e.end);
			if (minutesStart >= startEvent && minutesStart < endEvent) 
			{
				return yes;
			} 
			else if (minutesStart <= startEvent && minutesEnd > startEvent) 
			{
				return yes;
			}
		}
		return no;
	}

	public String getEvents(String date) 
	{
		ArrayList<Event> event = map.get(date);
		Collections.sort(event, timeCompare());
		String events = "";
		for (Event e : event) 
		{
			events = events + e.toString() + "\n";
		}
		return events;
	}

	public void save() 
	{
		if (map.isEmpty())
		{
			return;
		}
		try {
			FileOutputStream fileOut = new FileOutputStream("events.txt");
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(map);
			objectOut.close();
			objectOut.close();
		} 
		
		catch (IOException error) 
		{
			error.printStackTrace();
		}
	}

	private static Comparator<Event> timeCompare() 
	{
		return new Comparator<Event>() 
		{
			@Override
			public int compare(Event event1, Event event2) 
			{
				if (event1.start.substring(1, 3).equals(event1.start.substring(1, 3))) 
				{
					return Integer.parseInt(event1.start.substring(3, 5)) - Integer.parseInt(event2.start.substring(3, 5));
				}
				return Integer.parseInt(event1.start.substring(1, 3)) - Integer.parseInt(event2.start.substring(1, 3));
			}
		};
	}
	
	private void load() 
	{
		try {
			FileInputStream fileIn = new FileInputStream("event.txt");
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			HashMap<String, ArrayList<Event>> maps = (HashMap<String, ArrayList<Event>>) objectIn.readObject();
			for (String date : maps.keySet()) 
			{
				if (isEvent(date))
				{
					ArrayList<Event> event = map.get(date);
					event.addAll(maps.get(date));
				} 
				else 
				{
					map.put(date, maps.get(date));
				}
			}
			objectIn.close();
			fileIn.close();
		} 
		catch (IOException excep) 
		{
			
		} 
		catch (ClassNotFoundException c) 
		{
			System.out.println("Class not found");
			c.printStackTrace();
		}
	}

	private int convert(String time) 
	{
		int hours = Integer.valueOf(time.substring(0, 2));
		return hours * 60 + Integer.valueOf(time.substring(3));
	}
}