import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;


/**
  	Model Class that has all the functional methods for the calendar instance
  		@author Daniel Saneel Dennis
 */
public class MyCalendar {
	ArrayList<String> days = new ArrayList<>(7);
	TreeMap<LocalDate, ArrayList<Event>> map;
	ArrayList<Event> recurringEvents;
	ArrayList<Event> oneTime;
	LocalDate cal;
	DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");

	public MyCalendar() {
		cal = LocalDate.now();
		map = new TreeMap<>();
		initList(cal);
		recurringEvents = new ArrayList<>(); 
	}


	/**
	 * print the current month
	 * @param gCal2 a Calendar instance
	 */
	private void initList(LocalDate cal) {
		days.add("Mo");
		days.add("Tu");
		days.add("We");
		days.add("Th");
		days.add("Fr");
		days.add("Sa");
		days.add("Su");
	}



	/**
	 * creates the event in the calendar
	 * @param Event e
	 */
	public void createEvent(Event e) {
		LocalDate date = e.sDate;
		if(!map.containsKey(date)) {
			ArrayList<Event> newList = new ArrayList<>();
			newList.add(e);
			map.put(date, newList);
		}
		else {
			map.get(date).add(e);

		}
	}


	/**
	 * Method checks if the given event time clashes with any existing events
	 * @param Event e
	 * @return true if e does not clash with any other event
	 */
	public boolean timeValidated(Event e) {
		LocalDate date = e.sDate;
		LocalTime stime = e.sTime;
		LocalTime etime = e.eTime;
		ArrayList<Event> list  = new ArrayList<Event>();
		if(map.containsKey(date)) {
			list = map.get(date);

			for(int i = 0; i<list.size(); i++) {
				Event evnt = list.get(i);

				if(e.sTime.equals(evnt.sTime) || e.eTime.equals(evnt.eTime)) {
					return false;
				}
				//check for start times
				if(e.sTime.isBefore(evnt.sTime)) {
					if(!e.eTime.isBefore(evnt.sTime)) {
						return false;
					}
				}
				//check for end time
				if(e.sTime.isAfter(evnt.sTime)) {
					if(!e.sTime.isAfter(evnt.eTime)) {
						return false;
					}
				}
			}
			return true;
		}
		else {
			return true;
		}
	}

	/**
	 * Initialize the Calender
	 * Load Events if the file events.txt exists
	 * prints the current month and eny events if present
	 */
	void initializeFile(MyCalendar cal, File file) {

		String path = System.getProperty("user.dir");
		String fName = path+"/events.txt";
		try {
			File events  = file;
			if(file.exists()) {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine(); 

				String name = null;
				String second = null;
				if(line != null) {
					while(line!=null) {
						name = line;
						second = br.readLine();
						String[] arr = null;
						if(!br.equals("")) {
							arr = second.split(" ");
						}
						if(arr.length == 5) {
							cal.createRecurringFromFile(name, arr);
						}
						else if(arr.length == 3) {
							cal.createEventFromFile(name, arr);
						}
						line = br.readLine();
					}
				}
				//initializeFile(file);
			}
			else {
				file.createNewFile();
			}
		}
		catch(Exception x){
			x.printStackTrace();
		}
	}


	/**
	 * create a onne-time event from file
	 * @param arr
	 */
	public void createEventFromFile(String name, String[] arr) {
		LocalDate date = LocalDate.parse(arr[0]);
		LocalTime stime = LocalTime.parse(arr[1], timeFormat);
		LocalTime etime = LocalTime.parse(arr[2], timeFormat);
		Event e = new Event(name,date,stime,etime);
		createEvent(e);
	}


	/**
	 * create a recurring event from file
	 * @param arr
	 */
	public void createRecurringFromFile(String name, String[] arr) {
		// TODO Auto-generated method stub
		char[] days = arr[0].toCharArray();
		LocalTime stime = LocalTime.parse(arr[1]);
		LocalTime etime = LocalTime.parse(arr[2]);
		LocalDate sdate = LocalDate.parse(arr[3]);
		LocalDate edate = LocalDate.parse(arr[4]);

		int[] dayNum = new int[days.length];
		for(int i = 0; i<days.length; i++) {
			if(days[i] == 'M') {
				dayNum[i] = 1;
			}
			else if(days[i] == 'T') {
				dayNum[i] = 2;
			}
			else if(days[i] == 'W') {
				dayNum[i] = 3;
			}
			else if(days[i] == 'R') {
				dayNum[i] = 4;
			}
			else if(days[i] == 'F') {
				dayNum[i] = 5;
			}
			else if(days[i] == 'A') {
				dayNum[i] = 6;
			}
			else if(days[i] == 'S') {
				dayNum[i] = 7;
			}
		}

		for(LocalDate d = sdate; !d.isAfter(edate); d = d.plusDays(1)) {
			for(int i = 0; i<dayNum.length; i++) {
				if(d.getDayOfWeek().getValue() == dayNum[i]) {
					Event e = new Event(name, sdate, d, edate, stime, etime, arr[0]);
					createEvent(e);
				}
			}
		}
		Event rEvent = new Event(name, sdate, null, edate, stime, etime, arr[0]);
		recurringEvents.add(rEvent);
	}

	/**
	 * method displays all the events on a given date
	 * @param date
	 */
	public String viewByDate(LocalDate date) {
		ArrayList<Event> list = new ArrayList<Event>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM dd YYYY");

		String str = formatter.format(date);

		if(map.containsKey(date)) {
			list = map.get(date);
			formatter = DateTimeFormatter.ofPattern("E, MMM dd YYYY");
			str = formatter.format(date);
			for(int i = 0; i<list.size(); i++) {
				Event e = list.get(i);
				str = str + "\n\n" + "\t" + e.sTime + " - " + e.eTime + ": " + e.name;
			}
		}
		else {
			str = str + "\n\n"+":: No Event Scheduled Yet ::";
		}

		return str;

	}

	/**
	 * print the list of events ordered by date and time
	 * @return list of String with the list of events
	 */
	public String eventList() {

		String str = "";
		if(map.isEmpty()) {
			str = "No Events Scheduled Yet";
			return str;
		}
		str = "One Time Events: \n";
		str = str + "________________ \n2020\n";

		for(Map.Entry<LocalDate, ArrayList<Event>> entry: map.entrySet() ) {

			LocalDate currentDate = entry.getKey();
			ArrayList<Event> list = map.get(currentDate);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE MMMM dd");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");
			for(int i = 0;i<list.size();i++) {
				Event e = list.get(i);
				if(e.recurring == false) {
					str = str + "   "+formatter.format(currentDate) +"  " + timeFormatter.format(e.sTime) + " - " + timeFormatter.format(e.eTime) + "  " + e.name + "\n";
				}
			}
		}

		//for recurring events, add this later
		/*
		str = str + "\nRecurring Events:\n";
		str = str + "________________ \n";
		if(recurringEvents.size()==0) {
			str = str + "No Recurring Event Scheduled\n";
			return str;
		}
		for(int i = 0; i<recurringEvents.size(); i++) {
			Event e = recurringEvents.get(i);
			str = str + e.name +"\n";
			str = str + e.rdays + " " + e.sTime + " " + e.eTime + " " + e.recurringStartDate + " " + e.eDate +"\n";
		}
		 */
		return str;

	}

	//add these features later
	/**
	 * delete selected one time event
	 * @param date
	 * @param ename
	 *
	public void deleteSelected(LocalDate date, String ename) {
		if(!map.containsKey(date)){
			System.out.println("No event Scheduled");
		}
		else {
			ArrayList<Event> list = map.get(date);
			for(int i = 0; i<list.size(); i++) {
				if(list.get(i).name.equals(ename)) {
					list.remove(i);
					System.out.println("Event Successfully Deleted");
					break;

				}
			}
		}
	}


	/**
	 * delete all events for a specific date
	 * @param date

	public void deleteAll(LocalDate date) {
		if(!map.containsKey(date)) {
			System.out.println("No Events Scheduled For the Date");
		}
		else {
			for(int i = map.get(date).size()-1; i>=0; i--) {
				map.get(date).remove(i);
			}
			System.out.println("Events Successfully Deleted");
		}
	}

	/**
	 * deletes the given recurring event from memory
	 * @param name

	public void deleteRecurring(String name) {
		for(Map.Entry<LocalDate, ArrayList<Event>> entry: map.entrySet()) {
			LocalDate d = entry.getKey();
			ArrayList<Event> list = map.get(d);
			for(int i = 0; i<list.size(); i++) {
				Event e = list.get(i);
				if(e.recurring) {
					if(e.name.equals(name)) {
						list.remove(e);
					}
				}
			}
		}
		for(int i = 0; i<recurringEvents.size(); i++) {
			Event e  = recurringEvents.get(i);
			if(e.name.equals(name)) {
				recurringEvents.remove(e);
			}
		}
	}
	 */




	/**
	 * method to view and scroll calendar by dates
	 * @param d the date
	 * @return String str with all event details
	 */
	public String viewByD(LocalDate d) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMMM dd, YYYY");
		System.out.println("\n" + formatter.format(d));
		String str = "";
		if(map.containsKey(d)) {
			ArrayList<Event> list = map.get(d);
			for(int i = 0; i<list.size(); i++) {
				str = str + "\n" + list.get(i).name + " : " + list.get(i).sTime + " - " + list.get(i).eTime;
			}
		}
		else {
			str = "::No Scheduled Events::\n";
		}
		return str;
	}

	/**
	 * saves event list on the output.txt file
	 * 
	 */
	public void quit() {
		try {
			File file = new File("output.txt");
			File eventFile = new File("events.txt");
			FileWriter write = null;
			FileWriter eventwrite = null;


			if(!file.exists()) {
				file.createNewFile();
				write = new FileWriter(file.getAbsoluteFile());
			}
			if(!eventFile.exists()) {
				eventFile.createNewFile();
				eventwrite = new FileWriter(file.getAbsoluteFile());
			}
			String path = file.getAbsolutePath();
			String eventPath = eventFile.getAbsolutePath();

			write = new FileWriter(path);
			eventwrite = new FileWriter(eventPath);

			BufferedWriter writer = new BufferedWriter(write);
			BufferedWriter eventBWriter = new BufferedWriter(eventwrite);


			for(Map.Entry<LocalDate, ArrayList<Event>> entry: map.entrySet()) {
				LocalDate d = entry.getKey();
				ArrayList<Event> list = map.get(d);
				for(int i = 0; i<list.size(); i++) {
					Event e = list.get(i);
					writer.write(e.name + "\n" );
					eventBWriter.append(e.name + "\n" );
					writer.write(e.sDate + " " + e.sTime + " " + e.eTime + "\n");
					eventBWriter.append(e.sDate + " " + e.sTime + " " + e.eTime + "\n");
				}
			}
			writer.close();
			eventBWriter.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}
}
