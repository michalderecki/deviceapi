package pl.michalderecki.deviceapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.michalderecki.deviceapi.dto.DeviceEntry;
import pl.michalderecki.deviceapi.dto.TopologyEntry;
import pl.michalderecki.deviceapi.entity.Device;
import pl.michalderecki.deviceapi.repository.DeviceRepository;
import pl.michalderecki.deviceapi.response.EntryResponse;
import pl.michalderecki.deviceapi.response.Response;
import pl.michalderecki.deviceapi.response.Status;
import pl.michalderecki.deviceapi.utils.DevicesComparator;

@Service
public class DeviceApiService {

	private static final Logger logger = LogManager.getLogger(DeviceApiService.class);

	@Autowired
	private DeviceRepository deviceRepository;

	public Response register(Device device) {
		if (device.getMacAddress().isBlank()) {
			return new Response(Status.ERROR, "macAddress cannot be blank");
		}
		if (device.getUplinkMacAddress() != null && device.getUplinkMacAddress().isBlank()) {
			device.setUplinkMacAddress(null);
		} else if(device.getUplinkMacAddress() != null && !deviceRepository.existsById(device.getUplinkMacAddress())) {
			return new Response(Status.ERROR, "There is no device match to uplinkMacAddress");
		}
		if (deviceRepository.existsById(device.getMacAddress())) {
			return new Response(Status.ERROR, "Device of given macAddress is already register");
		}

		deviceRepository.saveAndFlush(device);
		logger.debug(device);
		return new Response(Status.OK);

	}

	public List<DeviceEntry> getSortedDevices() {
		return deviceRepository.findAll().stream().sorted(new DevicesComparator())
				.map(d -> new DeviceEntry(d.getMacAddress(), d.getDeviceType())).collect(Collectors.toList());
	}

	public EntryResponse<DeviceEntry> getDeviceEntry(String macAddress) {
		Optional<DeviceEntry> result = deviceRepository.findById(macAddress)
				.map(device -> new DeviceEntry(device.getMacAddress(), device.getDeviceType()));
		return result.isPresent() ? new EntryResponse<DeviceEntry>(result.get(), Status.OK)
				: new EntryResponse<DeviceEntry>(Status.ERROR, "Device of this macAddress is not register");
	}

	public List<TopologyEntry> getTopology() {
		// add all roots
		List<TopologyEntry> topologyEntries = deviceRepository.findTopologyWithoutUplink();
		// feed child
		for (TopologyEntry parent : topologyEntries) {
			feedTopologyChildrens(parent);
		}
		return topologyEntries;
	}

	private void feedTopologyChildrens(TopologyEntry parent) {
		List<TopologyEntry> childrens = deviceRepository.findTopologyByUplink(parent.getMacAddress());
		for (TopologyEntry child : childrens) {
			feedTopologyChildrens(child);
		}
		parent.setChildrens(childrens);
	}

	public EntryResponse<TopologyEntry> getTopologyEntry(String macAddress) {
		if (deviceRepository.existsById(macAddress)) {
			TopologyEntry parent = new TopologyEntry(macAddress);
			feedTopologyChildrens(parent);
			return new EntryResponse<TopologyEntry>(parent, Status.OK);
		} else {
			return new EntryResponse<TopologyEntry>(Status.ERROR, "Device of this macAddress is not register");
		}
	}
}
