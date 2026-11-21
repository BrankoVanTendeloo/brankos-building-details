package branko.brankosbuildingdetails.client.particle;

import branko.brankosbuildingdetails.BrankosBuildingDetails;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class FallingDirtPieceParticle extends SingleQuadParticle {
	private TextureAtlasSprite sprite;

	public FallingDirtPieceParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, TextureAtlasSprite sprite) {
		super(level, x, y, z, sprite);
		this.sprite = sprite;
		this.setSize(0.01F, 0.01F);
		this.gravity = 0.04F;
		this.xd = vx;
		this.yd = vy;
		this.zd = vz;
		this.lifetime = (int) (64.0D / (random.nextFloat() * 0.8F + 0.2F));
		this.setColor(0.4745F, 0.3333F, 0.2275F);
	}

	@Override
	public void tick() {
		xo = x;
		yo = y;
		zo = z;
		preMoveUpdate();
		if (removed) {
			return;
		}
		yd -= gravity;
		move(xd, yd, zd);
		postMoveUpdate();
		if (removed) {
			return;
		}
		xd *= 0.98D;
		yd *= 0.98D;
		zd *= 0.98D;
	}

	protected void preMoveUpdate() {
		if (--lifetime <= 0) {
			remove();
		}
	}

	protected void postMoveUpdate() {
		if (onGround) {
			remove();
			level.addParticle(BrankosBuildingDetails.FALLING_DIRT_PIECE_LAND_PARTICLE, x, y, z, 0.0D, 0.0D, 0.0D);
			float volume = Mth.randomBetween(random, 0.3F, 1.0F);
			level.playLocalSound(x, y, z, BrankosBuildingDetails.FALLING_DIRT_PIECE_DROP_SOUND, SoundSource.BLOCKS, volume, 1.0F, false);
		}
	}

	@Override
	public void setSpriteFromAge(SpriteSet sprites) {
		setSprite(sprite);
	}

	protected void setSprite(TextureAtlasSprite sprite) {
		this.sprite = sprite;
	}

	@Override
	public SingleQuadParticle.Layer getLayer() {
		return SingleQuadParticle.Layer.OPAQUE;
	}

	@Override
	public void remove() {
		if (!removed) {
			removed = true;
		}
	}
}
