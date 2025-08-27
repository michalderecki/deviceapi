package pl.michalderecki.deviceapi.rest;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pl.michalderecki.deviceapi.dto.DeviceEntry;
import pl.michalderecki.deviceapi.dto.TopologyEntry;
import pl.michalderecki.deviceapi.entity.Device;
import pl.michalderecki.deviceapi.response.EntryResponse;
import pl.michalderecki.deviceapi.response.Response;
import pl.michalderecki.deviceapi.response.Status;
import pl.michalderecki.deviceapi.service.DeviceApiService;

@RestController
@RequestMapping("/api")
public class DeviceApiRestController {
	
	@Autowired
	private DeviceApiService deviceApiService;
	
	@PutMapping("/register")
	public ResponseEntity<Response> follow(@RequestBody(required = true) @Valid Device device) {
		return ResponseEntity.ok(deviceApiService.register(device));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/device/{macAddress}")
	@ResponseBody
	public ResponseEntity getDeviceEntry(@PathVariable String macAddress) {
		EntryResponse<DeviceEntry> response = deviceApiService.getDeviceEntry(macAddress);
		
		return response.getStatus() == Status.OK ? ResponseEntity.ok(response.getEntry()) : ResponseEntity.ok(response.getMessage());
	}
	
	@PostMapping("/devices")
	public List<DeviceEntry> getDevices() {
		return deviceApiService.getSortedDevices();
	}
	
	@PostMapping("/topology")
	public List<TopologyEntry> getTopology() {
		return deviceApiService.getTopology();
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/topology/{macAddress}")
	@ResponseBody
	public ResponseEntity getTopologyEntry(@PathVariable String macAddress) {
		EntryResponse<TopologyEntry> response = deviceApiService.getTopologyEntry(macAddress);
	
		return response.getStatus() == Status.OK ? ResponseEntity.ok(response.getEntry()) : ResponseEntity.ok(response.getMessage());
		
	}
}
