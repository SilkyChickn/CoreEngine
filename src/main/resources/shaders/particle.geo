#version 400 core

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

out vec2 tex_frag_in;
out vec3 pos_frag_in;

uniform mat4 vpMat;
uniform mat4 fMat;

uniform vec2 scale;
uniform vec3 pos;

void main(void){

    mat4 mMat = mat4(fMat[0], fMat[1], fMat[2], vec4(pos, 1.0f));
    mat4 mvpMat = vpMat * mMat;

    //Bottom left corner
    pos_frag_in = pos +(vec3(-0.5f, -0.5f, 0.0f) * vec3(scale.x, scale.y, 0.0f));
    gl_Position = mvpMat * vec4(pos_frag_in -pos, 1.0);
    tex_frag_in = vec2(0, 0);
    EmitVertex();

    //Bottom right corner
    pos_frag_in = pos +(vec3(0.5f, -0.5f, 0.0f) * vec3(scale.x, scale.y, 0.0f));
    gl_Position = mvpMat * vec4(pos_frag_in -pos, 1.0);
    tex_frag_in = vec2(1, 0);
    EmitVertex();

    //Top left corner
    pos_frag_in = pos +(vec3(-0.5f, 0.5f, 0.0f) * vec3(scale.x, scale.y, 0.0f));
    gl_Position = mvpMat * vec4(pos_frag_in -pos, 1.0);
    tex_frag_in = vec2(0, 1);
    EmitVertex();

    //Top right corner
    pos_frag_in = pos +(vec3(0.5f, 0.5f, 0.0f) * vec3(scale.x, scale.y, 0.0f));
    gl_Position = mvpMat * vec4(pos_frag_in -pos, 1.0);
    tex_frag_in = vec2(1, 1);
    EmitVertex();

    EndPrimitive();
}
