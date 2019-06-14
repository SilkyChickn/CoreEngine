#version 400 core

in vec2 tex_frag_in;

out vec4 out_Color;

uniform sampler2D colorTexture;
uniform sampler2D depthTexture;

uniform vec2 size;
uniform vec2 origin;
uniform float intensity;
uniform float brightness;
uniform int quality;

void main(void){
	vec4 sumColor = vec4(0.0);
	vec2 centeredTex = tex_frag_in +(size * 0.5f -origin);
	
	for(int i = 0; i < quality; i++){
		float scale = 1.0f -intensity * (float(i) / 11.0f);
		sumColor += texture(colorTexture, centeredTex * scale +origin);
	}
	
	out_Color = 1.0f * sumColor / quality * brightness;
}