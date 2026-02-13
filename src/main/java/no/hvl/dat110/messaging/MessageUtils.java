package no.hvl.dat110.messaging;

import java.util.Arrays;

public class MessageUtils {

	public static final int SEGMENTSIZE = 128;
	public static int MESSAGINGPORT = 8080;
	public static String MESSAGINGHOST = "localhost";

	public static byte[] encapsulate(Message message) {

		byte[] segment = null;
		byte[] data;

		// encapulate/encode the payload data of the message and form a segment
		// according to the segment format for the messaging layer

		data = message.getData();

		if (data == null) {
			throw new IllegalArgumentException("Message payload cannot be null");
		}
		if (data.length > 127) {
			throw new IllegalArgumentException("Message payload too long: " + data.length);
		}

		segment = new byte[SEGMENTSIZE];

		// header byte: payload length (0..127)
		segment[0] = (byte) data.length;

		// payload starts at index 1
		System.arraycopy(data, 0, segment, 1, data.length);

		return segment;
	}

	public static Message decapsulate(byte[] segment) {

		Message message = null;

		// decapsulate segment and put received payload data into a message
		if (segment == null) {
			throw new IllegalArgumentException("Segment cannot be null");
		}
		if (segment.length != SEGMENTSIZE) {
			throw new IllegalArgumentException("Segment must be exactly " + SEGMENTSIZE + " bytes");
		}

		int len = segment[0] & 0xFF; // unsigned
		if (len > 127) {
			throw new IllegalArgumentException("Invalid payload length: " + len);
		}

		byte[] payload = Arrays.copyOfRange(segment, 1, 1 + len);
		message = new Message(payload);

		return message;
	}
}
