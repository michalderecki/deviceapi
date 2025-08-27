package pl.michalderecki.deviceapi.entity;

public enum DeviceType {
	Gateway(0),
	Switch(1),
	AccessPoint(2);
	
	private final int sortingOrder;

	private DeviceType(int sortingOrder) {
		this.sortingOrder = sortingOrder;
		
	}

	public int getSortingOrder() {
		return sortingOrder;
	}
	
}
