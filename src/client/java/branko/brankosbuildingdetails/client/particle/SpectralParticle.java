package branko.brankosbuildingdetails.client.particle;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;

@Environment(EnvType.CLIENT)
public class SpectralParticle extends SimpleAnimatedParticle {
	public SpectralParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprites) {
		super(level, x, y, z, sprites, 0.01F);
		this.xd = vx;
		this.yd = vy;
		this.zd = vz;
		this.lifetime = 20 + random.nextInt(20);
		this.setSpriteFromAge(sprites);
	}

	@Override
	public void tick() {
		// Float upward very slowly (before super.tick() applies gravity)
		yd += 0.0002D;
		// Slow down horizontal movement
		xd *= 0.95D;
		zd *= 0.95D;
		super.tick();

		// Fade out over time (no fade in - spawn at full opacity)
		float progress = (float) age / lifetime;
		if (progress > 0.5F) {
			setAlpha(1.0F - (progress - 0.5F) / 0.5F);
		} else {
			setAlpha(1.0F);
		}
	}

	@Override
	public void remove() {
		if (!removed) {
			removed = true;
		}
	}
}
