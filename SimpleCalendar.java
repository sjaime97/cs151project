public class SimpleCalendar 
{

	public static void main(String[] args) 
	{
		CalendarData model = new CalendarData();
		ViwableCalendar viewer = new ViwableCalendar(model);
		model.attach(viewer);
		model.update();
		model.save();
	}

}