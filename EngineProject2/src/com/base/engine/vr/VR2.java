package com.base.engine.vr;

import static org.lwjgl.openvr.VR.ETrackedDeviceProperty_Prop_ModelNumber_String;
import static org.lwjgl.openvr.VR.ETrackedDeviceProperty_Prop_SerialNumber_String;
import static org.lwjgl.openvr.VR.VR_GetVRInitErrorAsEnglishDescription;
import static org.lwjgl.openvr.VR.VR_GetVRInitErrorAsSymbol;
import static org.lwjgl.openvr.VR.VR_InitInternal;
import static org.lwjgl.openvr.VR.VR_IsHmdPresent;
import static org.lwjgl.openvr.VR.VR_IsRuntimeInstalled;
import static org.lwjgl.openvr.VR.VR_RuntimePath;
import static org.lwjgl.openvr.VR.VR_ShutdownInternal;
import static org.lwjgl.openvr.VR.k_unTrackedDeviceIndex_Hmd;
import static org.lwjgl.openvr.VRSystem.VRSystem_GetRecommendedRenderTargetSize;
import static org.lwjgl.openvr.VRSystem.VRSystem_GetStringTrackedDeviceProperty;
import static org.lwjgl.openvr.VRSystem.VRSystem_GetProjectionMatrix;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.openvr.HmdMatrix44;
import org.lwjgl.openvr.OpenVR;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRCompositor;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.system.CallbackI.V;
import org.lwjgl.system.MemoryStack;

import com.base.engine.core.util.Util;
import math.Matrix4f;

public class VR2 {
	public static FloatBuffer leftEyePerspective;
	public static FloatBuffer rightEyePerspective;
	public static int renderWidth = -1;
	public static int renderHeight = -1;
	
	 public static void init() {
	        System.err.println("VR_IsRuntimeInstalled() = " + VR_IsRuntimeInstalled());
	        System.err.println("VR_RuntimePath() = " + VR_RuntimePath());
	        System.err.println("VR_IsHmdPresent() = " + VR_IsHmdPresent());

	        try (MemoryStack stack = stackPush()) {
	            IntBuffer peError = stack.mallocInt(1);

	            int token = VR_InitInternal(peError, VR.EVRApplicationType_VRApplication_Scene);
	            if (peError.get(0) == 0) {
	                try {
	                    OpenVR.create(token);

	                    System.err.println("Model Number : " + VRSystem_GetStringTrackedDeviceProperty(
	                        k_unTrackedDeviceIndex_Hmd,
	                        ETrackedDeviceProperty_Prop_ModelNumber_String,
	                        peError
	                    ));
	                    System.err.println("Serial Number: " + VRSystem_GetStringTrackedDeviceProperty(
	                        k_unTrackedDeviceIndex_Hmd,
	                        ETrackedDeviceProperty_Prop_SerialNumber_String,
	                        peError
	                    ));

	                    IntBuffer w = stack.mallocInt(1);
	                    IntBuffer h = stack.mallocInt(1);
	                    VRSystem_GetRecommendedRenderTargetSize(w, h);
	                
	                    renderWidth = w.get(0);
	                    renderHeight = h.get(0);
	                    System.err.println("Recommended width : " + w.get(0));
	                    System.err.println("Recommended height: " + h.get(0));
	                    
	                    
	                    HmdMatrix44 left = new HmdMatrix44(Util.createByteBuffer(16*4));
	                    HmdMatrix44 right = new HmdMatrix44(Util.createByteBuffer(16*4));
	                    VRSystem_GetProjectionMatrix(VR.EVREye_Eye_Left, 0.001f, 1000f, left);
	                    VRSystem_GetProjectionMatrix(VR.EVREye_Eye_Right, 0.001f, 1000f, right);
	                    leftEyePerspective = left.m();
	                    rightEyePerspective = left.m();
	                    
	                    //System.err.println("IS direct " + VRSystem.VRSystem_SetDisplayVisibility(true));
	                } catch(Exception e) {
	                	e.printStackTrace();
	                    VR_ShutdownInternal();
	                }
	            } else {
	                System.out.println("INIT ERROR SYMBOL: " + VR_GetVRInitErrorAsSymbol(peError.get(0)));
	                System.out.println("INIT ERROR  DESCR: " + VR_GetVRInitErrorAsEnglishDescription(peError.get(0)));
	            }
	        }
	    }
	 
	 public static void stop() {
		 VRSystem.VRSystem_ReleaseInputFocus();
		 //VRCompositor.VRCompositor_CompositorQuit();
		 VR_ShutdownInternal();
	 }
	 
}
