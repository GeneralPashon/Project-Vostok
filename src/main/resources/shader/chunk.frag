#version 330

in vec4 color;
centroid in vec2 uv;

uniform sampler2D u_atlas;

void main(){
    gl_FragColor = texture(u_atlas, uv) * color;
}
