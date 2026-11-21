package branko.brankosbuildingdetails;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;
import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBreakContext;
import branko.brankosbuildingdetails.entity.GlowSpectralItemFrameEntity;
import branko.brankosbuildingdetails.entity.SpectralItemFrameEntity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.Identifier;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrankosBuildingDetails implements ModInitializer {
	public static final String MOD_ID = "brankos-building-details";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static EntityType<SpectralItemFrameEntity> SPECTRAL_ITEM_FRAME_TYPE;
	public static EntityType<GlowSpectralItemFrameEntity> GLOW_SPECTRAL_ITEM_FRAME_TYPE;

	public static final SimpleParticleType SPECTRAL_PARTICLE = FabricParticleTypes.simple(true);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		ResourceKey<EntityType<?>> spectralKey = ResourceKey.create(Registries.ENTITY_TYPE, id("spectral_item_frame"));
		SPECTRAL_ITEM_FRAME_TYPE = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			spectralKey,
			EntityType.Builder.<SpectralItemFrameEntity>of(SpectralItemFrameEntity::new, MobCategory.MISC)
				.noLootTable()
				.sized(0.5F, 0.5F)
				.eyeHeight(0.0F)
				.build(spectralKey)
		);

		ResourceKey<EntityType<?>> glowSpectralKey = ResourceKey.create(Registries.ENTITY_TYPE, id("glow_spectral_item_frame"));
		GLOW_SPECTRAL_ITEM_FRAME_TYPE = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			glowSpectralKey,
			EntityType.Builder.<GlowSpectralItemFrameEntity>of(GlowSpectralItemFrameEntity::new, MobCategory.MISC)
				.noLootTable()
				.sized(0.5F, 0.5F)
				.eyeHeight(0.0F)
				.build(glowSpectralKey)
		);

		Registry.register(BuiltInRegistries.PARTICLE_TYPE, id("spectral"), SPECTRAL_PARTICLE);

		BrankosBuildingDetailsBlocks.initialize();
		BrankosBuildingDetailsItems.initialize();

		// Track which face is being attacked when player starts breaking a glass panel
		AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
			if (level.isClientSide()) {
				return InteractionResult.PASS;
			}

			BlockState state = level.getBlockState(pos);
			if (!(state.getBlock() instanceof GlassPanelBlock)) {
				return InteractionResult.PASS;
			}

			// The clicked direction tells us which face of the block was clicked.
			// For a glass panel, we need to find which panel face is at that location.
			// Since panels are thin (1/16 block), the clicked direction helps us determine
			// which face the player is targeting.
			Direction face = findFaceAtDirection(state, pos, player, direction);
			if (face != null && GlassPanelBlock.hasFace(state, face)) {
				GlassPanelBreakContext.startBreaking(player, pos, face);
			}

			return InteractionResult.PASS;
		});

		PlayerBlockBreakEvents.CANCELED.register((world, player, pos, state, blockEntity) -> {
			GlassPanelBreakContext.stopBreaking(player);
		});
	}

	/**
	 * Determines which glass panel face the player is targeting by raycasting from the player's
	 * eye position to get the exact hit location, then checking which panel face that location
	 * falls within.
	 */
	private Direction findFaceAtDirection(BlockState state, BlockPos pos, Player player, Direction clickedDirection) {
		// Raycast from the player to get the exact hit location on the block
		double reach = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
		HitResult hitResult = player.pick(reach, 0.0F, false);
		if (!(hitResult instanceof BlockHitResult blockHit)) {
			return null;
		}
		if (!blockHit.getBlockPos().equals(pos)) {
			return null;
		}

		// Check the hit location against each face's thin panel area
		double x = blockHit.getLocation().x() - pos.getX();
		double y = blockHit.getLocation().y() - pos.getY();
		double z = blockHit.getLocation().z() - pos.getZ();

		// Check each face that exists in the block state
		for (Direction face : Direction.values()) {
			if (!GlassPanelBlock.hasFace(state, face)) {
				continue;
			}
			boolean onFace = switch (face) {
				case NORTH -> z <= 1.0 / 16.0;
				case SOUTH -> z >= 15.0 / 16.0;
				case EAST -> x >= 15.0 / 16.0;
				case WEST -> x <= 1.0 / 16.0;
				case UP -> y >= 15.0 / 16.0;
				case DOWN -> y <= 1.0 / 16.0;
				default -> false;
			};
			if (onFace) {
				return face;
			}
		}
		return null;
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
