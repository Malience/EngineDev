@VERTEX
#version 330 core
  
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord0;

out vec2 TexCoord0;

void main()
{
    gl_Position = vec4(position, 1.0f);
    TexCoord0 = vec2(texCoord0.x, texCoord0.y);
}

@FRAGMENT
#version 330 core

in vec2 TexCoord0;

uniform sampler2D texture0;
//uniform mat4 view;
//uniform int useView;

out vec4 FragColor;

void main()
{
    //if(useView == 1) FragColor = view * vec4(texture(texture0, TexCoord0).rgb, 1.0f);
    FragColor = vec4(texture(texture0, TexCoord0).rgb, 1.0f);
} 