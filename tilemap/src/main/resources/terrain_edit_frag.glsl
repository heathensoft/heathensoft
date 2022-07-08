#version 440

out vec4 f_color;

uniform sampler2D u_sampler;
uniform vec2 u_inc; // (1/width, 1/height)
uniform int u_mix; // conditional

in VS_OUT {
    vec2 uv;
} vs;

const vec2 adj[8] = vec2[8] (
vec2(-1, 1),vec2( 0, 1),vec2( 1, 1),
vec2(-1, 0),            vec2( 1, 0),
vec2(-1,-1),vec2( 0,-1),vec2( 1,-1)
);

void main()
{
    vec2 uv = vs.uv;
    vec4 texColor = texture(u_sampler,uv);
    vec4 color;
    if(u_mix > 0) { // MIX COLORS (Second pass)
        color = texColor;
        for(int i = 0; i < 8; i++) {
            vec2 sample_uv = (adj[i] * u_inc) + uv;
            color += texture(u_sampler,sample_uv);
        } color /= 9;
    } else { // STACK LAYERS (First pass)
        float r = texColor.r;
        float g = texColor.g;
        float b = texColor.b;
        float a = texColor.a;
        float r_i = 1.0 - r;
        float g_i = 1.0 - g;
        float b_i = 1.0 - b;
        float a_i = 1.0 - a;
        r *= (g_i * b_i * a_i);
        g *= (b_i * a_i);
        b *= (a_i);
        color = vec4(r,g,b,a);
    } f_color = color;
}