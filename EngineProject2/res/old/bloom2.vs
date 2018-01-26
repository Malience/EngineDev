#version 330 core
  
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord0;

out vec2 TexCoord0;

void main()
{
    gl_Position = vec4(position, 1.0f);
    TexCoord0 = vec2(texCoord0.x, texCoord0.y);
}