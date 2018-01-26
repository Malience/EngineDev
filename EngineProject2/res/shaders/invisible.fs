#version 330 core

in vec2 TexCoord0;

out vec4 FragColor;

uniform sampler2D diffuse;

void main()
{
	vec4 tex = texture(diffuse, TexCoord0);
	if(tex.a < 0.5) discard;
    FragColor = tex;
} 