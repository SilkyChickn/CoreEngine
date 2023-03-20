#version 400 core

in vec2 tex_frag_in;
in vec4 csp_frag_in;
in vec3 tcam_frag_in;
in vec3 pos_frag_in;

out vec4 out_Color[8];

uniform sampler2D dudvMap;
uniform sampler2D normalMap;
uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D depthTexture;

uniform float waveStrength;
uniform vec4 multiplicativeColor;
uniform vec3 additiveColor;
uniform float softEdgeDepth;
uniform float offset;
uniform float shininess;
uniform float shineDamper;
uniform float reflectionEnabled;
uniform float refractionEnabled;

const float near = 0.01f;
const float far = 1000.0f;
const float distFactor = 0.1f;

void main(void){

    vec2 ndc = (csp_frag_in.xy/csp_frag_in.w)/2.0 + 0.5;
    vec2 refractTexCoords = vec2(ndc.x, ndc.y);

    float uwdepth = texture(depthTexture, refractTexCoords).r;
    float floorDistance = 2.0 * near * far / (far + near -(2.0 * uwdepth -1.0) * (far -near));

    float wdepth = gl_FragCoord.z;
    float waterDistance = 2.0 * near * far / (far + near -(2.0 * wdepth -1.0) * (far -near));
    float waterDepth = floorDistance -waterDistance;

    vec2 distTexCoords = texture(dudvMap, vec2(tex_frag_in.x +offset, tex_frag_in.y)).rg * distFactor;
    distTexCoords = tex_frag_in +vec2(distTexCoords.x, distTexCoords.y +offset);
    vec2 distortion = (texture(dudvMap, distTexCoords).rg * 2.0 - 1.0) * waveStrength * clamp(waterDepth * softEdgeDepth, 0.0, 1.0);

	vec3 normal = normalize(2.0f * texture(normalMap, distTexCoords).rbg -1.0);

    vec4 refractionColor = vec4(1.0);
    float alpha = 1;
    if(refractionEnabled == 1.0) {
        refractTexCoords += distortion;
        refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);
        refractionColor = texture(refractionTexture, refractTexCoords);
	    alpha = clamp(waterDepth * softEdgeDepth, 0.0, 1.0);
    }

    vec4 reflectionColor = vec4(1.0);
    if(reflectionEnabled == 1.0) {
        vec2 reflectTexCoords = vec2(ndc.x, -ndc.y);
        reflectTexCoords += distortion;

        reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
        reflectTexCoords.y = clamp(reflectTexCoords.y, -0.999, -0.001);

        reflectionColor = texture(reflectionTexture, reflectTexCoords);
    }
	
    vec3 normToCameraVector = normalize(tcam_frag_in);
    float refractionFactor = dot(normToCameraVector, vec3(0, 1, 0));
	refractionFactor = pow(refractionFactor, multiplicativeColor.a);
    
	out_Color[0] = mix(reflectionColor, refractionColor, refractionFactor) * vec4(multiplicativeColor.rgb, 1) + vec4(additiveColor, 0);
    out_Color[0].a = alpha;
	out_Color[1] = vec4(pos_frag_in, 1);
	out_Color[2] = vec4(normal, 1);
	out_Color[3] = vec4(shininess, shineDamper, 1, 1);
	out_Color[4] = vec4(1, 1, 1, 1);
	out_Color[5] = vec4(0, 0, 0, 1);
	out_Color[6] = vec4(0, 0, 0, 1);
	out_Color[7] = vec4(0, 0, 0, 1);
}