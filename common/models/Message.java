package common.models;

import java.util.HashMap;
import java.util.Map;

public class Message {
	private final Type type;
	private final Map<String, Object> body;

	/*
	 * Empty constructor needed for JSON Serialization/Deserialization
	 */
	public Message() {
		type = null;
		body = null;
	}

	public Message(Map<String, Object> body, Type type) {
		this.body = body;
		this.type = type;
	}
	public Message(Type type) {
		this.type = type;
		body = new HashMap<>();
	}

	public Type getType() {
		return type;
	}

	public <T> T getFromBody(String fieldName) {
		return (T) body.get(fieldName);
	}

	public int getIntFromBody(String fieldName) {
		return (int) ((double) ((Double) body.get(fieldName)));
	}

	public void addObject(String field, Object object){
		body.put(field, object);
	}

	public enum Type {
		command,
		response,
		file_request,
		download_request
	}

	@Override
	public String toString() {
		return "Message{" +
				"type=" + type +
				", body=" + body +
				'}';
	}
}
