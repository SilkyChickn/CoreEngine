#version 400 core

in vec2 position;

out vec2 tex_frag_in;

uniform mat4 vpMat;
uniform float scale;

void main(void){
	tex_frag_in = position * 0.5 -0.5;
	gl_Position = vpMat * (vec4(position * scale, 0, 1));
}