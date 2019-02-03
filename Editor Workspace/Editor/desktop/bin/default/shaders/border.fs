varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float borderSize;
uniform vec4 borderColor;

void main()
{
	float thr =
		texture2D(u_texture, v_texCoords + vec2(borderSize, 0)).a +
		texture2D(u_texture, v_texCoords + vec2(-borderSize, 0)).a +
		texture2D(u_texture, v_texCoords + vec2(0, borderSize)).a +
		texture2D(u_texture, v_texCoords + vec2(0, -borderSize)).a;
	
	if(texture2D(u_texture, v_texCoords).a <= 0.5 && thr > 0.5){
		gl_FragColor = borderColor;
	}
	else{
		vec4 orig = texture2D(u_texture, v_texCoords);
		gl_FragColor = mix(orig, vec4(0.0, 0.0, 1.0, orig.a), 0.1);
	}
	
   
}