/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.kinect;

import iristk.system.IrisUtils;

import java.io.File;

import iristk.net.kinect.*;
import iristk.net.kinect2.*;
import net.sf.jni4net.Bridge;

public class KinectV2 extends Kinect2Wrapper implements IKinect {

	static {
		try {
			IrisUtils.addJavaLibPath(IrisUtils.getPackageLibPath(KinectV2.class));
			String arch = IrisUtils.is64arch() ? "x64" : "x86"; 
			if (IrisUtils.is64arch())
				IrisUtils.addJavaLibPath(IrisUtils.getPackagePath(KinectV2.class, "lib/" + arch));
			else
				IrisUtils.addJavaLibPath(IrisUtils.getPackagePath(KinectV2.class, "lib/" + arch));
			Bridge.init();
			Bridge.LoadAndRegisterAssemblyFrom(new File(IrisUtils.getPackageLibPath(KinectV1.class), "IrisTK.Net.Kinect.j4n.dll"));
			Bridge.LoadAndRegisterAssemblyFrom(new File(IrisUtils.getPackagePath(KinectV2.class), "lib/" + arch + "/IrisTK.Net.Kinect2.j4n.dll"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String id;

	public KinectV2(String id) {
		System.out.println("Initializing Kinect v2");
		this.id = id;
		super.start();
	}
	
	public KinectV2() {
		this("kinect");
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void addDepthFrameListener(DepthFrameListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addSensorElevationListener(SensorElevationListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public float getVerticalFOV() {
		return 60;
	}

	@Override
	public float getHorizontalFOV() {
		return 70;
	}

	@Override
	public int getCameraViewHeight() {
		return 1080;
	}

	@Override
	public int getCameraViewWidth() {
		return 1920;
	}

}
