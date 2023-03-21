#version 400 core

in vec2 tex_frag_in;
in vec3 nrm_frag_in;
in vec4 pos_frag_in; //W = AO
in float vis_frag_in;

out vec4 out_Color[8];

uniform sampler2D bladesTexture;
uniform vec3 bladesColor;

void main(void){
	vec4 color = texture(bladesTexture, tex_frag_in);
	
	if(vis_frag_in == 0.0f || color.a < 0.5f) 
		discard;
	
	out_Color[0] = color * vec4(bladesColor, vis_frag_in);
	out_Color[1] = vec4(pos_frag_in.xyz, 1);
	out_Color[2] = vec4(nrm_frag_in, 1);
	out_Color[3] = vec4(0, 0, 0, 1);
	out_Color[4] = vec4(1, 1, pos_frag_in.w, 1);
	out_Color[5] = vec4(0, 0, 0, 1);
	out_Color[6] = vec4(0, 0, 0, 1);
	out_Color[7] = vec4(0, 0, 0, 1);
}