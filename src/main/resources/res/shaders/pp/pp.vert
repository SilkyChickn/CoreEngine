#version 400 core

in vec2 position;

out vec2 tex_frag_in;

void main(void){
	tex_frag_in = position * 0.5 +0.5;
	gl_Position = vec4(position.x, position.y, 0, 1);
}