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
out vec4 FragColor;
  
in vec2 TexCoord0;

uniform sampler2D scene;
uniform sampler2D bloomBlur;
uniform float exposure;
uniform float gamma;
uniform int bloom;

void main()
{   
	vec3 hdrColor = texture(scene, TexCoord0).rgb;  
	if(bloom == 1){          
	    vec3 bloomColor = texture(bloomBlur, TexCoord0).rgb;
	    hdrColor += bloomColor; // additive blending
	    
    }
    // tone mapping
    vec3 result = vec3(1.0) - exp(-hdrColor * exposure);
    // also gamma correct while we're at it       
    result = pow(result, vec3(1.0 / gamma));
    FragColor = vec4(result, 1.0);
} 