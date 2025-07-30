package smartcityconnect2.traffic;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import io.grpc.stub.StreamObserver;
import java.util.logging.Logger;
import smartcityconnect2.traffic.TrafficLightServiceGrpc.TrafficLightServiceImplBase;

public class TrafficLightServer extends TrafficLightServiceImplBase {

    private static final Logger logger = Logger.getLogger(TrafficLightServer.class.getName()); //creating a logger to print the message to the console

    public static void main(String[] args) throws IOException, InterruptedException {

        TrafficLightServer trafficServer = new TrafficLightServer(); //create an instance of the server class to register it to the gRPC

        int port = 50051; //default port

        Server server = ServerBuilder.forPort(port) //starts building the server on the default port
                .addService(trafficServer) //register our server implementation
                .build() //finalizes the server
                .start();  //launches the server so it can start getting requests

        logger.info("Server started, listening on " + port);

        server.awaitTermination();  //keeps the server on awaiting requests, otherwise it would shut down

    } //main class

    @Override
    //Method that will be automatically invoked when client is sending a LightStatus request
    public void getLightStatus(LightStatusRequest request, StreamObserver<LightStatusResponse> responseObserver) { //StreamObserver sends the reply back to the client

        logger.info("GetLightStatus request received for intersection: " + request.getIntersectionId()); //debug print saying the server got the request

        String status, direction = "";

        if (request.getIntersectionId().equals("01")) {
            status = "Green";
            direction = "Southbound";
        } else if (request.getIntersectionId().equals("02")) {
            status = "Red";
            direction = "Eastbound";
        } else {
            status = "Unknown";
            direction = "Unknown";
        }
 
        LightStatusResponse response = LightStatusResponse
                .newBuilder() 
                .setStatus(status)
                .setDirection(direction)
                .build();

    responseObserver.onNext (response); //Send the reply created back to the client
    responseObserver.onCompleted (); //Response is complete and no further messages will follow
    
    } //getLightStatus
    
    
    @Override
    public void streamTrafficUpdates(ZoneRequest request, StreamObserver<TrafficUpdate> responseObserver) {

        logger.info("StreamTrafficUpdates request received for zone: " + request.getZoneId());

        try {
            for (int i = 0; i < 3; i++) { //send 3 updates
                TrafficUpdate update = TrafficUpdate
                        .newBuilder()
                        .setIntersectionId(request.getZoneId())
                        .setStatus(i % 2 == 0 ? "Heavy Traffic" : "Light Traffic") //Alternating patterns to examplify. 
                        .build();

                responseObserver.onNext(update); //Send the reply created back to the client
                Thread.sleep(100); // Simulate delay between updates
            } //for loop
        } catch (InterruptedException e) {
            logger.warning("Server streaming interrupted: " + e.getMessage());
        } finally {
            responseObserver.onCompleted();
        }

    } //streamTrafficUpdates

} //closes class
