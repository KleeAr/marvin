package ar.com.klee.marvin.client.model;

public class UserSetting {

	private Long userId;
    private Long miniumTripTime; //In hours
    private Long miniumTripDistance; //in kilometers
    private String emergencyNumber;
    private String emergencySMS;
    private int orientation;
    private boolean openAppWhenStop = false;
    private String appToOpenWhenStop;
    private String hotspotName = "MRVN";
    private String hotspotPassword = "marvinHotSpot";
	
	public UserSetting() {
		
	}

	public UserSetting(Long userId, Long miniumTripTime, Long miniumTripDistance, String emergencyNumber,
			String emergencySMS, int orientation, boolean openAppWhenStop, String appToOpenWhenStop, String hotspotName,
			String hotspotPassword) {
		super();
		this.userId = userId;
		this.miniumTripTime = miniumTripTime;
		this.miniumTripDistance = miniumTripDistance;
		this.emergencyNumber = emergencyNumber;
		this.emergencySMS = emergencySMS;
		this.orientation = orientation;
		this.openAppWhenStop = openAppWhenStop;
		this.appToOpenWhenStop = appToOpenWhenStop;
		this.hotspotName = hotspotName;
		this.hotspotPassword = hotspotPassword;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getMiniumTripTime() {
		return miniumTripTime;
	}

	public void setMiniumTripTime(Long miniumTripTime) {
		this.miniumTripTime = miniumTripTime;
	}

	public Long getMiniumTripDistance() {
		return miniumTripDistance;
	}

	public void setMiniumTripDistance(Long miniumTripDistance) {
		this.miniumTripDistance = miniumTripDistance;
	}

	public String getEmergencyNumber() {
		return emergencyNumber;
	}

	public void setEmergencyNumber(String emergencyNumber) {
		this.emergencyNumber = emergencyNumber;
	}

	public String getEmergencySMS() {
		return emergencySMS;
	}

	public void setEmergencySMS(String emergencySMS) {
		this.emergencySMS = emergencySMS;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public boolean isOpenAppWhenStop() {
		return openAppWhenStop;
	}

	public void setOpenAppWhenStop(boolean openAppWhenStop) {
		this.openAppWhenStop = openAppWhenStop;
	}

	public String getAppToOpenWhenStop() {
		return appToOpenWhenStop;
	}

	public void setAppToOpenWhenStop(String appToOpenWhenStop) {
		this.appToOpenWhenStop = appToOpenWhenStop;
	}

	public String getHotspotName() {
		return hotspotName;
	}

	public void setHotspotName(String hotspotName) {
		this.hotspotName = hotspotName;
	}

	public String getHotspotPassword() {
		return hotspotPassword;
	}

	public void setHotspotPassword(String hotspotPassword) {
		this.hotspotPassword = hotspotPassword;
	}

	
	
	
}