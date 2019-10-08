#version 400 core

in vec2 tex_frag_in;

out vec4 out_Color;

uniform sampler2D colorTexture;
uniform sampler2D depthTexture;

uniform sampler2D strengthTexture;

uniform vec2 area;
uniform vec3 color;

const float zNear = 0.01f;
const float zFar  = 2000.0f;

float LinearizeDepth(){
    float depth = texture2D(depthTexture, tex_frag_in).r;
    return (2.0 * zNear) / (zFar + zNear - depth * (zFar - zNear));
}

void main(void){
	float strength = texture(strengthTexture, tex_frag_in).g;
	vec4 textureColor = texture(colorTexture, tex_frag_in);
	
	float depth = LinearizeDepth();
	float visibility = exp(-pow((depth * area.x), area.y));
	vec4 finalFog = mix(vec4(color, 1.0), textureColor, visibility);
	
	out_Color = mix(textureColor, finalFog, strength);
}