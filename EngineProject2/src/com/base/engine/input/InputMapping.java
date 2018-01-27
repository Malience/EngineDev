package com.base.engine.input;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import com.base.engine.core.CoreEngine;
import com.base.engine.rendering.Camera;
import com.base.engine.rendering.opengl.GLFWRenderingEngine;
import com.base.engine.rendering.opengl.GLFWRenderingEngine2;

public class InputMapping {
	private static Runnable[] actionMap;
	private static Stack<Integer> activeActions;
	private static Runnable[] statesMap;
	private static HashSet<Integer> activeStates;
	private static Range[] rangesMap;
	private static float[] rangeValues;
	//private static HashSet<Integer> activeRanges;
	private static Stack<Integer> activeRanges;
	private static HashMap<String, Integer> actionNameMap;
	private static String[] actionIndexMap;
	private static HashMap<String, Integer> statesNameMap;
	private static String[] statesIndexMap;
	private static HashMap<String, Integer> rangesNameMap;
	private static String[] rangesIndexMap;
	static long frameTime;
	
	public static Stack<ActionPair> actions;
	public static Stack<ActionPair> states;
	public static Stack<RangePair> ranges;
	
	static void init(){
		if(actions == null) actions = new Stack<ActionPair>();
//		actions.push(new ActionPair("Jump", (Runnable)() -> System.out.println("Jump!")));
//		actions.push(new ActionPair("ActionTest", (Runnable)() -> {System.out.println("Action Works!");}));
//		actions.push(new ActionPair("LockCursor", Camera::lockCursor));
//		actions.push(new ActionPair("UnlockCursor", Camera::unlockCursor));
//		actions.push(new ActionPair("UseView", GLFWRenderingEngine::useView));
//		actions.push(new ActionPair("Debug", GLFWRenderingEngine::debug));
//		actions.push(new ActionPair("Bloom", GLFWRenderingEngine::bloom));
//		actions.push(new ActionPair("Shadows", GLFWRenderingEngine::shadows));
//		actions.push(new ActionPair("SSAO", GLFWRenderingEngine2::ssao));
//		actions.push(new ActionPair("RaiseFilter", GLFWRenderingEngine::raiseFilter));
//		actions.push(new ActionPair("LowerFilter", GLFWRenderingEngine::lowerFilter));
		
		if(states == null) states = new Stack<ActionPair>();
//		states.push(new ActionPair("StateJump", (Runnable)() -> System.out.println("Jumping!")));
//		states.push(new ActionPair("StateTest", (Runnable)() -> {System.out.println("State Works!");}));
//		states.push(new ActionPair("MoveForward", Camera::moveForward));
//		states.push(new ActionPair("MoveBackward", Camera::moveBackward));
//		states.push(new ActionPair("MoveLeft", Camera::moveLeft));
//		states.push(new ActionPair("MoveRight", Camera::moveRight));
//		states.push(new ActionPair("MovePlayerForward", CoreEngine::movePlayerForward));
//		states.push(new ActionPair("MovePlayerBackward", CoreEngine::movePlayerBackward));
//		states.push(new ActionPair("MovePlayerLeft", CoreEngine::movePlayerLeft));
//		states.push(new ActionPair("MovePlayerRight", CoreEngine::movePlayerRight));
//		states.push(new ActionPair("RaiseExposure", GLFWRenderingEngine::raiseExposure));
//		states.push(new ActionPair("LowerExposure", GLFWRenderingEngine::lowerExposure));
//		states.push(new ActionPair("RaiseGamma", GLFWRenderingEngine::raiseGamma));
//		states.push(new ActionPair("LowerGamma", GLFWRenderingEngine::lowerGamma));
		
		if(ranges == null) ranges = new Stack<RangePair>();
//		ranges.push(new RangePair("JumpHeight", (Range)(float f) -> System.out.println("Jump height " + f)));
//		ranges.push(new RangePair("Scroll", GLFWRenderingEngine::changeFov));
//		ranges.push(new RangePair("RangeTest", (Range)(float f) -> System.out.println("Range Works! " + f)));
//		ranges.push(new RangePair("RotateYaw", Camera::rotateYaw));
//		ranges.push(new RangePair("RotatePitch", Camera::rotatePitch));
		
		InputMapping.init(actions, states, ranges);
	}
	
	static void init(Stack<ActionPair> actions, Stack<ActionPair> states, Stack<RangePair> ranges){
		actionMap = new Runnable[actions.size()];
		activeActions = new Stack<Integer>();
		statesMap = new Runnable[states.size()];
		activeStates = new HashSet<Integer>(statesMap.length);
		rangesMap = new Range[ranges.size()];
		rangeValues = new float[rangesMap.length];
		//activeRanges = new HashSet<Integer>(rangesMap.length);
		activeRanges = new Stack<Integer>();
		
		actionNameMap = new HashMap<String, Integer>(actionMap.length);
		actionIndexMap = new String[actionMap.length];
		statesNameMap = new HashMap<String, Integer>(statesMap.length);
		statesIndexMap = new String[statesMap.length];
		rangesNameMap = new HashMap<String, Integer>(rangesMap.length);
		rangesIndexMap = new String[rangesMap.length];
		
		for(int i = 0; i < actionMap.length; i++){
			ActionPair ap = actions.pop();
			actionMap[i] = ap.action;
			actionNameMap.put(ap.name, i);
			actionIndexMap[i] = ap.name;
		}
		for(int i = 0; i < statesMap.length; i++){
			ActionPair ap = states.pop();
			statesMap[i] = ap.action;
			statesNameMap.put(ap.name, i);
			statesIndexMap[i] = ap.name;
		}
		for(int i = 0; i < rangesMap.length; i++){
			RangePair ap = ranges.pop();
			rangesMap[i] = ap.action;
			rangesNameMap.put(ap.name, i);
			rangesIndexMap[i] = ap.name;
		}
	}
	
	static int getActionIndex(String name){Integer i = actionNameMap.get(name); return i == null ? -1 : i;}
	static int getStateIndex(String name){Integer i = statesNameMap.get(name); return i == null ? -1 : i;}
	static int getRangeIndex(String name){Integer i = rangesNameMap.get(name); return i == null ? -1 : i;}
	
	static void invokeCallbacks(){
		while(!activeActions.isEmpty())actionMap[activeActions.pop()].run();
		for(int i : activeStates)statesMap[i].run();
		while(!activeRanges.isEmpty()){int i = activeRanges.pop();rangesMap[i].run(rangeValues[i]);}
	}
	
	static void invokeAction(int key){activeActions.push(key);}
	static void invokeState(int key, boolean activate){if(activate) activeStates.add(key);else activeStates.remove(key);}
	static void invokeRange(int key, float value){activeRanges.add(key);rangeValues[key] = value;}
	static void invokeRange(int key){activeRanges.add(key);}
	
	public static class ActionPair{
		private String name;
		private Runnable action;
		public ActionPair(String name, Runnable action){
			this.name = name;
			this.action = action;
		}
	}
	
	@FunctionalInterface
	public interface Range{void run(float f);}
	
	public static class RangePair{
		private String name;
		private Range action;
		public RangePair(String name, Range action){
			this.name = name;
			this.action = action;
		}
	}
}
