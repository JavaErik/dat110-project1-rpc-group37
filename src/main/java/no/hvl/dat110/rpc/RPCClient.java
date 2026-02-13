package no.hvl.dat110.rpc;

import no.hvl.dat110.TODO;
import no.hvl.dat110.messaging.*;

public class RPCClient {

	// underlying messaging client used for RPC communication
	private MessagingClient msgclient;

	// underlying messaging connection used for RPC communication
	private MessageConnection connection;
	
	public RPCClient(String server, int port) {
	
		msgclient = new MessagingClient(server,port);
	}
	
	public void connect() {
		
		// connect using the RPC client
		connection = msgclient.connect();
		if (connection == null) {
			throw new IllegalStateException("RPCClient: could not establish messaging connection");
		}
	}
	
	public void disconnect() {
		
		// disconnect by closing the underlying messaging connection
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

	/*
	 Make a remote call om the method on the RPC server by sending an RPC request message and receive an RPC reply message

	 rpcid is the identifier on the server side of the method to be called
	 param is the marshalled parameter of the method to be called
	 */

		public byte[] call(byte rpcid, byte[] param) {
		
		byte[] returnval = null;

		if (connection == null) {
			throw new IllegalStateException("RPCClient: not connected");
		}
		if (param == null) {
			param = new byte[0];
		}

		/*

		The rpcid and param must be encapsulated according to the RPC message format

		The return value from the RPC call must be decapsulated according to the RPC message format

		*/
				
		// 1) encapsulate into RPC request payload
		byte[] rpcRequest = RPCUtils.encapsulate(rpcid, param);

		// 2) send request over messaging layer
		connection.send(new Message(rpcRequest));

		// 3) receive reply over messaging layer
		Message reply = connection.receive();
		if (reply == null) {
			throw new IllegalStateException("RPCClient: received null reply");
		}

		byte[] rpcReply = reply.getData();

		// (Optional but nice sanity check)
		if (rpcReply == null || rpcReply.length < 1) {
			throw new IllegalStateException("RPCClient: invalid RPC reply");
		}
		byte replyRpcid = rpcReply[0];
		if (replyRpcid != rpcid) {
			throw new IllegalStateException("RPCClient: mismatching rpcid in reply");
		}

		// 4) decapsulate return value (payload)
		returnval = RPCUtils.decapsulate(rpcReply);

		return returnval;
	}

}
