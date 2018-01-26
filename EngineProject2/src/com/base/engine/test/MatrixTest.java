package com.base.engine.test;

import math.Matrix4f;

public class MatrixTest {
	public static void main(String [] args){
		Matrix4f m = new Matrix4f();
		m.m32 = -1;
		System.out.println(m+"\n");
		m.invert();
		System.out.println(m);
		
	}
}
