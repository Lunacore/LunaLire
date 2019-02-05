<scene name="Cena 1" ID="0">
	<object class="br.com.lunacore.MyObject" z="10" name="Negresco">
		<transform>
			<position x="-43.192455" y="-147.54945"/>
			<rotation value="-23.439419"/>
			<scale x="0.8105184" y="0.72388995"/>
		</transform>
		<component class="br.com.lunacore.lunalire.components.SpriteComponent">
			<sprite loc="sidney.png" flipX="false" flipY="false"/>
		</component>
		<custom thisIsAPublicString=""/>
	</object>
	<object class="br.com.lunacore.MyObject" z="0" name="Camera">
		<transform>
			<position x="-571.0705" y="270.11978"/>
			<rotation value="0.0"/>
			<scale x="1.031007" y="1.0587174"/>
		</transform>
		<component class="br.com.lunacore.lunalire.components.ShapeRendererComponent">
			<rectangle a="1.0" b="1.0" angle="0.0" width="1280.0" height="720.0" x="0.0" g="1.0" shapeType="line" y="0.0" r="1.0"/>
		</component>
		<component class="br.com.lunacore.lunalire.components.CameraComponent">
			<viewport zoom="1.0" width="1280.0" height="720.0"/>
		</component>
		<custom thisIsAPublicString=""/>
	</object>
	<object z="1" name="Teste">
		<transform>
			<position x="-613.8725" y="1045.143"/>
			<rotation value="0.0"/>
			<scale x="4.3538413" y="4.715521"/>
		</transform>
		<component class="br.com.lunacore.lunalire.components.SpriteComponent">
			<sprite loc="negresco.png" flipX="false" flipY="false"/>
		</component>
	</object>
</scene>
