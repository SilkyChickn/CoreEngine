#version 400 core

in vec2 position;

out vec2 tex_tcs_in;
out vec2 pos_tcs_in;

uniform float chunkSize;
uniform vec2 chunkOffset;

void main(void){
	vec2 vertexPosition = (position * chunkSize) +chunkOffset;
	
	tex_tcs_in = vertexPosition;
	pos_tcs_in = vec2(vertexPosition.x, vertexPosition.y);
}