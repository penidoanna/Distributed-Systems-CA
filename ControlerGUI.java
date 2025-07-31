package smartcityconnect2.client;

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

import smartcityconnect2.airquality.AirQualityServiceGrpc;
import smartcityconnect2.airquality.AQRequest;
import smartcityconnect2.airquality.AQResponse;
import smartcityconnect2.traffic.TrafficLightServiceGrpc;
import smartcityconnect2.traffic.LightStatusRequest;
import smartcityconnect2.traffic.LightStatusResponse;
import smartcityconnect2.transport.PublicTransportServiceGrpc;
import smartcityconnect2.transport.CrowdReport;
import smartcityconnect2.transport.CrowdSummary;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

public class ControllerGUI implements ActionListener {

    private static final Logger logger = Logger.getLogger(ControllerGUI.class.getName());

    private JTextField entry1, reply1;
    private JTextField entry2, reply2;
    private JTextField entry3, reply3;

    private ManagedChannel airQualityChannel;
    private ManagedChannel trafficLightChannel;
    private ManagedChannel publicTransportChannel;

    //Contructor
    public ControllerGUI() { // Initialize channels

        airQualityChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        trafficLightChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        publicTransportChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();
    } //Constructor

    private JPanel getAirQualityServiceJPanel() {

        JPanel panel = new JPanel();

        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.X_AXIS);

        JLabel label = new JLabel("Enter Zone Id");
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        entry1 = new JTextField("", 10);
        panel.add(entry1);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton button = new JButton("Invoke Air Quality Service");
        button.addActionListener(this);
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        reply1 = new JTextField("", 10);
        reply1.setEditable(false);
        panel.add(reply1);

        panel.setLayout(boxlayout);

        return panel;
    } //getAirQualityServiceJPanel

    private JPanel getTrafficLightServiceJPanel() {

        JPanel panel = new JPanel();

        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.X_AXIS);

        JLabel label = new JLabel("Enter Intersection Id");
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        entry2 = new JTextField("", 10);
        panel.add(entry2);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton button = new JButton("Invoke Traffic Light Service");
        button.addActionListener(this);
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        reply2 = new JTextField("", 10);
        reply2.setEditable(false);
        panel.add(reply2);

        panel.setLayout(boxlayout);

        return panel;
    } //getTrafficLightServiceJPanel

    private JPanel getPublicTransportServiceJPanel() {

        JPanel panel = new JPanel();

        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.X_AXIS);

        JLabel label = new JLabel("Enter Stop Number");
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        entry3 = new JTextField("", 10);
        panel.add(entry3);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton button = new JButton("Invoke Public Transport Service");
        button.addActionListener(this);
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        reply3 = new JTextField("", 10);
        reply3.setEditable(false);
        panel.add(reply3);

        panel.setLayout(boxlayout);

        return panel;
    } //getPublicTransportServiceJPanel

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        ControllerGUI gui = new ControllerGUI();
        gui.build();
        });
    }

    private void build() {

        JFrame frame = new JFrame("Service Controller Sample");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Shutdown hook to close channels
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                airQualityChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                trafficLightChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                publicTransportChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "Interrupted while shutting down channels", e);
            }
        }));

        // Set the panel to add buttons
        JPanel panel = new JPanel();

        // Set the BoxLayout to be X_AXIS: from left to right
        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);

        panel.setLayout(boxlayout);

        // Set border for the panel
        panel.setBorder(new EmptyBorder(new Insets(50, 100, 50, 100)));

        panel.add(getAirQualityServiceJPanel()); //bidirectional streaming             
        panel.add(getTrafficLightServiceJPanel());
        panel.add(getPublicTransportServiceJPanel()); //client-streaming RPC

        // Set size for the frame
        frame.setSize(300, 300);

        // Set the window to be visible as the default to be false
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        String label = button.getActionCommand();
    }
    
        public void invokeAirQualityService(){
            
            System.out.println("Air Quality Service to be invoked ...");
            SwingUtilities.invokeLater(() -> reply1.setText("Processing..."));
            
            try {
                AirQualityServiceGrpc.AirQualityServiceStub asyncStub = AirQualityServiceGrpc.newStub(airQualityChannel);
                CountDownLatch latch = new CountDownLatch(1);
                StringBuilder result = new StringBuilder();
                
                StreamObserver<AQRequest> requestObserver = asyncStub.monitorAirQuality(new StreamObserver<AQResponse>() {
                    @Override
                    public void onNext(AQResponse response) {
                        result.append("Air Quality Information: ").append(response.getAqi()).append(", Status: ").append(response.getStatus()).append(" ");
                    }
                       
                    @Override
                    public void onError(Throwable t) {
                        logger.log(Level.WARNING, "Air Quality Service failed", t);
                        SwingUtilities.invokeLater(() -> reply1.setText("Error: " + t.getMessage()));
                        latch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        SwingUtilities.invokeLater(() -> reply1.setText(result.toString()));
                        latch.countDown();
                    }
                });
                
                AQRequest request = AQRequest //Send request
                        .newBuilder()
                        .setZoneId(entry1.getText())
                        .build();
                requestObserver.onNext(request);
                requestObserver.onCompleted();
                
                if(!latch.await(10, TimeUnit.SECONDS)){
                    SwingUtilities.invokeLater(() ->reply1.setText("Timeout"));
                }
                
            } catch (Exception ex){
                logger.log(Level.SEVERE, "Error invoking Air Quality Service", ex);
                SwingUtilities.invokeLater(() -> reply1.setText("Error: " + ex.getMessage()));
            }   
        } //invokeAirQualityService
                        
            ManagedChannel channel = ManagedChannelBuilder
                    .forAddress("localhost", 50051)
                    .usePlaintext()
                    .build();
            
            AirQualityServiceGrpc.AirQualityServiceBlockingStub blockingStub = AirQualityServiceGrpc.newBlockingStub(channel);

            //preparing message to send
            smartcityconnect2.airquality.AQRequest request = smartcityconnect2.airquality.AQRequest.newBuilder().setText(entry1.getText()).build();

            //retreving reply from service
            smartcityconnect2.airquality.AQResponse response = blockingStub.airqualityDo(request);

            reply1.setText(String.valueOf(response.getLength()));

        } else if (label.equals("Invoke Traffic Light Service")) {
            System.out.println("Traffic Light Service to be invoked ...");

            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();
            TrafficLightServiceGrpc.TrafficLightServiceBlockingStub blockingStub = TrafficLightServiceGrpc.newBlockingStub(channel);

            //preparing message to send
            smartcityconnect2.traffic.LightStatusRequest request = smartcityconnect2.traffic.LightStatusRequest.newBuilder().setText(entry2.getText()).build();

            //retreving reply from service
            smartcityconnect2.traffic.LightStatusResponse response = blockingStub.trafficDo(request);

            reply2.setText(String.valueOf(response.getLength()));

        } else if (label.equals("Invoke Public Transport Service")) {
            System.out.println("Public Transport Service to be invoked ...");

            /*
			 * 
             */
            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50053).usePlaintext().build();
            PublicTransportServiceGrpc.PublicTransportServiceBlockingStub blockingStub = PublicTransportServiceGrpc.newBlockingStub(channel);

            //preparing message to send
            smartcityconnect2.transport.CrowdReport request = smartcityconnect2.transport.CrowdReport.newBuilder().setText(entry3.getText()).build();

            //retreving reply from service
            smartcityconnect2.transport.CrowdSummary response = blockingStub.transportDo(request);

            reply3.setText(String.valueOf(response.getLength()));

        } else {

        }

    }

}
