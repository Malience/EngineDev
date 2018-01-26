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

layout (location = 0) out vec4 FragColor;

in vec2 TexCoord0;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;

uniform sampler2D ssao;
uniform sampler2D shadowMap;

struct DirectionalLight {
	vec3 direction;
	
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};

uniform vec3 viewPos;
uniform DirectionalLight directionalLight;
uniform int ssaoEnabled = 1;
uniform int shadowsEnabled = 1;
uniform int shadowSamples;

bool SSAO_ENABLED = ssaoEnabled == 1;
bool SHADOWS_ENABLED = shadowsEnabled == 1;

vec3 CalcDirectionalLight(DirectionalLight light, vec3 normal, vec3 viewDir, float AmbientOcclusion, vec3 Albedo, float Specular) {
	vec3 lightDir = normalize(-light.direction);

	//ambient
	vec3 ambient = light.ambient * Albedo;
	if(SSAO_ENABLED) ambient *= AmbientOcclusion;

	//diffuse
	float diff = max(dot(normal, lightDir), 0.0);
	vec3 diffuse =  light.diffuse * diff * Albedo;
	
	//specular
	vec3 halfwayDir = normalize(lightDir + viewDir);
	float spec = pow(max(dot(normal, halfwayDir), 0.0), 32);
	vec3 specular = light.specular * spec * Specular;
	
	return ambient + diffuse + specular;
}

void main()
{
	// retrieve data from G-buffer
    vec3 FragPos = texture(gPosition, TexCoord0).rgb;
    vec3 Normal = texture(gNormal, TexCoord0).rgb;
    vec3 Albedo = texture(gAlbedoSpec, TexCoord0).rgb;
    float Specular = texture(gAlbedoSpec, TexCoord0).a;
	vec3 norm = normalize(Normal);
	vec3 viewDir = normalize(viewPos - FragPos);
	float AmbientOcclusion = texture(ssao, TexCoord0).r;
	
	vec3 output = CalcDirectionalLight(directionalLight, norm, viewDir, AmbientOcclusion, Albedo, Specular);
	
    FragColor = vec4(output, 1.0f);
} 