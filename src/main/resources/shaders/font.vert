#version 400 core

in vec2 position;
in vec2 texCoord;

out vec2 tex_frag_in;
out vec3 pos_frag_in;
out vec3 norm_frag_in;

uniform vec2 offset;
uniform float scale;

uniform mat4 mMatText;

uniform mat4 vpMat;
	
void main(void){

	// Remove scale component
	mat4 transMat = mat4(mMatText);
	transMat[0].xyz = normalize(mMatText[0].xyz);
	transMat[1].xyz = normalize(mMatText[1].xyz);
	transMat[2].xyz = normalize(mMatText[2].xyz);

	tex_frag_in = texCoord;
	norm_frag_in = normalize((transMat * vec4(0, 0, -1, 1)).xyz);
	
	vec2 charPos = position * scale +offset;
	
	pos_frag_in = (transMat * vec4(charPos, 0.1, 1.0)).xyz;
	
	gl_Position = vpMat * vec4(pos_frag_in, 1.0);
}