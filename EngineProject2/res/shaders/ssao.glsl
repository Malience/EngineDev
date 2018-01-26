@VERTEX
#version 330 core
  
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord0;

out vec2 TexCoord0;

void main()
{
    gl_Position = vec4(position, 1.0f);
    TexCoord0 = texCoord0;
}

@FRAGMENT
#version 330 core

in vec2 TexCoord0;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D texNoise;

out float FragColor;

int kernelSize = 64;
float radius = 0.5;
float bias = 0.025;

uniform vec3 samples[64];
uniform mat4 projection;
uniform mat4 view;

const vec2 noiseScale = vec2(800.0/4.0, 600.0/4.0);

void main()
{
	vec3 fragPos = (view * vec4(texture(gPosition, TexCoord0).xyz, 1.0)).xyz;
	vec3 normal    = normalize(texture(gNormal, TexCoord0).rgb);
	normal = normalize(normal * 2.0 - 1.0);
	normal = normalize(transpose(inverse(mat3(view))) * normal);
	vec3 randomVec = normalize(texture(texNoise, TexCoord0 * noiseScale).xyz);
	
	vec3 tangent   = normalize(randomVec - normal * dot(randomVec, normal));
	vec3 bitangent = normalize(cross(normal, tangent));
	mat3 TBN       = mat3(tangent, bitangent, normal);
	
	float occlusion = 0.0;
	for(int i = 0; i < kernelSize; ++i)
	{
	    // get sample position
	    vec3 sample = TBN * samples[i]; // From tangent to view-space
	    sample = fragPos + sample * radius; 
	    
	    vec4 offset = vec4(sample, 1.0);
		offset      = projection * offset;    // from view to clip-space
		offset.xyz /= offset.w;               // perspective divide
		offset.xyz  = offset.xyz * 0.5 + 0.5; // transform to range 0.0 - 1.0
		
		float sampleDepth = (view * vec4(texture(gPosition, offset.xy).xyz, 1.0)).z;
		float rangeCheck = smoothstep(0.0, 1.0, radius / abs(fragPos.z - sampleDepth));
		occlusion       += (sampleDepth >= sample.z + bias ? 1.0 : 0.0) * rangeCheck;
	}  
	
	occlusion = 1.0 - (occlusion / kernelSize);
	FragColor = pow(occlusion,8);  
} 