<scene name="Cena 1" ID="0">
	<object z="0" name="Camera">
		<transform>
			<position x="-95.56128" y="-286.1482"/>
			<rotation value="0.0"/>
			<scale x="1.031007" y="1.0587174"/>
		</transform>
		<component class="br.com.lunacore.lunalire.components.CameraComponent">
			<viewport zoom="1.0" width="1360.6086" height="807.03534"/>
		</component>
	</object>
	<object class="br.com.lunacore.MyObject" z="1" name="Sidney">
		<transform>
			<position x="-427.12445" y="101.179375"/>
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
			<position x="217.38062" y="267.5409"/>
			<rotation value="0.0"/>
			<scale x="1.0" y="1.0"/>
		</transform>
		<component loc="fire.par" class="br.com.lunacore.lunalire.components.ParticleComponent"/>
	</object>
</scene>
