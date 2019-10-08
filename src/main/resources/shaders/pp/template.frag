#version 400 core

in vec2 tex_frag_in;

out vec4 out_Color;

uniform sampler2D colorTexture;
uniform sampler2D depthTexture;

void main(void){
	out_Color = texture(colorTexture, tex_frag_in);
}