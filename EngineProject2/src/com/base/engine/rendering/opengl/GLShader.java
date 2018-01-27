package com.base.engine.rendering.opengl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.Scanner;

import org.lwjgl.opengl.GL11;

import com.base.engine.core.util.Util;
import com.base.engine.rendering.DirectionalLight;
import com.base.engine.rendering.GLFWWindow;
import com.base.engine.rendering.Light;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.MaterialMap;
import com.base.engine.rendering.PointLight;
import com.base.engine.rendering.SpotLight;
import math.Matrix4f;
import math.Vector3f;

public abstract class GLShader {
	//private static final IntBuffer INT_BUFFER = Util.createIntBuffer(1);
	//private static final FloatBuffer FLOAT_BUFFER = Util.createFloatBuffer(1);
	//TODO: Switch to Stack Buffers
	private static final FloatBuffer VEC3_BUFFER = Util.createFloatBuffer(3);
	private static final FloatBuffer VEC4_BUFFER = Util.createFloatBuffer(4);
	private static final FloatBuffer MAT4_BUFFER = Util.createFloatBuffer(16);
	
	//private static IntBuffer setInt(int v){return setBuffer(v, INT_BUFFER);}
	//private static FloatBuffer setFloat(float v){return setBuffer(v, FLOAT_BUFFER);}
	private static FloatBuffer setVec3(float[] v){return setBuffer(v, VEC3_BUFFER);}
	private static FloatBuffer setVec3(Vector3f v){return setBuffer(v, VEC3_BUFFER);}
	private static FloatBuffer setVec3(float x1, float x2, float x3){return setBuffer(x1, x2, x3, VEC3_BUFFER);}
	private static FloatBuffer setVec4(float[] v){return setBuffer(v, VEC4_BUFFER);}
	private static FloatBuffer setVec4(float x1, float x2, float x3, float x4){return setBuffer(x1, x2, x3, x4, VEC4_BUFFER);}
	private static FloatBuffer setMat4(float[] v){return setBuffer(v, MAT4_BUFFER);}
	private static FloatBuffer setMat4(Matrix4f v){return setBuffer(v, MAT4_BUFFER);}
	
	//private static IntBuffer setBuffer(int v, IntBuffer buffer){buffer.clear(); buffer.put(v); buffer.flip(); return buffer;}
	//private static FloatBuffer setBuffer(float v, FloatBuffer buffer){buffer.clear(); buffer.put(v); buffer.flip(); return buffer;}
	private static FloatBuffer setBuffer(float x1, float x2, float x3, FloatBuffer buffer)
	{buffer.clear(); buffer.put(x1); buffer.put(x2); buffer.put(x3); buffer.flip(); return buffer;}
	private static FloatBuffer setBuffer(float x1, float x2, float x3, float x4, FloatBuffer buffer)
	{buffer.clear(); buffer.put(x1); buffer.put(x2); buffer.put(x3); buffer.put(x4); buffer.flip(); return buffer;}
	private static FloatBuffer setBuffer(float[] v, FloatBuffer buffer){buffer.clear(); buffer.put(v); buffer.flip(); return buffer;}
	private static FloatBuffer setBuffer(Vector3f v, FloatBuffer buffer){buffer.clear(); Util.vectorToBuffer(buffer, v); buffer.flip(); return buffer;}
	private static FloatBuffer setBuffer(Matrix4f v, FloatBuffer buffer){buffer.clear(); Util.matrixToBuffer(buffer, v); buffer.flip(); return buffer;}
	
	public static void bindTexture(int shader, int[] textures){
		for(int i = 0; i < textures.length; i++){
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[i]);
			setUniform(shader, "texture" + i, i);
		}
	}
	
	public static void bindProgram(int program){GL20.glUseProgram(program);}
	public static void deleteProgram(int program){GL20.glDeleteProgram(program);}
	public static void deletePrograms(int[] programs){for(int i = 0; i < programs.length; i++) GL20.glDeleteProgram(programs[i]);}
	
	public static int compileProgram(String filename){
		try{
			String vertex = "", fragment = "";
			String current = "";
			String shader = "";
			File f = new File(filename);
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while(br.ready()) {
				line = br.readLine();
				if(line.length() > 0 && line.charAt(0) == '@') {
					switch(shader) {
					case "@VERTEX": vertex = current; break;
					case "@FRAGMENT": fragment = current; break;
					case "": break;
					default: System.err.println("Unsupported Shader Type: " + shader);
					}
					shader = line; current = "";
				} else current += line + "\n";
			}
			br.close();
			switch(shader) {
			case "@VERTEX": vertex = current; break;
			case "@FRAGMENT": fragment = current; break;
			case "": break;
			default: System.err.println("Unsupported Shader Type: " + shader);
			}
			return compileProgram(vertex, fragment);
//			String vertex = "", fragment = "";
//			InputStream in = Class.class.getResourceAsStream(path + ".vs");
//			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//			String line;
//			while((line = reader.readLine()) != null) vertex += line + "\n";
//			reader.close();
//			in.close();
//			in = Class.class.getResourceAsStream(path + ".fs");
//			reader = new BufferedReader(new InputStreamReader(in));
//			while((line = reader.readLine()) != null) fragment += line + "\n";
//			reader.close();
//			in.close();
//			return compileProgram(vertex, fragment);
		}catch(Exception e){e.printStackTrace();}
		return -1;
	}
	
	public static int compileProgram(String vertex, String fragment){
		
		int program = GL20.glCreateProgram();
		int vertex_shader = compileShader(vertex, GL20.GL_VERTEX_SHADER);
		int frag_shader = compileShader(fragment, GL20.GL_FRAGMENT_SHADER);
		
		GL20.glAttachShader(program, vertex_shader);
		GL20.glAttachShader(program, frag_shader);
		GL20.glLinkProgram(program);
		
		GL20.glGetProgramiv(program, GL20.GL_LINK_STATUS, SUCCESS);
		if(SUCCESS[0] != 1){System.out.println("ERROR::PROGRAM::LINK_FAILED\n" + GL20.glGetProgramInfoLog(program));}
		
		GL20.glDeleteShader(vertex_shader);
		GL20.glDeleteShader(frag_shader);
		
		return program;
	}
	
	private static final int[] SUCCESS = new int[1];
	private static int compileShader(String shader_code, int shader_type){
		int shader = GL20.glCreateShader(shader_type);
		GL20.glShaderSource(shader, shader_code);
		GL20.glCompileShader(shader);
		GL20.glGetShaderiv(shader, GL20.GL_COMPILE_STATUS, SUCCESS);
		if(SUCCESS[0] != 1){
			String shaderName;
			switch(shader_type){
			case GL20.GL_VERTEX_SHADER: shaderName = "Vertex"; break;
			case GL20.GL_FRAGMENT_SHADER: shaderName = "Fragment"; break;
			case GL32.GL_GEOMETRY_SHADER: shaderName = "Geometry"; break;
			default: System.err.println("Shader type unknown!"); return -1;
			}
			System.err.println(shaderName + " Shader Compilation Failed!\n" + GL20.glGetShaderInfoLog(shader));
			System.err.println(shader_code);
		}
		return shader;
	}
	
	
	public static void bindMaterial(int program, MaterialMap mat){
		if(mat.diffuse >= 0){
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.diffuse);
			setUniform(program, "material.diffuse", 0);
			if(mat.specular >= 0){
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.specular);
				setUniform(program, "material.specular", 1);
			} else setUniform(program, "material.specular", 0);
		}
		//setUniform(program, "material.shininess", mat.shininess);
	}
	
	public static void setMaterial(int program, Material mat){
		setUniformVec3(program, "material.ambient", mat.ambient);
		setUniformVec3(program, "material.diffuse", mat.diffuse);
		setUniformVec3(program, "material.specular", mat.specular);
		setUniform(program, "material.shininess", mat.shininess);
		
		if(mat.textures != null)GLShader.bindTexture(program, mat.textures); 
	}
	
	public static void setMaterial(int program, float[] ambient, float[] diffuse, float[] specular, float shininess){
		setUniformVec3(program, "material.ambient", ambient);
		setUniformVec3(program, "material.diffuse", diffuse);
		setUniformVec3(program, "material.specular", specular);
		setUniform(program, "material.shininess", shininess);
	}
	
	public static void setMaterial(int program, float[] material){
		setUniformVec3(program, "material.ambient", material[0], material[1], material[2]);
		setUniformVec3(program, "material.diffuse", material[3], material[4], material[5]);
		setUniformVec3(program, "material.specular", material[6], material[7], material[8]);
		setUniform(program, "material.shininess", material[9]);
	}
	
	public static void setLight(int program, Light light){
		setUniformVec3(program, "light.position", light.pos);
		setUniformVec3(program, "light.ambient", light.ambient);
		setUniformVec3(program, "light.diffuse", light.diffuse);
		setUniformVec3(program, "light.specular", light.specular);
	}
	
	public static void setLight(int program, float[] position, float[] ambient, float[] diffuse, float[] specular){
		setUniformVec3(program, "light.position", position);
		setUniformVec3(program, "light.ambient", ambient);
		setUniformVec3(program, "light.diffuse", diffuse);
		setUniformVec3(program, "light.specular", specular);
	}
	
	public static void setLight(int program, DirectionalLight light){
		setUniformVec3(program, "directionalLight.direction", light.direction);
		
		setUniformVec3(program, "directionalLight.ambient", light.ambient);
		setUniformVec3(program, "directionalLight.diffuse", light.diffuse);
		setUniformVec3(program, "directionalLight.specular", light.specular);
	}
	
	public static void setLight(int program, PointLight[] light){
		for(int i = 0; i < light.length; i++){
			setUniformVec3(program, "pointLight[" + i + "].position", light[i].position);
			
			setUniform(program, "pointLight[" + i + "].constant", light[i].constant);
			setUniform(program, "pointLight[" + i + "].linear", light[i].linear);
			setUniform(program, "pointLight[" + i + "].quadratic", light[i].quadratic);
			setUniform(program, "pointLight[" + i + "].radius", 10.0f);
			
			setUniformVec3(program, "pointLight[" + i + "].ambient", light[i].ambient);
			setUniformVec3(program, "pointLight[" + i + "].diffuse", light[i].diffuse);
			setUniformVec3(program, "pointLight[" + i + "].specular", light[i].specular);
		}
	}
	
	public static void setLight(int program, SpotLight light){
		setUniformVec3(program, "spotlight.position", light.position);
		setUniformVec3(program, "spotlight.direction", light.direction);
		
		setUniform(program, "spotlight.cutoff", light.cutoff);
		setUniform(program, "spotlight.outerCutoff", light.outerCutoff);
		
		setUniformVec3(program, "spotlight.ambient", light.ambient);
		setUniformVec3(program, "spotlight.diffuse", light.diffuse);
		setUniformVec3(program, "spotlight.specular", light.specular);
	}
	
	public static void setUniform(int uniform, int value){GL20.glUniform1i(uniform, value);}
	public static void setUniform(int uniform, float value){GL20.glUniform1f(uniform, value);}
	public static void setUniform(int uniform, boolean value){GL20.glUniform1i(uniform, value ? GL11.GL_TRUE : GL11.GL_FALSE);}
	public static void setUniformVec3(int uniform, float[] value){GL20.glUniform3fv(uniform, setVec3(value));}
	public static void setUniformVec3(int uniform, float x1, float x2, float x3){GL20.glUniform3fv(uniform, setVec3(x1, x2, x3));}
	public static void setUniformVec3(int uniform, FloatBuffer value){GL20.glUniform3fv(uniform, value);}
	public static void setUniformVec4(int uniform, float[] value){GL20.glUniform4fv(uniform, setVec4(value));}
	public static void setUniformVec4(int uniform, float x1, float x2, float x3, float x4){GL20.glUniform3fv(uniform, setVec4(x1, x2, x3, x4));}
	public static void setUniformVec4(int uniform, FloatBuffer value){GL20.glUniform4fv(uniform, value);}
	public static void setUniformMat4(int uniform, float[] value){GL20.glUniformMatrix4fv(uniform, true, setMat4(value));}
	public static void setUniformMat4(int uniform, Matrix4f value){GL20.glUniformMatrix4fv(uniform, true, setMat4(value));}
	public static void setUniformMat4(int uniform, FloatBuffer value){GL20.glUniformMatrix4fv(uniform, true, value);}
	
	public static void setUniform(int program, String uniformName, int value){GL20.glUniform1i(GL20.glGetUniformLocation(program, uniformName), value);}
	public static void setUniform(int program, String uniformName, float value){GL20.glUniform1f(GL20.glGetUniformLocation(program, uniformName), value);}
	public static void setUniform(int program, String uniformName, boolean value){GL20.glUniform1i(GL20.glGetUniformLocation(program, uniformName), value ? GL11.GL_TRUE : GL11.GL_FALSE);}
	public static void setUniformVec3(int program, String uniformName, float[] value){GL20.glUniform3fv(GL20.glGetUniformLocation(program, uniformName), setVec3(value));}
	public static void setUniformVec3(int program, String uniformName, Vector3f value){GL20.glUniform3fv(GL20.glGetUniformLocation(program, uniformName), setVec3(value));}
	public static void setUniformVec3(int program, String uniformName, float x1, float x2, float x3){GL20.glUniform3fv(GL20.glGetUniformLocation(program, uniformName), setVec3(x1, x2, x3));}
	public static void setUniformVec3(int program, String uniformName, FloatBuffer value){GL20.glUniform3fv(GL20.glGetUniformLocation(program, uniformName), value);}
	public static void setUniformVec4(int program, String uniformName, float[] value){GL20.glUniform4fv(GL20.glGetUniformLocation(program, uniformName), setVec4(value));}
	public static void setUniformVec4(int program, String uniformName, float x1, float x2, float x3, float x4){GL20.glUniform4fv(GL20.glGetUniformLocation(program, uniformName), setVec4(x1, x2, x3, x4));}
	public static void setUniformVec4(int program, String uniformName, FloatBuffer value){GL20.glUniform4fv(GL20.glGetUniformLocation(program, uniformName), value);}
	public static void setUniformMat4(int program, String uniformName, float[] value){GL20.glUniformMatrix4fv(GL20.glGetUniformLocation(program, uniformName), true, setMat4(value));}
	public static void setUniformMat4(int program, String uniformName, Matrix4f value){GL20.glUniformMatrix4fv(GL20.glGetUniformLocation(program, uniformName), true, setMat4(value));}
	public static void setUniformMat4(int program, String uniformName, FloatBuffer value){GL20.glUniformMatrix4fv(GL20.glGetUniformLocation(program, uniformName), true, value);}
	
	public static void setUniformVec3(int program, String uniformName, Vector3f[] value)
	{for(int i = 0; i < value.length; i++) GL20.glUniform3fv(GL20.glGetUniformLocation(program, uniformName + "[" + i + "]"), setVec3(value[i]));}
}
