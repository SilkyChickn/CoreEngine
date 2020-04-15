#version 400 core

in vec2 position;

out vec2 tex_frag_in;
out vec4 csp_frag_in;
out vec3 tcam_frag_in;
out vec3 pos_frag_in;

uniform mat4 mMat;
uniform mat4 vpMat;

uniform float tiling;
uniform vec3 camPos;

void main(void){
	
	vec2 posXZ = position * 0.5f +0.5f;
	vec4 worldPos = mMat * vec4(posXZ.x, 0, -posXZ.y +1, 1.0);
	
	tex_frag_in = posXZ * tiling;
	csp_frag_in = vpMat * worldPos;
	tcam_frag_in = camPos -worldPos.xyz;
	pos_frag_in = worldPos.xyz;
	
	gl_Position = csp_frag_in;
}