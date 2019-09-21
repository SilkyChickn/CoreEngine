#version 400 core

in vec3 position;
in vec2 texCoord;

out vec2 tex_frag_in;
out vec3 nrm_frag_in;
out vec4 pos_frag_in; //W = AO
out float vis_frag_in;

uniform mat4 vpMat;
uniform vec3 camPos;

uniform sampler2D densityMap;
uniform vec2 area;
uniform sampler2D windMap;
uniform float windOffset;
uniform float windIntensity;
uniform float windMapTiling;
uniform float scale;
uniform int tuftCount;
uniform float tuftDistance;

uniform sampler2D heightMap;
uniform sampler2D lightMap;
uniform float amplitude;
uniform mat4 mMatTerr;

const float NO_GRAS_THRESHOLD = 0.1f;
const float AO_OFFSET = 10.0f;

void main(void){
	tex_frag_in = texCoord;
	
	float offsetX = mod(gl_InstanceID, tuftCount);
	float offsetZ = (gl_InstanceID -offsetX) / tuftCount -(tuftCount/2);
	
	offsetX -= (tuftCount/2);
	offsetX *= tuftDistance;
	offsetZ *= tuftDistance;
	
	vec3 off = vec3(0.0);
	off.x = offsetX +camPos.x -mod(offsetX +camPos.x, tuftDistance);
	off.z = offsetZ +camPos.z -mod(offsetZ +camPos.z, tuftDistance);
	
	vec2 posTerrSpaceXZ = (off.xz -vec2(mMatTerr[0][3], mMatTerr[2][3])) / mMatTerr[0][0];
	
	float density = texture(densityMap, posTerrSpaceXZ).r;
	
	vec2 wind = 2.0f * texture(windMap, posTerrSpaceXZ * windMapTiling +windOffset).rg -1.0f;
	wind *= windIntensity * position.y * density;
	
	vec3 worldPos = position * scale * vec3(1, density, 1) +off +vec3(wind.x, 0, wind.y);
	posTerrSpaceXZ = (worldPos.xz -vec2(mMatTerr[0][3], mMatTerr[2][3])) / mMatTerr[0][0];
	
	float height = texture(heightMap, posTerrSpaceXZ).r * amplitude * mMatTerr[0][0];
	worldPos.y += height;
	
	float camDistance = length(camPos -worldPos);
	vis_frag_in = clamp(exp(-pow(camDistance * area.x, area.y)), 0.0, 1.0);
	
	pos_frag_in = vec4(worldPos, position.y * scale +AO_OFFSET);
	nrm_frag_in = (2.0f * texture(lightMap, posTerrSpaceXZ) -1.0f).rbg;
	
	if(density == 0.0f)
		vis_frag_in = 0.0f;
	
	gl_Position = vpMat * vec4(worldPos, 1.0);
}