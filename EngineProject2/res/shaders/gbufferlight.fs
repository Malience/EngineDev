#version 330 core

layout (location = 0) out vec4 FragColor;
layout (location = 1) out vec4 BrightColor;

in vec2 TexCoord0;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;

struct DirectionalLight {
	vec3 direction;
	
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};

struct PointLight {
	vec3 position;
	
	float constant;
	float linear;
	float quadratic;
	float radius;
	
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};

uniform vec3 viewPos;
uniform DirectionalLight directionalLight;
uniform PointLight[10] pointLight;
uniform sampler2D shadowMap;
uniform sampler2D ssao;
uniform int shadowSamples;
uniform int lights;
uniform mat4 lightSpaceMatrix;
uniform int usessao;
uniform int useshadows;

float ShadowCalculation(vec4 fragPosLightSpace, vec3 Normal, vec3 lightDir){
	vec3 projCoord = fragPosLightSpace.xyz / fragPosLightSpace.w;
	
	projCoord = projCoord * 0.5 + 0.5;
	if(projCoord.z > 1.0) return 0.0;
	
	float closestDepth = texture(shadowMap, projCoord.xy).r;
	float currentDepth = projCoord.z;
	
	float bias = max(0.05 * (1.0 - dot(Normal, lightDir)), 0.005);
	
	if(shadowSamples > 0){
		float shadow = 0.0;
		vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
		for(int x = -shadowSamples; x <= shadowSamples; ++x)
		{
	    	for(int y = -shadowSamples; y <= shadowSamples; ++y)
	    	{
	        	float pcfDepth = texture(shadowMap, projCoord.xy + vec2(x, y) * texelSize).r; 
	        	shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;        
	    	}    
		}
		
		shadow /= (shadowSamples * (shadowSamples + 1)) * 4 + 1;
		
		return shadow;
	}
	
	float shadow = currentDepth - bias > closestDepth ? 1.0 : 0.0;
	return shadow;
}
vec3 CalcDirectionalLight(DirectionalLight light, vec3 normal, vec3 viewDir, float AmbientOcclusion, vec4 FragPosLightSpace, vec3 Albedo, float Specular) {
	vec3 lightDir = normalize(-light.direction);

	//ambient
	vec3 ambient = light.ambient * Albedo;
	if(usessao == 1) ambient *= AmbientOcclusion;

	//diffuse
	float diff = max(dot(normal, lightDir), 0.0);
	vec3 diffuse =  light.diffuse * diff * Albedo;
	
	//specular
	vec3 halfwayDir = normalize(lightDir + viewDir);
	float spec = pow(max(dot(normal, halfwayDir), 0.0), 32);
	vec3 specular = light.specular * spec * Specular;
	
	float shadow = useshadows == 1 ? ShadowCalculation(FragPosLightSpace, normal, lightDir) : 0.0;
	return ambient + (1.0 - shadow) * (diffuse + specular);
}

vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir, float AmbientOcclusion, vec4 FragPosLightSpace, vec3 Albedo, float Specular) {
	vec3 lightDir = normalize(light.position - fragPos);
	//attenuation
	float distance = length(light.position - fragPos);
	float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

	//ambient
	vec3 ambient = attenuation * light.ambient * Albedo;
	if(usessao == 1) ambient *= AmbientOcclusion;

	//diffuse
	float diff = max(dot(normal, lightDir), 0.0);
	vec3 diffuse =  attenuation * light.diffuse * diff * Albedo;
	
	//specular
	vec3 halfwayDir = normalize(lightDir + viewDir);
	float spec = pow(max(dot(normal, halfwayDir), 0.0), 32);
	vec3 specular = attenuation * light.specular * spec * Specular;
	
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
	vec3 viewDir = normalize(viewPos-FragPos);
	vec4 FragPosLightSpace = lightSpaceMatrix * vec4(FragPos, 1.0);
	float AmbientOcclusion = texture(ssao, TexCoord0).r;
	
	
	vec3 output = CalcDirectionalLight(directionalLight, norm, viewDir, AmbientOcclusion, FragPosLightSpace, Albedo, Specular);
	
	for(int i = 0; i < lights; i++){
		float distance = length(pointLight[i].position - FragPos);
		if(distance < pointLight[i].radius)
        	output += CalcPointLight(pointLight[i], norm, FragPos, viewDir, AmbientOcclusion, FragPosLightSpace, Albedo, Specular);
    }
        
   	//output += CalcSpotLight(spotLight, norm, FragPos, viewDir);
    
    //vec3 hdrmapped = vec3(1.0) - exp(-output * exposure);
	//hdrmapped = pow(hdrmapped, vec3(1.0 / gamma));
	
    
    float brightness = dot(output, vec3(0.2126, 0.7152, 0.0722));
    if(brightness > 1.0) BrightColor = vec4(output, 1.0);
    
    FragColor = vec4(output, 1.0f);
} 