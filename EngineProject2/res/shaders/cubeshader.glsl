@VERTEX
#version 330 core
  
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoord0;

out vec2 TexCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    gl_Position = projection * view * model * vec4(position, 1.0f);
    TexCoord = vec2(texCoord0.x, 1.0f - texCoord0.y);
}

@FRAGMENT
#version 330 core

in vec3 ourColor;
in vec2 TexCoord;

out vec4 color;

uniform sampler2D texture0;
uniform sampler2D texture1;

void main()
{
    color = mix(texture(texture0, TexCoord), texture(texture1, TexCoord), 0.2);
} 