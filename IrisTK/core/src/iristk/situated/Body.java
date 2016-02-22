package iristk.situated;

public class Body extends Item {

	@RecordField
	public BodyPart head = new BodyPart();
	
	@RecordField
	public BodyPart handLeft;
	
	@RecordField
	public BodyPart handRight;

	@RecordField
	public Location gaze;

	@RecordField
	public String recId;

	@RecordField
	public String sensor;
	
	public Body(String id) {
		this.id = id;
	}
	
	public Body() {
	}
	
	public double gazeAngle(Location loc) {
		if (gaze != null && head != null && head.location != null) {
			return gaze.subtract(getHeadLocation()).toRotation().angleTo(loc.subtract(getHeadLocation()).toRotation());
		} else if (head != null && head.location != null && head.rotation != null) {
			return head.rotation.angleTo(loc.subtract(getHeadLocation()).toRotation());
		}
		return -1;
	}
	
	public boolean lookingAt(Location loc) {
		double angle = gazeAngle(loc);
		return (angle != -1 && angle < 15);
	}
	
	public Location getHeadLocation() {
		return location.add(head.location);
	}
	
	public boolean hasHeadRotation() {
		return head.rotation != null;
	}
	
	/**
	 * Returns the total rotation of the head (including torso rotation)
	 */
	public Rotation getHeadRotation() {
		return rotation.add(head.rotation);
	}

	/**
	 * Transforms absoluteLocation into a location relative to the location of this body's head and the rotation of its torso
	 */
	public Location getRelative(Location absoluteLocation) {
		return absoluteLocation.subtract(getHeadLocation()).rotate(rotation.invert());
	}
	
	/**
	 * Transforms relativeLocation (to this body's head and the rotation of its torso) into an absolute location 
	 */
	public Location getAbsolute(Location relativeLocation) {
		return relativeLocation.rotate(rotation).add(getHeadLocation());
	}
		
}
