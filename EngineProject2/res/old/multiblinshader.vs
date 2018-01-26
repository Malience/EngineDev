#version 330 core
  
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoord0;

out vec2 TexCoord0;
out vec3 Normal;
out vec3 FragPos;


uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
	vec4 worldPos = model * vec4(position, 1.0f);
    gl_Position = projection * view * worldPos;
    
    
    TexCoord0 = texCoord0;
    Normal = mat3(transpose(inverse(model))) * normal;
    FragPos = vec3(worldPos);
}