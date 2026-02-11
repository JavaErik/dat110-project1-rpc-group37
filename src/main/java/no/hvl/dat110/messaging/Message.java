package no.hvl.dat110.messaging;

import java.util.Arrays;

import no.hvl.dat110.TODO;

public class Message {

	public static final int MAX_PAYLOAD_SIZE = 127;


	// the up to 127 bytes of data (payload) that a message can hold
	private final byte[] data;

	// construction a Message with the data provided
	public Message(byte[] data) {

		if(data==null) {
			throw new IllegalArguementException("Message data cannot be null");

		}
		if(data.length > MAX_PAYLOAD_SIZE) {
			throw new illegallArguementException("Message data cannot be longer than " + MAX_PAYLOAD_SIZE +"bytes");
		}
		this.data = Arrays.copyOf(data,data.length);
	}
	public byte[] getData() {
		return Arrays.copyOf(data, data.length);
	}
	public int getLength() {
		return data.length;
	}
		
	}



