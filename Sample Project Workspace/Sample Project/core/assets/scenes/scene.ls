<scene name="Cena 1" ID="0">
	<object z="0" name="Camera">
		<transform>
			<position x="338.56247" y="-232.26254"/>
			<rotation value="0.0"/>
			<scale x="1.0" y="1.0"/>
		</transform>
		<component class="br.com.lunacore.lunalire.components.ShapeRendererComponent">
			<rectangle a="1.0" r="1.0" angle="0.0" width="1280.0" b="1.0" x="0.0" g="1.0" shapeType="line" y="0.0" height="720.0"/>
		</component>
		<component class="br.com.lunacore.lunalire.components.CameraComponent">
			<viewport zoom="1.0" width="0.0" height="0.0"/>
		</component>
	</object>
	<object z="1" name="Sidney">
		<transform>
			<position x="85.84473" y="-166.41162"/>
			<rotation value="0.0"/>
			<scale x="1.0" y="1.0"/>
		</transform>
		<component class="br.com.lunacore.lunalire.components.SpriteComponent">
			<sprite loc="sidney.png" flipX="false" flipY="false"/>
		</component>
		<object z="10" name="Negresco">
			<transform>
				<position x="153.31549" y="58.13434"/>
				<rotation value="29.415482"/>
				<scale x="2.0085099" y="2.188506"/>
			</transform>
			<component class="br.com.lunacore.lunalire.components.SpriteComponent">
				<sprite loc="negresco.png" flipX="false" flipY="false"/>
			</component>
		</object>
	</object>
</scene>
