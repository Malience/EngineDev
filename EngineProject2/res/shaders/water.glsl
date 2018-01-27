@VERTEX
#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoord0;

out vec4 TexCoord0;
out vec2 TexCoord1;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

const float tiling = 6;

void main()
{
	//TexCoord0 = ;
	gl_Position = projection * view * model * vec4(position.x, 0.0, position.y, 1.0);
	TexCoord0 = gl_Position;
	TexCoord1 = vec2(position.x / 2.0 + 0.5, position.y / 2.0 + 0.5) * tiling;
}

@FRAGMENT
#version 330 core

in vec4 TexCoord0;
in vec2 TexCoord1;

out vec4 FragColor;

uniform sampler2D reflection;
uniform sampler2D refraction;
uniform sampler2D dudv;
uniform float moveFactor;

const float strength = 0.02;

void main()
{
	vec2 ndc = (TexCoord0.xy / TexCoord0.w) / 2.0 + 0.5;
	
	vec2 dist1 = (texture(dudv, vec2(TexCoord1.x + moveFactor, TexCoord1.y)).rg * 2.0 - 1.0) * strength;
	vec2 dist2 = (texture(dudv, vec2(-TexCoord1.x + moveFactor, TexCoord1.y + moveFactor)).rg * 2.0 - 1.0) * strength;
	vec2 dist = dist1 + dist2;
	
	vec2 reflectCoord = vec2(ndc.x, -ndc.y) + dist;
	reflectCoord.x = clamp(reflectCoord.x, 0.001, 0.999);
	reflectCoord.y = clamp(reflectCoord.y, -0.999, -0.001);
	vec2 refractCoord = clamp(vec2(ndc.x, ndc.y) + dist, 0.001, 0.999);
	
	vec4 reflect = texture(reflection, reflectCoord);
	vec4 refract = texture(refraction, refractCoord);
	
	
	
	FragColor = mix(reflect, refract, 0.5);
}
