package pl.michalderecki.deviceapi.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class TopologyEntry implements Serializable {
	
	private static final long serialVersionUID = -145793523432611997L;
	@NotNull
	@NonNull
	private String macAddress;
	private List<TopologyEntry> childrens = new ArrayList<>();
	
}
