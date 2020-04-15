#version 400 core

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

in vec2 tex_geo_in[];

out vec2 tex_frag_in;
out vec3 pos_frag_in;

uniform mat4 vpMat;
uniform mat4 mMat;

uniform vec4 clipPlane;

void main(void){
    
    for(int i = 0; i < 3; i++){
        
		vec4 worldPosition = mMat * gl_in[i].gl_Position;
		
        //GL Position / Clip Distance
        gl_Position = vpMat * worldPosition;
        gl_ClipDistance[0] = dot(worldPosition, clipPlane);
		
        //Set Values For Fragment Shader
        tex_frag_in = tex_geo_in[i];
		pos_frag_in = (worldPosition).xyz;
        
        EmitVertex();
    }
    
    EndPrimitive();
}
