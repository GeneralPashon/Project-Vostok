#version 330

in vec4 color;
centroid in vec2 uv;

uniform sampler2D u_atlas;

void main(){
    vec4 color = texture(u_atlas, uv) * color;
    if(color.a <= 0)
        discard;

    gl_FragColor = color;
}
