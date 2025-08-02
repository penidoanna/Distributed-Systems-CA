package smartcityconnect2.airquality;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.logging.Logger;
import smartcityconnect2.airquality.AirQualityServiceGrpc.AirQualityServiceImplBase;
import io.grpc.stub.StreamObserver;
import java.util.logging.Level;

public class AirQualityServer extends AirQualityServiceImplBase {

    private static final Logger logger = Logger.getLogger(AirQualityServer.class.getName()); //creating a logger to print the message to the console

    public static void main(String[] args) throws IOException, InterruptedException {

        int port = 50052; 
        AirQualityServer server = new AirQualityServer(); //create an instance of the server class to register it to the gRPC

        Server grpcServer = ServerBuilder.forPort(port) //starts building the server on the default port
                .addService(server) //register our server implementation
                .build() //finalizes the server
                .start(); //launches the server so it can start getting requests
          
        logger.info("Server started, listening on " + port);
        grpcServer.awaitTermination();
    } //main class

    @Override
    //Method that will be automatically invoked when client is sending a AQrequest
    public StreamObserver<AQRequest> monitorAirQuality(StreamObserver<AQResponse> responseObserver) {
        return new StreamObserver<AQRequest>() {
                
            @Override
            public void onNext(AQRequest request) {
                try {
                    String zoneId = request.getZoneId(); //Process each request and send a response
                    int aqi = (int)(Math.random() * 100); 
                    String status = aqi < 50 ? "Good" : aqi < 100 ? "Moderate" : "Unhealthy";  //if the air pollution is less than 50, the quality is good; between 51 and 100, moderate, else, unhealthy

                    logger.info("Processing air quality request for zone: "); //Debug message that will print once server has started running
                    
                    AQResponse response = AQResponse.newBuilder()
                        .setAqi(aqi)
                        .setStatus(status)
                        .build();
                    
                    responseObserver.onNext(response); //Send the reply created back to the client
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error processing request: " + e.getMessage(), e);
                }
            } //closes onNext
                                       
            @Override
            public void onError(Throwable t) {
                logger.warning("Error in client stream: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                logger.info("Client stream completed");
                responseObserver.onCompleted();
            }
        };
    } //closes Monitor Air Quality

} //closes class
