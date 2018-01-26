@VERTEX
#version 330 core
  
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoord0;

out vec3 FragPos;
out vec3 Normal;
out vec2 TexCoord0;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
	vec4 worldPos = model * vec4(position, 1.0f);
    FragPos = worldPos.xyz;
    Normal = transpose(inverse(mat3(model))) * normal;
    TexCoord0 = texCoord0;
    gl_Position = projection * view * worldPos;
}

@FRAGMENT
#version 330 core
layout (location = 0) out vec3 gPositionDepth;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;

in vec3 FragPos;
in vec3 Normal;
in vec2 TexCoord0;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

void main()
{
	vec3 blendMapColor = texture(blendMap, TexCoord0).rgb;
	
	float backTexture = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
	vec2 tiledCoord0 = TexCoord0 * 40.0;
	vec3 backgroundTextureColor = texture(backgroundTexture, tiledCoord0).rgb * backTexture;
	vec3 rTextureColor = texture(rTexture, tiledCoord0).rgb * blendMapColor.r;
	vec3 gTextureColor = texture(gTexture, tiledCoord0).rgb * blendMapColor.g;
	vec3 bTextureColor = texture(bTexture, tiledCoord0).rgb * blendMapColor.b;

    // store the fragment position vector in the first gbuffer texture
    gPositionDepth = FragPos;
    
    // also store the per-fragment normals into the gbuffer
    
    gNormal = normalize(Normal);
    
    // and the diffuse per-fragment color
    gAlbedoSpec.rgb = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;
    
    // store specular intensity in gAlbedoSpec's alpha component
    gAlbedoSpec.a = 0.1;
} 