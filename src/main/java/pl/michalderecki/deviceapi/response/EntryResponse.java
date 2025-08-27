package pl.michalderecki.deviceapi.response;

import lombok.Data;

@Data
public class EntryResponse<T> extends Response {
	private T entry;
	
	public EntryResponse(T entry, Status status) {
		this.entry = entry;
		this.status = status;
	}

	public EntryResponse(Status status, String message) {
		super(status, message);
	}
	
}
