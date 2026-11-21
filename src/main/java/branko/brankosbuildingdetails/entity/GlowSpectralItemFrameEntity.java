package branko.brankosbuildingdetails.entity;

import branko.brankosbuildingdetails.BrankosBuildingDetails;
import branko.brankosbuildingdetails.BrankosBuildingDetailsItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class GlowSpectralItemFrameEntity extends GlowItemFrame {
	public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
		"brankos-building-details", "block/glow_spectral_item_frame"
	);

	public GlowSpectralItemFrameEntity(EntityType<? extends GlowItemFrame> entityType, Level level) {
		super(entityType, level);
	}

	public GlowSpectralItemFrameEntity(EntityType<? extends GlowItemFrame> entityType, Level level, BlockPos pos, Direction direction) {
		super(entityType, level);
		this.pos = pos;
		this.setDirection(direction);
		this.xRotO = this.getXRot();
		this.yRotO = this.getYRot();
		this.recalculateBoundingBox();
	}

	public Identifier getTextureId() {
		return TEXTURE;
	}

	@Override
	protected ItemStack getFrameItemStack() {
		return new ItemStack(BrankosBuildingDetailsItems.GLOW_SPECTRAL_ITEM_FRAME);
	}

	@Override
	public void setItem(ItemStack stack, boolean broadcast) {
		boolean wasInvisible = isInvisible();
		super.setItem(stack, broadcast);
		setInvisible(!stack.isEmpty());
		if (wasInvisible != isInvisible() && !level().isClientSide()) {
			spawnTransitionParticles();
		}
	}

	private void spawnTransitionParticles() {
		ServerLevel serverLevel = (ServerLevel) level();
		BlockPos pos = this.pos;
		double centerX = pos.getX() + 0.5D;
		double centerY = pos.getY() + 0.5D;
		double centerZ = pos.getZ() + 0.5D;
		int count = 4 + level().getRandom().nextInt(3);
		for (int i = 0; i < count; i++) {
			double offsetX = (level().getRandom().nextDouble() - 0.5D) * 0.6D;
			double offsetY = (level().getRandom().nextDouble() - 0.5D) * 0.6D;
			double offsetZ = (level().getRandom().nextDouble() - 0.5D) * 0.6D;
			double vx = (level().getRandom().nextDouble() - 0.5D) * 0.02D;
			double vy = (level().getRandom().nextDouble() - 0.5D) * 0.02D;
			double vz = (level().getRandom().nextDouble() - 0.5D) * 0.02D;
			serverLevel.sendParticles(BrankosBuildingDetails.SPECTRAL_PARTICLE,
				centerX + offsetX, centerY + offsetY, centerZ + offsetZ,
				1, vx, vy, vz, 0.01D);
		}
	}
}
