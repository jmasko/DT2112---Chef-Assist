package iristk.kinect;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import iristk.net.kinect.BodyListener;
import iristk.net.kinect.KinectBody;
import iristk.net.kinect.KinectBodyPart;
import iristk.net.kinect.KinectBodySet;
import iristk.situated.Agent;
import iristk.situated.Body;
import iristk.situated.Location;
import iristk.situated.Rotation;
import iristk.situated.Situation;
import iristk.situated.SystemAgent;
import iristk.vision.CameraViewItem;
import iristk.vision.CameraViewDecorator;

public class KinectCameraView implements CameraViewDecorator, BodyListener {

	private IKinect kinect;
	
	private KinectModule kinectModule;
	private SystemAgent systemAgent;
	private Situation situation;

	private int color;
	private float stroke;

	private KinectBodySet bodySet;

	private long lastBodySeen;
	
	private List<CameraViewItem> bodyItems = new ArrayList<>();

	public KinectCameraView(KinectModule kinectModule) {
		kinectModule.addBodyListener(this);
		this.kinect = kinectModule.getKinect();
	}
	
	public KinectCameraView(KinectModule kinectModule, SystemAgent systemAgent) {
		this(kinectModule);
		this.kinectModule = kinectModule;
		this.systemAgent = systemAgent;
		this.situation = systemAgent.getSituation();
	}
	
	public String getBodyIdAt(float x, float y) {
		if (bodySet != null) {
			int height = kinect.getCameraViewHeight();
			int width = kinect.getCameraViewWidth();
			int xp = (int) (x * width);
			int yp = (int) (y * height);
			for (int i = 0; i < bodySet.size(); i++) {
				KinectBody kbody = bodySet.get(i);
				int[] xy = kinect.mapSkeletonPointToColorPoint(kbody.getHead().getLocX(), kbody.getHead().getLocY(), kbody.getHead().getLocZ());
				int h = height / 8, w = (int)(h * 0.75);
				int x1 = xy[0]-w;
				int y1 = xy[1]-h;
				int x2 = x1 + w*2;
				int y2 = y1 + h*2;
				if (xp > x1 && xp < x2 && yp > y1 && yp < y2) {
					return new Long(kbody.getId()).toString();
				}
			}
		}
		return null;
	}

	@Override
	public synchronized void onBodiesReceived(KinectBodySet bodySet) {
		this.bodySet = bodySet;
		lastBodySeen = System.currentTimeMillis();

		float height = kinect.getCameraViewHeight();
		float width = kinect.getCameraViewWidth();

		bodyItems.clear();
		for (int i = 0; i < bodySet.size(); i++) {

			KinectBody kbody = bodySet.get(i);
			
			color = Color.GREEN.getRGB();
			stroke = 1f;
			
			if (systemAgent != null) {
				Agent attended = systemAgent.getAttendedUser();
				String bodyId = kinectModule.getBodyId(kbody.getId() + "");
				if (attended != null && bodyId != null) {
					Body body = situation.getBodyBySensorId(kinect.getId(), bodyId);
					if (body != null && body.id == attended.id) {
						stroke = 2;
						color = Color.RED.getRGB();
					}
				}
			}
			
			int[] xy = kinect.mapSkeletonPointToColorPoint(kbody.getHead().getLocX(), kbody.getHead().getLocY(), kbody.getHead().getLocZ());

			float h = height / 8, w = (int)(h * 0.75);

			CameraViewItem head = new CameraViewItem();

			head = new CameraViewItem();
			head.x = (xy[0]-w) / width;
			head.y = (xy[1]-h) / height;
			
			head.width = (w*2) / width;
			head.height = (h*2) / height;
			head.shape = CameraViewItem.SHAPE_RECT;
			head.color = color;
			head.stroke = stroke;
			bodyItems.add(head);

			if (kbody.getHandLeft() != null) {
				bodyItems.add(getHand(kbody.getHandLeft()));
			}
			if (kbody.getHandRight() != null) {
				bodyItems.add(getHand(kbody.getHandRight()));
			}
			
		}
	}

	private CameraViewItem getHand(KinectBodyPart hand) {
		float height = kinect.getCameraViewHeight();
		float width = kinect.getCameraViewWidth();
		float hw = height / 16;
		int[] xy = kinect.mapSkeletonPointToColorPoint(hand.getLocX(), hand.getLocY(), hand.getLocZ());
		CameraViewItem chand = new CameraViewItem();
		chand.x = (xy[0]-hw) / width;
		chand.y = (xy[1]-hw) / height;
		chand.width = (hw*2) / width;
		chand.height = (hw*2) / height;
		//System.out.println(chand.width + " " + chand.height + " " + width + " " + height);
		chand.shape = CameraViewItem.SHAPE_OVAL;
		chand.color = color;
		chand.stroke = stroke;
		return chand;
	}

	public synchronized List<CameraViewItem> getItemList() {
		if (System.currentTimeMillis() - lastBodySeen > 1000)
			bodyItems.clear();
		List<CameraViewItem> items = new ArrayList<>(bodyItems);

		if (systemAgent != null && systemAgent.gaze != null) {
			Location kloc = kinectModule.getSensor().location;
			Rotation krot = kinectModule.getSensor().rotation;
					
			Location loc = systemAgent.gaze.subtract(kloc).rotate(krot.invert());
			
			int[] xy = kinectModule.getKinect().mapSkeletonPointToColorPoint((float)loc.x, (float)loc.y, (float)loc.z);
			float height = kinect.getCameraViewHeight();
			float width = kinect.getCameraViewWidth();
			CameraViewItem gaze  = new CameraViewItem();
			int w = 10;
			gaze.x = (xy[0]-w) / width;
			gaze.y = (xy[1]-w) / height;
			gaze.width = (w*2) / width;
			gaze.height = (w*2) / height;
			gaze.shape = CameraViewItem.SHAPE_OVAL;
			gaze.stroke = 2;
			gaze.color = Color.RED.getRGB();
			items.add(gaze);
		}
			
		return items;
	}

	@Override
	public void decorate(Graphics2D g, int width, int height) {
		for (CameraViewItem item : getItemList()) {
			//System.out.println(item + " " + width + " " + height);
			item.decorate(g, width, height);
		}
	}

}
