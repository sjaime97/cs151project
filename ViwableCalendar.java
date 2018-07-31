import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

enum MONTHS 
{
	January, February, March, April, May, June, July, August, September, October, November, December;
}

enum DAYS 
{
	
	Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;
}
public class ViwableCalendar implements ChangeListener 
{

	private JLabel header = new JLabel();
	private JButton create = new JButton("Create Event");
	private JButton next = new JButton("Next Day/Event");
	private int colored = -1;
	private int daysNumMax;
	private CalendarData model;
	private DAYS[] days = DAYS.values();
	private MONTHS[] months = MONTHS.values();
	private JButton previous = new JButton("Previous Day/Event");
	private JTextPane eventBox = new JTextPane();
	private ArrayList<JButton> dayButtons = new ArrayList<JButton>();
	private JFrame f = new JFrame("Calendar");
	private JPanel viewableCalendar = new JPanel();


	public ViwableCalendar(CalendarData model) 
	{
		daysNumMax = model.getDaysNumMax();
		viewableCalendar.setLayout(new GridLayout(0, 7));
		eventBox.setPreferredSize(new Dimension(400, 400));
		eventBox.setEditable(false);
		this.model = model;
		
		create();
		blank();
		addButton();
		highlightEvents();
		dateViewable(model.getChosen());
		coloredDate(model.getChosen() - 1);

		create.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				createEventDialog();
			}
		});
		JButton monthBack = new JButton("<<");
		monthBack.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				model.monthBefore();
				create.setEnabled(false);
				next.setEnabled(false);
				previous.setEnabled(false);
				eventBox.setText("");
			}
		});
		JButton monthNext = new JButton(">>");
		monthNext.addActionListener(new ActionListener() 
		{

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				model.monthFollowing();
				create.setEnabled(false);
				next.setEnabled(false);
				previous.setEnabled(false);
				eventBox.setText("");
			}
		});
		
		JPanel monthBox = new JPanel();
		monthBox.setLayout(new BorderLayout());
		header.setText("                                                          " + months[model.getMonth()] + " " + model.getYear());
		monthBox.add(header, BorderLayout.NORTH);
		monthBox.add(new JLabel("       S                 M               T                W                 TH                    F                 S"), BorderLayout.CENTER);
		monthBox.add(viewableCalendar, BorderLayout.SOUTH);
		
		JPanel dayEventBox = new JPanel();
		dayEventBox.setLayout(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = 0;
		con.gridy = 0;
		JScrollPane scrollDay = new JScrollPane(eventBox);
		scrollDay.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		dayEventBox.add(scrollDay, con);
		JPanel button = new JPanel();
		next.addActionListener(new ActionListener() 
		{

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				model.followingDay();
			}
		});
		previous.addActionListener(new ActionListener() 
		{

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				model.dayPrior();
			}
		});
	    button.add(previous);
		button.add(create);
		button.add(next);
		con.gridx = 0;
		con.gridy = 1;
		dayEventBox.add(button, con);

		JButton end = new JButton("Quit");
		end.addActionListener(new ActionListener()  
		{

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				model.save();
				System.exit(0);
			}
		});

		f.add(monthBack);
		f.add(monthBox);
		f.add(monthNext);
		f.add(dayEventBox);
		f.add(end);
		f.setLayout(new FlowLayout());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}
 

	private void create() 
	{
		for (int i = 1; i <= daysNumMax; i++) 
		{
			final int d = i;
			JButton daysOfTheWeek = new JButton(Integer.toString(d));
	
			daysOfTheWeek.addActionListener(new ActionListener() 
			{
	
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					dateViewable(d);
					coloredDate(d - 1);
					create.setEnabled(true);
					next.setEnabled(true);
					previous.setEnabled(true);
				}
			});
			dayButtons.add(daysOfTheWeek);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) 
	{
		if (model.hasdifferentMonth()) 
		{
			daysNumMax = model.getDaysNumMax();
			dayButtons.clear();
			viewableCalendar.removeAll();
			header.setText("                                                        " + months[model.getMonth()] + " " + model.getYear());
			create();
			blank();
			addButton();
			highlightEvents();
			colored = -1;
			model.restartNewMonth();
			f.pack();
			f.repaint();
		} 
		else 
		{
			dateViewable(model.getChosen());
			coloredDate(model.getChosen() - 1);
		}
	}

	private void addButton() 
	{
		for (JButton d : dayButtons) 
		{
			viewableCalendar.add(d);
		}
	}

	private void blank() 
	{
		for (int j = 1; j < model.getDay(1); j++) 
		{
			JButton empty = new JButton();
			empty.setEnabled(false);
			viewableCalendar.add(empty);
		}
	}
	private void createEventDialog() 
	{
		final JDialog eventDialog = new JDialog();
		eventDialog.setTitle("Create Event");
		eventDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		final JTextField text = new JTextField(30);
		final JTextField start = new JTextField(10);
		final JTextField end = new JTextField(10);
		JButton eventSave = new JButton("Save Event");
		eventSave.addActionListener(new ActionListener() 
		{

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if (text.getText().isEmpty()) 
				{
					return;
				}
				if ((!text.getText().isEmpty() && (start.getText().isEmpty() ||end.getText().isEmpty()))
						|| start.getText().length() != 5
						|| end.getText().length() != 5
						|| !start.getText().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")
						|| !end.getText().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]"))
				{
					JDialog errorMessage = new JDialog();
					errorMessage.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
					errorMessage.setLayout(new GridLayout(2, 0));
					errorMessage.add(new JLabel("Time Must Be In HH:MM Format"));
					JButton confirm = new JButton("Ok");
					confirm.addActionListener(new ActionListener() 
					{
						@Override
						public void actionPerformed(ActionEvent e) 
						{
							errorMessage.dispose();
						}
					});
					errorMessage.add(confirm);
					errorMessage.pack();
					errorMessage.setVisible(true);
				} 
				else if (!text.getText().equals("")) 
				{
					if (model.doEventsClash(start.getText(), end.getText())) 
					{
						JDialog clashWarning = new JDialog();
						clashWarning.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
						clashWarning.setLayout(new GridLayout(2, 0));
						clashWarning.add(new JLabel("ERROR: Event Conflicts With Other Event"));
						JButton confirm1 = new JButton("Ok");
						confirm1.addActionListener(new ActionListener() 
						{

							@Override
							public void actionPerformed(ActionEvent e) 
							{
								clashWarning.dispose();
							}
						});
						clashWarning.add(confirm1);
						clashWarning.pack();
						clashWarning.setVisible(true);
					} 
					else 
					{
						eventDialog.dispose();
						model.createEvent(text.getText(), start.getText(), end.getText());
						dateViewable(model.getChosen());
						highlightEvents();
					}
				}
			}
		});
		
		eventDialog.setLayout(new GridBagLayout());
		JLabel dates = new JLabel();
		dates.setText(model.getMonth() + 1 + "/" + model.getChosen() + "/" + model.getYear());
		dates.setBorder(BorderFactory.createEmptyBorder());

		GridBagConstraints con = new GridBagConstraints();
		con.insets = new Insets(2, 2, 2, 2);
		con.gridx = 0;
		con.gridy = 0;
		
		eventDialog.add(dates, con);
		con.gridy = 1;
		con.weightx = 1.0;
		
		con.anchor = GridBagConstraints.LINE_START;
		eventDialog.add(new JLabel("Event"), con);
		con.gridy = 2;
		
		eventDialog.add(text, con);
		con.gridy = 3;
		con.weightx = 0.0;
		
		con.anchor = GridBagConstraints.LINE_START;
		eventDialog.add(new JLabel("Start Time"), con);
		
		con.anchor = GridBagConstraints.CENTER;
		eventDialog.add(new JLabel("End Time"), con);
		
		con.gridy = 4;
		con.anchor = GridBagConstraints.LINE_START;
		eventDialog.add(start, con);
		
		con.anchor = GridBagConstraints.CENTER;
		eventDialog.add(end, con);
		
		con.anchor = GridBagConstraints.LINE_END;
		eventDialog.add(eventSave, con);
		eventDialog.pack();
		
		eventDialog.setVisible(true);
	}
	
	private void dateViewable(int x) 
	{
		model.setChosenDate(x);
		String weekday = days[model.getDay(x) - 1] + "";
		String date = (model.getMonth() + 1) + "/" + x + "/" + model.getYear();
		String event = "";
		if (model.isEvent(date)) 
		{
			event = event + model.getEvents(date);
		}
		eventBox.setText(weekday + " " + date + "\n" + event);
		eventBox.setCaretPosition(0);
	}

	private void coloredDate(int x) 
	{
		Border b = new LineBorder(Color.GREEN, 2); 
		dayButtons.get(x).setBorder(b); 
		if (colored != -1) 
		{
			dayButtons.get(colored).setBorder(new JButton().getBorder());
		}
		colored = x;
	}

	private void highlightEvents() 
	{
		for (int i = 1; i <= daysNumMax; i++) 
		{
			if (model.isEvent((model.getMonth() + 1) + "/" + i + "/" + model.getYear())) 
			{
				dayButtons.get(i - 1).setBackground(Color.GREEN);
			}
		}
	}
}