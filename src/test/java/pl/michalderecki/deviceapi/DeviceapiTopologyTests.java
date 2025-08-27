package pl.michalderecki.deviceapi;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import pl.michalderecki.deviceapi.entity.Device;
import pl.michalderecki.deviceapi.entity.DeviceType;
import pl.michalderecki.deviceapi.repository.DeviceRepository;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceapiTopologyTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private DeviceRepository deviceRepository;

	@Test
	void shoulNotdReturnTopologyifNotExists() {
		String macAdress = "aa:bb:cc:dd:ee:dd";
		String expectedResponse = "Device of this macAddress is not register";
		System.out.println(expectedResponse);
		try {
			assertFalse(deviceRepository.existsById(macAdress));
			mvc.perform(post("/api/topology/" + macAdress).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andExpect(content().string(expectedResponse));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Exception occurs");
		}
	}

	@Test
	void shouldReturnTopology() {
		ArrayList<Device> devices = new ArrayList<>();
		devices.add(new Device("aa:aa:aa:aa:aa:aa", DeviceType.Gateway, null));
		devices.add(new Device("bb:bb:bb:bb:bb:bb", DeviceType.Gateway, null));
		devices.add(new Device("aa:aa:aa:aa:aa:ab", DeviceType.Switch, "aa:aa:aa:aa:aa:aa"));
		devices.add(new Device("aa:aa:aa:aa:aa:ac", DeviceType.Switch, "aa:aa:aa:aa:aa:aa"));
		devices.add(new Device("aa:aa:aa:aa:aa:01", DeviceType.AccessPoint, "aa:aa:aa:aa:aa:ab"));
		devices.add(new Device("aa:bb:cc:dd:ee:02", DeviceType.AccessPoint, "aa:aa:aa:aa:aa:ac"));

		try {
			deviceRepository.saveAllAndFlush(devices);
			assertEquals(6, deviceRepository.count());
			mvc.perform(post("/api/topology").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(2)))
					.andExpect(jsonPath("$[0].macAddress", is(devices.get(0).getMacAddress())))
					.andExpect(jsonPath("$[1].macAddress", is(devices.get(1).getMacAddress())))
					.andExpect(jsonPath("$[1].childrens", hasSize(0))).andExpect(jsonPath("$[0].childrens", hasSize(2)))
					.andExpect(jsonPath("$[0].childrens[0].macAddress", is(devices.get(2).getMacAddress())))
					.andExpect(jsonPath("$[0].childrens[1].macAddress", is(devices.get(3).getMacAddress())))
					.andExpect(jsonPath("$[0].childrens[0].childrens", hasSize(1)))
					.andExpect(jsonPath("$[0].childrens[1].childrens", hasSize(1)))
					.andExpect(
							jsonPath("$[0].childrens[0].childrens[0].macAddress", is(devices.get(4).getMacAddress())))
					.andExpect(
							jsonPath("$[0].childrens[1].childrens[0].macAddress", is(devices.get(5).getMacAddress())))
					.andExpect(jsonPath("$[0].childrens[0].childrens[0].childrens", hasSize(0)))
					.andExpect(jsonPath("$[0].childrens[1].childrens[0].childrens", hasSize(0)));

			deviceRepository.deleteAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Exception occurs");
		}
	}

	@Test
	void shouldReturnTopologyByMac() {
		ArrayList<Device> devices = new ArrayList<>();
		devices.add(new Device("aa:aa:aa:aa:aa:aa", DeviceType.Gateway, null));
		devices.add(new Device("bb:bb:bb:bb:bb:bb", DeviceType.Gateway, null));
		devices.add(new Device("aa:aa:aa:aa:aa:ab", DeviceType.Switch, "aa:aa:aa:aa:aa:aa"));
		devices.add(new Device("aa:aa:aa:aa:aa:ac", DeviceType.Switch, "aa:aa:aa:aa:aa:aa"));
		devices.add(new Device("aa:aa:aa:aa:aa:01", DeviceType.AccessPoint, "aa:aa:aa:aa:aa:ab"));
		devices.add(new Device("aa:bb:cc:dd:ee:02", DeviceType.AccessPoint, "aa:aa:aa:aa:aa:ac"));

		try {
			deviceRepository.saveAllAndFlush(devices);
			assertEquals(6, deviceRepository.count());
			mvc.perform(post("/api/topology/" + devices.get(3).getMacAddress()).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.macAddress", is(devices.get(3).getMacAddress())))
					.andExpect(jsonPath("$.childrens", hasSize(1)))
					.andExpect(jsonPath("$.childrens[0].macAddress", is(devices.get(5).getMacAddress())))
					.andExpect(jsonPath("$.childrens[0].childrens", hasSize(0)));

			deviceRepository.deleteAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Exception occurs");
		}
	}

}
