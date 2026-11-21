package branko.brankosbuildingdetails.item;

import branko.brankosbuildingdetails.BrankosBuildingDetails;
import branko.brankosbuildingdetails.entity.GlowSpectralItemFrameEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemFrameItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class GlowSpectralItemFrameItem extends ItemFrameItem {
	public GlowSpectralItemFrameItem(EntityType<? extends HangingEntity> entityType, Properties properties) {
		super(entityType, properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		BlockPos pos = context.getClickedPos();
		Direction clickedFace = context.getClickedFace();
		BlockPos blockPos = pos.relative(clickedFace);
		Player player = context.getPlayer();
		ItemStack itemInHand = context.getItemInHand();

		if (player != null && !this.mayPlace(player, clickedFace, itemInHand, blockPos)) {
			return InteractionResult.FAIL;
		}

		Level level = context.getLevel();
		GlowSpectralItemFrameEntity entity = new GlowSpectralItemFrameEntity(
			BrankosBuildingDetails.GLOW_SPECTRAL_ITEM_FRAME_TYPE, level, blockPos, clickedFace
		);

		EntityType.<HangingEntity>createDefaultStackConfig(level, itemInHand, player).apply(entity);
		if (entity.survives()) {
			if (!level.isClientSide()) {
				entity.playPlacementSound();
				level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());
				level.addFreshEntity(entity);
			}
			itemInHand.shrink(1);
			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.CONSUME;
		}
	}
}
