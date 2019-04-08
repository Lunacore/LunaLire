<scene name="Cena 1" ID="0">
	<object z="0" name="Camera">
		<transform>
			<position x="-72.314644" y="84.49667"/>
			<rotation value="0.0"/>
			<scale x="1.031007" y="1.0587174"/>
		</transform>
		<component class="br.com.lunacore.lunalire.components.CameraComponent">
			<viewport zoom="1.0" width="1402.7971" height="854.4223"/>
		</component>
	</object>
	<object class="br.com.lunacore.MyObject" z="1" name="Sidney">
		<transform>
			<position x="-471.26257" y="154.60973"/>
			<rotation value="0.0"/>
			<scale x="1.0" y="1.0"/>
		</transform>
		<component class="br.com.lunacore.lunalire.components.SpriteComponent">
			<sprite loc="sidney.png" flipX="false" flipY="false"/>
		</component>
		<custom tumalaca=""/>
		<object z="2" name="Negresco">
			<transform>
				<position x="163.0321" y="52.078575"/>
				<rotation value="35.721222"/>
				<scale x="2.0" y="2.0"/>
			</transform>
			<component class="br.com.lunacore.lunalire.components.SpriteComponent">
				<sprite loc="negresco.png" flipX="false" flipY="false"/>
			</component>
		</object>
	</object>
	<object z="0" name="Particle">
		<transform>
			<position x="493.82465" y="-227.27051"/>
			<rotation value="0.0"/>
			<scale x="1.0" y="1.0"/>
		</transform>
		<component loc="fire.par" class="br.com.lunacore.lunalire.components.ParticleComponent"/>
	</object>
</scene>
