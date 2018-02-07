package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public abstract class GLRendering {
	public static void renderMesh(int mesh, int indices, int shader, int[] textures){if(textures != null)GLShader.bindTexture(shader, textures);renderMesh(mesh, indices);}
	public static void renderMesh(int mesh, int indices){GL30.glBindVertexArray(mesh);GL11.glDrawElements(GL11.GL_TRIANGLES, indices, GL11.GL_UNSIGNED_INT, 0);}
	
	static int quadVAO = 0;
	static int quadVBO;
	public static void renderQuad()
	{
	    if (quadVAO == 0)
	    {
	        float quadVertices[] = {
	            // positions        // texture Coords
	            -1.0f,  1.0f, 0.0f, 0.0f, 1.0f,
	            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
	             1.0f,  1.0f, 0.0f, 1.0f, 1.0f,
	             1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
	        };
	        // setup plane VAO
	        quadVAO = GL30.glGenVertexArrays();
	        quadVBO = GL15.glGenBuffers();
	        GL30.glBindVertexArray(quadVAO);
	        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, quadVBO);
	        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, quadVertices, GL15.GL_STATIC_DRAW);
	        GL20.glEnableVertexAttribArray(0);
	        GL20.glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0);
	        GL20.glEnableVertexAttribArray(1);
	        GL20.glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 12);
	    }
	    GL30.glBindVertexArray(quadVAO);
	    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
	    GL30.glBindVertexArray(0);
	}
}
