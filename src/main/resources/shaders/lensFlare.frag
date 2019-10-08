#version 400 core

in vec2 tex_frag_in;

out vec4 out_Color;

uniform sampler2D lensFlareTexture;

void main(void){
	out_Color = texture(lensFlareTexture, tex_frag_in);
}