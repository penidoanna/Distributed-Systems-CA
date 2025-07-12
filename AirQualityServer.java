package smartcity.airquality;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.logging.Logger;
import smartcity.airquality.AirQualityServiceGrpc.AirQualityServiceImplBase;
import io.grpc.stub.StreamObserver;

public class AirQualityServer extends AirQualityServiceImplBase {
    
    private static final Logger logger = Logger.getLogger(AirQualityServer.class.getName()); //creating a logger to print the message to the console

    public static void main(String[] args) {
        
        AirQualityServer aqserver = new AirQualityServer();
        
        int port = 50051;
        
        try {
            Server server = ServerBuilder.forPort(port) //starts building the server on the default port
			    .addService(aqserver) //register our server implementation
			    .build()   //finalizes the server
			    .start();  //launches the server so it can start getting requests
			
            server.awaitTermination();  //keeps the server on awaiting requests, otherwise it would shut down
                        
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();   
        }
        
        logger.info("Server started, listening on " + port); //message that will print once server has started running
    } //closes main method
    
        @Override
        public StreamObserver<AQRequest> monitorAirQuality(StreamObserver<AQResponse> responseObserver){
            return new StreamObserver<AQRequest>(){
                @Override
                public void onNext(AQRequest request){
                    String zoneId = request.getZoneId(); //Process each request and send a response
                    int aqi = (int)(Math.random() * 100);
                    String status = aqi < 50 ? "Good" : aqi < 100 ? "Moderate" : "Unhealthy";  //if the air pollution is less than 50, the quality is good; between 51 and 100, moderate, else, unhealthy
                    
                    AQResponse response = AQResponse.newBuilder()
                            .setAqi(aqi)
                            .setStatus(status)
                            .build();
                    
                    responseObserver.onNext(response); 
                } //closes onNext
        
                @Override
                public void onError(Throwable t) {
                    logger.warning("Error in client stream: " + t.getMessage());
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };
        } //closes Monitor Air Quality
        
    } //closes class
