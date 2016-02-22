package iristk.kinect;

import java.util.*;

import iristk.net.kinect.KinectBody;
import iristk.situated.Rotation;
import iristk.util.Utils;

public class BodyBuffer {
	
	private HashMap<Long,Buffer> buffers = new HashMap<>();

	public void add(KinectBody kbody) {
		if (!buffers.containsKey(kbody.getId())) {
			buffers.put(kbody.getId(), new Buffer());
		}
		Buffer buffer = buffers.get(kbody.getId());
		buffer.add(kbody);
		if (buffer.size() > 5) {
			buffer.remove(0);
		}
	}
	
	public Rotation getHeadRotation(long id) {
		Buffer buffer = buffers.get(id);
		List<Float> xs = new LinkedList<>();
		List<Float> ys = new LinkedList<>();
		List<Float> zs = new LinkedList<>();
		for (KinectBody kbody : buffer) {
			if (kbody.getHead().getHasRotation()) {
				xs.add(kbody.getHead().getRotX());
				ys.add(kbody.getHead().getRotY());
				zs.add(kbody.getHead().getRotZ());
			}
		}
		if (xs.size() > 0) 
			return new Rotation(Utils.median(xs), Utils.median(ys), Utils.median(zs));
		else
			return null;
	}
	
	private static class Buffer extends LinkedList<KinectBody> {
	}
	
}
