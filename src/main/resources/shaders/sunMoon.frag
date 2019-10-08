#version 400 core

in vec2 tex_frag_in;

out vec4 out_Color[8];

uniform sampler2D colorTexture;

void main(void){
	out_Color[0] = texture(colorTexture, tex_frag_in);
	out_Color[1] = vec4(0, 0, 0, 1);
	out_Color[2] = vec4(0, 0, 0, 1);
	out_Color[3] = vec4(0, 0, 0, 1);
	out_Color[4] = vec4(0, 0, 0, 1);
	out_Color[5] = vec4(0, 0, 0, 1);
	out_Color[6] = vec4(0, 0, 0, 1);
	out_Color[7] = texture(colorTexture, tex_frag_in);
}