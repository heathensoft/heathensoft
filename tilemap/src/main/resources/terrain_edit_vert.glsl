#version 440

layout (location=0) in vec4 a_pos; // should be NDC
layout (location=1) in vec2 a_uv;


out VS_OUT {
    vec2 uv;
} fs;

void main() {

    gl_Position = a_pos;
    fs.uv = a_uv;
}

