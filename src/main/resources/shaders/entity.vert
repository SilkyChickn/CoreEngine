#version 400 core

in vec3 position;
in vec2 texCoord;
in vec3 normal;
in vec3 tangent;

out vec2 tex_frag_in;
out vec3 tan_frag_in;
out vec3 bit_frag_in;
out vec3 nrm_frag_in;
out vec4 pos_frag_in;

uniform mat4 transMat;
uniform mat4 vpMat;

uniform float tiling;

uniform vec4 clipPlane;

void main(void){
	tex_frag_in = texCoord * tiling;
	pos_frag_in = transMat * vec4(position, 1.0);
	
	tan_frag_in = normalize((transMat * vec4(tangent, 0.0)).xyz);
	nrm_frag_in = normalize((transMat * vec4(normal, 0.0)).xyz);
	bit_frag_in = normalize(cross(nrm_frag_in, tan_frag_in));
	
	gl_ClipDistance[0] = dot(pos_frag_in, clipPlane);
	gl_Position = vpMat * pos_frag_in;
}