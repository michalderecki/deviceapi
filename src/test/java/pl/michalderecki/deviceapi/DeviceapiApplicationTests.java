package pl.michalderecki.deviceapi;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import io.micrometer.common.util.StringUtils;
import pl.michalderecki.deviceapi.entity.Device;
import pl.michalderecki.deviceapi.entity.DeviceType;
import pl.michalderecki.deviceapi.repository.DeviceRepository;
import pl.michalderecki.deviceapi.response.Status;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceapiApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private DeviceRepository deviceRepository;

	@Test
	void shouldAllowAddDeviceWithoutUplink() {
		String macAdress = "aa:bb:cc:dd:ee:ff";
		DeviceType deviceType = DeviceType.Gateway;
		String request = addDevice(macAdress, deviceType.toString(), null);
		String expectedResponse = prepareResponseOfRegister(Status.OK, null);

		try {
			mvc.perform(put("/api/register").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8").content(request)).andExpect(status().isOk())
					.andExpect(content().string(expectedResponse));

			Optional<Device> device = deviceRepository.findById(macAdress);
			checkDevice(macAdress, deviceType, null, device.get());

			deviceRepository.deleteAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Exception occurs");
		}
	}

	@Test
	void shouldAllowAddDeviceWithUplink() {
		String macAdress = "aa:bb:cc:dd:ee:dd";
		DeviceType deviceType = DeviceType.Switch;
		String uplinkMacAddress = "aa:aa:aa:aa:aa:aa";
		String request = addDevice(macAdress, deviceType.toString(), uplinkMacAddress);
		String expectedResponse = prepareResponseOfRegister(Status.OK, null);
		Device parent = new Device(uplinkMacAddress, DeviceType.Gateway, null);
		try {
			deviceRepository.saveAndFlush(parent);

			assertTrue(deviceRepository.existsById(uplinkMacAddress));

			mvc.perform(put("/api/register").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8").content(request)).andExpect(status().isOk())
					.andExpect(content().string(expectedResponse));

			Optional<Device> device = deviceRepository.findById(macAdress);
			checkDevice(macAdress, deviceType, uplinkMacAddress, device.get());

			deviceRepository.deleteAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Exception occurs");
		}
	}

	@Test
	void shouldNotAllowAddDeviceWithUplinkThatNotExists() {
		String macAdress = "aa:bb:cc:dd:ee:dd";
		DeviceType deviceType = DeviceType.Switch;
		String uplinkMacAddress = "aa:aa:aa:aa:aa:aa";
		String request = addDevice(macAdress, deviceType.toString(), uplinkMacAddress);
		String expectedResponse = prepareResponseOfRegister(Status.ERROR,
				"There is no device match to uplinkMacAddress");

		try {
			mvc.perform(put("/api/register").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8").content(request)).andExpect(status().isOk())
					.andExpect(content().string(expectedResponse));

			Optional<Device> device = deviceRepository.findById(macAdress);
			assertFalse(device.isPresent());

			deviceRepository.deleteAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Exception occurs");
		}
	}

	@Test
	void shouldNotAllowAddDeviceWithBlankMac() {
		String macAdress = "       ";
		DeviceType deviceType = DeviceType.Gateway;
		String request = addDevice(macAdress, deviceType.toString(), null);
		String expectedResponse = prepareResponseOfRegister(Status.ERROR, "macAddress cannot be blank");

		try {
			mvc.perform(put("/api/register").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8").content(request)).andExpect(status().isOk())
					.andExpect(content().string(expectedResponse));

			Optional<Device> device = deviceRepository.findById(macAdress);
			assertFalse(device.isPresent());

			deviceRepository.deleteAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Exception occurs");
		}
	}

	@Test
	void shouldNotAllowAddDeviceThatExists() {
		String macAdress = "aa:bb:cc:dd:ee:dd";
		DeviceType deviceType = DeviceType.AccessPoint;
		String request = addDevice(macAdress, deviceType.toString(), null);
		String expectedResponse = prepareResponseOfRegister(Status.ERROR,
				"Device of given macAddress is already register");

		Device existingOne = new Device(macAdress, deviceType, null);
		try {
			deviceRepository.saveAndFlush(existingOne);

			assertTrue(deviceRepository.existsById(macAdress));
			mvc.perform(put("/api/register").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8").content(request)).andExpect(status().isOk())
					.andExpect(content().string(expectedResponse));

			deviceRepository.deleteAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Exception occurs");
		}
	}

	@Test
	void shouldReturnEmptyListOfDeivesIfNoneExists() {
		try {
			assertEquals(0, deviceRepository.count());
			mvc.perform(post("/api/devices").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
					.andExpect(content().string("[]"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Exception occurs");
		}
	}

	@Test
	void shouldReturnDeviceByMac() {
		String macAdress = "aa:bb:cc:dd:ee:dd";
		DeviceType deviceType = DeviceType.AccessPoint;
		Device device = new Device(macAdress, deviceType, null);
		try {
			deviceRepository.saveAndFlush(device);
			assertTrue(deviceRepository.existsById(macAdress));
			mvc.perform(post("/api/device/" + macAdress).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andExpect(jsonPath("$.macAddress", is(macAdress)))
					.andExpect(jsonPath("$.deviceType", is(deviceType.toString())));

			deviceRepository.deleteAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Exception occurs");
		}
	}

	@Test
	void shoulNotdReturnDeviceByMacifNotExists() {
		String macAdress = "aa:bb:cc:dd:ee:dd";
		String expectedResponse = "Device of this macAddress is not register";

		try {
			assertFalse(deviceRepository.existsById(macAdress));
			mvc.perform(post("/api/device/" + macAdress).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andExpect(content().string(expectedResponse));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Exception occurs");
		}
	}

	@Test
	void shouldReturnDevicesInExpectedOrder() {
		ArrayList<Device> devices = new ArrayList<>();
		devices.add(new Device("aa:aa:aa:aa:aa:aa", DeviceType.Gateway, null));
		devices.add(new Device("aa:aa:aa:aa:aa:ab", DeviceType.Switch, "aa:aa:aa:aa:aa:aa"));
		devices.add(new Device("aa:aa:aa:aa:aa:ac", DeviceType.Switch, "aa:aa:aa:aa:aa:aa"));
		devices.add(new Device("aa:aa:aa:aa:aa:01", DeviceType.AccessPoint, "aa:aa:aa:aa:aa:ab"));
		devices.add(new Device("aa:bb:cc:dd:ee:02", DeviceType.AccessPoint, "aa:aa:aa:aa:aa:ac"));
		devices.add(new Device("bb:bb:bb:bb:bb:bb", DeviceType.Gateway, null));

		try {
			deviceRepository.saveAllAndFlush(devices);
			assertEquals(6, deviceRepository.count());
			mvc.perform(post("/api/devices").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(6)))
					.andExpect(jsonPath("$[0].deviceType", is(devices.get(0).getDeviceType().toString())))
					.andExpect(jsonPath("$[1].deviceType", is(devices.get(5).getDeviceType().toString())))
					.andExpect(jsonPath("$[2].deviceType", is(devices.get(1).getDeviceType().toString())))
					.andExpect(jsonPath("$[3].deviceType", is(devices.get(2).getDeviceType().toString())))
					.andExpect(jsonPath("$[4].deviceType", is(devices.get(3).getDeviceType().toString())))
					.andExpect(jsonPath("$[5].deviceType", is(devices.get(4).getDeviceType().toString())));

			deviceRepository.deleteAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Exception occurs");
		}
	}

	private String addDevice(String macAddress, String deviceType, String uplinkMacAdress) {
		StringBuilder requestBody = new StringBuilder("{\"macAddress\": \"");
		requestBody.append(macAddress);
		requestBody.append("\",\"deviceType\": \"");
		requestBody.append(deviceType);
		if (StringUtils.isNotBlank(uplinkMacAdress)) {
			requestBody.append("\",\"uplinkMacAddress\": \"");
			requestBody.append(uplinkMacAdress);
		}
		requestBody.append("\"}");

		return requestBody.toString();
	}

	private void checkDevice(String macAddress, DeviceType deviceType, String uplinkMacAdress, Device device) {
		assertNotNull(device);
		assertEquals(macAddress, device.getMacAddress());
		assertEquals(macAddress, device.getMacAddress());
		assertEquals(uplinkMacAdress, device.getUplinkMacAddress());

	}

	private String prepareResponseOfRegister(Status status, String message) {
		StringBuilder responseBody = new StringBuilder("{\"status\":\"");
		responseBody.append(status.toString());
		responseBody.append("\",\"message\":");
		if (message != null) {
			responseBody.append("\"");
			responseBody.append(message);
			responseBody.append("\"");
		} else {
			responseBody.append("null");
		}
		responseBody.append("}");
		return responseBody.toString();
	}

}
