package com.base.engine.physics;

import com.base.engine.core.GameObject;
import com.base.engine.rendering.Camera;
import com.base.engine.rendering.Projection;

public abstract class WorldStructure {
	public abstract void update(float delta);
	public abstract void checkFrustum(Frustum frustrum);
	public abstract void checkFrustum(Camera camera);
	public static boolean checkFrustum(Camera camera, Projection proj, GameObject o){
		return false;
	}
}
