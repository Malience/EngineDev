#version 330 core

in vec2 TexCoord0;

uniform sampler2D texture0;

out vec4 FragColor;

void main()
{
    FragColor = vec4(texture(texture0, TexCoord0).rgb, 1.0f);
} 