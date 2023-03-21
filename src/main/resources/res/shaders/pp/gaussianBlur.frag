#version 400 core

in vec2 tex_frag_in;

out vec4 out_Color;

uniform sampler2D colorTexture;
uniform sampler2D depthTexture;

uniform float directions;
uniform float quality;
uniform float size;
uniform vec2 resolution;

const float Pi = 6.28318530718; // Pi*2

void main(void){
    vec2 radius = size / resolution.xy;
    vec4 color = texture(colorTexture, tex_frag_in);
    
    // Blur calculations
    for(float d = 0.0; d < Pi; d += Pi / directions){
		for(float i = 1.0 / quality; i <= 1.0; i += 1.0 / quality){
			color += texture(colorTexture, tex_frag_in + vec2(cos(d), sin(d)) * radius * i);		
        }
    }
    
    // Output to screen
    color /= quality * directions - 15.0;
    out_Color = color;
}
