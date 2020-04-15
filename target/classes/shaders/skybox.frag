#version 400 core

const int MAX_CUBE_MAPS = 10;

in vec3 tex_frag_in;

out vec4 out_Color[8];

uniform int cubeMapCount;
uniform samplerCube cubeMapTextures[MAX_CUBE_MAPS];
uniform float blendingFactors[MAX_CUBE_MAPS];

void main(void){
	
	out_Color[0] = vec4(0, 0, 0, 1);
	for(int i = 0; i < cubeMapCount; i++){
		out_Color[0] += texture(cubeMapTextures[i], tex_frag_in) * blendingFactors[i];
	}
	
    out_Color[1] = vec4(0, 0, 0, 1);
    out_Color[2] = vec4(0, 0, 0, 1);
    out_Color[3] = vec4(0, 0, 0, 1);
    out_Color[4] = vec4(0, 0, 0, 1);
    out_Color[5] = vec4(0, 0, 0, 1);
    out_Color[6] = vec4(0, 0, 0, 1);
    out_Color[7] = vec4(0, 0, 0, 1);
}
