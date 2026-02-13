package no.hvl.dat110.rpc;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import no.hvl.dat110.TODO;

public class RPCUtils {
	
	public static byte[] encapsulate(byte rpcid, byte[] payload) {

		
		byte[] rpcmsg = null;
		
		// TODO - START
		
		// Encapsulate the rpcid and payload in a byte array according to the RPC message syntax / format
		
		if (payload == null) {
			payload = new byte[0];
		}
			rpcmsg = new byte[1+ payload.length];
			rpcmsg[0] = rpcid;
			System.arraycopy(payload, 0, rpcmsg, 1, payload.length);

			return rpcmsg;
		
		// TODO - END
		
	}
	
	public static byte[] decapsulate(byte[] rpcmsg) {
		
		byte[] payload = null;
		
		// TODO - START
		
		// Decapsulate the rpcid and payload in a byte array according to the RPC message syntax
		
		if (rpcmsg == null || rpcmsg.length < 1) {
			throw new IllegalArgumentException("rpcmsg must contain at least 1 byte (rpcid)");
		}
			payload = Arrays.copyOfRange(rpcmsg, 1, rpcmsg.length);

			return payload;
		
		// TODO - END
		
		
	}

	// convert String to byte array
	public static byte[] marshallString(String str) {
		
		byte[] encoded = null;

		if (str == null) {
			str = "";
		}

		byte[] utf8 = str.getBytes(StandardCharsets.UTF_8);

		// 4 bytes length + content
		ByteBuffer bb = ByteBuffer.allocate(4 + utf8.length);
		bb.putInt(utf8.length);
		bb.put(utf8);

		encoded = bb.array();
		return encoded;
	}

	public static String unmarshallString(byte[] data) {
		
		String decoded = null; 

		if (data == null || data.length < 4) {
			throw new IllegalArgumentException("String data must be at least 4 bytes (length header)");
		}

		ByteBuffer bb = ByteBuffer.wrap(data);
		int len = bb.getInt();

		if (len < 0 || len > data.length - 4) {
			throw new IllegalArgumentException("Invalid string length: " + len);
		}

		byte[] utf8 = new byte[len];
		bb.get(utf8);

		decoded = new String(utf8, StandardCharsets.UTF_8);
		return decoded;
	}
	
	
	public static byte[] marshallVoid() {
		
		// void has no content
		return new byte[0];
	}
		
	
	public static void unmarshallVoid(byte[] data) {
		
		// Nothing to decode; accept null or empty, reject unexpected content if you want strictness
		// Many tests accept empty array. We'll be strict-ish:
		if (data == null) {
			return;
		}
		if (data.length != 0) {
			throw new IllegalArgumentException("Void should have empty payload, but got " + data.length + " bytes");
		}
	}

	// convert boolean to a byte array representation
	public static byte[] marshallBoolean(boolean b) {
		
		byte[] encoded = new byte[1];
				
		if (b) {
			encoded[0] = 1;
		} else
		{
			encoded[0] = 0;
		}
		
		return encoded;
	}

	// convert byte array to a boolean representation
	public static boolean unmarshallBoolean(byte[] data) {
		
		return (data[0] > 0);
		
	}

	// integer to byte array representation
	public static byte[] marshallInteger(int x) {
		
		byte[] encoded = null;

		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(x);
		encoded = bb.array();

		return encoded;
	}
	
	// byte array representation to integer
	public static int unmarshallInteger(byte[] data) {
		
		int decoded = 0;

		if (data == null || data.length < 4) {
			throw new IllegalArgumentException("Integer data must be at least 4 bytes");
		}

		ByteBuffer bb = ByteBuffer.wrap(data);
		decoded = bb.getInt();

		return decoded;
	}
}
