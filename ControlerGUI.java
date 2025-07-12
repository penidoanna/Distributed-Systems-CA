package smartcity.client;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import smartcity.airquality.AirQualityServiceGrpc;
import smartcity.traffic.TrafficLightServiceGrpc;
import smartcity.transport.PublicTransportServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;


public class ControllerGUI implements ActionListener{


	private JTextField entry1, reply1;
	private JTextField entry2, reply2;
	private JTextField entry3, reply3;
	private JTextField entry4, reply4;


	private JPanel getAirQualityServiceJPanel() {

		JPanel panel = new JPanel();

		BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.X_AXIS);

		JLabel label = new JLabel("Enter Zone ID")	;
		panel.add(label);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));
		entry1 = new JTextField("",10);
		panel.add(entry1);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton button = new JButton("Get Air Quality");
		button.addActionListener(this);
		panel.add(button);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));

		reply1 = new JTextField("", 10);
		reply1 .setEditable(false);
		panel.add(reply1 );

		panel.setLayout(boxlayout);

		return panel;

	}

	private JPanel getTrafficLightServiceJPanel() {

		JPanel panel = new JPanel();

		BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.X_AXIS);

		JLabel label = new JLabel("Enter Zone ID")	;
		panel.add(label);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));
		entry2 = new JTextField("",10);
		panel.add(entry2);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton button = new JButton("Get Traffic Light Status");
		button.addActionListener(this);
		panel.add(button);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));

		reply2 = new JTextField("", 10);
		reply2 .setEditable(false);
		panel.add(reply2 );

		panel.setLayout(boxlayout);

		return panel;

	}

	private JPanel getPublicTransportServiceJPanel() {

		JPanel panel = new JPanel();

		BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.X_AXIS);

		JLabel label = new JLabel("Enter Bus Stop number")	;
		panel.add(label);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));
		entry3 = new JTextField("",10);
		panel.add(entry3);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton button = new JButton("Get crowd report");
		button.addActionListener(this);
		panel.add(button);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));

		reply3 = new JTextField("", 10);
		reply3 .setEditable(false);
		panel.add(reply3 );

		panel.setLayout(boxlayout);

		return panel;

	}

	public static void main(String[] args) {

		ControllerGUI gui = new ControllerGUI();

		gui.build();
	}

	private void build() { 

		JFrame frame = new JFrame("Service Controller Sample");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set the panel to add buttons
		JPanel panel = new JPanel();

		// Set the BoxLayout to be X_AXIS: from left to right
		BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);

		panel.setLayout(boxlayout);

		// Set border for the panel
		panel.setBorder(new EmptyBorder(new Insets(50, 100, 50, 100)));
	
		panel.add( getAirQualityServiceJPanel() );
		panel.add( getTrafficLightServiceJPanel() );
		panel.add( getPublicTransportServiceJPanel() );

		// Set size for the frame
		frame.setSize(300, 300);

		// Set the window to be visible as the default to be false
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton)e.getSource();
		String label = button.getActionCommand();  

		if (label.equals("Get Air Quality")) {
			System.out.println("Get Air Quality to be invoked ...");
                        
			ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
                        AirQualityServiceGrpc.AirQualityServiceStub asyncStub = AirQualityServiceGrpc.newStub(channel);

			//preparing message to send
			smartcity.airquality.AQRequest request = smartcity.airquality.AQRequest.newBuilder().setText(entry1.getText()).build();

			//retreving reply from service
			smartcity.airquality.AQResponse response = asyncStub.AirQualityServiceDo(request);

			reply1.setText( String.valueOf( response.getLength()) );
		
		}else if (label.equals("Get Traffic Light Status")) {
			System.out.println("Traffic Light status to be invoked ...");

			ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();
			TrafficLightServiceGrpc.TrafficLightServiceStub asyncStub = TrafficLightServiceGrpc.newStub(channel);

			//preparing message to send
			smartcity.traffic.LightStatusRequest request = smartcity.traffic.LightStatusRequest.newBuilder().setText(entry2.getText()).build();

			//retreving reply from service
			smartcity.traffic.LightStatusResponse response = asyncStub.TrafficLightDo(request);

			reply2.setText( String.valueOf( response.getLength()) );
			
		}else if (label.equals("Get crowd report")) {
			System.out.println("Crowd report to be invoked ...");

			ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50053).usePlaintext().build();
			PublicTransportServiceGrpc.PublicTransportServiceStub asyncStub = PublicTransportServiceGrpc.newStub(channel);

			//preparing message to send
			smartcity.transport.CrowdReport request = smartcity.transport.CrowdReport.newBuilder().setText(entry3.getText()).build();

			//retreving reply from service
			smartcity.transport.CrowdSummary response = asyncStub.PublicTransportServiceDo(request);

			reply3.setText( String.valueOf( response.getLength()) );
		
		} else {
			
		}

	}

}
