#version 400 core

in vec2 tex_frag_in;
in vec3 nrm_frag_in;
in vec4 pos_frag_in;

out vec4 out_Color[8];

uniform vec3 camPos;

uniform sampler2D diffuseMap;

uniform float shininess;
uniform float shineDamper;

uniform vec3 diffuseColor;
uniform vec3 pickingColor;
uniform vec3 glowColor;

const float disp_offset = 0.01f;
const float ALPHA_THRESHOLD = 0.5f;

vec4 getDiffuseColor(vec2 texCoords){
	vec4 diffuseMapCol = texture(diffuseMap, texCoords);
	
	//Alpha
	if(diffuseMapCol.a < ALPHA_THRESHOLD){
		discard;
	}
	
	return diffuseMapCol * vec4(diffuseColor, 1.0);
}

void main(void){
	vec2 texCoords = tex_frag_in;
	texCoords.y = 1.0 - texCoords.y;
	
	out_Color[0] = getDiffuseColor(texCoords);
	out_Color[1] = pos_frag_in;
	out_Color[2] = vec4(nrm_frag_in, 1.0);
	out_Color[3] = vec4(shininess, shineDamper, 0, 1);
	out_Color[4] = vec4(1.0, 1.0, 1.0, 1.0);
	out_Color[5] = vec4(pickingColor, 1.0);
	out_Color[6] = vec4(glowColor, 1.0);
	out_Color[7] = vec4(0, 0, 0, 1);
}