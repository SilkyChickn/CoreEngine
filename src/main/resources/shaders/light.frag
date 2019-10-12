#version 400 core

const int MAX_LIGHTS = 25; //Keep syncron with deferred renderer!

out vec4 out_Color;

in vec2 tex_frag_in;

//GBuffer Textures
uniform sampler2D colorBuffer;
uniform sampler2D positionBuffer;
uniform sampler2D normalBuffer;
uniform sampler2D variable0Buffer;
uniform sampler2D variable1Buffer;

//Shadow Light
uniform sampler2D shadowMap;
uniform mat4 toShadowMapSpace;
uniform float enableShadows;

//Ambient Lights
uniform vec3 alColors[MAX_LIGHTS];
uniform float alIntensities[MAX_LIGHTS];
uniform int alCount;

//Directional Lights
uniform vec3 dlColors[MAX_LIGHTS];
uniform float dlIntensities[MAX_LIGHTS];
uniform vec3 dlDirections[MAX_LIGHTS];
uniform int dlCount;

//Point Lights
uniform vec3 plColors[MAX_LIGHTS];
uniform float plIntensities[MAX_LIGHTS];
uniform vec3 plPositions[MAX_LIGHTS];
uniform vec2 plAttenuations[MAX_LIGHTS];
uniform int plCount;

//Spot Lights
uniform vec3 slColors[MAX_LIGHTS];
uniform float slIntensities[MAX_LIGHTS];
uniform vec3 slPositions[MAX_LIGHTS];
uniform vec2 slAttenuations[MAX_LIGHTS];
uniform vec3 slDirections[MAX_LIGHTS];
uniform vec2 slLightCones[MAX_LIGHTS];
uniform int slCount;

uniform vec3 camPos;

//Diffuse factor of ambient lights
vec3 getAmbientDiffuse(){
	vec3 alDiffuse = vec3(0.0f);
	
	for(int i = 0; i < alCount; i++){
		alDiffuse += alColors[i] / alIntensities[i];
	}
	
	return alDiffuse;
}

//Diffuse factor of directional lights
vec3 getDirectionalDiffuse(vec3 normal){
	vec3 dlDiffuse = vec3(0.0f);
	
	for(int i = 0; i < dlCount; i++){
		float brightness = max(dot(-normalize(dlDirections[i]), normal), 0.0);
		dlDiffuse += (dlColors[i] * brightness) / dlIntensities[i];
	}
	
	return dlDiffuse;
}

//Diffuse and Specular factor of point lights
vec3[2] getPointDiffuseSpecular(
	vec3 normal, vec3 position, vec3 toCam, float reflectivity, float shineDamper, float useFakeDiffuseLighting){
	
	vec3 plDiffuse = vec3(0.0f);
	vec3 plSpecular = vec3(0.0f);
	
	for(int i = 0; i < plCount; i++){
	
		vec3 toLight = plPositions[i] -position;
		float toLightDistance = length(toLight);
		toLight /= toLightDistance;
		
		float attenuation = plIntensities[i] + plAttenuations[i].x * toLightDistance + 
			plAttenuations[i].y * toLightDistance * toLightDistance;
		
		//Diffuse
		float brightness;
		if(useFakeDiffuseLighting == 1.0){
			brightness = max(dot(toLight, vec3(0, 1, 0)), 0.0);
			plDiffuse += (plColors[i] * brightness) / attenuation;
		}else{
			brightness = max(dot(toLight, normal), 0.0);
			plDiffuse += (plColors[i] * brightness) / attenuation;
		}
		
		//Specular
		vec3 reflectVec = reflect(-toLight, normal);
		brightness = max(dot(reflectVec, toCam), 0.0);
		plSpecular += (plColors[i] * reflectivity * pow(brightness, shineDamper)) / attenuation;
	}
	
	vec3 result[2];
	result[0] = plDiffuse;
	result[1] = plSpecular;
	
	return result;
}

//Diffuse and Specular factor of spot lights
vec3[2] getSpotDiffuseSpecular(
	vec3 normal, vec3 position, vec3 toCam, float reflectivity, float shineDamper, float useFakeDiffuseLighting){
	
	vec3 slDiffuse = vec3(0.0f);
	vec3 slSpecular = vec3(0.0f);
	
	for(int i = 0; i < slCount; i++){
		
		vec3 toLight = slPositions[i] -position;
		float toLightDistance = length(toLight);
		toLight /= toLightDistance;
		
		float attenuation = slIntensities[i] + slAttenuations[i].x * toLightDistance + 
			slAttenuations[i].y * toLightDistance * toLightDistance;
		
		vec3 lightDir = normalize(slDirections[i]);
		float coneIntense = max(dot(lightDir, -toLight), 0.0f);
		
		if(coneIntense < slLightCones[i].y){
			continue;
		}else if(coneIntense < slLightCones[i].x){
			float diff = slLightCones[i].x -slLightCones[i].y;
			coneIntense = (coneIntense -slLightCones[i].y) / diff;
		}else{
			coneIntense = 1.0f;
		}
		
		//Diffuse
		float brightness;
		if(useFakeDiffuseLighting == 1.0){
			brightness = max(dot(toLight, vec3(0, 1, 0)), 0.0) * coneIntense;
			slDiffuse += (slColors[i] * brightness) / attenuation;
		}else{
			brightness = max(dot(toLight, normal), 0.0) * coneIntense;
			slDiffuse += (slColors[i] * brightness) / attenuation;
		}
		
		//Specular
		vec3 reflectVec = reflect(-toLight, normal);
		brightness = max(dot(reflectVec, toCam), 0.0) * coneIntense;
		slSpecular += (slColors[i] * reflectivity * pow(brightness, shineDamper)) / attenuation;
	}
	
	vec3 result[2];
	result[0] = slDiffuse;
	result[1] = slSpecular;
	
	return result;
}

//Factor if object is in shadow
float getShadowFactor(vec3 position){
	if(enableShadows == 0.0f) return 0.0f;

	//Get position on shadowmap
	vec3 shadowMapPos = (0.5 + (0.5 * toShadowMapSpace * vec4(position, 1.0))).xyz;

	//Calculate shadow map value
	float shadowFactor = 0;
	if(shadowMapPos.x >= 0 && shadowMapPos.x <= 1 && shadowMapPos.y >= 0 && shadowMapPos.y <= 1){
		float shadowMapColor = texture(shadowMap, shadowMapPos.xy).r;

		//Check if texel is in shadow
		if(shadowMapPos.z - 0.00001f > shadowMapColor){
			shadowFactor -= 0.15f;
		}
	}

	return shadowFactor;
}

void main(void){
	vec4 color 			= texture(colorBuffer, tex_frag_in);
	
	vec3 variable1 		= texture(variable1Buffer, tex_frag_in).rgb;
	float effected 		= variable1.x;
	float ao 			= variable1.z;
	
	if(effected == 0.0f){
		out_Color = color;
		return;
	}
	
	vec3 variable0 					= texture(variable0Buffer, tex_frag_in).rgb;
	float reflectivity 				= variable0.x;
	float shineDamper 				= max(1.0, variable0.y);
	float useFakeDiffuseLighting 	= variable0.z;

	vec3 position 		= texture(positionBuffer, tex_frag_in).rgb;
	vec3 normal 		= texture(normalBuffer, tex_frag_in).rgb;
	
	vec3 toCam = normalize(camPos -position);
	
	vec3[] pointDiffuseSpecular = getPointDiffuseSpecular(
		normal, position, toCam, reflectivity, shineDamper, useFakeDiffuseLighting);
	vec3[] spotDiffuseSpecular 	= getSpotDiffuseSpecular(
		normal, position, toCam, reflectivity, shineDamper, useFakeDiffuseLighting);
	
	vec3 diffuseFactor 	= (getAmbientDiffuse() +getDirectionalDiffuse(normal) +
		pointDiffuseSpecular[0] +spotDiffuseSpecular[0]) * ao;
	vec3 specularFactor = (pointDiffuseSpecular[1] +spotDiffuseSpecular[1]) * ao;

	float shadowFactor = getShadowFactor(position);

	vec4 finalLighting = vec4((color.rgb * diffuseFactor) + specularFactor +shadowFactor, 1.0);
	
	out_Color = mix(color, finalLighting, effected);
}