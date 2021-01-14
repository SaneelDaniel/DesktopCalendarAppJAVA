import java.time.LocalDate;
import java.time.LocalTime;
import java.util.TreeSet;

/**
 * Event Class Creates a OneTime/Recurring Event Object with given variables
 * @author Daniel Saneel Dennis
 *
 */
public class Event  {
	
	LocalDate recurringStartDate;
	LocalDate sDate;
	LocalDate eDate;
	LocalTime sTime;
	LocalTime eTime;
	String name;
	boolean recurring = false;
	String rdays = null;
	
	
	/**
	 * Constructor for Recurring Event
	 * @param name
	 * @param rsDate
	 * @param sDate
	 * @param eDate
	 * @param sTime
	 * @param eTime
	 * @param days
	 */
	public Event(String name, LocalDate rsDate, LocalDate sDate, LocalDate eDate, LocalTime sTime, LocalTime eTime, String days) {
		this.name = name;
		recurringStartDate = rsDate;
		this.sDate = sDate;
		this.eDate = eDate;
		this.sTime = sTime;
		this.eTime = eTime;
		recurring = true;
		rdays = days;
	}
	
	/**
	 * Constructor for One Time Event
	 * @param name
	 * @param date
	 * @param stime
	 * @param etime
	 */
	public Event(String name, LocalDate date, LocalTime stime, LocalTime etime) {
		this.name = name;
		this.sDate = date;
		this.sTime = stime;
		this.eTime = etime;
	}
}

