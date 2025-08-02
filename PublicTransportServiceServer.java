package smartcityconnect2.transport;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.logging.Logger;
import io.grpc.stub.StreamObserver;
import smartcityconnect2.transport.PublicTransportServiceGrpc.PublicTransportServiceImplBase;
import io.grpc.stub.StreamObserver;

public class PublicTransportServiceServer extends PublicTransportServiceImplBase {

    private static final Logger logger = Logger.getLogger(PublicTransportServiceServer.class.getName()); //creating a logger to print the message to the console

    public static void main(String[] args) throws IOException, InterruptedException {

        PublicTransportServiceServer publicTransportServer = new PublicTransportServiceServer();

        int port = 50051; //Default port

        Server server = ServerBuilder.forPort(port) //starts building the server on the default port
                .addService(publicTransportServer) //register our server implementation
                .build() //finalizes the server
                .start();  //launches the server so it can start getting requests

        logger.info("Server started, listening on " + port);

        server.awaitTermination();
    } //main class

    @Override
    public void sendCrowdReports(CrowdReport request, StreamObserver<CrowdSummary> responseObserver) {

        int simcount = (int) (Math.random() * 100); //random crowd count
        String status;

        if (simcount == 0) {
            status = "Nobody's there.";
        } else if (simcount > 50) {
            status = "Very crowded";
        } else if (simcount > 20) {
            status = "Moderate crowd";
        } else {
            status = "Low crowd";
        }

        logger.info("SendCrowdReports completed. Overall status: " + status);

        responseObserver.onNext(CrowdSummary
                .newBuilder()
                .setOverallStatus(status)
                .build());
        responseObserver.onCompleted();
    } //SendCrowdReports

} //closes class
