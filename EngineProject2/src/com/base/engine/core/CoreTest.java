package com.base.engine.core;

import java.util.Stack;

import org.lwjgl.system.Configuration;

import com.base.engine.input.GLFWInputEngine;
import com.base.engine.input.InputMapping;
import com.base.engine.input.InputMapping.ActionPair;
import com.base.engine.input.InputMapping.RangePair;
import com.base.engine.physics.PhysicsEngine;
import com.base.engine.rendering.Camera;
import com.base.engine.rendering.opengl.GLFWRenderingEngine2;

public class CoreTest {
	public static void main(String [] args) {
		Configuration.STACK_SIZE.set(25000);
		CoreEngine engine = new CoreEngine(new Engine[] {new GLFWInputEngine(), new PhysicsEngine(), new GLFWRenderingEngine2()});
		
		Stack<ActionPair> actions = InputMapping.actions = new Stack<ActionPair>();
		
		actions.push(new ActionPair("LockCursor", Camera::lockCursor));
		actions.push(new ActionPair("UnlockCursor", Camera::unlockCursor));
		
		Stack<ActionPair> states = InputMapping.states = new Stack<ActionPair>();
		
		states.push(new ActionPair("MoveForward", Camera::moveForward));
		states.push(new ActionPair("MoveBackward", Camera::moveBackward));
		states.push(new ActionPair("MoveLeft", Camera::moveLeft));
		states.push(new ActionPair("MoveRight", Camera::moveRight));
		
		Stack<RangePair> ranges = InputMapping.ranges = new Stack<RangePair>();
		
		ranges.push(new RangePair("RotateYaw", Camera::rotateYaw));
		ranges.push(new RangePair("RotatePitch", Camera::rotatePitch));
		
		engine.start();
		engine.run();
	}
}
