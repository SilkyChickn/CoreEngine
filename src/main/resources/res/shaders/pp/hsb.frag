#version 400 core

in vec2 tex_frag_in;

out vec4 out_Color;

uniform sampler2D colorTexture;
uniform sampler2D depthTexture;

uniform vec3 hsb;

//Convert a rgb color into a hsb color
vec3 rgb2hsb(vec3 c){
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
	
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

//Convert a hsb color into a rgb color
vec3 hsb2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main(void){
	vec4 inColor = texture(colorTexture, tex_frag_in);
	vec3 newHsb = rgb2hsb(inColor.rgb) * hsb;
	out_Color = vec4(hsb2rgb(newHsb), inColor.a);
}