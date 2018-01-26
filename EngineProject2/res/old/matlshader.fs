#version 330 core

in vec2 TexCoord0;
in vec3 Normal;
in vec3 FragPos;

out vec4 FragColor;

struct Material {
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	float shininess;
};

struct Light {
	vec3 position;
	
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};


uniform sampler2D texture0;
uniform sampler2D texture1;
uniform vec3 viewPos;
uniform Material material;
uniform Light light;

void main()
{
	//ambient
	vec3 ambient = material.ambient * light.ambient;


	//diffuse
	vec3 norm = normalize(Normal);
	vec3 lightDir = normalize(light.position - FragPos);
	float diff = max(dot(norm, lightDir), 0.0);
	vec3 diffuse = (diff * material.diffuse) * light.diffuse;
	
	
	//specular
	vec3 viewDir = normalize(viewPos - FragPos);
	vec3 reflectDir = reflect(-lightDir, norm);
	
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
	vec3 specular = (spec *material.specular) * light.specular;  
	

    FragColor = vec4((ambient + diffuse + specular), 1.0f) * mix(texture(texture0, TexCoord0), texture(texture1, TexCoord0), 0.2);
} 