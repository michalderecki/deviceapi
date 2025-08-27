package pl.michalderecki.deviceapi.utils;

import java.util.Comparator;

import pl.michalderecki.deviceapi.entity.Device;

public class DevicesComparator implements Comparator<Device> {

	
	/**
	 *  At this time when enum order is Gateway, Switch, AccessPoint it would be enough to sort by default order but in case 
	 *  when we add new device type we want stay with actual order of that 3 types 
	 */
	@Override
	public int compare(Device o1, Device o2) {
		return Integer.compare(o1.getDeviceType().getSortingOrder(), o2.getDeviceType().getSortingOrder());
	}
	
}
