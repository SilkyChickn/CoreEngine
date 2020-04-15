#version 400 core

in vec3 position;

out vec2 tex_frag_in;

void main(void){
	gl_Position = vec4(position, 1.0f);
}