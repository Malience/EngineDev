#version 330 core

in vec3 FragPos;
in vec3 Normal;

out vec4 color;

uniform vec3 lightColor;
uniform vec3 lightPos;
uniform vec3 viewPos;

uniform vec3 ambient;
uniform vec3 diffuse;
uniform vec3 specular;
uniform float shininess;

void main()
{
	// ambient
    vec3 Ambient = lightColor * ambient;
  	
    // diffuse 
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 Diffuse = lightColor * (diff * diffuse);
    
    // specular
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);  
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
    vec3 Specular = lightColor * (spec * specular);  
        
    vec3 result = Ambient + Diffuse + Specular;
    color = vec4(result, 1.0);
} 