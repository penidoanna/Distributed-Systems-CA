package smartcityconnect2.transport;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import smartcityconnect2.transport.PublicTransportServiceGrpc.PublicTransportServiceStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class PublicTransportServiceClient {
    
    private static final Logger logger = Logger.getLogger(PublicTransportServiceClient.class.getName()); //creating a logger to print the message to the console
    
    public static void main(String[] args) throws Exception {
       String host = "localhost"; //declare host variable
       int port = 50051; //default port for gRPC
       
       // First a channel is being created to the server from client.
       ManagedChannel channel = ManagedChannelBuilder
               .forAddress(host, port)
               .usePlaintext() // As it is a local demo of GRPC, we can disable encryption.
               .build(); //finalizes the channel creation
       
       PublicTransportServiceGrpc.PublicTransportServiceStub asyncStub = PublicTransportServiceGrpc.newStub(channel);
              
        try {
            sendCrowdReports(asyncStub);
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    } //main method

    private static void sendCrowdReports(PublicTransportServiceStub asyncStub) {
        logger.info("sendCrowdReports request received");
        
        //Unary request
        CrowdReport request = CrowdReport
                .newBuilder()
                .setStopNum("001")
                .build();
     
        asyncStub.sendCrowdReports(request, new StreamObserver<CrowdSummary>() {
            @Override
            public void onNext(CrowdSummary summary) {
                logger.info("Crowd Report: " + summary.getOverallStatus());
            }

            @Override
            public void onError(Throwable t) {
                logger.log(Level.WARNING, "sendCrowdReports request failed", t.getMessage());
            }

            @Override
            public void onCompleted() {
                logger.info("sendCrowdReports completed");
            }      
         });
    } //closes sendCrowdReports
    
} //closes class

