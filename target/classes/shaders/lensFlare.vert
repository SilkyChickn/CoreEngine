#version 400 core

in vec2 position;

out vec2 tex_frag_in;

uniform float size;
uniform vec3 pos;

uniform mat4 pMat;

void main(void){
	tex_frag_in = position * 0.5 +0.5;
	gl_Position = pMat * vec4(position * size, 0, 1.0) +vec4(pos, 0);
}