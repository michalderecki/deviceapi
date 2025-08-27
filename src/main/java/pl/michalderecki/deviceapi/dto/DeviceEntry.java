package pl.michalderecki.deviceapi.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.michalderecki.deviceapi.entity.DeviceType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceEntry implements Serializable {
	private static final long serialVersionUID = -6341444132364837935L;
	@NotNull
	private String macAddress;
	@NotNull
	private DeviceType deviceType;
	
}
