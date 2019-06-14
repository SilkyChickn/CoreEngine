#version 400 core

in vec3 position;

out vec3 tex_frag_in;

uniform mat4 vpMat;
uniform mat4 transMat;

uniform float size;
uniform vec3 camPos;

void main(void){
    tex_frag_in = position;
    gl_Position = vpMat * (transMat * vec4(position * size, 1.0) +vec4(camPos, 0.0));
}
