package pl.michalderecki.deviceapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pl.michalderecki.deviceapi.dto.TopologyEntry;
import pl.michalderecki.deviceapi.entity.Device;

public interface DeviceRepository extends JpaRepository<Device, String> {
	@Query("select new pl.michalderecki.deviceapi.dto.TopologyEntry(d.macAddress) from Device d where d.uplinkMacAddress is null")
	public List<TopologyEntry> findTopologyWithoutUplink();
	
	@Query("select new pl.michalderecki.deviceapi.dto.TopologyEntry(d.macAddress) from Device d where d.uplinkMacAddress = :uplinkMapAddress")
	public List<TopologyEntry> findTopologyByUplink(@Param("uplinkMapAddress") String uplinkMapAddress);
}
