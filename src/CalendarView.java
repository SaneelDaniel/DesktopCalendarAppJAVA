import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
  	Class creates and holds the GUI / View of the calendar app
  	@author Daniel Saneel Dennis
 */
public class CalendarView {


	//JComponents
	private Container mainContainer;
	private JFrame	mainFrame;

	private JPanel topPanel;
	private JPanel monthViewPanel;
	private JPanel dayTextPanel;
	private JTextArea dayDetails;
	private JLabel monthYearLabel;

	//Instances
	private MyCalendar calInstance = null;
	private LocalDate currentDate = null;

	//lists
	private ArrayList<JButton> dayNames = new ArrayList<>();
	private	ArrayList<JButton> dayButtons = new ArrayList<>();
	private Month currentMonth = null;

	/**
	 * constructor for calendar view instance
	 * @param title the title of the frame
	 * @param cal the attached calendar model instance
	 */
	public CalendarView(String title, MyCalendar cal) {
		calInstance = cal;
		currentDate = calInstance.cal;
		currentMonth = cal.cal.getMonth();
		mainFrame = new JFrame(title);
		mainFrame.setSize(1200, 350);
		mainFrame.setLocation(100, 100);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainContainer = mainFrame.getContentPane();
		mainContainer.setLayout(new BorderLayout(4,4));
		mainContainer.setBackground(Color.black);
		createView();
	}


	/**
	 * calls the respective panel create methods and sets action listeners appropriately
	 */
	private void createView() {
		createTopPanel();
		createMonthViewPanel();
		createDayScrollPane();
		addDayButtonListeners();		
	}


	/**
	 * Method creates the panel viewing day details
	 * sets text area object
	 */
	private void createDayScrollPane() {
		dayTextPanel = new JPanel();
		dayTextPanel.setBackground(Color.gray);
		dayDetails = new JTextArea();
		
		
		dayTextPanel.setLayout(new BorderLayout());
		dayDetails.setFont(new Font("Times New Roman", Font.LAYOUT_LEFT_TO_RIGHT, 14));
		String str = calInstance.viewByDate(calInstance.cal.now());
		dayDetails.setText(str);
		dayDetails.setVisible(true);
		dayTextPanel.add(dayDetails);		
		dayDetails.setForeground(Color.black);
		dayDetails.setBackground(Color.gray);
		mainContainer.add(dayTextPanel);
		dayDetails.setSize(dayTextPanel.getSize());
	}



	/*
	 * creates the top panel of the frame, and sets buttons and action performed
	 */
	private void createTopPanel() {
		topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(6));
		topPanel.setBackground(Color.gray);


		//label to display current month and year
		monthYearLabel = new JLabel(currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.US) +", "+ currentDate.getYear() + "\t");
		monthYearLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
		monthYearLabel.setBackground(Color.gray);
		monthYearLabel.setForeground(Color.black);

		//previous month button
		JButton prevButton = new JButton("<");
		prevButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentDate = currentDate.minusMonths(1);
				dayDetails.setText(calInstance.viewByDate(currentDate));
				setMonthYearLabel();
				fillMonthView();
			}

		});


		//next month button
		JButton nextButton = new JButton(">");
		nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentDate = currentDate.plusMonths(1);
				setMonthYearLabel();
				dayDetails.setText(calInstance.viewByDate(currentDate));
				fillMonthView();
			}

		});


		/**
		 * Create Event Dialog, frame, buttons and text fields
		 */
		JButton createButton = new JButton("Create");
		createButton.addActionListener(new ActionListener() {

			@Override
			/*
			 * creates a jframe for user input 
			 */
			public void actionPerformed(ActionEvent e) {
				Border border = BorderFactory.createLineBorder(Color.black);
				JFrame eventFrame = new JFrame();
				eventFrame.setSize(500, 300);
				eventFrame.setLocation(100, 300);
				Container eventContainer = eventFrame.getContentPane();
				eventContainer.setLayout(new FlowLayout());

				JTextField nameEntry = new JTextField();
				nameEntry.setSize(500, 40);
				nameEntry.setText("Please Enter The Name Here");
				nameEntry.setBorder(border);

				JTextField startTime = new JTextField();
				startTime.setSize(500,40);
				startTime.setText("StartTime: HH:mm(24 HRS)");
				startTime.setBorder(border);

				JTextField endTime = new JTextField();
				endTime.setSize(500,40);
				endTime.setText("EndTime: HH:mm(24 HRS)");
				endTime.setBorder(border);

				JButton saveButton = new JButton("SAVE");
				saveButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						String name = nameEntry.getText();
						String startT = startTime.getText();
						String endT = endTime.getText();


						LocalTime stime;
						LocalTime etime;
						stime = LocalTime.parse(startT);
						etime = LocalTime.parse(endT);

						Event event = new Event(name, currentDate,stime,etime);


						if(calInstance.timeValidated(event)) {
							calInstance.createEvent(event);
							dayDetails.setText("Event Successfully Created");
							dayDetails.append("\n");
							dayDetails.append(calInstance.viewByDate(currentDate));
							eventFrame.dispose();
						}
						else {
							dayDetails.setText("The Event Times Collide with another event on the same date");
							dayDetails.append("\n");
							dayDetails.append(calInstance.viewByDate(currentDate));
						}


					}

				});

				JPanel panel = new JPanel();
				panel.setLayout(new FlowLayout());
				panel.setBackground(Color.gray);
				eventContainer.setBackground(Color.gray);
				panel.add(nameEntry);
				panel.add(startTime);
				panel.add(new JLabel("to"));
				panel.add(endTime);
				panel.add(saveButton);				

				eventFrame.add(panel);
				eventFrame.pack();
				eventFrame.setVisible(true);
			}
		});
		//create ends

		//event List Button, prints all events in the text pane
		JButton eventList = new JButton("EventList");
		eventList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dayDetails.setText(calInstance.eventList());

			}

		});

		//Prev Day Button goes to the previous day and lists the day details 
		JButton prevDayDeatilsButton = new JButton("Prev Day");
		prevDayDeatilsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentDate = currentDate.minusDays(1);
				String str = calInstance.viewByDate(currentDate);
				dayDetails.setText(str);
				if(!currentDate.getMonth().equals(currentMonth)) {
					fillMonthView();
					setMonthYearLabel();
				}
			}

		});

		//Next Day Button goes to the previous day and lists the day details 
		JButton nextDayDeatilsButton = new JButton("Next Day");
		nextDayDeatilsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentDate = currentDate.plusDays(1);
				String str = calInstance.viewByDate(currentDate);
				dayDetails.setText(str);
				if(!currentDate.getMonth().equals(currentMonth)) {
					fillMonthView();
					setMonthYearLabel();
				}
			}

		});

		//Quit button, calls calendar instance's quit function, and closes the frame
		JButton quitButton = new JButton("quit");
		quitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				calInstance.quit();
				mainFrame.dispose();
			}

		});

		topPanel.add(monthYearLabel);
		topPanel.add(prevButton);
		topPanel.add(nextButton);
		topPanel.add(createButton);
		topPanel.add(eventList);
		topPanel.add(prevDayDeatilsButton);
		topPanel.add(nextDayDeatilsButton);
		topPanel.add(quitButton);
		mainContainer.add(topPanel, BorderLayout.NORTH);
	}


	/**
	 * sets the month year label
	 */
	private void setMonthYearLabel() {
		monthYearLabel.setText(currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.US)+", "+ currentDate.getYear());		
	}



	/**
	 * creates the month view panel and calls fillMonth Method to fill the respective month view
	 */
	private void createMonthViewPanel() {
		monthViewPanel = new JPanel();
		monthViewPanel.setBackground(Color.gray);
		monthViewPanel.setLayout(new GridLayout(7,7, 3,3));
		dayNames.add(new JButton("Su"));
		dayNames.add(new JButton("Mo"));
		dayNames.add(new JButton("Tu"));
		dayNames.add(new JButton("We"));
		dayNames.add(new JButton("Th"));
		dayNames.add(new JButton("Fr"));
		dayNames.add(new JButton("Sa"));

		for(int i = 0; i<dayNames.size(); i++) {
			JButton button = dayNames.get(i);
			button.setBackground(Color.gray);
			button.setFont(new Font("Times New Roman", Font.BOLD, 22));
			monthViewPanel.add(button);
		}

		for(int i = 0; i<42; i++) {
			JButton button = new JButton();
			dayButtons.add(button);
			monthViewPanel.add(button);
		}
		fillMonthView();
		mainContainer.add(monthViewPanel, BorderLayout.WEST);	
	}


	/*
	 * fill the button text's and date arrangements
	 */
	private void fillMonthView() {

		LocalDate date = currentDate;
		LocalDate firstDay = LocalDate.of(date.getYear(), date.getMonth().getValue(), 1);
		int lastDay = firstDay.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();

		int firstDayValue = firstDay.getDayOfWeek().getValue();

		int dayCount = 1;
		int lastDayButtonValue = 0;

		for(int i = firstDayValue; i<dayButtons.size(); i++) {
			if(dayCount>lastDay) {
				break;
			}

			lastDayButtonValue = i;

			JButton button = dayButtons.get(i);
			button.setText(Integer.toString(dayCount));

			if(dayCount == calInstance.cal.getDayOfMonth()) {
				button.setBorder(BorderFactory.createLineBorder(Color.black));
			}
			else {
				button.setBorder(BorderFactory.createLineBorder(Color.black));
			}
			button.setVisible(true);

			dayCount++;
		}

		for(int i = 0 ;i<firstDayValue; i++) {
			dayButtons.get(i).setVisible(false);
		}

		for(int i = lastDayButtonValue+1; i<dayButtons.size(); i++) {
			dayButtons.get(i).setVisible(false);
		}
	}


	/**
	 * method adds action listeners to all the day buttons
	 */
	private void addDayButtonListeners() {
		for(int i = 0; i<dayButtons.size(); i++) {
			final JButton button = dayButtons.get(i);

			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int dayValue = Integer.parseInt(button.getText());
					LocalDate newDate = LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), dayValue);
					currentDate = newDate;
					String setText = calInstance.viewByDate(newDate);
					dayDetails.setText(setText);
				}

			});
		}

	}


	/**
	 * sets visibility of the frame
	 * @param b
	 */
	public void setVisible(boolean b) {
		mainFrame.setVisible(b);
	}

}
