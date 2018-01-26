#version 330 core
layout (location = 2) out vec4 gAlbedoSpecOut;

in vec2 TexCoord0;

uniform vec3 viewPos;
uniform vec3 skycolor;
uniform sampler2D gPosition;
uniform sampler2D gAlbedoSpec;

const float density = 0.007;
const float gradient = 1.5;

void main()
{
	vec3 FragPos = texture(gPosition, TexCoord0).rgb;
    vec4 AlbedoSpec = texture(gAlbedoSpec, TexCoord0);
    
    float distance = length(FragPos - viewPos);
    float visibility = exp(-pow(distance * density, gradient));
    visibility = clamp(visibility, 1.0, 0.0);
    
    float spec = max(AlbedoSpec.w - visibility, 0.0);
    vec3 out_color = mix(skycolor, AlbedoSpec.xyz, visibility);
    
    gAlbedoSpecOut.rgb = out_color;
    gAlbedoSpecOut.w = spec;
} 