#version 400 core

in vec3 position;

uniform mat4 transMat;
uniform mat4 vpMat;

void main(void){
    gl_Position = vpMat * transMat * vec4(position, 1.0);
}