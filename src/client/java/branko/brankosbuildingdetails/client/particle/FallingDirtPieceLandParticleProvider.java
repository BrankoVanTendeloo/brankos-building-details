package branko.brankosbuildingdetails.client.particle;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

@Environment(EnvType.CLIENT)
public class FallingDirtPieceLandParticleProvider implements ParticleProvider<SimpleParticleType> {
	private final SpriteSet spriteSet;

	public FallingDirtPieceLandParticleProvider(SpriteSet spriteSet) {
		this.spriteSet = spriteSet;
	}

	@Override
	public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double vx, double vy, double vz, RandomSource random) {
		return new FallingDirtPieceLandParticle(level, x, y, z, this.spriteSet.get(random));
	}
}
