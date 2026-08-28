package fabiofdez.knots_and_rings.mixin;

import fabiofdez.knots_and_rings.feature.SaplingType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(LeavesBlock.class)
public abstract class LeavesMixin extends BlockBehaviorMixin {

  @Override
  protected List<ItemStack> knots_and_rings$getDrops(List<ItemStack> drops, BlockState state, ServerLevel level) {
    drops.replaceAll((stack) -> {
      if (!(stack.getItem() instanceof BlockItem item)) return stack;

      SaplingType type = SaplingType.ofSapling(item.getBlock());
      if (type != SaplingType.NONE) {
        ItemStack newStack = type.seed().asItem().getDefaultInstance();
        newStack.setCount(stack.getCount());
        return newStack;
      }

      return stack;
    });

    return drops;
  }
}
