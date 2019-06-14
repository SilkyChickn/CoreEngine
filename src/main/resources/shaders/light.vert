#version 400 core

in vec2 position;

out vec2 tex_frag_in;

void main(void){
	tex_frag_in = (position +1.0) / 2.0;
	gl_Position = vec4(position, 0.0, 1.0);
}