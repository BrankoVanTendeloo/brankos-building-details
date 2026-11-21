package branko.brankosbuildingdetails.client.mixin;

import branko.brankosbuildingdetails.BrankosBuildingDetails;
import branko.brankosbuildingdetails.BrankosBuildingDetailsBlocks;

import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameRenderer.class)
public abstract class ItemFrameRendererMixin {
	@Shadow private BlockModelResolver blockModelResolver;

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void extractRenderState(ItemFrame entity, ItemFrameRenderState state, float partialTicks, CallbackInfo ci) {
		BlockState blockState;
		if (entity.getType() == BrankosBuildingDetails.SPECTRAL_ITEM_FRAME_TYPE) {
			blockState = BrankosBuildingDetailsBlocks.SPECTRAL_ITEM_FRAME_BLOCK.defaultBlockState();
		} else if (entity.getType() == BrankosBuildingDetails.GLOW_SPECTRAL_ITEM_FRAME_TYPE) {
			blockState = BrankosBuildingDetailsBlocks.GLOW_SPECTRAL_ITEM_FRAME_BLOCK.defaultBlockState();
			state.isGlowFrame = true;
		} else {
			return;
		}

		if (state.isInvisible) {
			return;
		}

		BlockModelRenderState frameModel = state.frameModel;
		frameModel.clear();
		blockModelResolver.update(frameModel, blockState, ItemFrameRenderer.BLOCK_DISPLAY_CONTEXT);
	}
}
