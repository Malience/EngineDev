#version 330 core
  
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoord0;

out vec2 TexCoord0;

uniform mat4 model;
uniform mat4 lightView;

void main()
{
    gl_Position = lightView * model * vec4(position, 1.0f);
    TexCoord0 = vec2(texCoord0.x, texCoord0.y);
}