package pl.michalderecki.deviceapi.entity;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="devices")
public class Device implements Serializable {
	private static final long serialVersionUID = 3882403964573258836L;
	
	@NotNull
	@Id
	private String macAddress;
	@NotNull
	private DeviceType deviceType;
	private String uplinkMacAddress;
}
