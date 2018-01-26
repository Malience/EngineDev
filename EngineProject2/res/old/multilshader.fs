#version 330 core

in vec2 TexCoord0;
in vec3 Normal;
in vec3 FragPos;

out vec4 FragColor;

struct Material {
	sampler2D diffuse;
	sampler2D specular;
	float shininess;
};

struct Light {
	vec3 position;
	
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};

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
	
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};

struct SpotLight {
	vec3 position;
	vec3 direction;
	float cutoff;
	float outerCutoff;
	
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};

uniform vec3 viewPos;
uniform Material material;
uniform Light light;
uniform DirectionalLight directionalLight;
uniform PointLight[10] pointLight;
uniform SpotLight spotLight;
uniform int lights;

vec3 CalcDirectionalLight(DirectionalLight light, vec3 normal, vec3 viewDir) {
	vec3 lightDir = normalize(-light.direction);

	//ambient
	vec3 ambient = light.ambient * vec3(texture(material.diffuse, TexCoord0));

	//diffuse
	float diff = max(dot(normal, lightDir), 0.0);
	vec3 diffuse =  light.diffuse * diff * vec3(texture(material.diffuse, TexCoord0));
	
	//specular
	vec3 reflectDir = reflect(-lightDir, normal);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
	vec3 specular = light.specular * spec * vec3(texture(material.specular, TexCoord0));
	
	return ambient + diffuse + specular;
}

vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir) {
	vec3 lightDir = normalize(light.position - fragPos);
	//attenuation
	float distance = length(light.position - fragPos);
	float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

	//ambient
	vec3 ambient = attenuation * light.ambient * vec3(texture(material.diffuse, TexCoord0));

	//diffuse
	float diff = max(dot(normal, lightDir), 0.0);
	vec3 diffuse =  attenuation * light.diffuse * diff * vec3(texture(material.diffuse, TexCoord0));
	
	//specular
	vec3 reflectDir = reflect(-lightDir, normal);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
	vec3 specular = attenuation * light.specular * spec * vec3(texture(material.specular, TexCoord0));
	
	return ambient + diffuse + specular;
}

vec3 CalcSpotLight(SpotLight light, vec3 normal, vec3 fragPos, vec3 viewDir) {
	vec3 lightDir = normalize(light.position - fragPos);
	float theta = dot(lightDir, normalize(-light.direction));
	
	if(theta > light.cutoff){
		//soft edges
		float epsilon = light.cutoff - light.outerCutoff;
		float intensity = clamp((theta - light.outerCutoff) / epsilon, 0.0, 1.0); 
	
		//ambient
		vec3 ambient = intensity * light.ambient * vec3(texture(material.diffuse, TexCoord0));
	
		//diffuse
		float diff = max(dot(normal, lightDir), 0.0);
		vec3 diffuse =  intensity * light.diffuse * diff * vec3(texture(material.diffuse, TexCoord0));
		
		//specular
		vec3 reflectDir = reflect(-lightDir, normal);
		float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
		vec3 specular = intensity * light.specular * spec * vec3(texture(material.specular, TexCoord0));
		
		return ambient + diffuse + specular;
	} else return light.ambient * vec3(texture(material.diffuse, TexCoord0));
}


void main()
{
	vec3 norm = normalize(Normal);
	vec3 viewDir = normalize(viewPos - FragPos);	
	
	vec3 output = CalcDirectionalLight(directionalLight, norm, viewDir);
	
	for(int i = 0; i < lights; i++)
        output += CalcPointLight(pointLight[i], norm, FragPos, viewDir);
        
   	output += CalcSpotLight(spotLight, norm, FragPos, viewDir);
    
    FragColor = vec4(output, 1.0f);
} 