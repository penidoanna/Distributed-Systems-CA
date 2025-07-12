package smartcity.transport;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import smartcity.transport.PublicTransportServiceGrpc.PublicTransportServiceStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.CountDownLatch;

public class PublicTransportServiceClient {
    
    private static final Logger logger = Logger.getLogger(PublicTransportServiceClient.class.getName()); //creating a logger to print the message to the console
    
    public static void main(String[] args) throws Exception {
       String host = "localhost";
       int port = 50051; //default port for gRPC
       
       ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
               .usePlaintext()
               .build();
       
       PublicTransportServiceGrpc.PublicTransportServiceStub asyncStub = PublicTransportServiceGrpc.newStub(channel);
        
        CountDownLatch latch = new CountDownLatch(1);
        
        PublicTransportServiceClient client = new PublicTransportServiceClient();
        
        try {
            String[] zones = {"Z1", "Z2", "Z3"}; //sending this array to the server
            for (String zone_id : zones){
                ZoneRequest request = ZoneRequest.newBuilder()
                        .setZoneId(zone_id)
                        .build();
                
                
                
                requestObserver.onNext(request);
                Thread.sleep(1000); // simulate time delay
            }
            ZoneRequest request = ZoneRequest.newBuilder().setName(zone_id).build();
            ZoneRequest response = asyncStub.ZoneRequest(request);
            logger.info("The air quality at the moment is ", e.getStatus());
            
        } catch (StatusRuntimeException e){
            logger.log(Level.WARNING, "RPC failed (0)", e.getStatus());
            
            return;
            
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  
        } //closes try
        
    } //closes main method
    
} //closes class
