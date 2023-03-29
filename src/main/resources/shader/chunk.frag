varying vec4 color;
varying vec2 uv;

uniform sampler2D u_atlas;

void main(){
    vec4 color = texture2D(u_atlas, uv) * color;
    if(color.a <= 0)
        discard;

    gl_FragColor = color;
}
