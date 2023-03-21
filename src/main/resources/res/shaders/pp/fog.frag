#version 400 core

in vec2 tex_frag_in;

out vec4 out_Color;

uniform sampler2D colorTexture;
uniform sampler2D depthTexture;

uniform sampler2D strengthTexture;
uniform sampler2D blendingTexture;

uniform vec2 area;
uniform vec3 color;

uniform float blending;

uniform vec2 cameraPlanes;

float LinearizeDepth(){
    float depth = texture2D(depthTexture, tex_frag_in).r;
    return (2.0 * cameraPlanes.x) / (cameraPlanes.y + cameraPlanes.x - depth * (cameraPlanes.y - cameraPlanes.x));
}

void main(void){
	float strength = texture(strengthTexture, tex_frag_in).g;
	vec4 textureColor = texture(colorTexture, tex_frag_in);

	vec3 blendColor = color;
	if(blending == 1.0f){
		blendColor = texture(blendingTexture, tex_frag_in).rgb;
	}
	
	float depth = LinearizeDepth();
	float visibility = exp(-pow((depth * area.x), area.y));
	vec4 finalFog = mix(vec4(blendColor, 1.0), textureColor, visibility);
	
	out_Color = mix(textureColor, finalFog, strength);
}