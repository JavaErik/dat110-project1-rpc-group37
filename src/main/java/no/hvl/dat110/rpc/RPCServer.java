package no.hvl.dat110.rpc;

import java.util.HashMap;
import java.util.Map;

import no.hvl.dat110.messaging.Message;
import no.hvl.dat110.messaging.MessageConnection;
import no.hvl.dat110.messaging.MessagingServer;

public class RPCServer {

    private final MessagingServer messagingServer;
    private MessageConnection connection;

    // registry for rpcid -> remote implementation
    private final Map<Integer, RPCRemoteImpl> registry = new HashMap<>();

    private volatile boolean running = true;

    public RPCServer(int port) {
        this.messagingServer = new MessagingServer(port);

        // Internal stop() RPC uses rpcid = 0 (reserved for internal use)
        new RPCServerStopImpl((byte) 0, this);
        // (RPCServerStopImpl registers itself via RPCRemoteImpl constructor)
    }

    public void register(int rpcid, RPCRemoteImpl impl) {

        // IMPORTANT: rpcid 0 is reserved, but must be allowed for internal stop() registration
        if (impl == null) {
            throw new IllegalArgumentException("impl cannot be null");
        }

        registry.put(rpcid, impl);
    }

    // Accept a client connection (call before run())
    public void accept() {
        connection = messagingServer.accept();
        if (connection == null) {
            throw new IllegalStateException("Could not accept messaging connection");
        }
    }

    // Main server loop
    public void run() {

    // Tests may start run() directly without calling accept()
    if (connection == null) {
        accept();
    }

    running = true;

    while (running) {

        Message request = connection.receive();
        if (request == null) {
            break;
        }

        byte[] rpcRequest = request.getData();
        if (rpcRequest == null || rpcRequest.length < 1) {
            continue;
        }

        int rpcid = rpcRequest[0] & 0xFF;
        byte[] params = RPCUtils.decapsulate(rpcRequest);

        RPCRemoteImpl impl = registry.get(rpcid);
        if (impl == null) {
            byte[] emptyReply = RPCUtils.encapsulate((byte) rpcid, new byte[0]);
            connection.send(new Message(emptyReply));
            continue;
        }

        byte[] returnValue = impl.invoke(params);
        if (returnValue == null) {
            returnValue = new byte[0];
        }

        byte[] reply = RPCUtils.encapsulate((byte) rpcid, returnValue);
        connection.send(new Message(reply));
    }

    stopServerSockets();
}


    // Called by stop RPC
    public void stop() {
        running = false;
    }

    private void stopServerSockets() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (Exception ignored) {}

        try {
            messagingServer.stop();
        } catch (Exception ignored) {}
    }
}
