#version 330 core
  
layout (location = 0) in vec3 position;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
	mat4 scale = mat4(mat3(1.1, 0,0, 0,1.1,0, 0,0,1.1));
    gl_Position = projection * view * model * scale * vec4(position, 1.0f);
}