#version 400 core

in vec2 tex_frag_in;
in vec3 tan_frag_in;
in vec3 bit_frag_in;
in vec3 nrm_frag_in;
in vec4 pos_frag_in;

out vec4 out_Color[8];

uniform vec3 camPos;

uniform sampler2D diffuseMap;
uniform sampler2D normalMap;
uniform sampler2D specularMap;
uniform sampler2D displacementMap;
uniform sampler2D aoMap;
uniform sampler2D glowMap;

uniform float displacementFactor;
uniform float reflectivity;
uniform float shineDamper;

uniform vec3 diffuseColor;
uniform vec3 pickingColor;
uniform vec3 glowColor;

const float disp_offset = 0.01f;
const float ALPHA_THRESHOLD = 0.5f;

vec4 getDiffuseColor(vec2 texCoords){
    vec4 diffuseMapCol = texture(diffuseMap, texCoords);

    //Alpha
    if(diffuseMapCol.a < ALPHA_THRESHOLD){
        discard;
    }

    return diffuseMapCol * vec4(diffuseColor, 1.0);
}

vec4 getNormal(vec2 texCoords, mat3 tbnMat){
    vec3 normalMapNorm = normalize(2.0 * texture(normalMap, texCoords).rgb -1.0);
    return vec4(tbnMat * normalMapNorm, 1);
}

vec2 getSpecular(vec2 texCoords){
    float specMapFac = texture(specularMap, texCoords).r;
    return vec2(specMapFac * reflectivity, shineDamper);
}

float getAo(vec2 texCoords){
    return texture(aoMap, texCoords).r;
}

vec4 getGlowing(vec2 texCoords){
    vec4 glowingMapCol = texture(glowMap, texCoords);
    return glowingMapCol * vec4(glowColor, 1.0);
}

vec2 getParallaxDistortion(mat3 tbnMat){
    vec3 normToCam = normalize(transpose(tbnMat) * normalize(camPos -pos_frag_in.xyz));

    float disp = texture(displacementMap, tex_frag_in).r;
    float bias = displacementFactor / 2.0;
    vec2 displacement = normToCam.xz * (disp * displacementFactor + (-bias + (bias * disp_offset)));

    return tex_frag_in +displacement;
}

void main(void){
    mat3 tbnMat = mat3(tan_frag_in, bit_frag_in, nrm_frag_in);
    vec2 texCoords = getParallaxDistortion(tbnMat);

    out_Color[0] = getDiffuseColor(texCoords);
    out_Color[1] = pos_frag_in;
    out_Color[2] = getNormal(texCoords, tbnMat);
    out_Color[3] = vec4(getSpecular(texCoords), 0, 1);
    out_Color[4] = vec4(1.0, 1.0, getAo(texCoords), 1.0);
    out_Color[5] = vec4(pickingColor, 1.0);
    out_Color[6] = getGlowing(texCoords);
    out_Color[7] = vec4(0, 0, 0, 1);
}