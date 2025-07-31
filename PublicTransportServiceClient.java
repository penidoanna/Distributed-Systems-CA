package smartcityconnect2.transport;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import smartcityconnect2.transport.PublicTransportServiceGrpc.PublicTransportServiceStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;

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
        
        final CountDownLatch latch = new CountDownLatch(1);
         // Handling the stream from server using onNext (logic for handling each message in stream), onError, onCompleted (logic will be executed after the completion of stream)
         StreamObserver<CrowdReport> requestObserver = asyncStub.sendCrowdReports(new StreamObserver<CrowdSummary>() { 
            @Override
            public void onNext(CrowdSummary report) {
                logger.info("Crowd Report: " + report.getOverallStatus());
            }

            @Override
            public void onError(Throwable t) {
                logger.log(Level.WARNING, "sendCrowdReports request failed", t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("sendCrowdReports completed");
                latch.countDown();
            }      
         });
         
         try {
             CrowdReport example1 = CrowdReport // Creating a request message.
                     .newBuilder()
                     .setStopNum("001")
                     .setCount(10)
                     .build();
                
                requestObserver.onNext(example1);
                Thread.sleep(500); // simulate time delay
                
                CrowdReport example2 = CrowdReport
                     .newBuilder()
                     .setStopNum("002")
                     .setCount(50)
                     .build();
                
                requestObserver.onNext(example2);
                Thread.sleep(500); // simulate time delay
                
                CrowdReport example3 = CrowdReport
                     .newBuilder()
                     .setStopNum("003")
                     .setCount(2)
                     .build();
                
                requestObserver.onNext(example3);
                Thread.sleep(500); // simulate time delay
                
                requestObserver.onCompleted(); //End of requests
                        
        } catch (RuntimeException e){
            requestObserver.onError(e);
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "sendCrowdReports request interrupted", e.getMessage()); // TODO Auto-generated catch block
            requestObserver.onError(e);
        }       
    } //closes sendCrowdReports
    
} //closes class

