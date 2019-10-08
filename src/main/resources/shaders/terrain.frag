#version 400 core

in vec2 tex_frag_in;
in vec3 pos_frag_in;

out vec4 out_Color[8];

uniform sampler2D blendMap;
uniform sampler2D lightMap;

uniform vec3 camPos;

uniform float tiling;
uniform float tilingR;
uniform float tilingG;
uniform float tilingB;

uniform vec2 specular;
uniform vec2 specularR;
uniform vec2 specularG;
uniform vec2 specularB;

uniform float displacement;
uniform float displacementR;
uniform float displacementG;
uniform float displacementB;

uniform sampler2D aoTexture;
uniform sampler2D aoRTexture;
uniform sampler2D aoGTexture;
uniform sampler2D aoBTexture;

uniform sampler2D displacementTexture;
uniform sampler2D displacementRTexture;
uniform sampler2D displacementGTexture;
uniform sampler2D displacementBTexture;

uniform sampler2D specularTexture;
uniform sampler2D specularRTexture;
uniform sampler2D specularGTexture;
uniform sampler2D specularBTexture;

uniform vec3 diffuse;
uniform vec3 diffuseR;
uniform vec3 diffuseG;
uniform vec3 diffuseB;

uniform sampler2D diffuseTexture;
uniform sampler2D diffuseRTexture;
uniform sampler2D diffuseGTexture;
uniform sampler2D diffuseBTexture;

uniform sampler2D normalTexture;
uniform sampler2D normalRTexture;
uniform sampler2D normalGTexture;
uniform sampler2D normalBTexture;

const float disp_offset = 0.0f;

vec4 getFinalDiffuse(vec2 tiledTexCoords[4], vec4 blendingFactors){
	
    //Get Single TextureData Colors
    vec4 diffuse_color_0 = texture(diffuseTexture , tiledTexCoords[3]) * vec4(diffuse , 1.0) * blendingFactors.a;
    vec4 diffuse_color_1 = texture(diffuseRTexture, tiledTexCoords[0]) * vec4(diffuseR, 1.0) * blendingFactors.r;
    vec4 diffuse_color_2 = texture(diffuseGTexture, tiledTexCoords[1]) * vec4(diffuseG, 1.0) * blendingFactors.g;
    vec4 diffuse_color_3 = texture(diffuseBTexture, tiledTexCoords[2]) * vec4(diffuseB, 1.0) * blendingFactors.b;
	
    //Calculate Final Diffuse Color
    return diffuse_color_0 + diffuse_color_1 + diffuse_color_2 + diffuse_color_3;
}

vec4 getFinalNormal(vec2 tiledTexCoords[4], vec4 blendingFactors, mat3 tbnMat){
	
    //Get NormalMap Normals
    vec3 normal_0 = (2.0f * texture(normalTexture , tiledTexCoords[3]).rgb -1.0f) * blendingFactors.a;
    vec3 normal_1 = (2.0f * texture(normalRTexture, tiledTexCoords[0]).rgb -1.0f) * blendingFactors.r;
    vec3 normal_2 = (2.0f * texture(normalGTexture, tiledTexCoords[1]).rgb -1.0f) * blendingFactors.g;
    vec3 normal_3 = (2.0f * texture(normalBTexture, tiledTexCoords[2]).rgb -1.0f) * blendingFactors.b;
    
    //Calc FinalNormal in tangent space world space
    vec3 normal = normalize(normal_0 + normal_1 + normal_2 + normal_3);
	
    return vec4(normalize(tbnMat * normal), 1.0);
}

vec2 getFinalSpecular(vec2 tiledTexCoords[4], vec4 blendingFactors){
	
    //Get Single Specular Factors
    float specular_factor_0 = texture(specularTexture , tiledTexCoords[3]).r * blendingFactors.a;
    float specular_factor_1 = texture(specularRTexture, tiledTexCoords[0]).r * blendingFactors.r;
    float specular_factor_2 = texture(specularGTexture, tiledTexCoords[1]).r * blendingFactors.g;
    float specular_factor_3 = texture(specularBTexture, tiledTexCoords[2]).r * blendingFactors.b;
	
	//Calc Single speuclar settings
	vec2 specular_0 = specular  * blendingFactors.a;
	vec2 specular_1 = specularR * blendingFactors.r;
	vec2 specular_2 = specularG * blendingFactors.g;
	vec2 specular_3 = specularB * blendingFactors.b;
	
	float finalSpecularFactor = specular_factor_0 + specular_factor_1 + specular_factor_2 + specular_factor_3;
	vec2 finalSpecular = specular_0 + specular_1 + specular_2 + specular_3;
	
	return vec2(finalSpecularFactor * finalSpecular.x, finalSpecular.y);
}

float getFinalAo(vec2 tiledTexCoords[4], vec4 blendingFactors){
	
    //Get Single Ambient Occlusion Factors
    float ao_factor_0 = texture(aoTexture , tiledTexCoords[3]).r * blendingFactors.a;
    float ao_factor_1 = texture(aoRTexture, tiledTexCoords[0]).r * blendingFactors.r;
    float ao_factor_2 = texture(aoGTexture, tiledTexCoords[1]).r * blendingFactors.g;
    float ao_factor_3 = texture(aoBTexture, tiledTexCoords[2]).r * blendingFactors.b;
	
	return ao_factor_0 + ao_factor_1 + ao_factor_2 + ao_factor_3;
}

vec2[4] getParallaxDistortion(vec2 tiledTexCoords[4], mat3 tbnMat){
	vec3 toCam = normalize(transpose(tbnMat) * normalize(camPos -pos_frag_in));
	
	float disp_0 = texture(displacementTexture,  tex_frag_in * tiling ).r;
	float disp_1 = texture(displacementRTexture, tex_frag_in * tilingR).r;
	float disp_2 = texture(displacementGTexture, tex_frag_in * tilingG).r;
	float disp_3 = texture(displacementBTexture, tex_frag_in * tilingB).r;
	
	float bias_0 = displacement  / 2.0f;
	float bias_1 = displacementR / 2.0f;
	float bias_2 = displacementG / 2.0f;
	float bias_3 = displacementB / 2.0f;
	
	vec2 displacement_0 = toCam.xz * (disp_0 * displacement  + (-bias_0 + (bias_0 * disp_offset)));
	vec2 displacement_1 = toCam.xz * (disp_1 * displacementR + (-bias_1 + (bias_1 * disp_offset)));
	vec2 displacement_2 = toCam.xz * (disp_2 * displacementG + (-bias_2 + (bias_2 * disp_offset)));
	vec2 displacement_3 = toCam.xz * (disp_3 * displacementB + (-bias_3 + (bias_3 * disp_offset)));
	
	tiledTexCoords[3] += displacement_0;
	tiledTexCoords[0] += displacement_1;
	tiledTexCoords[1] += displacement_2;
	tiledTexCoords[2] += displacement_3;
	
	return tiledTexCoords;
}

void main(void){
	
    //Get Blending Factors from Blend Map
    vec4 blendingFactors = texture(blendMap, tex_frag_in);
    blendingFactors.a = 1.0 - (blendingFactors.r + blendingFactors.g + blendingFactors.b);
	
	//Calc tbn matrix to convert tangent to world space
	vec3 normal = normalize(2.0f * texture(lightMap, tex_frag_in).rbg -1.0f);
	vec3 tangent = vec3(normal.y, normal.x, 0);
	vec3 bitangent = vec3(0, normal.z, normal.y);
	mat3 tbnMat = mat3(tangent, bitangent, normal);
	
	//Calc tiledTexCoords for texture coords
    vec2 tiledTexCoords[4];
	tiledTexCoords[0] = tex_frag_in * tilingR;
	tiledTexCoords[1] = tex_frag_in * tilingG;
	tiledTexCoords[2] = tex_frag_in * tilingB;
	tiledTexCoords[3] = tex_frag_in * tiling ;
	
	tiledTexCoords = getParallaxDistortion(tiledTexCoords, tbnMat);
	
    //Set Final Colors
    out_Color[0] = getFinalDiffuse(tiledTexCoords, blendingFactors);
    out_Color[1] = vec4(pos_frag_in, 1.0);
    out_Color[2] = getFinalNormal(tiledTexCoords, blendingFactors, tbnMat);
	out_Color[3] = vec4(getFinalSpecular(tiledTexCoords, blendingFactors), 0, 1);
	out_Color[4] = vec4(1, 1, getFinalAo(tiledTexCoords, blendingFactors), 1);
	out_Color[5] = vec4(0, 0, 0, 1);
	out_Color[6] = vec4(0, 0, 0, 1);
	out_Color[7] = vec4(0, 0, 0, 1);
}