package no.hvl.dat110.rpc;

public class RPCServerStopImpl extends RPCRemoteImpl {

    private final RPCServer server;

    public RPCServerStopImpl(byte rpcid, RPCServer rpcserver) {
        super(rpcid, rpcserver);
        this.server = rpcserver;
    }

    // RPC server-side implementation of the built-in stop RPC method
    @Override
    public byte[] invoke(byte[] param) {

        // stop() takes no parameters
        RPCUtils.unmarshallVoid(param);

        // void return
        byte[] returnval = RPCUtils.marshallVoid();

        // stop server AFTER reply is prepared
        stop();

        return returnval;
    }

    public void stop() {
        System.out.println("RPC server executing stop");
        server.stop();
    }
}
