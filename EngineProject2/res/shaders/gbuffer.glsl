@VERTEX
#version 330 core
  
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
//layout (location = 4) in vec3 tangent;//Switch with 2 for normal mapping
//layout (location = 3) in vec3 bitangent;
layout (location = 2) in vec2 texCoord0;

out vec3 FragPos;
out vec3 Normal;
out vec2 TexCoord0;
//out mat3 TBN;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec4 clip_plane;

void main()
{
	//mat4 viewModel = view * model;
	vec4 worldPos = model * vec4(position, 1.0f);
	
	gl_ClipDistance[0] = dot(worldPos, clip_plane);
    
    //vec3 T = normalize(vec3(viewModel * vec4(tangent, 0.0)));
    //vec3 B = normalize(vec3(viewModel * vec4(bitangent, 0.0)));
    //vec3 N = normalize(vec3(viewModel * vec4(normal, 0.0)));
    
    //TBN = mat3(T,B,N);
    
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

uniform sampler2D diffuse0;
uniform sampler2D specular0;

void main()
{
    // store the fragment position vector in the first gbuffer texture
    gPositionDepth = FragPos;
    
    // also store the per-fragment normals into the gbuffer
    
    gNormal = normalize(Normal);
    
    // and the diffuse per-fragment color
    gAlbedoSpec.rgb = texture(diffuse0, TexCoord0).rgb;
    
    // store specular intensity in gAlbedoSpec's alpha component
    gAlbedoSpec.a = texture(specular0, TexCoord0).r;
} 