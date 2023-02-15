#version 400 core

in vec2 tex_frag_in;

out vec4 out_Color;

uniform sampler2D colorTexture;
uniform sampler2D depthTexture;

uniform sampler2D strengthTexture;

uniform float directions;
uniform float quality;
uniform float size;
uniform vec2 resolution;
uniform vec2 area;
uniform vec2 cameraPlanes;

const float Pi = 6.28318530718; // Pi*2

float LinearizeDepth(){
    float depth = texture2D(depthTexture, tex_frag_in).r;
    return (2.0 * cameraPlanes.x) / (cameraPlanes.y + cameraPlanes.x - depth * (cameraPlanes.y - cameraPlanes.x));
}

vec4 blurPixel(vec4 clearPixel){
    
    vec2 radius = size / resolution.xy;
    vec4 color = clearPixel;
    
    // Blur calculations
    for(float d = 0.0; d < Pi; d += Pi / directions){
		for(float i = 1.0 / quality; i <= 1.0; i += 1.0 / quality){
			color += texture(colorTexture, tex_frag_in + vec2(cos(d), sin(d)) * radius * i);		
        }
    }
    
    // Output to screen
    color /= quality * directions - 15.0;
    return color;
}

void main(void){

    vec4 clearPixel = texture(colorTexture, tex_frag_in);
    vec4 bluredPixel = blurPixel(clearPixel);

	float strength = texture(strengthTexture, tex_frag_in).g;
	float depth = LinearizeDepth();
	float visibility = exp(-pow((depth * area.x), area.y));
	vec4 finalBlur = mix(bluredPixel, clearPixel, visibility);

	out_Color = mix(clearPixel, finalBlur, strength);
}