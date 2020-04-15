#version 400 core

in vec2 tex_frag_in;

out vec4 out_Color;

uniform sampler2D colorTexture;
uniform sampler2D depthTexture;

uniform sampler2D sunTexture;

uniform vec2 size;
uniform vec2 origin;
uniform float intensity;
uniform float brightness;
uniform int quality;
uniform vec3 color;

void main(void){
	vec4 sumColor = vec4(0.0);
	vec2 centeredTex = tex_frag_in +(size * 0.5f -origin);
	
	for(int i = 0; i < quality; i++){
		float scale = 1.0f -intensity * (float(i) / 11.0f);
		sumColor += texture(sunTexture, centeredTex * scale +origin);
	}
	
	vec4 bluredSun = 1.0f * (sumColor / quality) * brightness * vec4(color, 1);
	
	out_Color = (texture(colorTexture, tex_frag_in) +bluredSun);
}