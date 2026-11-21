package branko.brankosbuildingdetails.client.particle;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

@Environment(EnvType.CLIENT)
public class FallingDirtPieceParticleProvider implements ParticleProvider<SimpleParticleType> {
	private final SpriteSet spriteSet;

	public FallingDirtPieceParticleProvider(SpriteSet spriteSet) {
		this.spriteSet = spriteSet;
	}

	@Override
	public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double vx, double vy, double vz, RandomSource random) {
		TextureAtlasSprite sprite = spriteSet.get(random);
		FallingDirtPieceParticle particle = new FallingDirtPieceParticle(level, x, y, z, vx, vy, vz, sprite);
		return particle;
	}
}
