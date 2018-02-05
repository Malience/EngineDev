package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.base.engine.core.util.Util;

public abstract class GLTexture {
	private static byte current_flags = 0b00111101;
	private static int[] sizes = new int[2];
	private static boolean[] alpha = new boolean[1];
	
	public static int createTexture(String filename){return createTexture(filename, current_flags);}
	public static int createTexture(String filename, int filter){
		int texture = glGenTextures();
		
		ByteBuffer buffer = loadTextureFile(filename, sizes, alpha);
		createTexture(texture, sizes[0], sizes[1], alpha[0] ? GL_RGBA : GL_RGB, filter, buffer);
		return texture;
	}
	public static int[] createTextures(String[] filenames){return createTextures(filenames, genTextures(new int[filenames.length]), 0);}
	public static int[] createTextures(String[] filenames, int filter){return createTextures(filenames, genTextures(new int[filenames.length]), filter);}
	public static int[] createTextures(String[] filenames, int[] textures){return createTextures(filenames, genTextures(new int[filenames.length]), 0);}
		public static int[] createTextures(String[] filenames, int[] textures, int filter){
		if(textures.length != filenames.length){System.err.println("filenames and out are different sizes!"); return null;}
		
		for(int i = 0; i < filenames.length; i++){
			ByteBuffer buffer = loadTextureFile(filenames[i], sizes, alpha);
			createTexture(textures[i], sizes[0], sizes[1], alpha[0] ? GL_RGBA : GL_RGB,  filter, buffer);
		}
		return textures;
	}
	
	public static void bindCubeMap(int cubemap){GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubemap);}
	public static void bindTexture(int texture, int uniform){
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GLShader.setUniform(uniform, 0);
	}
	
	public static void bindTextures(int[] textures, int[] uniforms){
		if(textures.length != uniforms.length){System.err.println("uniforms and uniforms are different sizes!"); return;}
		for(int i = 0; i < textures.length; i++){
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[i]);
			GLShader.setUniform(uniforms[i], 0);
		}
	}
	
	public static int createCubeMap(String filename){
		int texture = glGenTextures();
		
		ByteBuffer buffer = loadTextureFile(filename, sizes, alpha);
		loadCubeMap(texture, sizes[0], sizes[1], buffer);
		return texture;
	}
	
	public static int createCubeMap(String[] filenames){
		if(filenames.length != 6) return -1;
		int texture = glGenTextures();
		
		ByteBuffer[] buffers = new ByteBuffer[6];
		for(int i = 0; i < 6; i++) buffers[i] = loadTextureFile(filenames[i], sizes, alpha);
		loadCubeMap(texture, sizes[0], sizes[1], buffers);
		return texture;
	}
	
	private static int loadCubeMap(int cubemap, int width, int height, ByteBuffer buffer){
		glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubemap);
		
		for(int i = 0; i < 6; i++) glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA16, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);  
        
        glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
        return cubemap;
	}
	
	private static int loadCubeMap(int cubemap, int width, int height, ByteBuffer[] buffers){
		glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubemap);
		
		for(int i = 0; i < 6; i++) glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA16, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffers[i]);
        
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);  
        
        glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
        return cubemap;
	}
	
	public static int createTexture(int width, int height){return createTexture(width, height, current_flags);}
	public static int createTexture(int width, int height, byte flags){
		int texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);

        glTexImage2D(GL_TEXTURE_2D, 0, ((flags & 0b1) != 0) ? (((flags & 0b10) != 0) ? GL_RGBA16 : GL_RGBA8) : (((flags & 0b10) != 0) ? GL_RGB16 : GL_RGB8),
        		width, height, 0, ((flags & 0b1) != 0) ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, ((flags & 0b1000) != 0) ? GL_LINEAR : GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, ((flags & 0b100) != 0) ? GL_LINEAR : GL_NEAREST);
        
        glBindTexture(GL_TEXTURE_2D, 0);
        return texture;
	}
	
	public static int createTexture(int texture, int width, int height, int bytes, boolean floating_point, boolean alpha, int filter){
		glBindTexture(GL_TEXTURE_2D, texture);
		
		int internalformat = getInternalFormat(bytes, floating_point, alpha);
		int format = alpha ? GL_RGBA : GL_RGB;
		
        glTexImage2D(GL_TEXTURE_2D, 0, internalformat, width, height, 0, format, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, ((flags & 0b1000) != 0) ? GL_LINEAR : GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, ((flags & 0b100) != 0) ? GL_LINEAR : GL_NEAREST);
        
        glBindTexture(GL_TEXTURE_2D, 0);
        return texture;
	}
	
	private static int createTexture(int texture, int width, int height, int format)
	{return createTexture(texture, width, height, format, format, GL11.GL_UNSIGNED_BYTE, 0, -1, null, (ByteBuffer) null);}
	@SuppressWarnings("unused")
	private static int createTexture(int texture, int width, int height, int format, ByteBuffer data)
	{return createTexture(texture, width, height, format, format, GL11.GL_UNSIGNED_BYTE, 0, -1, null, data);}
	private static int createTexture(int texture, int width, int height, int format, int filter, ByteBuffer data)
	{return createTexture(texture, width, height, format, format, GL11.GL_UNSIGNED_BYTE, filter, -1, null, data);}
	private static int createTexture(int texture, int width, int height, int internalformat, int format)
	{return createTexture(texture, width, height, format, format, GL11.GL_UNSIGNED_BYTE, 0, -1, null, (ByteBuffer) null);}
	private static int createTexture(int texture, int width, int height, int internalformat, int format, int wrap, float[] bordercolor)
	{return createTexture(texture, width, height, format, format, GL11.GL_UNSIGNED_BYTE, 0, wrap, bordercolor, (ByteBuffer) null);}
	private static int createTexture(int texture, int width, int height, int internalformat, int format, int type, int filter, int wrap, float[] bordercolor, ByteBuffer data){
		glBindTexture(GL_TEXTURE_2D, texture);

        glTexImage2D(GL_TEXTURE_2D, 0, internalformat, width, height, 0, format, type, data);
        
        setFiltering(filter);
        if(wrap >= 0){glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrap);glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrap);}
        if(bordercolor != null) glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, bordercolor); 
        
        glBindTexture(GL_TEXTURE_2D, 0);
        return texture;
	}
	private static int createTexture(int texture, int width, int height, int internalformat, int format, int type, int filter, int wrap, float[] bordercolor, FloatBuffer data){
		glBindTexture(GL_TEXTURE_2D, texture);

        glTexImage2D(GL_TEXTURE_2D, 0, internalformat, width, height, 0, format, type, data);
        
        setFiltering(filter);
        if(wrap >= 0){glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrap);glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrap);}
        if(bordercolor != null) glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, bordercolor); 
        
        glBindTexture(GL_TEXTURE_2D, 0);
        return texture;
	}
	
	
	public static int createRedBuffer(int width, int height){return createTexture(GL11.glGenTextures(), width, height, GL_RED, GL_RGB);}
	public static int createColorbuffer(int width, int height){return createTexture(GL11.glGenTextures(), width, height, GL11.GL_RGB);}
	public static int createColorbufferf(int width, int height){return createTexture(GL11.glGenTextures(), width, height, GL30.GL_RGB16F, GL_RGB, GL_FLOAT, 1, -1, null, (ByteBuffer) null);}
	public static int createColorbuffer(int width, int height, boolean alpha){return createTexture(GL11.glGenTextures(), width, height, alpha ? GL11.GL_RGBA : GL11.GL_RGB);}
	public static int createColorbufferf(int width, int height, boolean alpha)
	{return createTexture(GL11.glGenTextures(), width, height, alpha ? GL30.GL_RGBA16F : GL30.GL_RGB16F, alpha ? GL11.GL_RGBA : GL11.GL_RGB, GL_FLOAT, 1, -1, null, (ByteBuffer) null);}
	public static int createDepthbuffer(int width, int height)
	{return createTexture(GL11.glGenTextures(), width, height, GL11.GL_DEPTH_COMPONENT, GL11.GL_DEPTH_COMPONENT, GL13.GL_CLAMP_TO_BORDER, new float[]{1.0f,1.0f,1.0f,1.0f});}
	public static int createStencilbuffer(int width, int height){return createTexture(GL11.glGenTextures(), width, height, GL11.GL_STENCIL_INDEX);}
	
	public static int createRedBuffer(int texture, int width, int height){return createTexture(texture, width, height, GL_RED, GL_RGB);}
	public static int createColorbuffer(int texture, int width, int height){return createTexture(texture, width, height, GL11.GL_RGB);}
	public static int createColorbufferf(int texture, int width, int height){return createTexture(texture, width, height, GL30.GL_RGB16F, GL_RGB, GL_FLOAT, 1, -1, null, (ByteBuffer) null);}
	public static int createColorbuffer(int texture, int width, int height, boolean alpha){return createTexture(texture, width, height, alpha ? GL11.GL_RGBA : GL11.GL_RGB);}
	public static int createColorbufferf(int texture, int width, int height, boolean alpha)
	{return createTexture(texture, width, height, alpha ? GL30.GL_RGBA16F : GL30.GL_RGB16F, alpha ? GL11.GL_RGBA : GL11.GL_RGB, GL_FLOAT, 1, -1, null, (ByteBuffer) null);}
	public static int createDepthbuffer(int texture, int width, int height)
	{return createTexture(texture, width, height, GL11.GL_DEPTH_COMPONENT, GL11.GL_DEPTH_COMPONENT, GL13.GL_CLAMP_TO_BORDER, new float[]{1.0f,1.0f,1.0f,1.0f});}
	public static int createStencilbuffer(int texture, int width, int height){return createTexture(texture, width, height, GL11.GL_STENCIL_INDEX);}
//	public static int createDepthStencil(int width, int height){return createTexture(width, height, GL30.GL_DEPTH24_STENCIL8, GL30.GL_DEPTH_STENCIL, GL30.GL_UNSIGNED_INT_24_8);}
	
	public static int createNoiseTexture(int width, int height){return createNoiseTexture(GL11.glGenTextures(), width, height);}
	public static int createNoiseTexture(int texture, int width, int height)
	{
		int size = width * height;
		FloatBuffer buffer = Util.createFloatBuffer(size * 3);
		
		for(int i = 0; i < size; i++){
			buffer.put(((float)Math.random()) * 2.0f - 1.0f);
			buffer.put(((float)Math.random()) * 2.0f - 1.0f);
			buffer.put(0.0f);
		}
		buffer.flip();
		createTexture(texture, width, height, GL30.GL_RGB16F, GL_RGB, GL_FLOAT, 0, GL_REPEAT, null, buffer);
		Util.free(buffer);
		return texture;
	}
	
	public static int getInternalFormat(int bytes, boolean floating_point, boolean alpha){
		switch(bytes){
		case 1: return GL11.GL_RED;
		case 8: return alpha ? GL11.GL_RGBA8: GL11.GL_RGB8;
		case 16: return floating_point ? (alpha ? GL30.GL_RGBA16F: GL30.GL_RGB16F) : (alpha ? GL11.GL_RGBA16: GL11.GL_RGB16);
		case 32: return alpha ? GL30.GL_RGBA32F: GL30.GL_RGB32F;
		default: return -1;
		}
	}
	
	public static void setFiltering(int texture, int filter){
		glBindTexture(GL_TEXTURE_2D, texture);
		setFiltering(filter);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	private static void setFiltering(int filter) {
		if(filter > 2 && GLContext.context.anisotropic_filtering > 0) {
			GL30.glGenerateMipmap(GL_TEXTURE_2D);
        	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        	if(filter > GLContext.context.anisotropic_filtering) 
        		GL11.glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, GLContext.context.anisotropic_filtering);
        	else
        		GL11.glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, filter);
        	 
		} else if(filter == 1) {	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        						glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        } else if(filter >= 2) {
			GL30.glGenerateMipmap(GL_TEXTURE_2D);
        	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		} else {
        	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		}
	}
	
	public static int genTextures(){return GL11.glGenTextures();}
	public static int[] genTextures(int num){int[] textures = new int[num]; GL11.glGenTextures(textures); return textures;}
	public static int[] genTextures(int[] textures){GL11.glGenTextures(textures); return textures;}
	
	public static void deleteTexture(int texture){GL11.glDeleteTextures(texture);}
	public static void deleteTextures(int[] textures){GL11.glDeleteTextures(textures);}
	
	private static ByteBuffer loadTextureFile(String filename, int[] sizes, boolean[] alpha) {
		try {
			BufferedImage image = null;
			File file = new File(filename);
			if(file.exists()) image = ImageIO.read(file);
			else {
				System.out.println("All hope has failed");
				try {
					InputStream in = Class.class.getResourceAsStream("/textures/" + filename);
					image = ImageIO.read(in);
					in.close();
				} catch(Exception e) {
					System.err.println("File not found: " + filename);
		        	e.printStackTrace();
		        	System.exit(1);
				}
			}
			int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
	
	        ByteBuffer buffer = Util.createByteBuffer(image.getWidth() * image.getHeight() * 4);
	        alpha[0] = image.getColorModel().hasAlpha();
	
	        for(int y = 0; y < image.getHeight(); y++)
	        {
	            for(int x = 0; x < image.getWidth(); x++)
	            {
	                int pixel = pixels[y * image.getWidth() + x];
	                
	                buffer.put((byte) ((pixel >> 16) & 0xFF));
	                buffer.put((byte) ((pixel >> 8) & 0xFF));
	                buffer.put((byte) ((pixel) & 0xFF));
	                if(alpha[0])
	                	buffer.put((byte) ((pixel >> 24) & 0xFF));
	                //else
	                //	buffer.put((byte)(0xFF));
	            }
	        }
	        buffer.flip();
	        sizes[0] = image.getWidth();
	        sizes[1] = image.getHeight();
	        return buffer;
		}
        catch(Exception e)
        {
        	System.err.println("Invalid File: " + filename);
        	e.printStackTrace();
        	System.exit(1);
        }
		return null;
	}
}
