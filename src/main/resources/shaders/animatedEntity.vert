#version 400 core

const int MAX_JOINTS = 50;

in vec3 position;
in vec2 texCoord;
in vec3 normal;
in vec3 tangent;
in ivec3 joints;
in vec3 weights;

out vec2 tex_frag_in;
out vec3 tan_frag_in;
out vec3 bit_frag_in;
out vec3 nrm_frag_in;
out vec4 pos_frag_in;

uniform mat4 transMat;
uniform mat4 vpMat;

uniform mat4 jointMat[MAX_JOINTS];
uniform int jointCount;

uniform float tiling;

uniform vec4 clipPlane;

void main(void){
    tex_frag_in = texCoord * tiling;

    pos_frag_in = vec4(0.0);
    nrm_frag_in = vec3(0.0);
    tan_frag_in = vec3(0.0);

    //Iterate through effected joints
    for(int i = 0; i < 3; i++){
        mat4 jointTrans = jointMat[joints[i]];

        //Interpolate position
        vec4 pos = jointTrans * vec4(position, 1.0);
        pos_frag_in += pos * weights[i];

        //Interpolate normal
        vec4 nrm = jointTrans * vec4(normal, 0.0);
        nrm_frag_in += nrm.xyz * weights[i];

        //Interpolate tangent
        vec4 tan = jointTrans * vec4(tangent, 0.0);
        tan_frag_in += tan.xyz * weights[i];
    }

    tan_frag_in = normalize((transMat * vec4(tan_frag_in, 0.0)).xyz);
    nrm_frag_in = normalize((transMat * vec4(nrm_frag_in, 0.0)).xyz);
    bit_frag_in = normalize(cross(nrm_frag_in, tan_frag_in));

    pos_frag_in = transMat * pos_frag_in;
    gl_ClipDistance[0] = dot(pos_frag_in, clipPlane);
    gl_Position = vpMat * pos_frag_in;
}