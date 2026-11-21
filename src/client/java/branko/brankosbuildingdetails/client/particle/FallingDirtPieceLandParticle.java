package branko.brankosbuildingdetails.client.particle;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

@Environment(EnvType.CLIENT)
public class FallingDirtPieceLandParticle extends SingleQuadParticle {
	public FallingDirtPieceLandParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
		super(level, x, y, z, sprite);
		this.setSize(0.01F, 0.01F);
		this.gravity = 0.06F;
		this.lifetime = 20;
		this.setColor(0.4745F, 0.3333F, 0.2275F);
	}

	@Override
	public void tick() {
		xo = x;
		yo = y;
		zo = z;
		yd -= gravity;
		move(xd, yd, zd);
		xd *= 0.98D;
		yd *= 0.98D;
		zd *= 0.98D;
		if (--lifetime <= 0) {
			remove();
		}
	}

	@Override
	public SingleQuadParticle.Layer getLayer() {
		return SingleQuadParticle.Layer.OPAQUE;
	}
}
