import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Simple Calendar Class acts as controller with main method, 
  		Calls Individual model and view class
  		@author danielsaneel
 */
public class SimpleCalendar {

	public static void main(String args[]) {
		MyCalendar cal = new MyCalendar();
		File file;
		String path = System.getProperty("user.dir");
		String fName = path+"/events.txt";
		file = new File(fName);
		cal.initializeFile(cal, file);
		CalendarView test = new CalendarView("MyCalendar", cal);
		test.setVisible(true);	
	}
}
