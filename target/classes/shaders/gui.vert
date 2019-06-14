#version 400 core

in vec2 position;

out vec2 tex_frag_in;
out vec3 pos_frag_in;
out vec3 norm_frag_in;

uniform mat4 transMat;
uniform mat4 vpMat;

void main(void){
	tex_frag_in = position * vec2(0.5, -0.5) +0.5;
	norm_frag_in = normalize((transMat * vec4(0, 0, -1, 1)).xyz);
	
	pos_frag_in = (transMat * vec4(position, 0, 1)).xyz;
	
	gl_Position = vpMat * vec4(pos_frag_in, 1.0);
}