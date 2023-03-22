#version 400 core

in vec3 position;
in vec2 texCoord;
in vec3 normal;

out vec2 tex_frag_in;
out vec3 nrm_frag_in;
out vec4 pos_frag_in;

uniform mat4 transMat;
uniform mat4 vpMat;

uniform float tiling;

uniform vec4 clipPlane;

void main(void){
	tex_frag_in = texCoord * tiling;
	pos_frag_in = transMat * vec4(position, 1.0);
	
	nrm_frag_in = normalize((transMat * vec4(normal, 0.0)).xyz);
	 
	gl_ClipDistance[0] = dot(pos_frag_in, clipPlane);
	gl_Position = vpMat * pos_frag_in;
}